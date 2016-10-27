package services.dataAccess;

import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostProto;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;

/**
 * Created by erik on 23/10/16.
 */
public class InMemoryAccessObject extends AbstractDataAccess {

    private HashMap<String, List<PostProto.Post>> postDataStore;
    private HashMap<String, List<PostListProto.PostList>> postListDataStore;

    public InMemoryAccessObject() {
        postDataStore = new HashMap<>();
        postListDataStore = new HashMap<>();
    }

    @Override
    public long addNewPost(String keyString, PostProto.Post post) {
        List<PostProto.Post> listAtKeyString = postDataStore.get(keyString);
        listAtKeyString.add(post);
        return listAtKeyString.size();
    }

    public long addNewPosts(String keyString, List<PostProto.Post> listOfPosts) {
        List<PostProto.Post> listAtKeyString = postDataStore.get(keyString);
        listAtKeyString.addAll(listOfPosts);
        return listAtKeyString.size();
    }

    @Override
    public long addNewPostList(String keyString, PostListProto.PostList postList) {
        List<PostListProto.PostList> listAtKeyString = postListDataStore.get(keyString);
        listAtKeyString.add(postList);
        return listAtKeyString.size();
    }

    @Override
    public Optional<PostProto.Post> popOldestPost(String keyString) {
        List<PostProto.Post> listAtKeyString = postDataStore.get(keyString);
        PostProto.Post oldestPost = null;

        if (listAtKeyString.size() > 0) {
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
    public Optional<byte[]> peekAtByte(String keyString) {
        Optional<PostListProto.PostList> post = peekAtPostList(keyString);
        if (post.isPresent()) {
            return Optional.of(post.get().toByteArray());
        } else {
            return Optional.empty();
        }
    }

    @Override
    public Optional<byte[]> peekAtByte(byte[] key) {
        return peekAtByte(key.toString());
    }

    @Override
    public Optional<PostListProto.PostList> peekAtPostList(String keyString) {
        List<PostListProto.PostList> listAtKeyString = postListDataStore.get(keyString);

        if (listAtKeyString.size() == 0) {
            return Optional.empty();
        } else {
            return Optional.of(listAtKeyString.get(0));
        }
    }

}
