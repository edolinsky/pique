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

// For reference:

/*  private class TestPost {
      String id = "1";
      String timestamp = "2";

      String source = "3";
      String source_link = "4";

      int popularity_score = 5;
      int popularity_velocity = 6;

      int num_comments = 7;
      int num_shares = 8;
      int num_likes = 9;
      String hashtag = "Hey";

      String text = "This is the text in the post. Woohoo!";
      String img_link = null;
      String ext_link = null;
  } */

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
    Post post = Post.newBuilder()
      .setId("1")
      .setTimestamp("2016-11-08 19:25:00")
      .setPopularityScore(10)
      .build();

    PostList list = PostList.newBuilder()
      .setPosts(0, post)
      .build();

    if (list == null) {
        return Optional.empty();
    } else {
        return Optional.of(list);
    }
  }
}
