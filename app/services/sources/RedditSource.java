package services.sources;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.TimeUnit;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.NetworkException;
import services.dataAccess.proto.PostProto;

import static services.PublicConstants.REDDIT_USER;
import static services.PublicConstants.REDDIT_PASS;
import static services.PublicConstants.REDDIT_CLIENTID;
import static services.PublicConstants.REDDIT_SECRET;

/**
 * Class that interacts with the JRAW library to get data
 *
 * @author Reid Oliveira, Sammie Jiang
 */

public class RedditSource implements JavaSource {


    private static final String REDDIT = "reddit";
    private static final String DEFAULT_TEXT = "N/A";
    private static final Integer MAX_REQUEST_SIZE = 100;
    private static final Integer MAX_SEARCH_PER_WINDOW = 60;
    private static final Long WINDOW_LENGTH = TimeUnit.MINUTES.toMillis(1);

    private RedditClient redditClient;

    //DiscordException??
    public RedditSource() throws NetworkException, OAuthException {
        UserAgent myUserAgent = UserAgent.of("desktop", System.getenv(REDDIT_CLIENTID), "v0.1", System.getenv(REDDIT_USER));
        redditClient = new RedditClient(myUserAgent);
        Credentials credentials = Credentials.script(System.getenv(REDDIT_USER),
                                                     System.getenv(REDDIT_PASS),
                                                     System.getenv(REDDIT_CLIENTID),
                                                     System.getenv(REDDIT_SECRET);
        OAuthData authData = redditClient.getOAuthHelper().easyAuth(credentials);

        redditClient.authenticate(authData);
        redditClient.me();
    }

    @Override
    public String getSourceName() {
        return "source:" + REDDIT;
    }

    @Override
    public long getQueryDelta() {
        return WINDOW_LENGTH/(MAX_SEARCH_PER_WINDOW * 4/5);
    }

    @Override
    public Collection<? extends String> getTrends(String country, String city) {
        return null;
    }

    @Override
    public List<PostProto.Post> getTrendingPosts(String trend, int numPosts, Long sinceId) {
        return null;
    }

    @Override
    public List<PostProto.Post> getMaxTrendingPostsSince(String trend, Long sinceId) {
        return null;
    }

    @Override
    public List<PostProto.Post> getMaxTrendingPosts(String trend) {
        return null;
    }

    public RedditClient getRedditClient() {
        return redditClient;
    }

    public void getTrendingSubreddits() {

    }

    public void getTopSubredditPosts() {

    }
}
