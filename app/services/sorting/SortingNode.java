package services.sorting;

import services.ThreadNotification;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostProto.Post;

import java.util.*;

import play.Logger;
import services.sorting.PostSorter.AbstractPostSorter;
import services.sorting.PostSorter.HashtagPostSorter;
import services.sorting.PostSorter.TopPostSorter;
import services.sorting.PostSorter.TrendingPostSorter;
import services.sorting.StringSorter.AbstractStringSorter;
import services.sorting.StringSorter.TopHashtagStringSorter;

import static services.PublicConstants.TOP;
import static services.PublicConstants.TRENDING;
import static services.PublicConstants.SORTING_NODE_INPUT_THRESHOLD;

public class SortingNode implements Runnable {

    private static final Long PROCESS_INPUT_THRESHOLD = Long.valueOf(System.getenv(SORTING_NODE_INPUT_THRESHOLD));
    private Calculator calc;

    private AbstractDataAccess dataSource;
    private ThreadNotification sortNotification;

    public SortingNode(AbstractDataAccess dataSource, ThreadNotification sortNotification) {
        this.dataSource = dataSource;
        this.sortNotification = sortNotification;
        calc = new Calculator();
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
                    Logger.debug("Sorter is waiting at " + new Date());
                    sortNotification.wait();
                } catch (InterruptedException e) {
                    Logger.error("Sorting Node Thread Exiting"); // this should never happen (always waiting)
                }
            }

            // once notified, run main sort process
            sort();

        }
    }

    /**
     * Main process of the sorting node
     */
    public void sort() {

        List<Post> newPosts = new ArrayList<>();

        // Obtain number of posts available for processing and exit if this does not meet the threshold
        Long numAvailablePosts = dataSource.getNumPostsInSources();
        if (numAvailablePosts < PROCESS_INPUT_THRESHOLD) {
            Logger.debug("Sorter is dissatisfied with the number of available posts. Waiting...");
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

        AbstractStringSorter topHashtagStringSorter = new TopHashtagStringSorter(dataSource);


        /*
           SORTING NEW POSTS
         */

        // calculate popularity score of all posts
        List<Post> calculatedPosts = calc.calculatePopularityScoreOfAllPosts(newPosts);

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
        Logger.info("Sorter loaded new top posts");

        // add new trending content in pages to dsiplay trending stack
        trendingPostSorter.load(newSortedTrendingPosts);
        Logger.info("Sorter loaded new trending posts");

        // add hashtag pages to their corresponding keys in data store
        hashtagPostSorter.load(postsByHashTag);
        Logger.info("Sorter loaded new hashtag posts");


        /*
           Update Top Hashtags
         */
        topHashtagStringSorter.load(topHashtagStringSorter.sort(Collections.emptyList()));
        Logger.info("Sorter sorted and loaded new top hashtags");

    }

    /** static getters **/
    public static Long getProcessInputThreshold() {
        return PROCESS_INPUT_THRESHOLD;
    }
}
