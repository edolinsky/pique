package services.sources;

import twitter4j.Location;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import javax.xml.ws.Response;
import java.util.Optional;

/**
 * Class that interacts with the twitter4j library to get data
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class TwitterSource extends AbstractJavaSource {

	// twitter object that acts as the router for all requests
	Twitter twitter;

	public TwitterSource() {
		twitter = new TwitterFactory().getInstance();

	}

	public void getTopTrending(String country){
		try{
			Optional<Location> location = getLocation(country);

			if (location.isPresent()) {
				Trends trends = twitter.getPlaceTrends(location.get().getWoeid());
			} else {
				// TODO weren't able to fufill the request, should probably notify someone
			}
		} catch (Exception e){
			// TODO
		}
	}

	/**
	 * Retrives the {@link Location} object for the specified country if it is available,
	 * else an empty Optional.
	 *
	 * @param country the name of the country
	 * @return {@link Optional} of the {@link Location}, or null if it is not available.
	 */
	private Optional<Location> getLocation(String country) {

		try {
			ResponseList<Location> availableLocations = twitter.getAvailableTrends();

			for (Location l : availableLocations) {
				if (l.getCountryName().equalsIgnoreCase(country)) {
					return Optional.of(l);
				}
			}

		} catch (Exception e) {
			// TODO
		}

		return Optional.empty(); // could not find the location
	}

}
