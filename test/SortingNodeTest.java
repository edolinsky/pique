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
import static services.dataAccess.TestDataGenerator.generateListOfPosts;

/**
 * Created by erik on 09/11/16.
 */
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

    @Test @Ignore
    public void testCalculatePopularity() {
        Post post = generateListOfPosts(1).get(0);
        node.calculatePopularity(post);
        // TODO asserts
    }

    @Test @Ignore
    public void testSortTopPostsZeroValues() {
        List<Post> posts = generateListOfPosts(10); // TODO these won't be zero value
        List<Post> sorted = node.sortTopPosts(posts);
        // TODO asserts
    }

    @Test @Ignore
    public void testSortTopPostsPositiveCase() {
        List<Post> posts = generateListOfPosts(10);
        List<Post> sorted = node.sortTopPosts(posts);
        // TODO asserts
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
