package services.dataAccess;

import com.google.protobuf.InvalidProtocolBufferException;
import redis.clients.jedis.BinaryJedis;
import redis.clients.jedis.Pipeline;
import redis.clients.jedis.Response;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostProto;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Created by erik on 23/10/16.
 */

public class RedisAccessObject extends AbstractDataAccess {
    private BinaryJedis redisAccess;

    public RedisAccessObject() {
        String redisUrl = System.getenv("redis_url");
        Integer redisPort = Integer.getInteger(System.getenv("redis_port"));
        redisAccess = new BinaryJedis(redisUrl, redisPort);
    }

    @Override
    public long addNewPost(String keyString, PostProto.Post post) {
        if (!redisAccess.isConnected()) {   // connect if needed
            redisAccess.connect();
        }
        // push post to right side of value list under key
        long result = redisAccess.rpush(keyString.getBytes(), post.toByteArray());

        redisAccess.disconnect();
        return result;
    }

    @Override
    public long addNewPosts(String keyString, List<PostProto.Post> listOfPosts) {

        Pipeline pipe = redisAccess.pipelined();

        for (PostProto.Post post : listOfPosts) {
            pipe.rpush(keyString.getBytes(), post.toByteArray());
        }
        // Response<byte[]> pipeId = pipe.get(keyString.getBytes());
        Response<List<Object>> results = pipe.exec();

        return (long) results.get().size();
    }

    @Override
    public long addNewPostList(String keyString, PostListProto.PostList postList){
        if (!redisAccess.isConnected()) {
            redisAccess.connect();
        }

        // push to left of value list under key
        long result = redisAccess.lpush(keyString.getBytes(), postList.toByteArray());

        redisAccess.disconnect();
        return result;
    }

    @Override
    public Optional<PostProto.Post> popOldestPost(String keyString) {
        PostProto.Post oldestPost = null;

        // connect to redis
        if(!redisAccess.isConnected()) {
            redisAccess.connect();
        }

        // pop from left of value list under key
        byte[] result = redisAccess.lpop(keyString.getBytes());

        redisAccess.disconnect();   // disconnect from redis

        // parse Post object from byte array
        try {
            oldestPost = PostProto.Post.parseFrom(result);
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

    @Override
    public Optional<byte[]> peekAtByte(String keyString) {
        return peekAtByte(keyString.getBytes());
    }

    @Override
    public Optional<byte[]> peekAtByte(byte[] key) {
        if(!redisAccess.isConnected()) {
            redisAccess.connect();
        }

        Optional<byte[]> entry = Optional.of(new byte[0]);
        List<byte[]> entryList = redisAccess.lrange(key, 0, 0);

        redisAccess.disconnect();

        if (!entryList.isEmpty()) {     // if we found something, take the first element
            entry = Optional.of(entryList.get(0));
        }
        return entry;
    }

    @Override
    public Optional<PostListProto.PostList> peekAtPostList(String keyString) {
        PostListProto.PostList postList = null;

        try {
            Optional<byte[]> postListByte = peekAtByte(keyString);
            if (postListByte.isPresent()) {
                postList = PostListProto.PostList.parseFrom(postListByte.get());
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
