package services.dataAccess;

import com.google.protobuf.InvalidProtocolBufferException;
import play.Logger;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static services.PublicConstants.DATA_SOURCE;
import static services.PublicConstants.REDIS_PORT;
import static services.PublicConstants.REDIS_URL;

/**
 * Created by erik on 23/10/16.
 */

@Singleton
public class RedisAccessObject extends AbstractDataAccess {

    private BinaryJedis redisAccess;
    private static final int KEY_TIMEOUT = 86400; // number of seconds from postList update or access to expiry

    public RedisAccessObject() {

        // initialize based on environment variables
        String redisUrl = System.getenv(REDIS_URL);
        Integer redisPort = Integer.valueOf(System.getenv(REDIS_PORT));
        redisAccess = new BinaryJedis(redisUrl, redisPort);
    }

    private void connect() {
        // todo: better return type, possible error handling
        if (!redisAccess.isConnected()) {
            redisAccess.connect();
        }
    }

    private void disconnect() {
        // todo: better return type, possible error handling
        redisAccess.disconnect();
    }

    @Override
    protected long addNewPost(String keyString, Post post) {

        connect();

        // push post to right side of value list under key
        long result = redisAccess.rpush(keyString.getBytes(), post.toByteArray());
        disconnect();

        return result;
    }

    @Override
    protected long addNewPosts(String keyString, List<Post> listOfPosts) {

        connect();
        Pipeline pipe = redisAccess.pipelined();
        pipe.multi();

        for (Post post : listOfPosts) {
            pipe.rpush(keyString.getBytes(), post.toByteArray());
        }
        // Response<byte[]> pipeId = pipe.get(keyString.getBytes());
        Response<List<Object>> results = pipe.exec();

        try {
            pipe.close();
        } catch (IOException IOe) {
            Logger.error("Problems closing Redis Pipe"); // todo: handle better
        }

        disconnect();

        return (long) results.get().size();
    }

    @Override
    protected Optional<Post> popFirstPost(String keyString) {
        Post oldestPost = null;

        connect();

        // pop from left of value list under key
        byte[] result = redisAccess.lpop(keyString.getBytes());

        disconnect();   // disconnect from redis

        if (result != null) {
            // parse Post object from byte array
            try {
                oldestPost = Post.parseFrom(result);
            } catch (InvalidProtocolBufferException iPBE) {
                //todo: handle this more elegantly
                Logger.warn("Invalid Protocol Buffer");
            }

        }

        // handle negative case and return
        if (oldestPost == null) {
            return Optional.empty();
        } else {
            return Optional.of(oldestPost);
        }
    }

    @Override
    protected List<Post> getAllPosts(String keyString) {

        connect();  // get all posts under a particular key (-1 refers to the last post in list)
        List<byte[]> byteList = redisAccess.lrange(keyString.getBytes(), 0, -1);
        disconnect();

        List<Post> postList = new ArrayList<>();

        // parse each post returned into Post objects
        for (byte[] bytes : byteList) {
            try {
                Post post = Post.parseFrom(bytes);
                postList.add(post);
            } catch (InvalidProtocolBufferException iPBE) {
                // todo: better error handling
                Logger.warn("Invalid Protocol Buffer");
            }
        }

        return postList;
    }

    @Override
    protected String deleteFirstNPosts(String keyString, Integer numPosts) {

        connect();
        String returnString = redisAccess.ltrim(keyString.getBytes(), numPosts, -1);
        disconnect();

        return returnString;
    }

    @Override
    protected long addNewPostList(String keyString, PostList postList) {

        byte[] key = keyString.getBytes();

        connect();

        // push to left of value list under key
        long result = redisAccess.lpush(key, postList.toByteArray());

        // trim list to contain only the first MAX_POSTLISTS PostLists.
        redisAccess.ltrim(key, 0, MAX_POSTLISTS - 1);

        // (re)set TTL on key to KEY_TIMEOUT seconds from now
        redisAccess.expire(key, KEY_TIMEOUT);

        disconnect();

        return result;
    }

    @Override
    protected Optional<PostList> getPostList(String keyString, Integer index) {
        PostList postList = null;

        try {
            Optional<byte[]> postListByte = getByte(keyString, index);
            if (postListByte.isPresent()) {
                postList = PostList.parseFrom(postListByte.get());
            }
        } catch (InvalidProtocolBufferException iPBE) {
            //todo: handle this more elegantly.
            Logger.warn("Invalid Protocol Buffer");
        }

        if (postList == null) {
            return Optional.empty();
        } else {
            return Optional.of(postList);
        }
    }

    @Override
    protected List<PostList> getAllPostLists(String keyString) {
        connect();  // get all posts under a particular key (-1 refers to the last post in list)
        List<byte[]> byteList = redisAccess.lrange(keyString.getBytes(), 0, -1);
        disconnect();

        List<PostList> listOfPostLists = new ArrayList<>();

        // parse each post returned into Post objects
        for (byte[] bytes : byteList) {
            try {
                PostList postList = PostList.parseFrom(bytes);
                listOfPostLists.add(postList);
            } catch (InvalidProtocolBufferException iPBE) {
                // todo: better error handling
                Logger.warn("Invalid Protocol Buffer");
            }
        }

        return listOfPostLists;
    }

    @Override
    public long getNumPostsInNameSpace(String nameSpace) {

        // get all keys matching namespace (with delimiter)
        List<String> keysInNameSpace = getKeysInNameSpace(nameSpace);
        List<Response<Long>> responseList = new ArrayList<>();

        // Connect to redis, with pipelined queries
        connect();
        Pipeline pipe = redisAccess.pipelined();
        pipe.multi();

        // get number of posts at each key in namespace, with responses stored in responseList
        responseList.addAll(keysInNameSpace.stream().map(keyString -> pipe.llen(keyString.getBytes())).collect(Collectors.toList()));

        // execute queries and disconnect
        pipe.exec();

        try {
            pipe.close();
        } catch (IOException IOe) {
            Logger.error("Problems closing Redis Pipe"); // todo: handle better
        }

        disconnect();

        // return sum of number of posts at each matching key
        return responseList.stream().mapToLong(Response::get).sum();

    }

    @Override
    public List<String> getKeysInNameSpace(String nameSpace) {

        // retrieve set of keys matching matchString
        connect();

        // match all keys with namespace by using wildcard '*'
        Set<byte[]> byteList = redisAccess.keys((nameSpace + NAMESPACE_DELIMITER + "*").getBytes());
        disconnect();

        // convert set of bytes to list of strings and return
        return byteList.stream().map(String::new).collect(Collectors.toList());
    }

    /**
     * Retrieves the byte array stored at the specified index in Redis under keyString. The byte array can then be
     * parsed into either a Post or PostList.
     *
     * @param keyString string corresponding to key in Redis (complete with namespace and delimiter)
     * @param index desired index of list under keyString
     * @return Optional of byte array stored at index, empty if not found.
     */
    private Optional<byte[]> getByte(String keyString, Integer index) {

        byte[] key = keyString.getBytes();

        connect();  // connect to redis and get element under key at index
        List<byte[]> entryList = redisAccess.lrange(key, index, index);
        redisAccess.expire(key, KEY_TIMEOUT); // reset key timeout to KEY_TIMEOUT seconds from access
        disconnect();

        if (entryList.isEmpty()) {     // if we found something, take the first element
            return Optional.empty();
        } else {
            return Optional.of(entryList.get(0));
        }
    }

}
