package services.content;

/**
 * An AutonmousDataCollector is a runnable collector that will continuously operate its given
 * {@link AbstractDataCollector}
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class DataCollectionRunner implements Runnable {

	AbstractDataCollector collector;

	public DataCollectionRunner(AbstractDataCollector collector) {
		this.collector = collector;
	}

	@Override
	public void run() {
		collector.collect();
	}
}
