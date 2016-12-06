package SortingTests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.PostSorter.AbstractPostSorter;
import services.sorting.PostSorter.HashtagPostSorter;

import static org.junit.Assume.assumeFalse;
import static org.junit.Assume.assumeTrue;
import static services.dataAccess.TestDataGenerator.generateListOfPosts;

import java.util.*;

public class HashtagPostSorterTest {

    private AbstractPostSorter sorter;
    private InMemoryAccessObject data;

    @Before
    public void abstractPostSorterSetup() {
        data = new InMemoryAccessObject();
        sorter = new HashtagPostSorter(data);
    }

    @Test
    public void testSortHashtags() {
        List<Post> posts = generateListOfPosts(100);
        Post testPost = Post.getDefaultInstance();
        Optional<Post> optPost = getPostWithHashtags(posts);

        if (optPost.isPresent()) {
            testPost = optPost.get();
        } else {
            // ignore test if optPost is not present
            assumeTrue(optPost.isPresent());
        }
        // ignore test if post is default instance
        assumeFalse(testPost.equals(Post.getDefaultInstance()));

        String testHashtag = testPost.getHashtag(1);

        // sort posts
        Map<String, List<Post>> sorted = sorter.sort(posts);

        // ensure that map contains the hashtag, and the post is stored under the hashtag key
        assertTrue(sorted.containsKey(testHashtag));
        assertTrue(sorted.get(testHashtag).contains(testPost));
    }

    @Test
    public void testSortEmptyList() {
        Map<String, List<Post>> sorted = sorter.sort(Collections.emptyList());
        assertEquals(Collections.emptyMap(), sorted);
    }

    @Test
    public void testLoadHashtags() {
        List<Post> posts = generateListOfPosts(100);

        // sort posts by hashtag and load
        Map<String, List<Post>> sorted = sorter.sort(posts);
        sorter.load(sorted);

        // all hashtags should have been loaded into data store
        assertEquals(sorted.keySet(), new HashSet<>(data.getAllHashTags()));
    }

    @Test
    public void testLoadEmptyMap() {
        sorter.load(Collections.emptyMap());

        assertEquals(Collections.emptyList(), data.getAllHashTags());
    }

    /**
     * Returns the first post within the specified list with 2 or more hashtags
     * @param posts list of posts
     * @return optional of the post if found; empty optional otherwise.
     */
    private Optional<Post> getPostWithHashtags(List<Post> posts) {
        Post testPost = null;

        // find one of the posts that has at least two hashtags, and obtain both it and one of the hashtags
        for (Post post : posts) {
            if (post.getHashtagCount() > 1) {
                testPost = post;
                break;
            }
        }

        if (testPost == null) {
            return Optional.empty();
        } else {
            return Optional.of(testPost);
        }
    }
}
