import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import services.ThreadNotification;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.SortingNode;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static services.dataAccess.TestDataGenerator.generateListOfPosts;

public class SortingNodeTest {

    private SortingNode node;
    private ThreadNotification notification;
    private AbstractDataAccess data;

    @Before
    public void sortingNodeTestSetup() {
        notification = new ThreadNotification();
        data = new InMemoryAccessObject();
        node = new SortingNode(data, notification);
    }

    @Test
    public void testPreparePostListWithPosts() {
        List<Post> posts = generateListOfPosts(10);
        PostList postList = node.preparePostList(posts);

        assertEquals(posts, postList.getPostsList());
    }

    @Test
    public void testPreparePostsListNoPosts() {
        PostList postList = node.preparePostList(new ArrayList<>());
        assertEquals(Collections.emptyList(), postList.getPostsList());
    }

    @Test
    public void testCalculatePopularity() {
        Post post = generateListOfPosts(1).get(0);
        Post post2 = node.calculatePopularity(post);

        assertTrue(post2.hasField(Post.getDescriptor().findFieldByNumber(Post.POPULARITY_SCORE_FIELD_NUMBER)));
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

        List<Post> sorted = node.sortTopPosts(zeroedPosts);

        assertEquals(Collections.emptyList(), sorted);
    }

    @Test
    public void testSortTopPostsPositiveCase() {
        int prevScore = Integer.MAX_VALUE;
        List<Post> posts = generateListOfPosts(10);
        List<Post> sorted = node.sortTopPosts(posts);

        for (Post post : sorted) {
            int popularityScore = post.getPopularityScore();
            assertTrue(popularityScore <= prevScore);
            prevScore = popularityScore;
        }
    }

    @Test @Ignore
    public void testSortTrending() {
        List<Post> posts = generateListOfPosts(10);
        List<Post> sorted = node.sortTrendingPosts(posts);
        // TODO asserts
    }

    @Test @Ignore
    public void testSortHashtags() {
        List<Post> posts = generateListOfPosts(10);
        Map<String, List<Post>> sorted = node.sortPostsByHashTag(posts);
        // TODO asserts
    }

}
