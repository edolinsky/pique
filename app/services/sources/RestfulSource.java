package services.sources;

import services.dataAccess.proto.PostProto;
import services.dataAccess.proto.PostProto.Post;

import java.net.HttpURLConnection;
import java.util.List;

/**
 * Class representing a source where data is obtained via RESTful API calls
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public interface RestfulSource extends Source {

	/**
	 * the version of the source being used
	 * @return
	 */
	public String getVersion();

	/**
	 * the url endpoint for the API HTTP requests
	 * @return
	 */
	public String getUrl();

	/**
	 * Generates a request to search on the listed trend
	 * @param trend
	 * @return
	 */
	public String generateRequestUrl(String trend);

	/**
	 * Adds any additional needed request headers as dictated by the source
	 * @param connection
     */
	public void addRequestHeaders(HttpURLConnection connection);

	/**
	 * Parses the http response from the api into a list of posts
	 * @param response
	 * @return
     */
	public List<Post> parseResponse(String response);

    public List<Post> filterPostsSince(List<Post> posts, long id);
}
