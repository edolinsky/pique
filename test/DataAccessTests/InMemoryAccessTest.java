package DataAccessTests;

import com.google.common.collect.Lists;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.dataAccess.proto.PostListProto.PostList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static services.dataAccess.TestDataGenerator.generateListOfPosts;
import static services.dataAccess.TestDataGenerator.generatePostList;
import static services.dataAccess.TestDataGenerator.randomHashtags;

public class InMemoryAccessTest {

    private static AbstractDataAccess inMemoryAccess = new InMemoryAccessObject();
    private static final String testKeyString = "test";
    private static final Integer numTestPosts = 10;

    @Before
    @After
    public void empty() {
        // Delete test key from data store before and after each test
        inMemoryAccess = new InMemoryAccessObject();
    }


    /*
     * Post Queue Management Tests
     */

    @Test
    public void addPostToEmptyMemory() {

        List<Post> posts = generateListOfPosts(numTestPosts);

        // Add new post to completely empty store. Should create new key entry, with new post at front of key
        inMemoryAccess.addNewPostFromSource(testKeyString, posts.get(0));
        assertEquals(posts.get(0), inMemoryAccess.getAllPostsFromSource(testKeyString).get(0));
    }

    @Test
    public void addPostToNonEmptyMemory() {

        List<Post> posts = generateListOfPosts(numTestPosts);

        // Initialize store with multiple posts, and add a single key
        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);
        inMemoryAccess.addNewPostFromSource(testKeyString, posts.get(0));

        // new post has been added to end of posts
        assertEquals(posts.get(0), inMemoryAccess.getAllPostsFromSource(testKeyString).get(numTestPosts));
    }

    @Test
    public void addPostsToEmptyMemory() {

        List<Post> posts = generateListOfPosts(numTestPosts);

        // add multiple posts to an empty key. Should create new key entry, with posts in order
        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);

        assertEquals(posts, inMemoryAccess.getAllPostsFromSource(testKeyString));
    }

    @Test
    public void addPostsToNonEmptyMemory() {

        List<Post> posts = generateListOfPosts(numTestPosts);

        // Add 2 sets of posts
        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);
        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);

        // new posts have been added in order and succeed old posts
        assertEquals(posts, inMemoryAccess.getAllPostsFromSource(testKeyString).subList(numTestPosts, 2 * numTestPosts));
    }

    @Test
    public void popEmptyPostMemory() {

        // Need to see that we handle an empty response correctly
        assertFalse(inMemoryAccess.popFirstPostFromSource(testKeyString).isPresent());
    }

    @Test
    public void popNonEmptyPostMemory() {

        List<Post> posts = generateListOfPosts(numTestPosts);

        // add posts
        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);

        // see if we pop them in order
        for (int i = 0; i < numTestPosts; i++) {
            assertEquals(Optional.of(posts.get(i)), inMemoryAccess.popFirstPostFromSource(testKeyString));
        }
    }

    @Test
    public void addPostsToNonEmptyMemoryInOddOrder() {

        List<Post> posts = generateListOfPosts(numTestPosts);

        // initialize temporary list with posts, and add one post at index 0
        ArrayList<Post> testList = new ArrayList<>(posts);
        testList.add(0, posts.get(0));  // add first element to beginning of test list

        // add first post to memory, then remaining posts
        inMemoryAccess.addNewPostFromSource(testKeyString, posts.get(0));
        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);

        // check if posts were added in correct order
        assertEquals(testList, inMemoryAccess.getAllPostsFromSource(testKeyString));
    }

    @Test
    public void getEmptyListofPosts() {

        // check for proper empty return value
        List<Post> emptyList = inMemoryAccess.getAllPostsFromSource(testKeyString);
        assertEquals(new ArrayList<Post>(), emptyList);
    }

    @Test
    public void getNonEmptyListofPosts() {

        List<Post> posts = generateListOfPosts(numTestPosts);

        // check for successful return, in-order
        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);
        assertEquals(posts, inMemoryAccess.getAllPostsFromSource(testKeyString));
    }

    @Test
    public void popOldestPostFromEmpty() {

        // check for proper error handling on empty keyspace
        assertEquals(Optional.empty(), inMemoryAccess.popFirstPostFromSource(testKeyString));
    }

    @Test
    public void popOldestPostFromNonEmpty() {

        List<Post> posts = generateListOfPosts(numTestPosts);

        // initialize with posts, and pop one post
        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);
        inMemoryAccess.popFirstPostFromSource(testKeyString);

        // pop second post to see if popped in order
        assertEquals(Optional.of(posts.get(1)), inMemoryAccess.popFirstPostFromSource(testKeyString));
    }

    /*
     * PostList management tests
     */

    @Test
    public void addDisplayPostListToEmptyMemory() {
        PostList postList = generatePostList(numTestPosts);

        // add postList to empty keyspace; should create new keyspace with this postList at beginning
        inMemoryAccess.addNewDisplayPostList(testKeyString, postList);
        assertEquals(Optional.of(postList), inMemoryAccess.getDisplayPostList(testKeyString, 0));
    }

    @Test
    public void addHashTagPostListToEmptyMemory() {
        PostList postList = generatePostList(numTestPosts);

        // add postList to empty hashtag keyspace; should create new keyspace with this postList at beginning
        inMemoryAccess.addNewHashTagPostList(testKeyString, postList);
        assertEquals(Optional.of(postList), inMemoryAccess.getHashTagPostList(testKeyString, 0));
    }

    @Test
    public void addDisplayPostListToNonEmptyMemory() {
        PostList postList = generatePostList(numTestPosts);

        inMemoryAccess.addNewDisplayPostList(testKeyString, postList);
        inMemoryAccess.addNewDisplayPostList(testKeyString, postList);
        assertEquals(Optional.of(postList), inMemoryAccess.getDisplayPostList(testKeyString, 1));
    }

    @Test
    public void addHashTagPostListToNonEmptyMemory() {
        PostList postList = generatePostList(numTestPosts);

        inMemoryAccess.addNewHashTagPostList(testKeyString, postList);
        inMemoryAccess.addNewHashTagPostList(testKeyString, postList);
        assertEquals(Optional.of(postList), inMemoryAccess.getHashTagPostList(testKeyString, 1));
    }

    @Test
    public void peekAtEmptyPostList() {

        // check for error handling on empty PostList
        assertFalse(inMemoryAccess.getDisplayPostList(testKeyString, 0).isPresent());
    }

    @Test
    public void getNonEmptyPostList() {
        PostList postList = generatePostList(numTestPosts);

        // initialize postlist
        inMemoryAccess.addNewDisplayPostList(testKeyString, postList);
        inMemoryAccess.addNewDisplayPostList(testKeyString, postList);

        // get postlist; test each twice to ensure we are not popping.
        assertEquals(Optional.of(postList), inMemoryAccess.getDisplayPostList(testKeyString, 0));
        assertEquals(Optional.of(postList), inMemoryAccess.getDisplayPostList(testKeyString, 0));
        assertEquals(Optional.of(postList), inMemoryAccess.getDisplayPostList(testKeyString, 1));
        assertEquals(Optional.of(postList), inMemoryAccess.getDisplayPostList(testKeyString, 1));
    }

    @Test
    public void getOutOfBoundsPostList() {
        PostList postList = generatePostList(numTestPosts);

        inMemoryAccess.addNewDisplayPostList(testKeyString, postList);
        assertEquals(Optional.empty(), inMemoryAccess.getDisplayPostList(testKeyString, Integer.MAX_VALUE));
    }

    /*
     * getNumPostsInNameSpace tests
     */

    @Test
    public void getNumPostsInEmptyNameSpace() {
        assertEquals(0, inMemoryAccess.getNumPostsInNameSpace(testKeyString));
    }

    @Test
    public void getNumPostsInNonEmptyNameSpace() {
        List<Post> posts = generateListOfPosts(numTestPosts);
        PostList postList = generatePostList(numTestPosts);

        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);
        inMemoryAccess.addNewPostsFromSource(testKeyString + "0", posts);
        inMemoryAccess.addNewPostsFromSource(testKeyString + "1", posts);
        inMemoryAccess.addNewHashTagPostList("asdfghwrtbgqer", postList); // hashtag namespace should not collide

        assertEquals(3 * numTestPosts, inMemoryAccess.getNumPostsInNameSpace("source"));
    }

    /*
     * getKeysInNameSpace tests
     */

    @Test
    public void getKeysInEmptyNameSpace() { // result on query for non-existent keys should be empty
        assertEquals(Collections.emptyList(), inMemoryAccess.getKeysInNameSpace(""));
    }

    @Test
    public void getKeysInNonEmptyNameSpace() {
        List<Post> posts = generateListOfPosts(numTestPosts);

        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);

        // should return list containing only testKeyString
        assertEquals( AbstractDataAccess.getSourceNamespace()
                + AbstractDataAccess.getNamespaceDelimiter()
                + testKeyString,
                inMemoryAccess.getKeysInNameSpace("source").get(0));
    }

    /*
     * deleteNPosts tests
     */

    @Test
    public void deleteNPostsFromNonEmptyListOfPosts() {
        List<Post> posts = generateListOfPosts(numTestPosts);

        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);

        // delete first 5 posts in testKeyString, check that what remains matches the subList at index 5 and beyond
        inMemoryAccess.deleteFirstNPostsFromSourceQueue(testKeyString, 5);
        assertEquals(posts.subList(5, numTestPosts), inMemoryAccess.getAllPostsFromSource(testKeyString));
    }

    @Test
    public void deleteNPostsFromEmptyListOfPosts() {

        // delete on empty should result in empty.
        inMemoryAccess.deleteFirstNPostsFromSourceQueue(testKeyString, 1);
        assertEquals(Collections.emptyList(), inMemoryAccess.getAllPostsFromSource(testKeyString));
    }

    @Test
    public void deleteMoreThanSizePostsFromListOfPosts() {
        List<Post> posts = generateListOfPosts(numTestPosts);

        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);

        // deleting more than size posts should result in empty list at keyString
        inMemoryAccess.deleteFirstNPostsFromSourceQueue(testKeyString, numTestPosts + 1);
        assertEquals(Collections.emptyList(), inMemoryAccess.getAllPostsFromSource(testKeyString));
    }

    @Test
    public void deleteZeroKeysFromListOfPosts() {
        List<Post> posts = generateListOfPosts(numTestPosts);

        inMemoryAccess.addNewPostsFromSource(testKeyString, posts);

        // calling delete on 0 keys should not change list
        inMemoryAccess.deleteFirstNPostsFromSourceQueue(testKeyString, 0);
        assertEquals(posts, inMemoryAccess.getAllPostsFromSource(testKeyString));
    }

    @Test
    public void testHashTagPostListReplaceSingle() {

        List<Post> posts = generateListOfPosts(numTestPosts);
        PostList postList = PostList.newBuilder().addAllPosts(posts).build();

        // build list containing one reversed postList
        PostList reversePostList = PostList.newBuilder().addAllPosts(Lists.reverse(posts)).build();
        List<PostList> postLists = new ArrayList<>();
        postLists.add(reversePostList);

        // add postList and replace it with the reverse
        inMemoryAccess.addNewHashTagPostList(testKeyString, postList);
        inMemoryAccess.replaceHashTagPostLists(testKeyString, postLists);

        // only reversed postList should remain
        assertEquals(Optional.of(reversePostList), inMemoryAccess.getHashTagPostList(testKeyString, 0));
        assertEquals(Optional.empty(), inMemoryAccess.getHashTagPostList(testKeyString, 1));
    }

    @Test
    public void testHashTagPostListReplaceEmptyList() {

        PostList postList = generatePostList(numTestPosts);

        // add postList to hashtag channel, and replace with empty postlist
        inMemoryAccess.addNewHashTagPostList(testKeyString, postList);
        inMemoryAccess.replaceHashTagPostLists(testKeyString, Collections.emptyList());

        // no postLists should exist
        assertEquals(Optional.empty(), inMemoryAccess.getHashTagPostList(testKeyString, 0));
        assertEquals(Optional.empty(), inMemoryAccess.getHashTagPostList(testKeyString, 1));
    }

    @Test
    public void testHashTagPostListReplace() {
        int numPostLists = numTestPosts;
        assert(numPostLists < AbstractDataAccess.getMaxPostlists());    // numPostLists must be less than max allowed

        List<Post> posts = generateListOfPosts(numTestPosts);

        // create list of PostLists, add in increasing size
        List<PostList> postLists = new ArrayList<>();
        for (int i = 0; i < numPostLists; i++) {
            postLists.add(PostList.newBuilder().addAllPosts(posts.subList(0, i+1)).build());
        }

        // replace empty channel with list of postLists
        inMemoryAccess.replaceHashTagPostLists(testKeyString, postLists);

        // postsLists list should be entered in order
        for (int i = 0; i < numPostLists; i++) {
            assertEquals(Optional.of(postLists.get(i)), inMemoryAccess.getHashTagPostList(testKeyString, i));
        }
    }


    /*
       addTopHasthags tests
     */

    @Test
    public void testAddTopHashtagsEmptyList() {

        List<String> hashtags = randomHashtags();
        int numTags = hashtags.size();

        // add hashtags, then empty list. Result should be no entries in channel
        inMemoryAccess.addTopHashtags(hashtags);
        inMemoryAccess.addTopHashtags(Collections.emptyList());
        assertEquals(Collections.emptyList(), inMemoryAccess.getTopHashTags(numTags));
    }

    @Test
    public void testAddTopHashtagsEmptyChannel() {
        List<String> hashtags = randomHashtags();
        int numTags = hashtags.size();

        // add list of hashtags to channel; they should then be stored in order
        inMemoryAccess.addTopHashtags(hashtags);
        assertEquals(hashtags, inMemoryAccess.getTopHashTags(numTags));
    }

    @Test
    public void testAddTopHashtags() {

        List<String> hashtags = randomHashtags();
        List<String> reversedHashtags = Lists.reverse(hashtags);
        int numTags = hashtags.size();

        // add hashtags, then reversed hashtags. Reversed hashtags should remain.
        inMemoryAccess.addTopHashtags(hashtags);
        inMemoryAccess.addTopHashtags(reversedHashtags);
        assertEquals(reversedHashtags, inMemoryAccess.getTopHashTags(numTags));
    }

}
