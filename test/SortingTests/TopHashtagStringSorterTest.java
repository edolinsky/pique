package SortingTests;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertEquals;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.PostSorter.AbstractPostSorter;
import services.sorting.PostSorter.HashtagPostSorter;
import services.sorting.StringSorter.AbstractStringSorter;
import services.sorting.StringSorter.TopHashtagStringSorter;

import static services.dataAccess.TestDataGenerator.generateListOfPosts;
import static services.dataAccess.TestDataGenerator.randomHashtags;
import static services.PublicConstants.TOP_HASHTAGS;
import static services.PublicConstants.NUM_TOP_HASHTAGS;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class TopHashtagStringSorterTest {

    private AbstractStringSorter sorter;
    private AbstractDataAccess data;
    private AbstractPostSorter hashtagPostSorter;

    @Before
    public void TopHashtagStringSorterTestSetup() {
        data = new InMemoryAccessObject();
        sorter = new TopHashtagStringSorter(data);
        hashtagPostSorter = new HashtagPostSorter(data);

    }

    @Test
    public void testSortTopHashtagsNull() {
        List<Post> posts = generateListOfPosts(1000);
        hashtagPostSorter.load(hashtagPostSorter.sort(posts));

        // test sort using null input
        List<String> topHashtags = sorter.sort(null).get(TOP_HASHTAGS);

        // check that strings are sorted in descending order of number of pages
        long pages_at_previous = AbstractDataAccess.getMaxPostlists();
        for (String hashtag : topHashtags) {
            long numPostLists = data.getNumHashTagPostLists(hashtag);
            assertTrue(numPostLists <= pages_at_previous);
            pages_at_previous = numPostLists;
        }
    }

    @Test
    public void testSortTopHashtagsEmpty() {
        List<Post> posts = generateListOfPosts(1000);
        hashtagPostSorter.load(hashtagPostSorter.sort(posts));

        // test sort using empty input
        List<String> topHashtags = sorter.sort(Collections.emptyList()).get(TOP_HASHTAGS);

        // check that strings are sorted in descending order of number of pages
        long pages_at_previous = AbstractDataAccess.getMaxPostlists();
        for (String hashtag : topHashtags) {
            long numPostLists = data.getNumHashTagPostLists(hashtag);
            assertTrue(numPostLists <= pages_at_previous);
            pages_at_previous = numPostLists;
        }
    }

    @Test
    public void testSortTopHashtagsOnInput() {
        List<Post> posts = generateListOfPosts(1000);
        hashtagPostSorter.load(hashtagPostSorter.sort(posts));

        // get list of all hashtags, and take the ceiling of half
        List<String> allHashtags = data.getAllHashTags();
        List<String> hashtags = allHashtags.subList(0, allHashtags.size() / 2 + 1);

        // sort using the top hashtags at input
        List<String> topHashtags = sorter.sort(hashtags).get(TOP_HASHTAGS);

        // check that strings are sorted in descending order of number of pages
        long pages_at_previous = AbstractDataAccess.getMaxPostlists();
        for (String hashtag : topHashtags) {
            long numPostLists = data.getNumHashTagPostLists(hashtag);
            assertTrue(numPostLists <= pages_at_previous);
            pages_at_previous = numPostLists;
        }

    }

    @Test
    public void testSortTopHashtagsNullInputNoneStored() {
        assertEquals(Collections.emptyList(), sorter.sort(null).get(TOP_HASHTAGS));
    }

    @Test
    public void testSortTopHashtagsEmptyInputNoneStored() {
        assertEquals(Collections.emptyList(), sorter.sort(Collections.emptyList()).get(TOP_HASHTAGS));
    }

    @Test
    public void testLoadTopHashtags() {
        // generate hashtags and load into map
        Map<String, List<String>> hashtagMap = generateTopHashtagMap();
        List<String> hashtags = hashtagMap.get(TOP_HASHTAGS);

        sorter.load(hashtagMap);

        assertEquals(hashtags, data.getTopHashTags(hashtags.size()));
    }

    @Test
    public void testLoadTopHashtagsEmptyInput() {
        Map<String, List<String>> hashtagMap = generateTopHashtagMap();

        sorter.load(hashtagMap);

        Map<String, List<String>> emptyHashtagMap = new HashMap<>();
        emptyHashtagMap.put(TOP_HASHTAGS, Collections.emptyList());
        sorter.load(emptyHashtagMap);

        // empty list should have replaced the original, giving us an empty channel
        assertEquals(Collections.emptyList(), data.getTopHashTags(hashtagMap.get(TOP_HASHTAGS).size()));
    }

    @Test
    public void testLoadTopHashtagsReplace() {
        Map<String, List<String>> firstHashtagMap = generateTopHashtagMap();
        Map<String, List<String>> secondHashtagMap = generateTopHashtagMap();
        List<String> secondHashtags = secondHashtagMap.get(TOP_HASHTAGS);

        sorter.load(firstHashtagMap);
        sorter.load(secondHashtagMap);

        // second list should have replaced first
        assertEquals(secondHashtags, data.getTopHashTags(secondHashtags.size()));
    }


    private Map<String, List<String>> generateTopHashtagMap() {
        List<String> hashtags = randomHashtags();
        Map<String, List<String>> hashtagMap = new HashMap<>();
        hashtagMap.put(TOP_HASHTAGS, hashtags);

        return hashtagMap;
    }
}
