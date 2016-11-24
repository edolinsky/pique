package SorterTests;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.sorting.AbstractStringSorter;
import services.sorting.TopHashtagStringSorter;

import java.util.List;
import java.util.Map;

import static services.dataAccess.TestDataGenerator.randomHashtags;

public class TopHashtagStringSorterTest {

    private AbstractStringSorter sorter;

    @Before
    public void TopHashtagStringSorterTestSetup() {
        AbstractDataAccess data = new InMemoryAccessObject();
        sorter = new TopHashtagStringSorter(data);
    }
}
