package services.sorting;

import com.google.common.collect.Lists;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.*;
import java.util.stream.Collectors;

public class HashtagPostSorter extends AbstractPostSorter {

    private static final String NO_HASHTAGS = "N/A";

    public HashtagPostSorter(AbstractDataAccess dataSource) {
        super(dataSource);
    }

    /**
     * Sorts a list of posts into individual binned lists, according to the hashtags therein
     *
     * @param posts list of posts to be sorted
     * @return map of hashtag strings to lists of posts that contain the key string hashtag
     */
    @Override
    public Map sort(List<Post> posts) {
        Map<String, List<Post>> postsByHashTag = new HashMap<>();

        // sort by popularity, in decreasing order
        List<Post> listOfPostsClone = new ArrayList<>(posts);
        listOfPostsClone.stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Post::getPopularityScore)))
                .collect(Collectors.toList());

        // iterate through individual posts
        for (Post post : posts) {

            // iterate over each hashtag contained in posts
            for (int hashtagIndex = 0; hashtagIndex < post.getHashtagCount(); hashtagIndex++) {
                String hashtag = post.getHashtag(hashtagIndex);

                // either create new hashtag-post list entry, or append to existing key
                if (postsByHashTag.containsKey(hashtag)) {
                    postsByHashTag.get(hashtag).add(post);

                } else {
                    ArrayList<Post> hashtagPosts = new ArrayList<>();
                    hashtagPosts.add(post);
                    postsByHashTag.put(hashtag, hashtagPosts);
                }
            }
        }

        postsByHashTag.remove(NO_HASHTAGS);   // remove posts with no hashtags

        return postsByHashTag;
    }

    /**
     * Prepares the new hashtag display channels given a list of new posts, pre-sorted into a map of hashtags to list
     * of posts containing that hashtag
     *
     * @param sortedPosts map of hashtags to list of posts containing that hashtag
     */
    @Override
    public long load(Map<String, List<Post>> sortedPosts) {
        Map<String, List<Post>> hashTagPosts = new HashMap<>(sortedPosts);
        List<String> oldHashTags = dataSource.getAllHashTags();     // retrieve list of all currently stored hashtags

        // iterate over existing hashtags
        oldHashTags.forEach(hashtag -> {

            // retrieve existing posts stored under hashtag
            List<Post> oldPosts = expandPostLists(dataSource.getAllHashtagPostLists(hashtag));

            // merge old posts and new posts, filtering out duplicate IDs
            Map<String, Post> uniquePosts = new HashMap<>();
            oldPosts.forEach(post -> uniquePosts.put(post.getId(), post));

            List<Post> newPosts = sortedPosts.get(hashtag);

            if (newPosts != null) {
                newPosts.forEach(post -> uniquePosts.put(post.getId(), post));
            }

            // load merged posts into map
            hashTagPosts.put(hashtag, uniquePosts.values().stream().collect(Collectors.toList()));

        });

        // load each hashtag list into data store under hashtag key
        long numPagesAdded = 0;
        for (Map.Entry<String, List<Post>> e : hashTagPosts.entrySet()) {
            numPagesAdded += addHashtagPages(e.getKey(), preparePages(e.getValue()));
        }

        return numPagesAdded;
    }


    /**
     * Loads a set of PostList pages into the hashtag namespace of the data store under key hashtag
     *
     * @param hashtag key string
     * @param pages   list of PostList objects (pages) to be added under key string
     * @return number of pages added
     */
    public int addHashtagPages(String hashtag, List<PostList> pages) {

        // Add pages in reverse (want highest rated pages at top of stack)
        dataSource.replaceHashTagPostLists(hashtag, Lists.reverse(pages));

        return pages.size();
    }
}
