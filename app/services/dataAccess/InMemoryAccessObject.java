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
    private Map<String, List<String>> stringListDataStore;

    public InMemoryAccessObject() {
        postDataStore = new HashMap<>();
        postListDataStore = new HashMap<>();
        stringListDataStore = new HashMap<>();
    }

    @Override
    protected long addNewPost(String keyString, Post post) {

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
    protected long addNewPosts(String keyString, List<Post> listOfPosts) {

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
    protected Optional<Post> popFirstPost(String keyString) {

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
    protected List<Post> getAllPosts(String keyString) {

        List<Post> listOfPosts = postDataStore.get(keyString);

        // return list of posts under a key, or empty list if key does not exist
        if (listOfPosts == null) {
            return Collections.emptyList();
        } else {
            return listOfPosts;
        }
    }

    @Override
    protected String deleteFirstNPosts(String keyString, Integer numPosts) {
        List<Post> listAtKeyString = postDataStore.get(keyString);

        if (listAtKeyString == null) {
            return "EMPTY";
        } else {
            if (listAtKeyString.size() <= numPosts) {   // clear entire list
                listAtKeyString.clear();
            } else {
                // clear subList from index 0 (inclusive) to index numPosts (exclusive)
                listAtKeyString.subList(0, numPosts).clear();
            }
            return "OK";
        }
    }

    @Override
    protected long addNewPostList(String keyString, PostList postList) {

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

        // truncate oldest PostList if this list has reached maximum size
        if (listAtKeyString.size() > MAX_POSTLISTS) {
            listAtKeyString.remove(listAtKeyString.size() - 1);
        }

        return listAtKeyString.size();
    }


    @Override
    protected Optional<PostList> getPostList(String keyString, Integer index) {
        List<PostList> listAtKeyString = postListDataStore.get(keyString);

        // get entry at index if it exists
        if (listAtKeyString == null || index >= listAtKeyString.size()) {
            return Optional.empty();
        } else {
            return Optional.of(listAtKeyString.get(index));
        }
    }

    @Override
    protected List<PostList> getAllPostLists(String keyString) {
        List<PostList> listOfPostLists = postListDataStore.get(keyString);

        // return list of posts under a key, or empty list if key does not exist
        if (listOfPostLists == null) {
            return Collections.emptyList();
        } else {
            return listOfPostLists;
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
    public List<String> getKeysInNameSpace(String nameSpace) {
        // filter key set for keys matching keyString and return filtered list
        return postDataStore.keySet().stream().filter(key -> key.contains(nameSpace + NAMESPACE_DELIMITER)).collect(Collectors.toList());
    }

    @Override
    protected long getListSize(String keyString) {

        List<PostList> postLists = postListDataStore.get(keyString);

        if (postLists != null) {
            return postLists.size();
        } else {
            return 0L;
        }

    }

    @Override
    protected List<String> getStringList(String keyString, long length) {
        List<String> stringList = stringListDataStore.get(keyString);

        if (stringList != null) {

            // list at keyString exists, return shorter of specified length or actual stringList size
            if (length > stringList.size()) {
                return new ArrayList<>(stringList);
            } else {
                return new ArrayList<>(stringList.subList(0, (int) length));
            }

        } else {
            // list at keyString does not exist; return empty list
            return Collections.emptyList();
        }
    }

    @Override
    protected long replaceStringList(String keyString, List<String> stringList) {

        stringListDataStore.put(keyString, new ArrayList<>(stringList));
        return stringList.size();
    }

}
