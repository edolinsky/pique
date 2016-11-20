package services.dataAccess;

import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;
import sun.util.resources.cldr.zh.CalendarData_zh_Hans_HK;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

public abstract class AbstractDataAccess {

    static final String NAMESPACE_DELIMITER = ":";

    private static final String DISPLAY_NAMESPACE = "display";
    private static final String HASHTAG_NAMESPACE = "hashtag";
    private static final String SOURCE_NAMESPACE = "source";
    private static final String TEST_NAMESPACE = "test";
    private static final String STRING_LIST_NAMESPACE = "stringlist";
    static final Integer MAX_POSTLISTS = 100;


    /**
     * Adds a new post to this data store's list of posts (end of queue) under a particular key.
     * If no key exists, a key-value pair is created and Post is the first element in the value list.
     *
     * @param keyString string denoting key in data store
     * @param post      Post object to be stored
     * @return length of list of posts under keyString after insertion of new post
     */
    abstract protected long addNewPost(String keyString, Post post);

    /**
     * Adds a series of posts to this data store's list of posts at a key, in order at the end of the queue.
     * If no key exists, a key-value pair is created and listOfPosts is stored in the value list.
     *
     * @param keyString   string denoting key in data store
     * @param listOfPosts list of posts to append to value list at key
     * @return length of list of posts under keyString after insertion of new posts
     */
    abstract protected long addNewPosts(String keyString, List<Post> listOfPosts);

    /**
     * Retrieves and removes post from the beginning of the queue at keyString in data store
     * If key does not exist, or list is empty, returns the empty optional
     *
     * @param keyString string denoting key in data store
     * @return The first element under keyString in data store, or the empty optional if not availalbe
     */
    abstract protected Optional<Post> popFirstPost(String keyString);

    /**
     * Retrieves all Posts under a particular keyString
     *
     * @param keyString string denoting key in data store
     * @return list of Posts at keyString; empty list if keyString does not exist in data store
     */
    abstract protected List<Post> getAllPosts(String keyString);

    /**
     * Deletes the first numPosts Posts under keyString. If numPosts is greater than the size of the list at keyString,
     * size elements are cleared.
     *
     * @param keyString string denoting key in data store
     * @param numPosts  number of posts to be deleted from beginning of list at keyString
     * @return string denoting status of trim operation
     */
    abstract protected String deleteFirstNPosts(String keyString, Integer numPosts); // todo: change return type

    /**
     * Adds a new postList entity to the beginning of this data store's list of postLists under a particular key.
     * If no key exists, a key-value pair is created and postList is stored at the beginning of the new value list.
     *
     * @param keyString string denoting key in data store
     * @param postList  postList entity to be entered at beginning of list under keyString
     * @return size of list at keyString after insertion of new postList
     */
    abstract protected long addNewPostList(String keyString, PostList postList);

    /**
     * Retrieves, but does not remove postList entity at specified index of the stack at keyString in data store
     * If key does not exist, or list is empty, returns the empty optional
     *
     * @param keyString string denoting key in data store
     * @param index index of desired PostList under keyString in data store
     * @return The first element under keyString in data store, or the empty optional if not available
     */
    abstract protected Optional<PostList> getPostList(String keyString, Integer index);

    /**
     * Retrieves, but does not remove all postLists under keyString in data store
     * If key does not exist, or list is empty, returns an empty list
     *
     * @param keyString string denoting key in data store
     * @return the list of all postList entities under keyString in data store
     */
    abstract protected List<PostList> getAllPostLists(String keyString);

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
     * Returns the size of the list at the specified key string
     * @param keyString desired key string in data store
     * @return the size of the list stored at keyString
     */
    abstract protected long getListSize(String keyString);

    /**
     * Retrieves, but does not remove, the first length elements of the list of strings stored at the specified key string
     * @return the list of strings stored at the specified key string
     */
    abstract protected List<String> getStringList(String keyString, long length);

    /**
     * Adds a new list of strings to the beginning of the list of strings at the specified key string. This removes
     * the old list of strings.
     * @return new length of list under keyString after insertion.
     */
    abstract protected long replaceStringList(String keyString, List<String> stringList);

    /**
     * Adds a new post to this data store's list of posts (end of queue) under a particular key in the source namespace.
     * If no key exists, a key-value pair is created and Post is the first element in the value list.
     *
     * @param source string denoting key in data store
     * @param post      Post object to be stored
     * @return length of list of posts at source under source namespace after insertion of new post
     */
    public long addNewPostFromSource(String source, Post post) {
        return addNewPost(SOURCE_NAMESPACE + NAMESPACE_DELIMITER + source, post);
    }

    /**
     * Adds a series of posts to this data store's list of posts at a key under the source namespace, in order at the
     * end of the queue. If no key exists, a key-value pair is created and listOfPosts is stored in the value list.
     *
     * @param source   string denoting key in data store
     * @param listOfPosts list of posts to append to value list at key
     * @return length of list of posts at source under source namespace after insertion of new posts
     */
    public long addNewPostsFromSource(String source, List<Post> listOfPosts){
        return addNewPosts(SOURCE_NAMESPACE + NAMESPACE_DELIMITER + source, listOfPosts);
    }

    /**
     * Adds a new postList entity to the beginning of this data store's list of postLists under a particular key in the
     * display namespace. If no key exists, a key-value pair is created and postList is stored at the beginning of
     * the new value list.
     *
     * @param displayString string denoting key in data store
     * @param postList  postList entity to be entered at beginning of list under keyString
     * @return size of list at displayString under display namespace after insertion of new postList
     */
    public long addNewDisplayPostList(String displayString, PostList postList) {
        return addNewPostList(DISPLAY_NAMESPACE + NAMESPACE_DELIMITER + displayString, postList);
    }

    /**
     * Adds a new postList entity to the beginning of this data store's list of postLists under a particular key in the
     * hashtag namespace. If no key exists, a key-value pair is created and postList is stored at the beginning of the
     * new value list.
     *
     * @param hashtag string denoting key in data store
     * @param postList  postList entity to be entered at beginning of list under keyString
     * @return size of list at specified hashtag under hashtag namespace after insertion of new postList
     */
    public long addNewHashTagPostList(String hashtag, PostList postList) {
        return addNewPostList(HASHTAG_NAMESPACE + NAMESPACE_DELIMITER + hashtag, postList);
    }

    /**
     * Retrieves all Posts under a particular keyString in the source namespace.
     *
     * @param source string denoting key under source namespace in data store
     * @return list of Posts at key source under source namespace; empty list if source:keyString does not exist in
     * data store
     */
    public List<Post> getAllPostsFromSource(String source) {
        return getAllPosts(SOURCE_NAMESPACE + NAMESPACE_DELIMITER + source);
    }

    /**
     * Retrieves and removes post from the beginning of the queue at keyString in data store
     * If key does not exist, or list is empty, returns the empty optional
     *
     * @param source string denoting key in data store
     * @return The first element under keyString in data store, or the empty optional if not availalbe
     */
    public Optional<Post> popFirstPostFromSource(String source) {
        return popFirstPost(SOURCE_NAMESPACE + NAMESPACE_DELIMITER + source);
    }

    /**
     * Retrieves all PostLists under a particular hashtag in the hashtag namespace.
     * @param hashtag string denoting key under hashtag namespace in data store
     * @return list of all posts stored under that hashtag
     */
    public List<PostList> getAllHashtagPostLists(String hashtag) {
        return getAllPostLists(HASHTAG_NAMESPACE + NAMESPACE_DELIMITER + hashtag);
    }

    public long getNumHashTagPostLists(String hashtag) {
        return getListSize(HASHTAG_NAMESPACE + NAMESPACE_DELIMITER + hashtag);
    }

    /**
     * Retrieves, but does not remove postList entity at the specified index from the stack at source under the display
     * namespace in data store. If key does not exist, or list is empty, returns the empty optional
     *
     * @param displayString string denoting desired display key in data store
     * @param index index of desired postList under key
     * @return The element at index of displayString under display namespace in data store,
     * or the empty optional if not available
     */
    public Optional<PostList> getDisplayPostList(String displayString, Integer index) {
        return getPostList(DISPLAY_NAMESPACE + NAMESPACE_DELIMITER + displayString, index);
    }

    /**
     * Retrieves, but does not remove postList entity at the specified index within data store under the hashtag
     * namespace. If key does not exist, or list is empty, returns the empty optional
     *
     * @param hashtag string denoting key in data store
     * @param index
     * @return The first element under keyString in data store, or the empty optional if not available
     */
    public Optional<PostList> getHashTagPostList(String hashtag, Integer index) {
        return getPostList(HASHTAG_NAMESPACE + NAMESPACE_DELIMITER + hashtag, index);
    }

    /**
     * Retrieves, but does not remove all postList entities under the specified name within data store under display
     * namespace. If displayName does not exist, or list is empty, returns an empty list.
     * @param displayName name of display list
     * @return list of postLists under that particular display name
     */
    public List<PostList> getAllDisplayPostLists(String displayName) {
        return getAllPostLists(DISPLAY_NAMESPACE + NAMESPACE_DELIMITER + displayName);
    }

    /**
     * Uses getNumPostsInNameSpace to retrieve the number of posts within the 'source' namespace
     *
     * @return the number of posts within the source namespace
     */
    public long getNumPostsInSources() {
        return getNumPostsInNameSpace(SOURCE_NAMESPACE);
    }

    /**
     * Retrieves the list of available sources (without proceeding namespace or delimiter) in the data Access object
     * @return A list containing the name of all sources
     */
    public List<String> getSources() {
        return getKeysInNameSpace(SOURCE_NAMESPACE).stream()
                .map(s -> s.substring(s.indexOf(NAMESPACE_DELIMITER) + 1))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the list of available hashtags (without proceeding namespace or delimiter) in the data store
     * @return A list containing all available hashtags.
     */
    public List<String> getAllHashTags() {
        return getKeysInNameSpace(HASHTAG_NAMESPACE).stream()
                .map(s -> s.substring(s.indexOf(NAMESPACE_DELIMITER) + 1))
                .collect(Collectors.toList());
    }

    /**
     * Retrieves the first numTopHashtags hashtags from the list of top hashtags
     * @param numTopHashtags number of top hashtags to be received
     * @return first numTopHashtags hashtags in the top hashtag list
     */
    public List<String> getTopHashTags(int numTopHashtags) {
        return getStringList(STRING_LIST_NAMESPACE + NAMESPACE_DELIMITER + HASHTAG_NAMESPACE, numTopHashtags);
    }

    /**
     * Replaces the current list of top hashtags with the specified list of hashtags
     * @param topHashTags list of strings, denoting hashtags
     * @return new length of top hashtag list
     */
    public long addTopHashtags(List<String> topHashTags) {
        return replaceStringList(STRING_LIST_NAMESPACE + NAMESPACE_DELIMITER + HASHTAG_NAMESPACE, topHashTags);
    }

    /**
     * Deletes the first numPosts Posts under keyString. If numPosts is greater than the size of the list at keyString,
     * size elements are cleared.
     *
     * @param source string denoting key in data store
     * @param numPosts  number of posts to be deleted from beginning of list at keyString
     * @return string denoting status of trim operation
     */
    public String deleteFirstNPostsFromSourceQueue(String source, Integer numPosts) {
        return deleteFirstNPosts(SOURCE_NAMESPACE + NAMESPACE_DELIMITER + source, numPosts);
    }

    /*
     * Static getters
     */

    public static Integer getMaxPostlists() {
        return MAX_POSTLISTS;
    }

    public static String getTestNamespace() {
        return TEST_NAMESPACE;
    }

    public static String getSourceNamespace() {
        return SOURCE_NAMESPACE;
    }

    public static String getHashtagNamespace() {
        return HASHTAG_NAMESPACE;
    }

    public static String getNamespaceDelimiter() {
        return NAMESPACE_DELIMITER;
    }

    public static String getDisplayNamespace() {
        return DISPLAY_NAMESPACE;
    }
}
