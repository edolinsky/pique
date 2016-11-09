package services.sources;

/**
 * Class representation of the Facebook RESTful API
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class FacebookSource extends AbstractRestfulSource {
	private static final String FACEBOOK = "facebook";
	private static final String URL = "graph.facebook.com";
	private static final String VERSION = "2.8";

	public FacebookSource() {
		super(FACEBOOK, URL, VERSION);
	}

	// TODO methods should be representative of the facebook API and help construct API calls
}
