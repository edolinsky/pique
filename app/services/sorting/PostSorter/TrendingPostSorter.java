package services.sorting.PostSorter;

import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostProto.Post;

import java.util.*;
import java.util.stream.Collectors;

import static services.PublicConstants.TOP;
import static services.PublicConstants.TRENDING;

public class TrendingPostSorter extends AbstractPostSorter {

    public TrendingPostSorter(AbstractDataAccess dataSource) { super(dataSource); }

    /**
     * Sorts a list of posts based on their popularity score relative to the existing display channels
     *
     * @param posts list of new posts to be evaluated
     * @return map of string under TRENDING to list of posts sorted by popularity Velocity
     */
    @Override
    public Map<String, List<Post>> sort(List<Post> posts) {
        Map<String, List<Post>> sortedPosts = new HashMap<>();

        // get velocity score relative to both top and existing trending posts
        List<Post> newPostsRelToTrending = calculateRelativePopularity(TRENDING, posts);
        List<Post> newPostsRelToTop = calculateRelativePopularity(TOP, posts);

        // filter out possible duplicates by unique id, giving preference to score relative to top (data is more recent)
        List<Post> allPosts = new ArrayList<>(newPostsRelToTop);
        allPosts.addAll(newPostsRelToTrending);

        sortedPosts.put(TRENDING, allPosts.stream()
                .filter(distinctById(Post::getId))
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Post::getPopularityVelocity)))
                .collect(Collectors.toList()));

        // sort in decreasing order of velocity score
        return sortedPosts;
    }

    @Override
    public long load(Map<String, List<Post>> sortedPosts) {
        if (sortedPosts.containsKey(TRENDING)) {
            return addDisplayPages(TRENDING, preparePages(sortedPosts.get(TRENDING)));
        } else {
            return -1;
        }
    }

    /**
     * Calculates the popularity velocity of a list of posts relative to the posts contained in the display data store
     * at displayName (i.e. top, trending, etc.)
     *
     * @param displayName string denoting name of display channel
     * @param newPosts    lists of posts to evaluate
     * @return list of posts, now with calculated popularity velocities
     */
    private List<Post> calculateRelativePopularity(String displayName, List<Post> newPosts) {
        List<Post> calculatedPosts = new ArrayList<>();

        // retrieve old posts from specified display channel
        List<Post> oldPosts = expandPostLists(dataSource.getAllDisplayPostLists(displayName));
        Map<String, Post> oldPostIdMap = new HashMap<>();

        // load old posts into map of unique IDs to post
        oldPosts.forEach(post -> oldPostIdMap.put(post.getId(), post));

        // calculate popularity velocity for each new post, relative to the same post in the past
        newPosts.forEach(newPost -> {
            // if old post does not exist on record, new post receives popularity velocity of 0
            int popularityVelocity = 0;

            Post oldPost = oldPostIdMap.get(newPost.getId());
            if (oldPost != null) {

                // If post does exist on record, calculate popularity velocity and rebuild post
                popularityVelocity = newPost.getPopularityScore() - oldPost.getPopularityScore();
                newPost = newPost.toBuilder().setPopularityVelocity(popularityVelocity).build();
            }

            calculatedPosts.add(newPost);
        });

        return calculatedPosts;
    }
}
