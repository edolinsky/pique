package services.dataAccess;

import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostProto;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by erik on 23/10/16.
 */
public class InMemoryAccessObject extends AbstractDataAccess {

    private HashMap<byte[], ArrayList<byte[]>> dataStore;

    public InMemoryAccessObject() {
        dataStore = new HashMap<>();
    }

    public long addNewPost(String keyString, PostProto.Post post) {
        ArrayList<byte[]> listAtKeyString = dataStore.get(keyString.getBytes());
        listAtKeyString.add(post.toByteArray());
        return listAtKeyString.size();
    }

    public long addNewPostList(String keyString, PostListProto.PostList postList) {
        ArrayList<byte[]> listAtKeyString = dataStore.get(keyString.getBytes());
        listAtKeyString.add(postList.toByteArray());
        return listAtKeyString.size();
    }

    public byte[] popOldestPost(String keyString) {
        ArrayList<byte[]> listAtKeyString = dataStore.get(keyString.getBytes());
        byte[] oldestPost = new byte[0];

        if (listAtKeyString.size() > 0) {
            oldestPost = listAtKeyString.get(0);
            listAtKeyString.remove(0);
        }
        return oldestPost;
    }

    public byte[] peekAt(String keyString) {
        return peekAt(keyString.getBytes());
    }

    public byte[] peekAt(byte[] key) {
        ArrayList<byte[]> listAtKeyString = dataStore.get(key);
        byte[] post = new byte[0];

        if (listAtKeyString.size() > 0) {
            post = listAtKeyString.get(0);
        }
        return post;
    }

}
