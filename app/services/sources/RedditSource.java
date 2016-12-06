package services.sources;

import services.dataAccess.proto.PostProto.Post;

import java.util.ArrayList;
import java.util.Collections;
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
import net.dean.jraw.models.Subreddit;
import net.dean.jraw.paginators.SubredditStream;
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
    private static final Integer MAX_SUBREDDIT_REQUEST_SIZE = 10;
    private static final Integer MAX_REQUEST_SIZE = 200;
    private static final Integer MAX_SEARCH_PER_WINDOW = 60;
    private static final Long WINDOW_LENGTH = TimeUnit.MINUTES.toMillis(1);

    private RedditClient redditClient;

    //DiscordException??
    public RedditSource() {
        UserAgent myUserAgent = UserAgent.of("desktop", System.getenv(REDDIT_CLIENTID), "v0.1", System.getenv(REDDIT_USER));
        redditClient = new RedditClient(myUserAgent);
        Credentials credentials = Credentials.script(System.getenv(REDDIT_USER),
                                                     System.getenv(REDDIT_PASS),
                                                     System.getenv(REDDIT_CLIENTID),
                                                     System.getenv(REDDIT_SECRET));

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

        SubredditStream subredditStream = new SubredditStream(redditClient, "popular");
        ArrayList<String> trendingSubreddits = new ArrayList<>();


        while(subredditStream.hasNext() && trendingSubreddits.size() < MAX_SUBREDDIT_REQUEST_SIZE) {
            Listing<Subreddit> subredditPage = subredditStream.next();

            for(int i=0; i<subredditStream.next().size(); i++) {
                trendingSubreddits.add(subredditPage.get(i).getDisplayName());
            }
        }

        return trendingSubreddits;
    }


    @Override
    public List<Post> getTrendingPosts(String trend, int numPosts, Long sinceId) {
        return parseSubmissions(getHotPosts());
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

    public List<Submission> getHotPosts() {
        SubredditPaginator sp = new SubredditPaginator(redditClient);

        sp.setLimit(MAX_REQUEST_SIZE);
        sp.setSorting(Sorting.HOT);
        sp.setTimePeriod(TimePeriod.DAY);
        sp.next(true);

        Listing<Submission> list = sp.getCurrentListing();

        return list;
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

        if (s.getThumbnail() != null) {
            builder.addImgLink(s.getThumbnail()); // Post thumbnail
        }

        builder.addExtLink(s.getUrl()); // Link to Reddit post or linked external site
        return builder.build();
    }
}
