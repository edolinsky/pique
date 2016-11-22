package services.content;

import play.Logger;
import services.ThreadNotification;

import javax.inject.Inject;
import java.util.Date;

/**
 * An AutonmousDataCollector is a runnable collector that will continuously operate its given
 * {@link AbstractDataCollector}
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class DataCollectionRunner implements Runnable {

	AbstractDataCollector collector;
    ThreadNotification notification;

	public DataCollectionRunner(AbstractDataCollector collector, ThreadNotification notify) {
		this.collector = collector;
        this.notification = notify;
	}

	@Override
	public void run() {

        while (true) {
            synchronized (notification) {
                int numPosts = collector.collect();
                logCollection(numPosts);
                notification.notify();
            }

            try {
                logSleep();
                Thread.sleep(collector.getSource().getQueryDelta());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
	}

    private void logCollection(int num) {
        Logger.info("Collector:" + collector.getSource().getSourceName() + " Collected " + num +
                " posts at " + new Date());
    }

    private void logSleep() {
        Logger.info("Collector:" + collector.getSource().getSourceName() + " is sleeping at "
                + new Date());

    }
}
