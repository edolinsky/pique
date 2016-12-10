package services.content;

import play.Logger;
import services.RehydrationRequest;
import services.dataAccess.AbstractDataAccess;
import services.sources.Rehydratable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class RehydrationRunner implements Runnable {

    private Rehydratable source;
    private RehydrationRequest request;
    private AbstractDataAccess dataAccess;
    private String KEY = "new";
    // half a million LONGs ~ 4MB and is roughly the max posts in a day from twitter
    private Integer MAX_CACHE_SIZE = 500000;

    private Integer remainingQueries;
    private Long nextRefresh;
    // roughly a days worth should be kept = (1 day / window length) * requests per window *
    // posts per request
    private Set<Long> trackedIds;
    private Iterator<Long> idIterator;

    public RehydrationRunner(Rehydratable source, RehydrationRequest request,
            AbstractDataAccess dataAccess) {
        this.source = source;
        this.request = request;
        this.dataAccess = dataAccess;

        remainingQueries = source.numRehydrationQueries();
        nextRefresh = new Date().getTime() + source.rehydrationWindowLength();

        trackedIds = Collections.newSetFromMap(new LinkedHashMap<Long, Boolean>(){
            protected boolean removeEldestEntry(Map.Entry<Long, Boolean> eldest) {
                return size() > MAX_CACHE_SIZE;
            }
        });
        idIterator = trackedIds.iterator();
    }

    @Override
    public void run() {

        while (true) {
            synchronized (request) {
                try {
                    // wait for request to rehydrate
                    Logger.info("Sorter is waiting at " + new Date());
                    request.wait();
                } catch (InterruptedException e) {
                    Logger.error("Sorting Node Thread Exiting");
                }
            }

            // once notified, check for new and rehydrate.
            if (request.size() >= source.maxPostsForRehydrate()) {
                receiveNew();
            }
            if (remainingQueries > 0) {
                rehydrate();
                remainingQueries --;
            } else {
                long time = new Date().getTime();
                if (time > nextRefresh) {
                    remainingQueries = source.numRehydrationQueries();
                    nextRefresh = time + source.rehydrationWindowLength();
                }
            }

        }
    }

    private void receiveNew() {
        List<Long> hydrateIds = request.receiveRehydrationRequest(source.maxPostsForRehydrate());
        hydrateIds.forEach(id -> trackedIds.add(id));
    }

    private void rehydrate() {
        List<Long> hydrateIds = new ArrayList<>();
        for (int i = 0; i < source.maxPostsForRehydrate(); i++) {
            if (idIterator.hasNext()) {
                hydrateIds.add(idIterator.next());
            } else {
                idIterator = trackedIds.iterator();
            }
        }
        dataAccess.addNewPostsFromSource(source.getSourceName(), source.rehydrate(hydrateIds));
    }

    private void logRehydration(int num) {
        Logger.info("Rehydrator for:" + source.getSourceName() + " rehydrated " +
                num + " posts at " + new Date());
    }

    private void logSleep() {
        Logger.info("Rehydrator for:" + source.getSourceName() + " is sleeping at "
                + new Date());

    }

    private void logExit() {
        Logger.info("Rehydrator for:" + source.getSourceName() +
                " has been interrupted and is exiting.");
    }
}
