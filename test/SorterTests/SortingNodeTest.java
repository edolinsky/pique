package SorterTests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import services.ThreadNotification;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.SortingNode;

import static org.junit.Assert.assertTrue;
import static services.dataAccess.TestDataGenerator.generateListOfPosts;

public class SortingNodeTest {

    private SortingNode node;

    @Before
    public void sortingNodeTestSetup() {
        ThreadNotification notification = new ThreadNotification();
        AbstractDataAccess data = new InMemoryAccessObject();
        node = new SortingNode(data, notification);
    }

    @Test
    public void testCalculatePopularity() {
        Post post = generateListOfPosts(1).get(0);
        Post post2 = node.calculatePopularityAndRebuild(post);

        assertTrue(post2.hasField(Post.getDescriptor().findFieldByNumber(Post.POPULARITY_SCORE_FIELD_NUMBER)));
    }

}
