import org.junit.Before;
import org.junit.Test;
import services.dataAccess.proto.PostProto.Post;
import services.sources.RedditSource;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
        assertEquals(1, post.size());
    }

    @Test
    public void testTextPost() {
        List<Post> post = redditSource.getPostsFrom("jokes", 1);
    }

    //Tests that when Reddit iterates through its pages, it's not continuously pulling the same page
    @Test
    public void testNoRepeatPagesWhenGettingMaxTrendingPosts() {
        List<Post> post = redditSource.getMaxTrendingPosts("");
        String postText = post.get(0).getText(0);

        Set<String> newIds = post.stream().map(p -> p.getId()).collect
                (Collectors.toSet());
        newIds.remove(0);
        assertFalse(newIds.contains(postText));
    }

}
