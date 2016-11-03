package services.dataAccess;

import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.List;
import java.util.Optional;

/**
 * Created by erik on 23/10/16.
 */

public abstract class AbstractDataAccess {

    abstract public long addNewPost(String keyString, Post post);
    abstract public long addNewPosts(String keyString, List<Post> listOfPosts);
    abstract public long addNewPostList(String keyString, PostList postList);

    abstract public List<Post> getAllPosts(String keyString);
    abstract public Optional<Post> popOldestPost(String keyString);
    abstract public Optional<PostList> peekAtPostList(String keyString);
}
