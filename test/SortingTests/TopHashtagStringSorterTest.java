package SortingTests;

import org.junit.Before;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.sorting.StringSorter.AbstractStringSorter;
import services.sorting.StringSorter.TopHashtagStringSorter;

public class TopHashtagStringSorterTest {

    private AbstractStringSorter sorter;

    @Before
    public void TopHashtagStringSorterTestSetup() {
        AbstractDataAccess data = new InMemoryAccessObject();
        sorter = new TopHashtagStringSorter(data);
    }
}
