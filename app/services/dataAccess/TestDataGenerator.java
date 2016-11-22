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

        for (int i = 0; i < numPosts; i++) {
            Post.Builder builder = Post.newBuilder();
            builder.setId(String.valueOf(i));
            builder.setTimestamp(DateTime.now().toDate().getTime());
            builder.addSource("Test Source");
            builder.addSourceLink("http://google.com");
            builder.addImgLink("http://image.com");
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
            tags.add(randomString("#", randomWordLength()));
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
