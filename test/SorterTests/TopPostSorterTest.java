package SorterTests;

import org.junit.Before;
import org.junit.Test;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.AbstractPostSorter;
import services.sorting.TopPostSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static services.dataAccess.TestDataGenerator.generateListOfPosts;
import static services.PublicConstants.TOP;

public class TopPostSorterTest {

    private AbstractPostSorter sorter;

    @Before
    public void topPostSorterTestSetup() {
        AbstractDataAccess data = new InMemoryAccessObject();
        sorter = new TopPostSorter(data);
    }

    @Test
    public void testSortTopPostsZeroValues() {
        List<Post> posts = generateListOfPosts(10);
        List<Post> zeroedPosts = new ArrayList<>();

        // set popularity parameters to 0
        posts.forEach(post -> {
            post = post.toBuilder()
                    .setNumComments(0)
                    .setNumLikes(0)
                    .setNumShares(0)
                    .build();
            zeroedPosts.add(post);

        });

        Map<String, List<Post>> sorted = sorter.sort(zeroedPosts);

        assertEquals(Collections.emptyList(), sorted.get(TOP));
    }

    @Test
    public void testSortTopPostsPositiveCase() {
        int prevScore = Integer.MAX_VALUE;
        List<Post> posts = generateListOfPosts(10);
        Map<String, List<Post>> sorted = sorter.sort(posts);

        for (Post post : sorted.get(TOP)) {
            int popularityScore = post.getPopularityScore();
            assertTrue(popularityScore <= prevScore);
            prevScore = popularityScore;
        }
    }
}
