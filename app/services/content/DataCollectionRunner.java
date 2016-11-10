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
                long numPosts = collector.collect();
                Logger.info("Collected " + numPosts + " posts at " + new Date());
                notification.notify();
            }

            try {
                Logger.info("Collector:" + collector.getSource().getSourceName() + " is sleeping at "
                        + new Date());
                Thread.sleep(collector.getSource().getQueryDelta());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
        }
	}
}
