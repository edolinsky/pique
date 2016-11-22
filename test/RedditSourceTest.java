import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import services.sources.RedditSource;
import net.dean.jraw.RedditClient;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.*;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.LoggedInAccount;

/**
 * Created by Sammie on 2016-11-16.
 */
public class RedditSourceTest {

    RedditSource redditSource;
    RedditClient reddit;

    @Before
    public void before() {
        try {
            redditSource = new RedditSource();
        } catch (Exception OAuthException) {
            System.out.println("OAuth Exception");
        }

        reddit = redditSource.getRedditClient();
    }

    /**
    @Test
    public void testAuthentication() {
        reddit = redditSource.getRedditClient();
        reddit.setLoggingMode(LoggingMode.ON_FAIL);
        Credentials creds = getCredentials();
        if (!reddit.isAuthenticated()) {
            try {
                reddit.authenticate(reddit.getOAuthHelper().easyAuth(creds));
            } catch (NetworkException e) {
                System.out.println("Network exception");
            }
        }
        this.account = new AccountManager(reddit);
        this.moderation = new ModerationManager(reddit);

        System.out.println(redditSource.getRedditClient().isAuthenticated());
    }*/

    /**
    @Test
    public void testMe() {
        try {
            validateModel(reddit.me());
        } catch (NetworkException e) {
            handle(e);
        }
    }*/

    @Test
    public void testIsLoggedIn() {
        try {
            LoggedInAccount acc = reddit.me();
            // /api/me.json returns '{}' when there is no logged in user
            boolean expected = acc.getDataNode() != null;

            System.out.println(redditSource.toString());
            System.out.println(reddit.toString());
            assertEquals(reddit.isAuthenticated(), expected);
        } catch (NetworkException e) {
            System.out.println("Network exception");
        }
    }

}
