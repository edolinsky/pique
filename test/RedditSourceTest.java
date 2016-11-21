import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;


import services.sources.RedditSource;
import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;

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
    public void testGetHotPosts() {
        for(Submission s : redditSource.getHotPosts()) {
            System.out.println(s);
        }
        assertEquals(200, redditSource.getHotPosts().size());
    }



}
