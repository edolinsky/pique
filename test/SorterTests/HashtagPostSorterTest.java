package SorterTests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.AbstractPostSorter;
import services.sorting.HashtagPostSorter;

import static services.dataAccess.TestDataGenerator.generateListOfPosts;

import java.util.List;
import java.util.Map;

public class HashtagPostSorterTest {

    private AbstractPostSorter sorter;

    @Before
    public void abstractPostSorterSetup() {
        AbstractDataAccess data = new InMemoryAccessObject();
        sorter = new HashtagPostSorter(data);
    }

    @Test
    @Ignore
    public void testSortHashtags() {
        List<Post> posts = generateListOfPosts(10);
        Map<String, List<Post>> sorted = sorter.sort(posts);
        // TODO asserts
    }
}
