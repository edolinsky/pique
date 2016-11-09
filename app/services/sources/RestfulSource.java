package services.sources;

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
	 * Generates the appropriate http request for the source using the params
	 * @param params
	 * @return
	 */
	public String generateRequest(String[] params);
}
