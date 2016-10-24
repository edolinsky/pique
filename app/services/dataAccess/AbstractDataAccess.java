package services.dataAccess;

import org.springframework.stereotype.Service;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostProto;

/**
 * Created by erik on 23/10/16.
 */

public abstract class AbstractDataAccess {
    abstract public long addNewPost(String keyString, PostProto.Post post);
    abstract public long addNewPostList(String keyString, PostListProto.PostList postList);
    abstract public byte[] popOldestPost(String keyString);
    abstract public byte[] peekAt(String keyString);
    abstract public byte[] peekAt(byte[] key);
}
