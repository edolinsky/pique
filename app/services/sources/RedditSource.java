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

/**
 * Class that interacts with the JRAW library to get data
 *
 * @author Reid Oliveira, Sammie Jiang
 */

public class RedditSource implements JavaSource {


    private static final String REDDIT = "reddit";
    private static final String DEFAULT_TEXT = "N/A";
    private static final Integer MAX_REQUEST_SIZE = 100;
    private static final String USER = "oppaskitty";
    private static final String PASS = "password";
    private static final String CLIENTID = "cdeuI7vNN86lSA";
    private static final String SECRET = "ZPe70S2QCDa596ju8u-0PuXcG7M";

    private RedditClient redditClient;

    //DiscordException??
    public RedditSource() throws NetworkException, OAuthException {
        UserAgent myUserAgent = UserAgent.of("desktop", CLIENTID, "v0.1", USER);
        redditClient = new RedditClient(myUserAgent);
        Credentials credentials = Credentials.script(USER, PASS, CLIENTID, SECRET);
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
        return TimeUnit.MINUTES.toMillis(1);
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
