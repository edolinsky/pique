package SortingTests;

import org.junit.Before;
import org.junit.Test;

import services.dataAccess.proto.PostProto.Post;
import services.sorting.Calculator;

import static org.junit.Assert.assertTrue;
import static services.dataAccess.TestDataGenerator.generateListOfPosts;

public class SortingNodeTest {

    private Calculator calc;

    @Before
    public void sortingNodeTestSetup() {
        calc = new Calculator();
    }

}
