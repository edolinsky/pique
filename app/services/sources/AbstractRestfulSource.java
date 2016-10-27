package services.sources;

/**
 * Class representing a source where data is obtained via RESTful API calls
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public abstract class AbstractRestfulSource extends AbstractSource {

	String url;
	String version;

	public AbstractRestfulSource(String url, String version) {
		this.url = url;
		this.version = version;
	}

	public String getUrl() {
		return url;
	}

	public String getVersion() {
		return version;
	}
}
