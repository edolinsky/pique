package services.sources;

import services.dataAccess.proto.PostProto.Post;
import twitter4j.HashtagEntity;
import twitter4j.Location;
import twitter4j.MediaEntity;
import twitter4j.Query;
import twitter4j.QueryResult;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.URLEntity;
import twitter4j.conf.ConfigurationBuilder;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static services.PublicConstants.TWITTER4J_ACCESS_TOKEN;
import static services.PublicConstants.TWITTER4J_ACCESS_TOKEN_SECRET;
import static services.PublicConstants.TWITTER4J_CONSUMER_KEY;
import static services.PublicConstants.TWITTER4J_CONSUMER_SECRET;

/**
 * Class that interacts with the twitter4j library to get data
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class TwitterSource implements Source {
    
    private static final String TWITTER = "twitter";
    private static final String DEFAULT_TEXT = "N/A";
	private static final Integer MAX_REQUEST_SIZE = 100;
    
	private Map<String, Set<Location>> cachedCodes = new HashMap<>();

	// twitter object that acts as the router for all requests
	private Twitter twitter;

	public TwitterSource() {
		ConfigurationBuilder cb = new ConfigurationBuilder();
		cb.setDebugEnabled(true)
				.setOAuthConsumerKey(System.getenv(TWITTER4J_CONSUMER_KEY))
				.setOAuthConsumerSecret(System.getenv(TWITTER4J_CONSUMER_SECRET))
				.setOAuthAccessToken(System.getenv(TWITTER4J_ACCESS_TOKEN))
				.setOAuthAccessTokenSecret(System.getenv(TWITTER4J_ACCESS_TOKEN_SECRET));
		TwitterFactory tf = new TwitterFactory(cb.build());
		twitter = tf.getInstance();
	}



    @Override
    public String getSourceName() {
        return "source:" + TWITTER;
    }

    @Override
    public long getQueryDelta() {
        return TimeUnit.MINUTES.toMillis(1);
    }

    /**
	 * Gets tweets corresponding to the current trending topics on Twitter
	 * @return
	 */
	public List<Post> getTrendingPosts(Trend trend, int numPosts) {
		Query trendQuery = new Query(trend.getQuery());
		trendQuery.setCount(numPosts);

		try {
			QueryResult result = twitter.search(trendQuery);
			return parseQueryResult(result);
		} catch (TwitterException e) {
			e.printStackTrace();
			// TODO
		}

		return Collections.emptyList();
	}

	public List<Post> getMaxTrendingPosts(Trend trend) {
		return getTrendingPosts(trend, MAX_REQUEST_SIZE);
	}

	/**
	 * Gets the {@link Trends} for a certain country, if it is available.
	 * @param country
	 * @param city
	 * @return an {@link Optional} containing the trends, or an empty {@link Optional} if
	 * {@link Trends} are not available for that country.
	 */
	public List<Trend> getTrends(String country, String city){

		try{
			Optional<Integer> id = getLocationId(country, city);

			if (id.isPresent()) {
				return Arrays.asList(twitter.getPlaceTrends(id.get()).getTrends());
			}
		} catch (TwitterException e){
			e.printStackTrace();
			// TODO
		}

		return Collections.emptyList();
	}

	/**
	 * Retrives the {@link Location} object for the specified country if it is available,
	 * else an empty Optional.
	 * @param country
	 * @param city
	 * @return {@link Optional} of the {@link Location}, or null if it is not available.
	 */
	private Optional<Integer> getLocationId(String country, String city) {
		// check if location is cached
		if (cachedCodes.containsKey(country)) {
			Set<Location> cities = cachedCodes.get(country);

			for (Location l : cities) {
				if (l.getName().equalsIgnoreCase(city)) {
					return Optional.of(l.getWoeid());
				}
			}
		}

		// else retrieve list of locations and store
		try {
			ResponseList<Location> availableLocations = twitter.getAvailableTrends();

			Optional<Integer> toReturn = Optional.empty();
			for (Location l : availableLocations) {
				if (l.getCountryName().equalsIgnoreCase(country)
						&& l.getName().equalsIgnoreCase(city)) {
					toReturn = Optional.of(l.getWoeid());
				}

				if (cachedCodes.containsKey(l.getCountryCode())) {
					cachedCodes.get(l.getCountryName()).add(l);
				} else {
					Set<Location> citySet = new HashSet<>();
					citySet.add(l);
					cachedCodes.put(l.getCountryName(), citySet);
				}
			}

			return toReturn;
		} catch (TwitterException e) {
			e.printStackTrace();
			// TODO
		}

		return Optional.empty(); // could not find the location
	}

	/**
	 * Generates Post object for every status in given queried tweets
	 * @param result
	 * @return
	 */
	private List<Post> parseQueryResult(QueryResult result) {
		if (result.getTweets() == null || result.getTweets().isEmpty()) {
			return Collections.emptyList();
		}

		return result.getTweets().stream().map(this::createPost).collect(Collectors.toList());
	}

	private Post createPost(Status s) {
		Post.Builder builder = Post.newBuilder();
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		builder.setId(String.valueOf(s.getId()));
		builder.setTimestamp(df.format(s.getCreatedAt()));
		builder.addSource(s.getUser().getScreenName());
		builder.addSourceLink("https://twitter.com"); // TODO get profile URL somehow
		builder.setPopularityScore(0);
		builder.setPopularityVelocity(0);
		builder.setNumComments(0); //TODO or maybe not possible
		setPostNumShares(s, builder);
		builder.setNumLikes(s.getFavoriteCount());
		builder.addText(s.getText());
		setPostHashtag(s, builder);
		setPostMedia(s, builder);
		setPostURL(s, builder);
		return builder.build();
	}

	private void setPostNumShares(Status s, Post.Builder builder) {
		if (s.getRetweetCount() == -1) { //If tweet was created before the retween count feature was enabled
			//TODO
		} else {
			builder.setNumShares(s.getRetweetCount());
		}
	}

	private void setPostHashtag(Status s, Post.Builder builder) {
		HashtagEntity[] hashtagArray = s.getHashtagEntities();
		if (hashtagArray == null || hashtagArray.length == 0) {
			builder.addHashtag(DEFAULT_TEXT);
		} else {
			for (int i = 0; i < hashtagArray.length; i++) {
				builder.addHashtag(hashtagArray[i].getText());
			}
		}
	}

	private void setPostMedia(Status s, Post.Builder builder) {
		MediaEntity[] mediaArray = s.getMediaEntities();
		if (mediaArray == null || mediaArray.length == 0) {
			builder.addImgLink(DEFAULT_TEXT);
		} else {
			for (int j = 0; j < mediaArray.length; j++) {
				builder.addImgLink(mediaArray[j].getMediaURL());
			}
		}
	}

	private void setPostURL(Status s, Post.Builder builder) {
		URLEntity[] urlArray = s.getURLEntities();
		if (urlArray == null || urlArray.length == 0) {
			builder.addExtLink(DEFAULT_TEXT);
		} else {
			for (int k = 0; k < urlArray.length; k++) {
				builder.addExtLink(urlArray[k].getURL());
			}
		}
	}
}
