package SortingTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assume.assumeTrue;

import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.Calculator;
import services.sorting.PostSorter.AbstractPostSorter;
import services.sorting.PostSorter.TrendingPostSorter;

import static services.PublicConstants.TRENDING;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static services.dataAccess.TestDataGenerator.generateListOfPosts;

public class TrendingPostSorterTest {

    private AbstractPostSorter sorter;
    private Calculator calc;
    private AbstractDataAccess data;

    @Before
    public void trendingPostSorterTestSetup() {
        data = new InMemoryAccessObject();
        sorter = new TrendingPostSorter(data);
        calc = new Calculator();
    }

    @Test
    public void testSortTrendingPostsPositiveCase() {
        List<Post> posts = generateListOfPosts(100);
        int prevScore = Integer.MAX_VALUE;

        // create new list of the same posts, but aged
        List<Post> oldPosts = agePosts(posts);
        Map<String, List<Post>> oldTrendingPosts = new HashMap<>();

        // load them into trending so that the sorter can calculate the difference
        oldTrendingPosts.put(TRENDING, calc.calculatePopularityScoreOfAllPosts(oldPosts));
        sorter.load(oldTrendingPosts);

        // sort new posts
        Map<String, List<Post>> sorted = sorter.sort(posts);

        // popularity score should decrease
        for (Post post : sorted.get(TRENDING)) {
            int popularityVelocity = post.getPopularityVelocity();
            assertTrue(popularityVelocity <= prevScore);
            prevScore = popularityVelocity;
        }
    }

    @Test
    public void testSortTrendingPostsEmptyInput() {
        // empty sorting input should give us empty list under TRENDING on output
        assertEquals(Collections.emptyList(), sorter.sort(Collections.emptyList()).get(TRENDING));
    }

    @Test
    public void testLoadTrendingPosts() {
        int numPosts = 10;
        List<Post> posts = generateListOfPosts(numPosts);

        // ignore this test if we create more than one page
        assumeTrue(numPosts <= AbstractPostSorter.getPageLimit());

        Map<String, List<Post>> trendingMap = new HashMap<>();
        trendingMap.put(TRENDING, posts);

        // load trending posts
        sorter.load(trendingMap);

        // check stored posts & order match entered
        List<Post> stored = data.getAllDisplayPostLists(TRENDING).get(0).getPostsList();
        assertEquals(posts, stored);
    }

    /**
     * Ages a list of posts by decreasing each post's timestamp by a set amount, and decreasing its number of likes,
     * shares, and comments by random amounts.
     * @return A copy of the input list, with posts aged artificially
     */
    private List<Post> agePosts(List<Post> posts) {
        List<Post> oldPosts = new ArrayList<>();
        final long age_millis = 1000000L; // 1000 seconds in millis

        // rebuild post
        for (Post newPost : posts) {
            int numComments = newPost.getNumComments();
            int numLikes = newPost.getNumLikes();
            int numShares = newPost.getNumShares();
            long timestamp = newPost.getTimestamp();

            // decrease comments/likes/shares by random values, bounded by their current value
            // decrease timestamp by age_millis, set above
            Post oldPost = newPost.toBuilder()
                    .setNumComments(numComments - ThreadLocalRandom.current().nextInt(0, numComments + 1))
                    .setNumLikes(numLikes - ThreadLocalRandom.current().nextInt(0, numLikes + 1))
                    .setNumShares(numShares - ThreadLocalRandom.current().nextInt(0, numShares + 1))
                    .setTimestamp(timestamp - age_millis)
                    .build();

            oldPosts.add(oldPost);
        }

        return oldPosts;
    }
}
