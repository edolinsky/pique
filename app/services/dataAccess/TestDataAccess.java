package services.dataAccess;

import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.List;
import java.util.Optional;

/**
 * Created by maria on 04/11/16.
 */

public class TestDataAccess extends AbstractDataAccess {

  private long x = 1;

  private List<Post> testPostList;
  private Optional<Post> testOptPost;
  private Optional<PostList> testOptPostList;

  public long addNewPost(String keyString, Post post) {
    return x;
  }

  public long addNewPosts(String keyString, List<Post> listOfPosts){
    return x;
  }

  public long addNewPostList(String keyString, PostList postList) {
    return x;
  }

  public List<Post> getAllPosts(String keyString) {
    return testPostList;
  }

  public Optional<Post> popOldestPost(String keyString) {
    return testOptPost;
  }

  public Optional<PostList> peekAtPostList(String keyString) {
    return testOptPostList;
  }
}
