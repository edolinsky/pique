package SortingTests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.PostSorter.AbstractPostSorter;
import services.sorting.PostSorter.TrendingPostSorter;

import java.util.List;
import java.util.Map;

import static services.dataAccess.TestDataGenerator.generateListOfPosts;

public class TrendingPostSorterTest {

    private AbstractPostSorter sorter;

    @Before
    public void trendingPostSorterTestSetup() {
        AbstractDataAccess data = new InMemoryAccessObject();
        sorter = new TrendingPostSorter(data);
    }

    @Test
    @Ignore
    public void testSortTrending() {
        List<Post> posts = generateListOfPosts(10);
        Map<String, List<Post>> sorted = sorter.sort(posts);
        // TODO asserts
    }
}
