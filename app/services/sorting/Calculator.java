package services.sorting;

import services.dataAccess.proto.PostProto.Post;

import java.util.ArrayList;
import java.util.List;

public class Calculator {

    private static final Double LIKE_WEIGHT = 0.9;
    private static final Double COMMENT_WEIGHT = 0.5;
    private static final Double SHARE_WEIGHT = 1.1;
    private static final Long THREE_DAYS_MILLIS = 259200000L;

    /**
     * Calculates and inserts popularity score to each post in a list of posts
     *
     * @param posts list of posts
     * @return the same list of posts, but with each post now containing a popularity score
     */
    public List<Post> calculatePopularityScoreOfAllPosts(List<Post> posts) {
        List<Post> calculatedPosts = new ArrayList<>();

        posts.forEach(post -> {
            post = calculatePopularityAndRebuild(post);
            calculatedPosts.add(post);
        });

        return calculatedPosts;
    }

    /**
     * Calculates the popularity of a given post, and injects the popularity score into that post
     *
     * @param post Post object to be evaluated
     * @return popularity score
     */
    public Post calculatePopularityAndRebuild(Post post) {

        long timestamp;

        // default to current timestamp if no timestamp is available
        if (post.hasField(Post.getDescriptor().findFieldByNumber(Post.TIMESTAMP_FIELD_NUMBER))) {
            timestamp = post.getTimestamp();
        } else {
            timestamp = System.currentTimeMillis();
        }

        // calculate popularity score of post
        int popularity = calculatePopularityScore(
                post.getNumComments(),
                post.getNumLikes(),
                post.getNumShares(),
                timestamp);

        // rebuild post with new score
        post = post.toBuilder().setPopularityScore(popularity).build();

        return post;
    }

    /**
     * Calculates the popularity score given the specified fields
     *
     * @param numComments number of comments associated with a post
     * @param numLikes    number of likes associated with a post
     * @param numShares   number of shares associated with a post
     * @return popularity score calculated given input parameters.
     * @return timeStamp  timestamp of post
     */
    private static int calculatePopularityScore(int numComments, int numLikes, int numShares, long timeStamp) {

        // evaluate linear time decay
        double decayFactor = (THREE_DAYS_MILLIS - (System.currentTimeMillis() - timeStamp)) / (double) THREE_DAYS_MILLIS;

        // evaluate popularity score, scaled by linear time decay
        double popularity = (

                decayFactor * (COMMENT_WEIGHT * numComments
                        + LIKE_WEIGHT * numLikes
                        + SHARE_WEIGHT * numShares)
        );

        // handle boundary conditions
        if (popularity < 0) {
            popularity = 0;
        } else if (popularity >= Integer.MAX_VALUE) {
            popularity = Integer.MAX_VALUE;
        }

        return (int) popularity;
    }

    /** static getters **/

    public static Long getThreeDaysMillis() {
        return THREE_DAYS_MILLIS;
    }
}
