package services.dataAccess;

import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.List;
import java.util.Optional;

/**
 * Created by erik on 23/10/16.
 */

public abstract class AbstractDataAccess {

    public static final String NAMESPACE_DELIMITER = ":";

    public static final String DISPLAY_NAMESPACE = "display";
    public static final String SOURCE_NAMESPACE = "source";
    public static final String TEST_NAMESPACE = "test";
    static final Integer MAX_POSTLISTS = 100;

    /**
     * Adds a new post to this data store's list of posts (end of queue) under a particular key.
     * If no key exists, a key-value pair is created and Post is the first element in the value list.
     *
     * @param keyString string denoting key in data store
     * @param post      Post object to be stored
     * @return length of list of posts under keyString after insertion of new post
     */
    abstract public long addNewPost(String keyString, Post post);

    /**
     * Adds a series of posts to this data store's list of posts at a key, in order at the end of the queue.
     * If no key exists, a key-value pair is created and listOfPosts is stored in the value list.
     *
     * @param keyString   string denoting key in data store
     * @param listOfPosts list of posts to append to value list at key
     * @return length of list of posts under keyString after insertion of new posts
     */
    abstract public long addNewPosts(String keyString, List<Post> listOfPosts);

    /**
     * Adds a new postList entity to the beginning of this data store's list of postLists under a particular key.
     * If no key exists, a key-value pair is created and postList is stored at the beginning of the new value list.
     *
     * @param keyString string denoting key in data store
     * @param postList  postList entity to be entered at beginning of list under keyString
     * @return size of list at keyString after insertion of new postList
     */
    abstract public long addNewPostList(String keyString, PostList postList);

    /**
     * Retrieves all Posts under a particular keyString
     *
     * @param keyString string denoting key in data store
     * @return list of Posts at keyString; empty list if keyString does not exist in data store
     */
    abstract public List<Post> getAllPosts(String keyString);

    /**
     * Retrieves and removes post from the beginning of the queue at keyString in data store
     * If key does not exist, or list is empty, returns the empty optional
     *
     * @param keyString string denoting key in data store
     * @return The first element under keyString in data store, or the empty optional if not availalbe
     */
    abstract public Optional<Post> popOldestPost(String keyString);

    /**
     * Retrieves, but does not remove postList entity from the beginning of the stack at keyString in data store
     * If key does not exist, or list is empty, returns the empty optional
     *
     * @param keyString string denoting key in data store
     * @return The first element under keyString in data store, or the empty optional if not available
     */
    abstract public Optional<PostList> peekAtPostList(String keyString);

    /**
     * Returns the number of posts within a particular namespace within the data store. If namespace does not exist,
     * returns 0.
     *
     * @param nameSpace string corresponding to the desired namespace (proceeds namespace delimiter in any unique key)
     * @return the number of posts within all keys under nameSpace
     */
    abstract public long getNumPostsInNameSpace(String nameSpace);

    /**
     * Returns a list of all keys under a specified namespace. Returns an empty list if no keys exist under a specific
     * namespace, or if namespace does not exist in data store.
     *
     * @param nameSpace string corresponding to the desired namespace (proceeds namespace delimiter in any unique key)
     * @return A list containing all string keys under nameSpace
     */
    abstract public List<String> getKeysInNameSpace(String nameSpace);

    /**
     * Deletes the first numPosts Posts under keyString. If numPosts is greater than the size of the list at keyString,
     * size elements are cleared.
     *
     * @param keyString string denoting key in data store
     * @param numPosts  number of posts to be deleted from beginning of list at keyString
     * @return string denoting status of trim operation
     */
    abstract public String deleteFirstNPosts(String keyString, Integer numPosts);

}
