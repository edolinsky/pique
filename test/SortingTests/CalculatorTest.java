package SortingTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.Calculator;

import java.util.Collections;
import java.util.List;

import static services.dataAccess.TestDataGenerator.generateListOfPosts;

public class CalculatorTest {

    private Calculator calc;

    @Before
    public void calculatorTestClassSetup() {
        calc = new Calculator();
    }

    @Test
    public void testCalculatePopularityAndRebuild() {
        Post post = generateListOfPosts(1).get(0);
        post = calc.calculatePopularityAndRebuild(post);

        // post should have popularity score
        assertTrue(post.hasField(Post.getDescriptor().findFieldByNumber(Post.POPULARITY_SCORE_FIELD_NUMBER)));
    }

    @Test
    public void testCalculatePopularityPastLowerBound() {
        Post post = generateListOfPosts(1).get(0);
        post = post.toBuilder()
                .setNumComments(0)
                .setNumLikes(0)
                .setNumShares(-1)
                .build();

        post = calc.calculatePopularityAndRebuild(post);

        // popularity score should be bounded to 0
        assertEquals(0, post.getPopularityScore());

    }

    @Test
    public void testCalculatePopularityAtLowerBound() {
        Post post = generateListOfPosts(1).get(0);
        post = post.toBuilder()
                .setNumComments(0)
                .setNumLikes(0)
                .setNumShares(0)
                .build();

        post = calc.calculatePopularityAndRebuild(post);

        // popularity score should be 0
        assertEquals(0, post.getPopularityScore());
    }

    @Test
    public void testCalculatePopularityAboveLowerBound() {
        Post post = generateListOfPosts(1).get(0);
        post = post.toBuilder()
                .setNumComments(0)
                .setNumShares(1)
                .setNumLikes(0)
                .build();

        post = calc.calculatePopularityAndRebuild(post);

        // popularity score should be reasonably small
        int popularityScore = post.getPopularityScore();
        assertTrue(0 <= popularityScore && popularityScore <= 5);

    }

    @Test
    public void testCalculatePopularityPastUpperBound() {
        Post post = generateListOfPosts(1).get(0);
        post = post.toBuilder()
                .setNumComments(Integer.MAX_VALUE)
                .setNumLikes(Integer.MAX_VALUE)
                .setNumShares(Integer.MAX_VALUE)
                .build();

        post = calc.calculatePopularityAndRebuild(post);

        // popularity score should be bounded to int max value
        assertEquals(Integer.MAX_VALUE, post.getPopularityScore());
    }

    @Test
    public void testCalculatePopularityOfAllPostsPositiveCase() {
        int numPosts = 10;
        List<Post> posts = generateListOfPosts(numPosts);

        posts = calc.calculatePopularityScoreOfAllPosts(posts);

        // all posts should be rebuilt with popularity score
        for (Post post : posts) {
            assertTrue(post.hasField(Post.getDescriptor().findFieldByNumber(Post.POPULARITY_SCORE_FIELD_NUMBER)));
        }
    }

    @Test
    public void testCalculatePopularityOfAllPostsEmptyInput() {
        List<Post> posts = calc.calculatePopularityScoreOfAllPosts(Collections.emptyList());

        // empty input gives empty output
        assertEquals(Collections.emptyList(), posts);
    }

    @Test
    public void testCalculatePopularityVelocityIncreasing() {
        int newPopScore = 10;
        int oldPopScore = 5;
        List<Post> posts = generateListOfPosts(1);

        Post newPost = posts.get(0).toBuilder().setPopularityScore(newPopScore).build();
        Post oldPost = posts.get(0).toBuilder().setPopularityScore(oldPopScore).build();

        // increase in popularity score gives positive popularity velocity
        assertEquals(newPopScore - oldPopScore, calc.calculatePopularityVelocity(newPost, oldPost).getPopularityVelocity());
    }

    @Test
    public void testCalculatePopularityVelocityDecreasing() {
        int newPopScore = 5;
        int oldPopScore = 10;
        List<Post> posts = generateListOfPosts(1);

        Post newPost = posts.get(0).toBuilder().setPopularityScore(newPopScore).build();
        Post oldPost = posts.get(0).toBuilder().setPopularityScore(oldPopScore).build();

        // decrease in popularity score gives negative popularity velocity
        assertEquals(newPopScore - oldPopScore, calc.calculatePopularityVelocity(newPost, oldPost).getPopularityVelocity());
    }

    @Test
    public void testCalculatePopularityVelocityZero() {
        int popScore = 1;
        List<Post> posts = generateListOfPosts(1);

        Post newPost = posts.get(0).toBuilder().setPopularityScore(popScore).build();
        Post oldPost = posts.get(0).toBuilder().setPopularityScore(popScore).build();

        // same popularity score gives 0 popularity velocity
        assertEquals(0, calc.calculatePopularityVelocity(newPost, oldPost).getPopularityVelocity());
    }

}
