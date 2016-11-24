package services.sorting;

import services.ThreadNotification;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.*;

import play.Logger;

import static services.PublicConstants.TOP;
import static services.PublicConstants.TRENDING;
import static services.PublicConstants.SORTING_NODE_INPUT_THRESHOLD;
import static services.PublicConstants.POSTS_PER_PAGE;

public class SortingNode implements Runnable {

    private static final Long PROCESS_INPUT_THRESHOLD = Long.valueOf(System.getenv(SORTING_NODE_INPUT_THRESHOLD));

    private static final Double LIKE_WEIGHT = 0.9;
    private static final Double COMMENT_WEIGHT = 0.5;
    private static final Double SHARE_WEIGHT = 1.1;

    private AbstractDataAccess dataSource;
    private ThreadNotification sortNotification;

    public SortingNode(AbstractDataAccess dataSource, ThreadNotification sortNotification) {
        this.dataSource = dataSource;
        this.sortNotification = sortNotification;
    }

    /**
     * Runtime loop for the sorting node
     */
    @Override
    public void run() {

        while (true) {
            synchronized (sortNotification) {
                try {
                    // wait for notification of new posts
                    Logger.info("Sorter is waiting at " + new Date());
                    sortNotification.wait();
                } catch (InterruptedException e) {
                    Logger.error("Sorting Node Thread Exiting");
                }
            }

            // once notified, run main sort process
            sort();

        }
    }

    /**
     * Main process of the sorting node
     */
    private void sort() {

        List<Post> newPosts = new ArrayList<>();

        // Obtain number of posts available for processing and exit if this does not meet the threshold
        Long numAvailablePosts = dataSource.getNumPostsInSources();
        if (numAvailablePosts < PROCESS_INPUT_THRESHOLD) {
            Logger.info("Sorter is dissatisfied with the number of available posts. Waiting...");
            return;
        }
        Logger.info("Sorting posts at " + new Date());

        /*
           GATHERING NEW POSTS
         */

        // Obtain all source channels, obtain all posts from each, and delete these posts from the source channels
        List<String> sourceKeys = dataSource.getSources();
        for (String key : sourceKeys) {
            List<Post> postsFromSource = dataSource.getAllPostsFromSource(key);   // get all posts provided by source

            newPosts.addAll(postsFromSource);                           // load posts into memory
            // delete all posts that have been gathered
            dataSource.deleteFirstNPostsFromSourceQueue(key, postsFromSource.size());
        }

        AbstractPostSorter topPostSorter = new TopPostSorter(dataSource);
        AbstractPostSorter trendingPostSorter = new TrendingPostSorter(dataSource);
        AbstractPostSorter hashtagPostSorter = new HashtagPostSorter(dataSource);

        AbstractStringSorter topHashtagSorter = new TopHashtagStringSorter(dataSource);


        /*
           SORTING NEW POSTS
         */

        // calculate popularity score of all posts
        List<Post> calculatedPosts = calculatePopularityScoreOfAllPosts(newPosts);

        // sort top posts and load in in pages
        Map<String, List<Post>> newSortedTopPosts = topPostSorter.sort(calculatedPosts);
        Logger.info("Sorter sorted " + newSortedTopPosts.get(TOP).size() + " new top posts.");

        // Sort trending posts, process into postList, and add to trending channel
        Map<String, List<Post>> newSortedTrendingPosts = trendingPostSorter.sort(calculatedPosts);
        Logger.info("Sorter sorted " + newSortedTrendingPosts.get(TRENDING).size() + " new trending posts.");

        // Finally sort hashtags, also in reverse order of popularity
        // Bin posts containing particular hashtags together, and add to individual channels
        Map<String, List<Post>> postsByHashTag = hashtagPostSorter.sort(calculatedPosts);
        Logger.info("Sorter sorted " + postsByHashTag.size() + " hashtags.");


        /*
           STORING SORTED DATA
         */

        // add top content in pages to display top stack
        topPostSorter.load(newSortedTopPosts);

        // add new trending content in pages to dsiplay trending stack
        trendingPostSorter.load(newSortedTrendingPosts);

        // add hashtag pages to their corresponding keys in data store
        hashtagPostSorter.load(postsByHashTag);


        /*
           Update Top Hashtags
         */
        topHashtagSorter.load(topHashtagSorter.sort(Collections.emptyList()));
        Logger.info("Sorter added new top hashtags");

    }

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

        // calculate popularity score of post
        int popularity = calculatePopularityScore(post.getNumComments(), post.getNumLikes(), post.getNumShares());

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
     */
    private int calculatePopularityScore(int numComments, int numLikes, int numShares) {
        // evaluate popularity score
        int popularity = (int) (
                COMMENT_WEIGHT * numComments
                        + LIKE_WEIGHT * numLikes
                        + SHARE_WEIGHT * numShares
        );

        // handle boundary conditions
        if (popularity < 0) {
            popularity = 0;
        } else if (popularity >= Integer.MAX_VALUE) {
            popularity = Integer.MAX_VALUE;
        }

        return popularity;
    }
}
