package services.dataAccess;

import org.joda.time.DateTime;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto;
import services.dataAccess.proto.PostProto.Post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
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

        Post.Builder test1Builder = Post.newBuilder();
        DateFormat test1Df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        test1Builder.setId("0");
        test1Builder.setTimestamp(test1Df.format(DateTime.now().toDate()));
        test1Builder.addSource("Alexs_tweets22");
        test1Builder.addSourceLink("https://twitter.com");
        test1Builder.addImgLink("https://pbs.twimg.com/media/Cxu8dwsWIAUgzJK.jpg");
        test1Builder.setPopularityScore(0);
        test1Builder.setPopularityVelocity(0);
        test1Builder.setNumComments(966);
        test1Builder.setNumShares(23);
        test1Builder.setNumLikes(1201);
        test1Builder.addHashtag("Friday");
        test1Builder.addHashtag("win");
        test1Builder.addHashtag("BlackFriday");
        test1Builder.addHashtag("shopping");
        test1Builder.addHashtag("ContestAlert");
        test1Builder.addHashtag("giveaway");
        test1Builder.addHashtag("sweepstakes");
        test1Builder.addText("RT @mommymakestime: Happy #Friday! Enter to #win 130$ Paypal/Amazon GC for #BlackFriday #shopping! #ContestAlert #giveaway #sweepstakes htt…");
        postListBuilder.addPosts(test1Builder.build());

        for (int i = 1; i < 49; i++) {
            Post.Builder builder = Post.newBuilder();
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            builder.setId(String.valueOf(i));
            builder.setTimestamp(df.format(DateTime.now().toDate()));
            builder.addSource("Entertainment Weekly");
            builder.addSourceLink("https://twitter.com");
            builder.addImgLink("N/A");
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

        testOptPostList = Optional.of(TestDataGenerator.generatePostList(50));
    }

    public long addNewPost(String keyString, Post post) {
        return x;
    }

    public long addNewPosts(String keyString, List<Post> listOfPosts) {
        return x;
    }

    @Override
    protected Optional<Post> popFirstPost(String keyString) {
        return null;
    }

    public long addNewPostList(String keyString, PostList postList) {
        return x;
    }

    public List<Post> getAllPosts(String keyString) {
        return testPostList;
    }

    @Override
    protected Optional<PostList> getPostList(String keyString, Integer index) {
        return null;
    }

    @Override
    protected List<PostList> getAllPostLists(String keyString) {
        return Collections.singletonList(testOptPostList.get());
    }

    @Override
    protected String deleteFirstNPosts(String keyString, Integer numPosts) {
        return null;
    }

    @Override
    public long getNumPostsInNameSpace(String nameSpace) {
        return 0;
    }

    @Override
    public List<String> getKeysInNameSpace(String nameSpace) {
        return null;
    }

    public Optional<Post> popOldestPost(String keyString) {
        return testOptPost;
    }

    public Optional<PostList> peekAtPostList(String keyString) {
        return testOptPostList;
    }

}
