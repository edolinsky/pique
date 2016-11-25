package services.sorting.PostSorter;

import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostProto.Post;
import services.sorting.Calculator;
import services.sorting.PostSorter.AbstractPostSorter;

import java.util.*;
import java.util.stream.Collectors;

import static services.PublicConstants.TOP;

public class TopPostSorter extends AbstractPostSorter {

    private static final int POPULARITY_THRESHOLD = 300;
    private Calculator calc;

    public TopPostSorter(AbstractDataAccess dataSource) {
        super(dataSource);
        calc = new Calculator();
    }

    /**
     * Sorts a list of posts based on their popularity scores (posts must contain popularity score)
     *
     * @param posts list of posts to be sorted
     * @return map of TOP string to sorted list of posts, in decreasing order of popularity
     */
    @Override
    public Map<String, List<Post>> sort(List<Post> posts) {
        Map<String, List<Post>> sortedPosts = new HashMap<>();
        List<Post> allTopPosts = new ArrayList<>(posts);

        // collect existing top posts and recalculate popularity score
        List<Post> oldPosts = new ArrayList<>(expandPostLists(dataSource.getAllDisplayPostLists(TOP)));
        oldPosts = calc.calculatePopularityScoreOfAllPosts(oldPosts);

        allTopPosts.addAll(oldPosts);

        // sort all top posts
        // filter out posts below popularity score, sort by popularity score in decreasing order
        sortedPosts.put(TOP, allTopPosts.stream()
                .filter(post -> post.getPopularityScore() > POPULARITY_THRESHOLD)
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Post::getPopularityScore)))
                .collect(Collectors.toList()));

        return sortedPosts;
    }

    @Override
    public long load(Map<String, List<Post>> sortedPosts) {
        if (sortedPosts.containsKey(TOP)) {
            return addDisplayPages(TOP, preparePages(sortedPosts.get(TOP)));
        } else {
            return -1;
        }
    }
}
