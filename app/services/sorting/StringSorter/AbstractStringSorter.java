package services.sorting.StringSorter;

import services.dataAccess.AbstractDataAccess;

import java.util.List;
import java.util.Map;

public abstract class AbstractStringSorter {

    protected AbstractDataAccess dataSource;

    public AbstractStringSorter(AbstractDataAccess dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Sorts a set of string based on some internal logic
     * @param strings a list of strings to be sorted
     * @return a set of key string(s) mapped to associated lists of strings
     */
    public abstract Map<String, List<String>> sort(List<String> strings);

    /**
     * Loads a set of sorted string list(s) into the data source
     * @param sortedStrings set of key string(s) mapped to string lists
     * @return the length of the particular channel after insertion, or -1 if unsuccessful
     */
    public abstract long load(Map<String, List<String>> sortedStrings);
}
