package services.dataAccess;

import com.google.protobuf.InvalidProtocolBufferException;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by erik on 23/10/16.
 */

public class RedisAccessObject extends AbstractDataAccess {

    private BinaryJedis redisAccess;

    public RedisAccessObject() {

        // initialize based on environment variables
        String redisUrl = System.getenv("redis_url");
        Integer redisPort = Integer.getInteger(System.getenv("redis_port"));
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

        for (Post post : listOfPosts) {
            pipe.rpush(keyString.getBytes(), post.toByteArray());
        }
        // Response<byte[]> pipeId = pipe.get(keyString.getBytes());
        Response<List<Object>> results = pipe.exec();

        disconnect();

        return (long) results.get().size();
    }

    @Override
    public long addNewPostList(String keyString, PostList postList){

        connect();

        // push to left of value list under key
        long result = redisAccess.lpush(keyString.getBytes(), postList.toByteArray());
        disconnect();

        return result;
    }

    @Override
    public List<Post> getAllPosts(String keyString) {

        connect();  // get all posts under a particular key (denoted by range 0 to -1)
        List<byte[]> byteList= redisAccess.lrange(keyString.getBytes(), 0, -1);
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

        // parse Post object from byte array
        try {
            oldestPost = Post.parseFrom(result);
        } catch (InvalidProtocolBufferException iPBE) {
            //todo: handle this more elegantly
            System.out.println("Invalid Protocol Buffer");
        }

        // handle negative case and return
        if (oldestPost == null) {
            return Optional.empty();
        } else {
            return Optional.of(oldestPost);
        }
    }

    private Optional<byte[]> peekAtByte(String keyString) {

        Optional<byte[]> entry = Optional.of(new byte[0]);
        byte[] key = keyString.getBytes();

        connect();  // connect to redis and get first element under key
        List<byte[]> entryList = redisAccess.lrange(key, 0, 0);
        disconnect();

        if (!entryList.isEmpty()) {     // if we found something, take the first element
            entry = Optional.of(entryList.get(0));
        }
        return entry;
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

}
