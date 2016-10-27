package services.sources;

/**
 * Class representation of the Twitter RESTful API
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class TwitterSource extends AbstractRestfulSource {
	public static final String URL = "https://api.twitter.com";
	public static final String VERSION = "1.1";

	public TwitterSource() {
		super(URL, VERSION);
	}

	// TODO methods should be representative of the twitter API and help construct API calls
}
