package services.sorting.StringSorter;

import services.dataAccess.AbstractDataAccess;

import java.util.*;
import java.util.stream.Collectors;

import static services.PublicConstants.NUM_TOP_HASHTAGS;
import static services.PublicConstants.TOP_HASHTAGS;

public class TopHashtagStringSorter extends AbstractStringSorter {

    public TopHashtagStringSorter(AbstractDataAccess dataSource) {
        super(dataSource);
    }

    /**
     * Evaluates the top NUM_TOP_HASHTAGS (max) of the input string based on the number of available postList pages
     * in the data store.
     *
     * @param strings List of hashtag strings to be evaluated. If empty or null, all available hashtags are evaluated.
     * @return map of string TOP_HASHTAGS to list of strings denoting most popular hashtags, in decreasing order
     */
    @Override
    public Map<String, List<String>> sort(List<String> strings) {

        // if no input list, default to evaluating the entire set of hashtags
        if (strings == null || strings.isEmpty()) {
            strings = dataSource.getAllHashTags();
        }

        Map<String, Long> hashtagPopularityMap = new HashMap<>();
        // retrieve number of pages of hashtag posts at each hashtag display
        strings.forEach(h -> {
            Long numPages = dataSource.getNumHashTagPostLists(h);

            hashtagPopularityMap.put(h, numPages);
        });

        // sort hashtags by number of pages available, in decreasing order
        LinkedHashMap<String, Long> sortedHashtagMap = hashtagPopularityMap.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        // take up to the first NUM_TOP_HASHTAGS (most popular) strings in the key set
        Map<String, List<String>> sortedHashTags = new HashMap<>();
        if (sortedHashtagMap.size() > NUM_TOP_HASHTAGS) {
            sortedHashTags.put(TOP_HASHTAGS, new ArrayList<>(sortedHashtagMap.keySet()).subList(0, NUM_TOP_HASHTAGS));
        } else {
            sortedHashTags.put(TOP_HASHTAGS, new ArrayList<>(sortedHashtagMap.keySet()));
        }

        return sortedHashTags;


    }

    /**
     * Adds a list of top hashtags to the top hashtags channel in the data store
     * @param sortedStrings a map containing the key string stored under TOP_HASHTAGS
     * @return the length of the storage channel after insertion, or -1 if unsuccessful
     */
    @Override
    public long load(Map<String, List<String>> sortedStrings) {
        if (sortedStrings.containsKey(TOP_HASHTAGS)) {
            return dataSource.addTopHashtags(sortedStrings.get(TOP_HASHTAGS));
        } else {
            return -1;
        }
    }


}
