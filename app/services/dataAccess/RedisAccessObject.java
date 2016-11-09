package services.dataAccess;

import com.google.protobuf.InvalidProtocolBufferException;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static services.PublicConstants.DATA_SOURCE;
import static services.PublicConstants.REDIS_PORT;
import static services.PublicConstants.REDIS_URL;

/**
 * Created by erik on 23/10/16.
 */

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
    public long addNewPost(String keyString, Post post) {

        connect();

        // push post to right side of value list under key
        long result = redisAccess.rpush(keyString.getBytes(), post.toByteArray());
        disconnect();

        return result;
    }

    @Override
    public long addNewPosts(String keyString, List<Post> listOfPosts) {

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
            System.out.println("Problems closing Redis Pipe"); // todo: handle better
        }

        disconnect();

        return (long) results.get().size();
    }

    @Override
    public long addNewPostList(String keyString, PostList postList) {

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
    public List<Post> getAllPosts(String keyString) {

        connect();  // get all posts under a particular key (denoted by range 0 to -1)
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
                System.out.println("Invalid Protocol Buffer");
            }
        }

        return postList;
    }

    @Override
    public Optional<Post> popOldestPost(String keyString) {
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
                System.out.println("Invalid Protocol Buffer");
            }

        }

        // handle negative case and return
        if (oldestPost == null) {
            return Optional.empty();
        } else {
            return Optional.of(oldestPost);
        }
    }

    private Optional<byte[]> peekAtByte(String keyString) {

        byte[] key = keyString.getBytes();

        connect();  // connect to redis and get first element under key
        List<byte[]> entryList = redisAccess.lrange(key, 0, 0);
        redisAccess.expire(key, KEY_TIMEOUT); // reset key timeout to KEY_TIMEOUT seconds from access
        disconnect();

        if (entryList.isEmpty()) {     // if we found something, take the first element
            return Optional.empty();
        } else {
            return Optional.of(entryList.get(0));
        }
    }

    @Override
    public Optional<PostList> peekAtPostList(String keyString) {
        PostList postList = null;

        try {
            Optional<byte[]> postListByte = peekAtByte(keyString);
            if (postListByte.isPresent()) {
                postList = PostList.parseFrom(postListByte.get());
            }
        } catch (InvalidProtocolBufferException iPBE) {
            //todo: handle this more elegantly.
            System.out.println("Invalid Protocol Buffer");
        }

        if (postList == null) {
            return Optional.empty();
        } else {
            return Optional.of(postList);
        }
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
            System.out.println("Problems closing Redis Pipe"); // todo: handle better
        }

        disconnect();

        // return sum of number of posts at each matching key
        return responseList.stream().mapToLong(Response::get).sum();

    }

    @Override
    public List<String> getKeysInNameSpace(String matchString) {

        // retrieve set of keys matching matchString
        connect();
        Set<byte[]> byteList = redisAccess.keys((matchString + NAMESPACE_DELIMITER + "*").getBytes());
        disconnect();

        // convert set of bytes to list of strings and return
        return byteList.stream().map(String::new).collect(Collectors.toList());
    }

    @Override
    public String deleteFirstNPosts(String keyString, Integer numPosts) {

        connect();
        String returnString = redisAccess.ltrim(keyString.getBytes(), numPosts, -1);
        disconnect();

        return returnString;
    }

}
