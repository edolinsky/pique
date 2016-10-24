package services.dataAccess;

import redis.clients.jedis.BinaryJedis;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostProto;
import java.util.List;

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

    public long addNewPost(String keyString, PostProto.Post post) {
        if (!redisAccess.isConnected()) {   // connect if needed
            redisAccess.connect();
        }
        // push post to right side of value list under key
        long result = redisAccess.rpush(keyString.getBytes(), post.toByteArray());

        redisAccess.disconnect();
        return result;
    }

    public long addNewPostList(String keyString, PostListProto.PostList postList){
        if (!redisAccess.isConnected()) {
            redisAccess.connect();
        }

        // push to left of value list under key
        long result = redisAccess.lpush(keyString.getBytes(), postList.toByteArray());

        redisAccess.disconnect();
        return result;
    }

    public byte[] popOldestPost(String keyString) {
        if(!redisAccess.isConnected()) {
            redisAccess.connect();
        }

        // pop from left of value list under key
        byte[] result = redisAccess.lpop(keyString.getBytes());

        redisAccess.disconnect();
        return result;
    }

    public byte[] peekAt(String keyString) {
        return peekAt(keyString.getBytes());
    }

    public byte[] peekAt(byte[] key) {
        if(!redisAccess.isConnected()) {
            redisAccess.connect();
        }

        byte[] entry = new byte[0];
        List<byte[]> entryList = redisAccess.lrange(key, 0, 0);

        redisAccess.disconnect();

        if (!entryList.isEmpty()) {     // if we found something, take the first element
            entry = entryList.get(0);
        }
        return entry;
    }

}
