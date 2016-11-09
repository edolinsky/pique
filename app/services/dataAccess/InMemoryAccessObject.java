package services.dataAccess;

import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by erik on 23/10/16.
 */
public class InMemoryAccessObject extends AbstractDataAccess {

    private Map<String, List<Post>> postDataStore;
    private Map<String, List<PostList>> postListDataStore;

    public InMemoryAccessObject() {
        postDataStore = new HashMap<>();
        postListDataStore = new HashMap<>();
    }

    @Override
    public long addNewPost(String keyString, Post post) {

        List<Post> listAtKeyString;

        // If key exists in map, append post to list,
        if (postDataStore.containsKey(keyString)) {
            listAtKeyString = postDataStore.get(keyString);
            listAtKeyString.add(post);

            // otherwise, create new map entry containing post in list
        } else {
            listAtKeyString = new ArrayList<>();
            listAtKeyString.add(post);
            postDataStore.put(keyString, listAtKeyString);
        }
        return listAtKeyString.size();
    }

    @Override
    public long addNewPosts(String keyString, List<Post> listOfPosts) {

        List<Post> listAtKeyString;

        // If key exists in map, append post to list
        if (postDataStore.containsKey(keyString)) {
            listAtKeyString = postDataStore.get(keyString);
            listAtKeyString.addAll(listOfPosts);

        } else {
            // otherwise initialize key
            listAtKeyString = postDataStore.put(keyString, new ArrayList<>(listOfPosts));
        }

        if (listAtKeyString == null) {
            return 0;
        } else {
            return listAtKeyString.size();
        }
    }

    @Override
    public long addNewPostList(String keyString, PostList postList) {

        List<PostList> listAtKeyString;

        // if key exists, add postList at *beginning* of list
        if (postListDataStore.containsKey(keyString)) {
            listAtKeyString = postListDataStore.get(keyString);
            listAtKeyString.add(0, postList);

            // if key does not exist, create new map entry and insert postList
        } else {
            listAtKeyString = new ArrayList<>();
            listAtKeyString.add(0, postList);
            postListDataStore.put(keyString, listAtKeyString);
        }

        // todo: implement pruning of postListDataStore for long-term operation (max size)

        return listAtKeyString.size();
    }

    @Override
    public List<Post> getAllPosts(String keyString) {

        List<Post> listOfPosts = postDataStore.get(keyString);

        // return list of posts under a key, or empty list if key does not exist
        if (listOfPosts == null) {
            return Collections.emptyList();
        } else {
            return listOfPosts;
        }
    }

    @Override
    public Optional<Post> popOldestPost(String keyString) {

        List<Post> listAtKeyString = postDataStore.get(keyString);
        Post oldestPost = null;

        if (listAtKeyString != null && listAtKeyString.size() > 0) {
            // pop post if one exists
            oldestPost = listAtKeyString.get(0);
            listAtKeyString.remove(0);
        }

        if (oldestPost == null) {
            return Optional.empty();
        } else {
            return Optional.of(oldestPost);
        }
    }

    @Override
    public Optional<PostList> peekAtPostList(String keyString) {
        List<PostList> listAtKeyString = postListDataStore.get(keyString);

        // peek at first postList if it exists
        if (listAtKeyString == null) {
            return Optional.empty();
        } else {
            return Optional.of(listAtKeyString.get(0));
        }
    }

    @Override
    public long getNumPostsInNameSpace(String nameSpace) {

        // get all keys in desired nameSpace
        List<String> keysInNameSpace = getKeysInNameSpace(nameSpace);

        // sum size of list at each key
        long count = 0;
        for (String key : keysInNameSpace) {
            count += postDataStore.get(key).size();
        }

        return count;
    }

    @Override
    public List<String> getKeysInNameSpace(String matchString) {
        // filter key set for keys matching keyString and return filtered list
        return postDataStore.keySet().stream().filter(key -> key.contains(matchString + NAMESPACE_DELIMITER)).collect(Collectors.toList());
    }

}
