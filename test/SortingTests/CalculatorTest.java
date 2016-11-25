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

        assertEquals(Integer.MAX_VALUE, post.getPopularityScore());
    }

    @Test
    public void testCalculatePopularityOfAllPostsPositiveCase() {
        int numPosts = 10;
        List<Post> posts = generateListOfPosts(numPosts);

        posts = calc.calculatePopularityScoreOfAllPosts(posts);

        for (Post post : posts) {
            assertTrue(post.hasField(Post.getDescriptor().findFieldByNumber(Post.POPULARITY_SCORE_FIELD_NUMBER)));
        }
    }

    @Test
    public void testCalculatePopularityOfAllPostsEmptyInput() {
        List<Post> posts = calc.calculatePopularityScoreOfAllPosts(Collections.emptyList());

        assertEquals(Collections.emptyList(), posts);
    }

}
