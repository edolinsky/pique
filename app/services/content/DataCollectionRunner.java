package services.content;

import play.Logger;
import services.RehydrationRequest;
import services.ThreadNotification;
import services.dataAccess.proto.PostProto;
import services.dataAccess.proto.PostProto.Post;
import services.sources.Rehydratable;

import javax.inject.Inject;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An AutonmousDataCollector is a runnable collector that will continuously operate its given
 * {@link AbstractDataCollector}
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class DataCollectionRunner implements Runnable {

	AbstractDataCollector collector;
    ThreadNotification notification;
    Thread rehydrator;
    RehydrationRequest rehydrationRequest;

	public DataCollectionRunner(AbstractDataCollector collector, ThreadNotification notify) {
		this.collector = collector;
        this.notification = notify;

        if (collector.getSource() instanceof Rehydratable) {
            rehydrationRequest = new RehydrationRequest();
            rehydrator = new Thread(new RehydrationRunner((Rehydratable) collector.getSource(),
                    rehydrationRequest, collector.dataAccess));
        }
	}

	@Override
	public void run() {

        while (true) {
            synchronized (notification) {
                int numPosts = collect();
                logCollection(numPosts);
                notification.notify();

                if (rehydrationRequest != null) {
                    rehydrationRequest.notify();
                }
            }

            try {
                logSleep();
                Thread.sleep(collector.getSource().getQueryDelta());
            } catch (InterruptedException e1) {
                logExit();
            }
        }
	}

    /**
     * Grabs a list of posts and puts it into the data tier
     * @return the number of posts collected
     */
    public int collect() {
        List<Post> posts = collector.fetch();
        collector.store(posts);
        rehydrationRequest.requestRehydrationFor(posts.stream()
                .map(p -> Long.parseLong(p.getId())).collect(Collectors.toList()));
        return posts.size();
    }

    private void logCollection(int num) {
        Logger.info("Collector:" + collector.getSource().getSourceName() + " Collected " + num +
                " posts at " + new Date());
    }

    private void logSleep() {
        Logger.info("Collector:" + collector.getSource().getSourceName() + " is sleeping at "
                + new Date());

    }

    private void logExit() {
        if (rehydrator != null) {
            rehydrator.interrupt();
        }
        Logger.info("Collector:" + collector.getSource().getSourceName() +
                " has been interrupted and is exiting.");
    }
}
