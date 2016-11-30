import org.junit.Before;
import org.junit.Test;
import services.dataAccess.proto.PostProto.Post;
import services.sources.RedditSource;

import static org.junit.Assert.*;

import java.util.List;

import net.dean.jraw.RedditClient;

/**
 * Created by Sammie on 2016-11-16.
 */
public class RedditSourceTest {

    RedditSource redditSource;
    RedditClient redditClient;

    @Before
    public void before() {
        try {
            redditSource = new RedditSource();
        } catch (Exception OAuthException) {
            System.out.println("OAuth Exception");
        }

        redditClient = redditSource.getRedditClient();
    }

    @Test
    public void testAuthentication() {
        assertTrue(redditClient.isAuthenticated());
    }

    @Test
    public void testGetTrendingPosts() {
        List<Post> post = redditSource.getTrendingPosts("", 1, null);
        System.out.println(post.get(0));
        assertEquals(1, post.size());
    }

    @Test
    public void testGetMaxTrendingPosts() {
        List<Post> post = redditSource.getMaxTrendingPosts("");
        assertEquals(900, post.size());
    }

}
