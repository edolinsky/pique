package SortingTests;

import org.junit.Before;
import org.junit.Test;

import services.ThreadNotification;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.Calculator;
import services.sorting.SortingNode;

import java.util.Collections;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static services.dataAccess.TestDataGenerator.generateListOfPosts;

public class SortingNodeTest {

    private AbstractDataAccess data;
    private ThreadNotification notification;
    private SortingNode node;

    private static final String TEST = "test";

    @Before
    public void sortingNodeTestSetup() {
        data = new InMemoryAccessObject();
        notification = new ThreadNotification();
        node = new SortingNode(data, notification);
    }

    @Test
    public void testSortingNodeFullRun() {
        List<Post> posts = generateListOfPosts(10000);
        data.addNewPostsFromSource(TEST, posts);

        node.sort();

        // data should be sorted and stored appropriately (more invasive cases covered in appropriate sorter tests)
        assertTrue(data.getKeysInNameSpace(AbstractDataAccess.getDisplayNamespace()).size() > 0);
        assertTrue(data.getKeysInNameSpace(AbstractDataAccess.getStringListNamespace()).size() > 0);
        assertTrue(data.getKeysInNameSpace(AbstractDataAccess.getHashtagNamespace()).size() > 0);
    }

    @Test
    public void testSortingNodeUnderThreshold() {
        List<Post> posts = generateListOfPosts((int) (SortingNode.getProcessInputThreshold() / 2));
        data.addNewPostsFromSource(TEST, posts);

        node.sort();

        // no data should be sorted, as the threshold was not met
        assertEquals(Collections.emptyList(), data.getKeysInNameSpace(AbstractDataAccess.getDisplayNamespace()));
        assertEquals(Collections.emptyList(), data.getKeysInNameSpace(AbstractDataAccess.getHashtagNamespace()));
        assertEquals(Collections.emptyList(), data.getKeysInNameSpace(AbstractDataAccess.getStringListNamespace()));
    }

}
