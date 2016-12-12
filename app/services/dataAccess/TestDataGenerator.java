package services.dataAccess;

import org.joda.time.DateTime;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Random;

public class TestDataGenerator {
    private static Random rand = new Random(new Date().getTime());
    private static int ASCII_LOWER_A = 97;
    private static int ALPHABET_LENGTH = 26;

    public static PostList generatePostList(int numPosts) {
        PostList.Builder postListBuilder = PostList.newBuilder();
        generateListOfPosts(numPosts).forEach(postListBuilder::addPosts);
        return postListBuilder.build();
    }

    public static List<Post> generateListOfPosts(int numPosts) {

        List<Post> posts = new ArrayList<>();

        Post.Builder test1Builder = Post.newBuilder();
        test1Builder.setId("0");
        test1Builder.setTimestamp(DateTime.now().toDate().getTime());
        test1Builder.addSource("TestSourceWithAReallyReallyReallyReallyLongName");
        test1Builder.addSourceLink("https://twitter.com");
        test1Builder.addImgLink("https://pbs.twimg.com/media/Cxu8dwsWIAUgzJK.jpg");
        test1Builder.setPopularityScore(0);
        test1Builder.setPopularityVelocity(0);
        test1Builder.setNumComments(966);
        test1Builder.setNumShares(23);
        test1Builder.setNumLikes(100271);
        test1Builder.addHashtag("Friday");
        test1Builder.addHashtag("win");
        test1Builder.addHashtag("BlackFriday");
        test1Builder.addHashtag("shopping");
        test1Builder.addHashtag("ContestAlert");
        test1Builder.addHashtag("giveaway");
        test1Builder.addHashtag("sweepstakes");
        test1Builder.addText("RT @mommymakestime: Happy #Friday! Enter to #win 130$ Paypal/Amazon GC for #BlackFriday #shopping! #ContestAlert #giveaway #sweepstakes htt…");
        posts.add(test1Builder.build());

        for (int i = 1; i < numPosts; i++) {
            Post.Builder builder = Post.newBuilder();
            builder.setId(String.valueOf(i));
            builder.setTimestamp(DateTime.now().toDate().getTime());
            builder.addSource("Test Source With A Really Really Really Really Long Name");
            builder.addSourceLink("http://imgur.com");
            builder.addImgLink("N/A");
            builder.setPopularityScore(0);
            builder.setPopularityVelocity(0);
            builder.setNumComments(rand.nextInt(1000));
            builder.setNumShares(rand.nextInt(1000));
            builder.setNumLikes(rand.nextInt(1000));
            randomHashtags().stream().forEach(builder::addHashtag);
            builder.addText(randomSentence(5));

            posts.add(builder.build());
        }

        return posts;
    }

    public static List<String> randomHashtags() {

        int numTags = rand.nextInt(5);

        if (!(numTags > 0)) {
            return Collections.emptyList();
        }

        List<String> tags = new ArrayList<>();
        for (int i = 0; i < numTags; i++) {
            tags.add(randomString("", randomWordLength()));
        }

        return tags;
    }

    public static String randomString(String prefix, int numLetters) {
        StringBuilder sb = new StringBuilder();
        sb.append(prefix);
        for (int i = 0; i < numLetters; i++) {
            sb.append((char) (rand.nextInt(ALPHABET_LENGTH) + ASCII_LOWER_A));
        }

        return sb.toString();

    }

    public static String randomSentence(int numWords) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < numWords; i++) {
            sb.append(randomString("", randomWordLength()));
            sb.append(" ");
        }

        return sb.toString().trim();
    }

    public static int randomWordLength() {
        return (rand.nextInt(8) + 2);
    }
}
