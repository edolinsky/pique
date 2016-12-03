import org.junit.Before;
import org.junit.Test;
import services.dataAccess.proto.PostProto.Post;
import services.sources.TwitterSource;
import twitter4j.Status;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

public class TwitterSourceTest {

	TwitterSource twitterSource;

	@Before
	public void before() {
		twitterSource = new TwitterSource();
	}

	@Test
	public void testGetTrendingPosts() {
		List<Post> post = twitterSource.getTrendingPosts("vancouver", 1, null);
		assertEquals(1, post.size());
	}

	@Test
	public void testGetTrends() {
		List<String> trends = twitterSource.getTrends("canada", "vancouver");
		assertFalse(trends.isEmpty());
	}

	@Test
	public void testNoRetweets() {
        List<Status> statuses = twitterSource.getStatusesForTrend("#vancouver", 100, null);

        statuses.stream().forEach(s -> assertFalse(s.isRetweet()));
    }

    @Test
    public void testGetNewerOnly() {
	    String trend = "vancouver";
        List<Post> post = twitterSource.getTrendingPosts(trend, 1, null);
        assertEquals(1, post.size());
        Long postId = Long.parseLong(post.get(0).getId());

        List<Post> newPosts = twitterSource.getMaxTrendingPostsSince(trend, postId);

        Set<Long> newIds = newPosts.stream().map(p -> Long.parseLong(p.getId())).collect
                (Collectors.toSet());
        assertFalse(newIds.contains(postId));
    }

}
