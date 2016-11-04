import junit.framework.TestCase;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.dataAccess.proto.PostListProto.PostList;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.junit.Assume.assumeTrue;

/**
 * Created by erik on 02/11/16.
 */
public class InMemoryAccessTest {

    private static InMemoryAccessObject inMemoryAccess = new InMemoryAccessObject();
    private static final String testKeyString = "test:test";
    private static final Integer numTestPosts = 10;
    private static final ArrayList<Post> posts = new ArrayList<>();
    private static PostList postList;

    @BeforeClass
    public static void inMemoryTestSetup() {
        // Initialize sample test values
        List<Post> tempPosts = new ArrayList<>();
        PostList.Builder postListBuilder = PostList.newBuilder();

        // build list of posts and postlists
        for (int i = 0; i < numTestPosts; i++) {
            Post.Builder postBuilder = Post.newBuilder();
            postBuilder.setId(String.valueOf(i));
            postBuilder.addHashtag("#id" + String.valueOf(i));
            postBuilder.addText("This is test post " + String.valueOf(i));

            tempPosts.add(postBuilder.build());
        }

        postListBuilder.addAllPosts(tempPosts);

        postList = postListBuilder.build();
        posts.addAll(tempPosts);
    }

    // Delete test key from data store before and after each test
    @Before
    @After
    public void empty() {
        inMemoryAccess = new InMemoryAccessObject();
    }

    @Test
    public void addPostToEmptyMemory() {

        // Add new post to completely empty store. Should create new key entry, with new post at front of key
        inMemoryAccess.addNewPost(testKeyString, posts.get(0));
        assertEquals(posts.get(0), inMemoryAccess.getAllPosts(testKeyString).get(0));
    }

    @Test
    public void addPostToNonEmptyMemory() {

        // Initialize store with multiple posts, and add a single key
        inMemoryAccess.addNewPosts(testKeyString, posts);
        inMemoryAccess.addNewPost(testKeyString, posts.get(0));

        // new post has been added to end of posts
        assertEquals(posts.get(0), inMemoryAccess.getAllPosts(testKeyString).get(numTestPosts));
    }

    @Test
    public void addPostsToEmptyMemory() {

        // add multiple posts to an empty key. Should create new key entry, with posts in order
        inMemoryAccess.addNewPosts(testKeyString, posts);

        assertEquals(posts, inMemoryAccess.getAllPosts(testKeyString));
    }

    @Test
    public void addPostsToNonEmptyMemory() {

        // Add 2 sets of posts
        inMemoryAccess.addNewPosts(testKeyString, posts);
        inMemoryAccess.addNewPosts(testKeyString, posts);

        // new posts have been added in order and succeed old posts
        assertEquals(posts, inMemoryAccess.getAllPosts(testKeyString).subList(numTestPosts, 2 * numTestPosts));
    }

    @Test
    public void popEmptyPostMemory() {

        // Need to see that we handle an empty response correctly
        assertFalse(inMemoryAccess.popOldestPost(testKeyString).isPresent());
    }

    @Test
    public void popNonEmptyPostMemory() {

        // add posts
        inMemoryAccess.addNewPosts(testKeyString, posts);

        // see if we pop them in order
        for (int i = 0; i < numTestPosts; i++) {
            assertEquals(Optional.of(posts.get(i)), inMemoryAccess.popOldestPost(testKeyString));
        }
    }

    @Test
    public void addPostListToEmptyMemory() {

        // add posts to empty keyspace; should create new keyspace with those posts in order
        inMemoryAccess.addNewPosts(testKeyString, posts);
        assertEquals(posts, inMemoryAccess.getAllPosts(testKeyString));
    }

    @Test
    public void addPostListToNonEmptyMemory() {

        // initialize temporary list with posts, and add one post at index 0
        ArrayList<Post> testList = new ArrayList<>(posts);
        testList.add(0, posts.get(0));  // add first element to beginning of test list

        // add first post to memory, then remaining posts
        inMemoryAccess.addNewPost(testKeyString, posts.get(0));
        inMemoryAccess.addNewPosts(testKeyString, posts);

        // check if posts were added in correct order
        assertEquals(testList, inMemoryAccess.getAllPosts(testKeyString));
    }

    @Test
    public void getEmptyListofPosts() {

        // check for proper empty return value
        List<Post> emptyList = inMemoryAccess.getAllPosts(testKeyString);
        assertEquals(new ArrayList<Post>(), emptyList);
    }

    @Test
    public void getNonEmptyListofPosts() {

        // check for successful return, in-order
        inMemoryAccess.addNewPosts(testKeyString, posts);
        assertEquals(posts, inMemoryAccess.getAllPosts(testKeyString));
    }

    @Test
    public void popOldestPostFromEmpty() {

        // check for proper error handling on empty keyspace
        assertEquals(Optional.empty(), inMemoryAccess.popOldestPost(testKeyString));
    }

    @Test
    public void popOldestPostFromNonEmpty() {

        // initialize with posts, and pop one post
        inMemoryAccess.addNewPosts(testKeyString, posts);
        inMemoryAccess.popOldestPost(testKeyString);

        // pop second post to see if popped in order
        assertEquals(Optional.of(posts.get(1)), inMemoryAccess.popOldestPost(testKeyString));
    }

    @Test
    public void peekAtEmptyPostList() {

        // check for error handling on empty PostList
        assertFalse(inMemoryAccess.peekAtPostList(testKeyString).isPresent());
    }

    @Test
    public void peekAtNonEmptyPostList() {

        // initialize postlist
        inMemoryAccess.addNewPostList(testKeyString, postList);

        // get postlist; test twice to ensure we are not popping.
        assertEquals(Optional.of(postList), inMemoryAccess.peekAtPostList(testKeyString));
        assertEquals(Optional.of(postList), inMemoryAccess.peekAtPostList(testKeyString));
    }

}
