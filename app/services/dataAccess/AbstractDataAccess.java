package services.dataAccess;

import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.List;
import java.util.Optional;

/**
 * Created by erik on 23/10/16.
 */

public abstract class AbstractDataAccess {

    /**
     * Adds a new post to this data store's list of posts (end of queue) under a particular key.
     * If no key exists, a key-value pair is created and Post is the first element in the value list.
     *
     * @param keyString string denoting key in data store
     * @param post Post object to be stored
     * @return length of list of posts under keyString after insertion of new post
     */
    abstract public long addNewPost(String keyString, Post post);

    /**
     * Adds a series of posts to this data store's list of posts at a key, in order at the end of the queue.
     * If no key exists, a key-value pair is created and listOfPosts is stored in the value list.
     *
     * @param keyString string denoting key in data store
     * @param listOfPosts list of posts to append to value list at key
     * @return length of list of posts under keyString after insertion of new posts
     */
    abstract public long addNewPosts(String keyString, List<Post> listOfPosts);

    /**
     * Adds a new postList entity to the beginning of this data store's list of postLists under a particular key.
     * If no key exists, a key-value pair is created and postList is stored at the beginning of the new value list.
     *
     * @param keyString string denoting key in data store
     * @param postList postList entity to be entered at beginning of list under keyString
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

}
