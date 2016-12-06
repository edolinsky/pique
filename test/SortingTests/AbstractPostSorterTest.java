package SortingTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assume.assumeTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostProto.Post;
import services.dataAccess.proto.PostListProto.PostList;
import services.sorting.PostSorter.AbstractPostSorter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static services.dataAccess.TestDataGenerator.generateListOfPosts;
import static services.dataAccess.TestDataGenerator.generatePostList;


/**
 * An AbstractPostSorterTest tests the functionalitites of methods implemented within the AbstractPostSorter class.
 * It does not test the functionality of its abstract methods.
 */
public class AbstractPostSorterTest {

    private AbstractPostSorter sorter;

    @Before
    public void abstractPostSorterSetup() {
        AbstractDataAccess data = new InMemoryAccessObject();

        sorter = new AbstractPostSorter(data) {

            // stub methods
            @Override
            public Map<String, List<Post>> sort(List<Post> posts) {
                return null;
            }

            @Override
            public long load(Map<String, List<Post>> sortedPosts) {
                return 0;
            }
        };
    }

    @Test
    public void testPreparePostListWithPosts() {
        List<Post> posts = generateListOfPosts(10);
        PostListProto.PostList postList = sorter.preparePostList(posts);

        assertEquals(posts, postList.getPostsList());
    }

    @Test
    public void testPreparePostsListNoPosts() {
        PostListProto.PostList postList = sorter.preparePostList(new ArrayList<>());
        assertEquals(Collections.emptyList(), postList.getPostsList());
    }

    @Test
    public void testPreparePagesPositiveCase() {
        int numFullPages = 4;

        // check that we have a valid page limit; if not, skip test
        assumeTrue(AbstractPostSorter.getPageLimit() > 0);

        // generate enough posts to fill numFullPages, and one extra post
        List<Post> posts = generateListOfPosts(numFullPages * AbstractPostSorter.getPageLimit() + 1);
        List<PostList> pages = sorter.preparePages(posts);

        assertTrue(pages.size() == numFullPages + 1);
        assertEquals(posts.get(posts.size() - 1), pages.get(pages.size() - 1).getPosts(0));

    }

    @Test
    public void testPreparePagesEmptyInput() {
        List<PostList> pages = sorter.preparePages(Collections.emptyList());
        assertEquals(Collections.emptyList(), pages);
    }

    @Test
    public void testExpandPagesPositiveCase() {
        int numPages = 10;
        int numPostsPerPage = AbstractPostSorter.getPageLimit();
        List<PostList> pages = new ArrayList<>();

        // generate pages and add to pages list
        for (int i = 0; i < numPages; i++) {
            pages.add(generatePostList(numPostsPerPage));
        }

        // expand and store
        List<Post> posts = sorter.expandPostLists(pages);

        // iterate through, ensuring that pages have been expanded in same order
        for (int i = 0; i < numPages; i++) {
            assertEquals(pages.get(i).getPostsList(), posts.subList(i * numPostsPerPage, (i+1) * numPostsPerPage));
        }

    }

    @Test
    public void testExpandPagesEmptyInput() {

        assertEquals(Collections.emptyList(), sorter.expandPostLists(Collections.emptyList()));
    }

}
