package services.sources;

import akka.pattern.FutureRef;
import services.content.Post;
import twitter4j.Location;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;

import javax.xml.ws.Response;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Class that interacts with the twitter4j library to get data
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class TwitterSource extends AbstractJavaSource {
	Map<String, Integer> cachedCodes = new HashMap<>();

	// twitter object that acts as the router for all requests
	Twitter twitter;

	public TwitterSource() {
		twitter = new TwitterFactory().getInstance();

	}

	/**
	 * Gets tweets corresponding to the current trending topics on Twitter
	 * @return
	 */
	public List<Post> getTopTrending() {
		// TODO get country code from FE request
		Optional<Trends> trends = getTrends("Canada");
		QueryResult result = null;

		if (trends.isPresent()) {
			for (Trend t : trends.get().getTrends()) {
				Query trendQuery = new Query(t.getQuery());
				trendQuery.setCount(100);

				try {
					result = twitter.search(trendQuery);
				} catch (Exception e) {
					// TODO
				}
			}
		}

		return parseQueryResult(result);
	}

	private List<Post> parseQueryResult(QueryResult result) {
		if (result.getTweets() == null || result.getTweets().isEmpty())
			return Collections.emptyList();

		List<Post> posts = new ArrayList<>();
		for (Status s : result.getTweets()) {
			// TODO
		}

		return posts;
	}

	/**
	 * Gets the {@link Trends} for a certain country, if it is available.
	 * @param country the country to get {@link Trends} for
	 * @return an {@link Optional} containing the trends, or an empty {@link Optional} if
	 * {@link Trends} are not available for that country.
	 */
	private Optional<Trends> getTrends(String country){

		try{
			Optional<Integer> id = getLocationId(country);

			if (id.isPresent()) {
				return Optional.of(twitter.getPlaceTrends(id.get()));
			}
		} catch (Exception e){
			// TODO
		}

		return Optional.empty();
	}

	/**
	 * Retrives the {@link Location} object for the specified country if it is available,
	 * else an empty Optional.
	 *
	 * @param country the name of the country
	 * @return {@link Optional} of the {@link Location}, or null if it is not available.
	 */
	private Optional<Integer> getLocationId(String country) {
		String countryLower = country.toLowerCase();
		if (cachedCodes.containsKey(countryLower)) {
			return Optional.of(cachedCodes.get(countryLower));
		}

		try {
			ResponseList<Location> availableLocations = twitter.getAvailableTrends();
			for (Location l : availableLocations) {
				if (l.getCountryName().equalsIgnoreCase(countryLower)) {
					cachedCodes.put(countryLower, l.getWoeid());
					return Optional.of(l.getWoeid());
				}
			}
		} catch (Exception e) {
			// TODO
		}

		return Optional.empty(); // could not find the location
	}

}
