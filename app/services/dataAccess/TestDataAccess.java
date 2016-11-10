package services.dataAccess;

import org.joda.time.DateTime;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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

    public TestDataAccess() {
        PostList.Builder postListBuilder = PostList.newBuilder();

        for (int i = 0; i < 50; i++) {
            Post.Builder builder = Post.newBuilder();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            builder.setId(String.valueOf(i));
            builder.setTimestamp(df.format(DateTime.now().toDate()));
            builder.addSource("Entertainment Weekly");
            builder.addSourceLink("https://twitter.com");
            builder.addImgLink("http://image.com");
            builder.setPopularityScore(0);
            builder.setPopularityVelocity(0);
            builder.setNumComments(966);
            builder.setNumShares(23);
            builder.setNumLikes(1201);
            builder.addHashtag("#EW");
            builder.addHashtag("#election");
            builder.addText("Supernatural's Misha Collins tears up talking about election results: 'I am not going to give up'");
            postListBuilder.addPosts(builder.build());
        }

        testOptPostList = Optional.of(postListBuilder.build());

    }

    public long addNewPost(String keyString, Post post) {
        return x;
    }

    public long addNewPosts(String keyString, List<Post> listOfPosts) {
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
