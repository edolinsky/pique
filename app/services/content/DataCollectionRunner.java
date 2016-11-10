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
            Logger.info("Notifier is sleeping for 3 seconds at " + new Date());
            try {
                Thread.sleep(collector.getSource().getQueryDelta());
            } catch (InterruptedException e1) {
                e1.printStackTrace();
            }
            synchronized (notification) {
                collector.collect();
                notification.notify();
            }
        }
	}
}
