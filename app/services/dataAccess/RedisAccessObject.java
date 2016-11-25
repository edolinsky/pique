package services.dataAccess;

import com.google.common.collect.Lists;
import com.google.protobuf.InvalidProtocolBufferException;
import play.Logger;
import redis.clients.jedis.*;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import javax.inject.Singleton;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static services.PublicConstants.DATA_SOURCE;
import static services.PublicConstants.REDIS_PORT;
import static services.PublicConstants.REDIS_URL;

@Singleton
public class RedisAccessObject extends AbstractDataAccess {

    private static JedisPool pool;
    private static final int KEY_TIMEOUT = 86400; // number of seconds from postList update or access to expiry

    public RedisAccessObject() {
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128);

        pool = new JedisPool(poolConfig, System.getenv(REDIS_URL));
    }

    @Override
    protected long addNewPost(String keyString, Post post) {
        long result;

        try (BinaryJedis redisAccess = pool.getResource()) {

            // push post to right side of value list under key
            result = redisAccess.rpush(keyString.getBytes(), post.toByteArray());
        }
        return result;
    }

    @Override
    protected long addNewPosts(String keyString, List<Post> listOfPosts) {
        long newLength;

        try (BinaryJedis redisAccess = pool.getResource()) {

            Pipeline pipe = redisAccess.pipelined();
            pipe.multi();

            for (Post post : listOfPosts) {
                pipe.rpush(keyString.getBytes(), post.toByteArray());
            }

            Response<List<Object>> results = pipe.exec();

            try {
                pipe.close();
            } catch (IOException IOe) {
                Logger.error("Problems closing Redis Pipe"); // todo: handle better
            }
            newLength = (long) results.get().size();
        }

        return newLength;
    }

    @Override
    protected Optional<Post> popFirstPost(String keyString) {
        Post oldestPost = null;
        byte[] result;

        try (BinaryJedis redisAccess = pool.getResource()) {
            // pop from left of value list under key
            result = redisAccess.lpop(keyString.getBytes());
        }

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
        List<byte[]> byteList;

        // connect to redis and obtain all posts under keystring (negative indexing for final post)
        try (BinaryJedis redisAccess = pool.getResource()) {
            byteList = redisAccess.lrange(keyString.getBytes(), 0, -1);
        }

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
        String returnString = "ERR";

        if (numPosts < 0) {
            return returnString; // todo: handle better
        }

        try (BinaryJedis redisAccess = pool.getResource()) {
            returnString = redisAccess.ltrim(keyString.getBytes(), numPosts, -1);
        }

        return returnString;
    }

    @Override
    protected long addNewPostList(String keyString, PostList postList) {
        long result;

        byte[] key = keyString.getBytes();

        try (BinaryJedis redisAccess = pool.getResource()) {

            // push to left of value list under key
            result = redisAccess.lpush(key, postList.toByteArray());

            // trim list to contain only the first MAX_POSTLISTS PostLists.
            redisAccess.ltrim(key, 0, MAX_POSTLISTS - 1);

            // (re)set TTL on key to KEY_TIMEOUT seconds from now
            redisAccess.expire(key, KEY_TIMEOUT);

        }

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
            Logger.warn("Invalid Post Protocol Buffer");
        }

        if (postList == null || index < 0) {
            return Optional.empty();
        } else {
            return Optional.of(postList);
        }
    }

    @Override
    protected List<PostList> getAllPostLists(String keyString) {
        List<byte[]> byteList;

        // get all posts under a particular key (-1 refers to the last post in list)
        try (BinaryJedis redisAccess = pool.getResource()) {
            byteList = redisAccess.lrange(keyString.getBytes(), 0, -1);
        }

        List<PostList> listOfPostLists = new ArrayList<>();

        // parse each post returned into Post objects
        for (byte[] bytes : byteList) {
            try {
                PostList postList = PostList.parseFrom(bytes);
                listOfPostLists.add(postList);
            } catch (InvalidProtocolBufferException iPBE) {
                // todo: better error handling
                Logger.warn("Invalid PostList Protocol Buffer");
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
        try (BinaryJedis redisAccess = pool.getResource()) {
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

        }

        // return sum of number of posts at each matching key
        return responseList.stream().mapToLong(Response::get).sum();

    }

    @Override
    public List<String> getKeysInNameSpace(String nameSpace) {
        Set<byte[]> byteList;

        // retrieve set of keys matching matchString
        try (BinaryJedis redisAccess = pool.getResource()) {

            // match all keys with namespace by using wildcard '*'
            byteList = redisAccess.keys((nameSpace + NAMESPACE_DELIMITER + "*").getBytes());
        }

        // convert set of bytes to list of strings and return
        return byteList.stream().map(String::new).collect(Collectors.toList());
    }

    @Override
    protected long getListSize(String keyString) {
        long size;

        try (BinaryJedis redisAccess = pool.getResource()) {
            size = redisAccess.llen(keyString.getBytes());
        }

        return size;
    }

    @Override
    protected List<String> getStringList(String keyString, long length) {
        List<byte[]> byteList;
        byte[] key = keyString.getBytes();

        try (BinaryJedis redisAccess = pool.getResource()) {

            // obtain max length of request, and scale to max length if needed
            long listLen = redisAccess.llen(key);
            if (length > listLen) {
                length = listLen;
            }

            byteList = redisAccess.lrange(keyString.getBytes(), 0, length);
        }

        // convert list of byte arrays to list of strings and return
        return byteList.stream().map(String::new).collect(Collectors.toList());

    }

    @Override
    protected long replaceStringList(String keyString, List<String> stringList) {
        int listLength = stringList.size();
        byte[] key = keyString.getBytes();
        List<String> reversedStringList = Lists.reverse(stringList);

        try (BinaryJedis redisAccess = pool.getResource()) {

            Pipeline pipe = redisAccess.pipelined();
            pipe.multi();

            // push strings in reverse order (so first is at front)
            for (String s : reversedStringList) {
                pipe.lpush(key, s.getBytes());
            }

            pipe.exec();

            try {
                pipe.close();
            } catch (IOException IOe) {
                Logger.error("Problems closing Redis Pipe"); // todo: handle better
            }

            // delete old entries, so that only new string list remains
            redisAccess.ltrim(key, 0, listLength - 1);
        }

        return listLength;
    }

    @Override
    protected long replacePostLists(String keyString, List<PostList> postLists) {
        int listLength = postLists.size();
        byte[] key = keyString.getBytes();
        List<PostList> reversedPostList = Lists.reverse(postLists);
        try (BinaryJedis redisAccess = pool.getResource()) {

            Pipeline pipe = redisAccess.pipelined();
            pipe.multi();

            // push postlists in reverse order (so first is at top of stack)
            for (PostList pl : reversedPostList) {
                pipe.lpush(key, pl.toByteArray());
            }

            pipe.exec();

            try {
                pipe.close();
            } catch (IOException IOe) {
                Logger.error("Problems closing Redis Pipe"); // todo: handle better
            }

            // delete old entries, so that only new postLists remain
            redisAccess.ltrim(key, 0, listLength - 1);
        }

        return listLength;
    }

    /**
     * Retrieves the byte array stored at the specified index in Redis under keyString. The byte array can then be
     * parsed into either a Post or PostList.
     *
     * @param keyString string corresponding to key in Redis (complete with namespace and delimiter)
     * @param index     desired index of list under keyString
     * @return Optional of byte array stored at index, empty if not found.
     */
    private Optional<byte[]> getByte(String keyString, Integer index) {
        List<byte[]> entryList;
        byte[] key = keyString.getBytes();

        try (BinaryJedis redisAccess = pool.getResource()) {// connect to redis and get element under key at index
            entryList = redisAccess.lrange(key, index, index);
            redisAccess.expire(key, KEY_TIMEOUT); // reset key timeout to KEY_TIMEOUT seconds from access
        }

        if (entryList.isEmpty() || index < 0) {     // if we found something, take the first element
            return Optional.empty();
        } else {
            return Optional.of(entryList.get(0));
        }
    }

}
