package SortingTests;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.Calculator;

import static org.junit.Assert.assertTrue;
import static services.dataAccess.TestDataGenerator.generateListOfPosts;

public class CalculatorTest {

    private Calculator calc;

    @Before
    public void calculatorTestClassSetup() {
        calc = new Calculator();
    }

    @Test
    public void testCalculatePopularity() {
        Post post = generateListOfPosts(1).get(0);
        Post post2 = calc.calculatePopularityAndRebuild(post);

        assertTrue(post2.hasField(Post.getDescriptor().findFieldByNumber(Post.POPULARITY_SCORE_FIELD_NUMBER)));
    }
}
