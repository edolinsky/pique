package services.dataAccess;

import org.springframework.stereotype.Service;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostProto;

import java.util.List;
import java.util.Optional;

/**
 * Created by erik on 23/10/16.
 */

public abstract class AbstractDataAccess {
    abstract public long addNewPost(String keyString, PostProto.Post post);
    abstract public long addNewPosts(String keyString, List<PostProto.Post> listOfPosts);
    abstract public long addNewPostList(String keyString, PostListProto.PostList postList);
    abstract public Optional<PostProto.Post> popOldestPost(String keyString);
    abstract public Optional<PostListProto.PostList> peekAtPostList(String keyString);
    abstract public Optional<byte[]> peekAtByte(String keyString);
    abstract public Optional<byte[]> peekAtByte(byte[] key);
}
