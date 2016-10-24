package services.content;

public abstract class AbstractOnDemandDataCollector extends AbstractDataCollector {

	public void collect() {
		post(fetch());
	}
}
