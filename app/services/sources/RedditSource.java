package services.sources;

import services.dataAccess.proto.PostProto.Post;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;
import net.dean.jraw.paginators.Sorting;
import net.dean.jraw.paginators.TimePeriod;

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
    private static final Integer MAX_REQUEST_SIZE = 900;
    private static final Integer MAX_SEARCH_PER_WINDOW = 60;
    private static final Long WINDOW_LENGTH = TimeUnit.MINUTES.toMillis(1);

    private RedditClient redditClient;
    private Credentials credentials;


    public RedditSource() {
        UserAgent myUserAgent = UserAgent.of("desktop", System.getenv(REDDIT_CLIENTID), "v0.1", System.getenv(REDDIT_USER));
        redditClient = new RedditClient(myUserAgent);

        credentials = Credentials.script(System.getenv(REDDIT_USER),
                                                     System.getenv(REDDIT_PASS),
                                                     System.getenv(REDDIT_CLIENTID),
                                                     System.getenv(REDDIT_SECRET));

        authenticateReddit();
    }

    private void authenticateReddit() {
        OAuthData authData = null;

        try {
            authData = redditClient.getOAuthHelper().easyAuth(credentials);
        } catch (NetworkException e) {
            e.printStackTrace();
            //TODO
        } catch (OAuthException o) {
            o.printStackTrace();
            //TODO
        }

        redditClient.authenticate(authData);
        redditClient.me();
    }

    @Override
    public String getSourceName() {
        return REDDIT;
    }

    @Override
    public long getQueryDelta() {
        return WINDOW_LENGTH/(MAX_SEARCH_PER_WINDOW * 1/5);
    }


    @Override
    public List<String> getTrends(String country, String city) {
        return Collections.emptyList();
    }


    @Override
    public List<Post> getTrendingPosts(String trend, int numPosts, Long sinceId) {
        return parseSubmissions(getHotPosts(numPosts));
    }

    @Override
    public List<Post> getMaxTrendingPostsSince(String trend, Long sinceId) {
        return getMaxTrendingPosts(trend);
    }

    @Override
    public List<Post> getMaxTrendingPosts(String trend) {
        return getTrendingPosts(trend, MAX_REQUEST_SIZE, null);
    }

    public RedditClient getRedditClient() {
        return redditClient;
    }

    /**
     * Iterates through pages of Reddit's front page and returns a list of all of the submissions
     * @param numPosts number of submissions to query
     * @return List of type Submission
     */
    public List<Submission> getHotPosts(int numPosts) {
        authenticateReddit();
        SubredditPaginator sp = new SubredditPaginator(redditClient);

        sp.setLimit(numPosts);
        sp.setSorting(Sorting.HOT);
        sp.setTimePeriod(TimePeriod.DAY);
        sp.next(true);

        List<Submission> hotPosts = new ArrayList<>();

        hotPosts.addAll(sp.getCurrentListing());

        //Iterate through next Reddit pages and add to hotPosts. Each page only returns 100
        while(sp.hasNext() && hotPosts.size() <= numPosts) {
            Listing<Submission> nextPage = sp.next();

            hotPosts.addAll(nextPage);
        }

        //Only return numPosts # of hot posts from the list
        hotPosts.removeAll(hotPosts.subList(numPosts-1, hotPosts.size()-1));

        redditClient.getOAuthHelper().revokeAccessToken(credentials);
        redditClient.deauthenticate();

        return hotPosts;
    }

    /**
     * Generates Post object for every submission in queried Reddit posts
     * @param submissions
     * @return
     */
    private List<Post> parseSubmissions(List<Submission> submissions) {
        if (submissions == null || submissions.isEmpty()) {
            return Collections.emptyList();
        }

        return submissions.stream().map(this::createPost).collect(Collectors.toList());
    }

    private Post createPost(Submission s) {
        Post.Builder builder = Post.newBuilder();
        builder.setId(String.valueOf(s.getCreated().getTime()));
        builder.setTimestamp(s.getCreated().getTime());
        builder.addSource(s.getAuthor());
        builder.addSourceLink("http://www.reddit.com/user/" + s.getAuthor()); // Link to post author
        builder.setPopularityScore(0);
        builder.setPopularityVelocity(0);
        builder.setNumComments(s.getCommentCount());
        builder.setNumLikes(s.getScore());
        builder.addText(s.getSelftext());
        builder.addHashtag(s.getSubredditName());

        if (s.getThumbnail() != null) {
            builder.addImgLink(s.getThumbnail()); // Post thumbnail
        }

        builder.addExtLink(s.getUrl()); // Link to Reddit post or linked external site
        return builder.build();
    }

}
