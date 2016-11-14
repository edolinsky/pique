package services.sources;

/**
 * Class representation of the Facebook RESTful API
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class FacebookSource implements RestfulSource {
	private static final String FACEBOOK = "facebook";
	private static final String URL = "graph.facebook.com";
	private static final String VERSION = "2.8";

	@Override
	public String getVersion() {
		return VERSION;
	}

	@Override
	public String getUrl() {
		return URL;
	}

	@Override
	public String getSourceName() {
		return FACEBOOK;
	}

	@Override
	public long getQueryDelta() {
		return 0;
	}

	@Override
	public String generateRequest(String[] params) {
		return null;
	}

	// TODO methods should be representative of the facebook API and help construct API calls
}
