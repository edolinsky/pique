package services.sources;

/**
 * Class representing a source of data
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public abstract class AbstractSource {
	String sourceName;

	public AbstractSource(String sourceName) {
		this.sourceName = sourceName;
	}

	public String getSourceName() {
		return sourceName;
	}
}
