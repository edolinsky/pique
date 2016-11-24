package SorterTests;

import org.junit.Before;
import org.junit.Test;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostProto;
import services.sorting.AbstractPostSorter;
import services.sorting.TopPostSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static services.dataAccess.TestDataGenerator.generateListOfPosts;

public class AbstractPostSorterTest {

    private AbstractPostSorter sorter;

    @Before
    public void abstractPostSorterSetup() {
        AbstractDataAccess data = new InMemoryAccessObject();
        sorter = new TopPostSorter(data);
    }

    @Test
    public void testPreparePostListWithPosts() {
        List<PostProto.Post> posts = generateListOfPosts(10);
        PostListProto.PostList postList = sorter.preparePostList(posts);

        assertEquals(posts, postList.getPostsList());
    }

    @Test
    public void testPreparePostsListNoPosts() {
        PostListProto.PostList postList = sorter.preparePostList(new ArrayList<>());
        assertEquals(Collections.emptyList(), postList.getPostsList());
    }
}
