import org.junit.Before;
import org.junit.Test;
import services.dataAccess.proto.PostProto.Post;
import services.sources.TwitterSource;
import twitter4j.Status;
import twitter4j.Trend;
import twitter4j.Trends;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TwitterSourceTest {

	TwitterSource twitterSource;

	@Before
	public void before() {
		twitterSource = new TwitterSource();
	}

	@Test
	public void testGetTrendingPosts() {
		List<Post> post = twitterSource.getTrendingPosts(new SampleTrend(), 1, null);
		assertEquals(1, post.size());
	}

	@Test
	public void testGetTrends() {
		List<Trend> trends = twitterSource.getTrends("canada", "vancouver");
		assertFalse(trends.isEmpty());
	}

	@Test
	public void testNoRetweets() {
        List<Status> statuses = twitterSource.getStatusesForTrend(new SampleTrend(), 100, null);

        statuses.stream().forEach(s -> assertFalse(s.isRetweet()));
    }

    @Test
    public void testGetNewerOnly() {
        Trend trend = new SampleTrend();
        List<Post> post = twitterSource.getTrendingPosts(trend, 1, null);
        assertEquals(1, post.size());
        Long postId = Long.parseLong(post.get(0).getId());

        List<Post> newPosts = twitterSource.getMaxTrendingPostsSince(trend, postId);

        Set<Long> newIds = newPosts.stream().map(p -> Long.parseLong(p.getId())).collect
                (Collectors.toSet());
        assertFalse(newIds.contains(postId));
    }

	/**
	 * This class is a sample trend used for testing. It is based off of a Trend object
	 * received from twitter4j and may need to be updated alongside that library
	 */
	private static class SampleTrend implements Trend {

		@Override
		public String getName() {
			return "#Vancouver";
		}

		@Override
		public String getURL() {
			return "http://twitter.com/search?q=%23Vancouver";
		}

		@Override
		public String getQuery() {
			return "%23Vancouver";
		}
	}

}
