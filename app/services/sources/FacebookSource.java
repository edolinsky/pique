package services.sources;

import services.dataAccess.proto.PostProto.Post;

import java.net.HttpURLConnection;
import java.util.Collection;
import java.util.List;

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
	public Collection<? extends String> getTrends(String country, String city) {
		return null;
	}

	@Override
	public String generateRequestUrl(String trend) {
		return null;
	}

	@Override
	public void addRequestHeaders(HttpURLConnection connection) {
	}

	@Override
	public List<Post> getPostsSince(String response) {
		return null;
	}

	// TODO methods should be representative of the facebook API and help construct API calls
}
