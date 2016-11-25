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

import static services.dataAccess.TestDataGenerator.generateListOfPosts;

import java.util.HashSet;
import java.util.List;
import java.util.Map;

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

        String testHashtag = "";
        Post testPost = Post.getDefaultInstance();

        // find one of the posts that has at least two hashtags, and obtain both it and one of the hashtags
        for (Post post : posts) {
            if (post.getHashtagCount() > 1) {
                testHashtag = post.getHashtag(0);
                testPost = post;
                break;
            }
        }

        // sort posts
        Map<String, List<Post>> sorted = sorter.sort(posts);

        // ensure that map contains the hashtag, and the post is stored under the hashtag key
        assertTrue(sorted.containsKey(testHashtag));
        assertTrue(sorted.get(testHashtag).contains(testPost));
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
}
