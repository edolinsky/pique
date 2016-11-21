package services.sorting;

import services.ThreadNotification;
import com.google.common.collect.Lists;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.*;
import java.util.stream.Collectors;

import play.Logger;

import static services.PublicConstants.NUM_TOP_HASHTAGS;
import static services.PublicConstants.TOP;
import static services.PublicConstants.TRENDING;
import static services.PublicConstants.SORTING_NODE_INPUT_THRESHOLD;
import static services.PublicConstants.POSTS_PER_PAGE;

public class SortingNode implements Runnable {

    private static final Long PROCESS_INPUT_THRESHOLD = Long.valueOf(System.getenv(SORTING_NODE_INPUT_THRESHOLD));
    private static final int POPULARITY_THRESHOLD = 300;
    private static final int PAGE_LIMIT = Integer.valueOf(System.getenv(POSTS_PER_PAGE));
    private static final String NO_HASHTAG = "N/A";

    private static final Double LIKE_WEIGHT = 0.9;
    private static final Double COMMENT_WEIGHT = 0.5;
    private static final Double SHARE_WEIGHT = 1.1;

    private AbstractDataAccess dataSource;
    private ThreadNotification sortNotification;

    public SortingNode(AbstractDataAccess dataSource, ThreadNotification sortNotification) {
        this.dataSource = dataSource;
        this.sortNotification = sortNotification;
    }

    /**
     * Runtime loop for the sorting node
     */
    @Override
    public void run() {

        while (true) {
            synchronized (sortNotification) {
                try {
                    // wait for notification of new posts
                    Logger.info("Sorter is waiting at " + new Date());
                    sortNotification.wait();
                } catch (InterruptedException e) {
                    Logger.error("Sorting Node Thread Exiting");
                }
            }

            // once notified, run main sort process
            sort();

        }
    }

    /**
     * Main process of the sorting node
     */
    private void sort() {

        List<Post> newPosts = new ArrayList<>();

        // Obtain number of posts available for processing and exit if this does not meet the threshold
        Long numAvailablePosts = dataSource.getNumPostsInSources();
        if (numAvailablePosts < PROCESS_INPUT_THRESHOLD) {
            Logger.info("Sorter is dissatisfied with the number of available posts. Waiting...");
            return;
        }
        Logger.info("Sorting posts at " + new Date());

        /*
           GATHERING NEW POSTS
         */

        // Obtain all source channels, obtain all posts from each, and delete these posts from the source channels
        List<String> sourceKeys = dataSource.getSources();
        for (String key : sourceKeys) {
            List<Post> postsFromSource = dataSource.getAllPostsFromSource(key);   // get all posts provided by source

            newPosts.addAll(postsFromSource);                           // load posts into memory
            // delete all posts that have been gathered
            dataSource.deleteFirstNPostsFromSourceQueue(key, postsFromSource.size());
        }

        /*
           SORTING NEW POSTS
         */

        // calculate popularity score of all posts
        List<Post> calculatedPosts = calculatePopularityScoreOfAllPosts(newPosts);

        // sort top posts and load in in pages
        List<Post> newSortedTopPosts = sortTopPosts(calculatedPosts);
        Logger.info("Sorter sorted " + newSortedTopPosts.size() + " new top posts.");

        // Sort trending posts, process into postList, and add to trending channel
        List<Post> newSortedTrendingPosts = sortTrendingPosts(calculatedPosts);
        Logger.info("Sorter sorted " + newSortedTrendingPosts.size() + " new trending posts.");

        // Finally sort hashtags, also in reverse order of popularity
        // Bin posts containing particular hashtags together, and add to individual channels
        Map<String, List<Post>> postsByHashTag = sortPostsByHashTag(calculatedPosts);
        Logger.info("Sorter sorted " + postsByHashTag.values().size() + " hashtags.");

        /*
           STORING SORTED DATA
         */

        // add top content in pages to display top stack
        addDisplayPages(TOP, preparePages(newSortedTopPosts));

        // add new trending content in pages to dsiplay trending stack
        addDisplayPages(TRENDING, preparePages(newSortedTrendingPosts));

        // add hashtag pages to their corresponding keys in data store
        addHashtagDisplay(postsByHashTag);

        // update
        updateTopHashtags();
        Logger.info("Sorter added new top hashtags");

    }

    /**
     * Converts a list of individual posts to a list of floor(listOfPosts / PAGE_LIMIT) + 1 postList pages in same order
     *
     * @param listOfPosts a list of Post objects to be converted into pages
     * @return list of postList objects containing max PAGE_LIMIT posts
     */
    public List<PostList> preparePages(List<Post> listOfPosts) {
        List<PostList> pages = new ArrayList<>();

        // partition into lists of max size PAGE_LIMIT, and convert into postList entities
        pages.addAll(Lists.partition(listOfPosts, PAGE_LIMIT).stream()
                .map(this::preparePostList)
                .collect(Collectors.toList()));

        return pages;
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

    /**
     * Loads a set of PostList pages into the display namespace of the data store under key
     *
     * @param key   key string
     * @param pages list of PostList objects to be added under key string
     * @return number of pages added
     */
    public int addDisplayPages(String key, List<PostList> pages) {
        ListIterator li = pages.listIterator(pages.size());

        while (li.hasPrevious()) {
            dataSource.addNewDisplayPostList(key, pages.get(li.previousIndex()));
            li.previous();
        }

        return pages.size();
    }

    /**
     * Prepares the new hashtag display channels given a list of new posts, pre-sorted into a map of hashtags to list
     * of posts containing that hashtag
     *
     * @param newPostsByHashtag map of hashtags to list of posts containing that hashtag
     */
    public void addHashtagDisplay(Map<String, List<Post>> newPostsByHashtag) {
        Map<String, List<Post>> hashTagPosts = new HashMap<>(newPostsByHashtag);
        List<String> oldHashTags = dataSource.getAllHashTags();     // retrieve list of all currently stored hashtags

        // iterate over existing hashtags
        oldHashTags.forEach(hashtag -> {

            // retrieve existing posts stored under hashtag
            List<Post> oldPosts = expandPostLists(dataSource.getAllHashtagPostLists(hashtag));

            // merge old posts and new posts, filtering out duplicate IDs
            Map<String, Post> uniquePosts = new HashMap<>();
            oldPosts.forEach(post -> uniquePosts.put(post.getId(), post));

            List<Post> newPosts = newPostsByHashtag.get(hashtag);

            if (newPosts != null) {
                newPosts.forEach(post -> uniquePosts.put(post.getId(), post));
            }

            // load merged posts into map
            hashTagPosts.put(hashtag, uniquePosts.values().stream().collect(Collectors.toList()));

        });

        // store posts sorted by hashtag in data store
        hashTagPosts.entrySet().forEach(e -> addHashtagPages(e.getKey(), preparePages(e.getValue())));
    }

    /**
     * Converts a list of Posts to a postList object
     *
     * @param listOfPosts list of post objects
     * @return postList object containing all specified posts
     */
    public PostList preparePostList(List<Post> listOfPosts) {
        PostList.Builder postListBuilder = PostList.newBuilder();
        postListBuilder.addAllPosts(listOfPosts);

        return postListBuilder.build();
    }

    /**
     * Sorts a list of posts based on a weighted popularity score (posts must contain popularity score)
     *
     * @param listOfPosts list of posts to be sorted
     * @return sorted list of posts, in decreasing order
     */
    public List<Post> sortTopPosts(List<Post> listOfPosts) {
        List<Post> listOfPostsClone = new ArrayList<>(listOfPosts);

        // filter out posts below popularity score, sort by popularity score in decreasing order
        return listOfPostsClone.stream()
                .filter(post -> post.getPopularityScore() > POPULARITY_THRESHOLD)
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Post::getPopularityScore)))
                .collect(Collectors.toList());
    }

    /**
     * Sorts a list of posts based on their popularity score relative to the existing display channels
     *
     * @param listOfPosts list of new posts to be evaluated
     * @return
     */
    public List<Post> sortTrendingPosts(List<Post> listOfPosts) {

        // get velocity score relative to both top and existing trending posts
        List<Post> newPostsRelToTrending = calculateRelativePopularity(TRENDING, listOfPosts);
        List<Post> newPostsRelToTop = calculateRelativePopularity(TOP, listOfPosts);

        // filter out possible duplicates by unique id, giving preference to score relative to top (data is more recent)
        Map<String, Post> uniquePosts = new HashMap<>();
        newPostsRelToTrending.forEach(post -> uniquePosts.put(post.getId(), post));
        newPostsRelToTop.forEach(post -> uniquePosts.put(post.getId(), post));

        // sort in decreasing order of velocity score
        return new ArrayList<>(uniquePosts.values().stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Post::getPopularityVelocity)))
                .collect(Collectors.toList()));
    }

    /**
     * Sorts a list of posts into individual binned lists, according to the hashtags therein
     *
     * @param listOfPosts list of posts to be sorted
     * @return map of hashtag strings to lists of posts that contain the key string hashtag
     */
    public Map<String, List<Post>> sortPostsByHashTag(List<Post> listOfPosts) {
        Map<String, List<Post>> postsByHashTag = new HashMap<>();

        // sort by popularity, in decreasing order
        List<Post> listOfPostsClone = new ArrayList<>(listOfPosts);
        listOfPostsClone.stream()
                .sorted(Collections.reverseOrder(Comparator.comparingInt(Post::getPopularityScore)))
                .collect(Collectors.toList());

        // iterate through individual posts
        for (Post post : listOfPosts) {

            // iterate over each hashtag contained in posts
            for (int hashtagIndex = 0; hashtagIndex < post.getHashtagCount(); hashtagIndex++) {
                String hashtag = post.getHashtag(hashtagIndex);

                // either create new hashtag-post list entry, or append to existing key
                if (postsByHashTag.containsKey(hashtag)) {
                    postsByHashTag.get(hashtag).add(post);

                } else {
                    ArrayList<Post> posts = new ArrayList<>();
                    posts.add(post);
                    postsByHashTag.put(hashtag, posts);
                }
            }
        }

        postsByHashTag.remove(NO_HASHTAG);   // remove posts with no hashtags

        return postsByHashTag;
    }

    /**
     * Calculates and inserts popularity score to each post in a list of posts
     *
     * @param posts list of posts
     * @return the same list of posts, but with each post now containing a popularity score
     */
    public List<Post> calculatePopularityScoreOfAllPosts(List<Post> posts) {
        List<Post> calculatedPosts = new ArrayList<>();

        posts.forEach(post -> {
            post = calculatePopularity(post);
            calculatedPosts.add(post);
        });

        return calculatedPosts;
    }

    /**
     * Calculates the popularity of a given post, and injects the popularity score into that post
     *
     * @param post Post object to be evaluated
     * @return popularity score
     */
    public Post calculatePopularity(Post post) {

        // calculate popularity score of post
        int popularity = calculatePopularityScore(post.getNumComments(), post.getNumLikes(), post.getNumShares());

        // rebuild post with new score
        post = post.toBuilder().setPopularityScore(popularity).build();

        return post;
    }

    /**
     * Calculates the popularity score given the specified fields
     *
     * @param numComments number of comments associated with a post
     * @param numLikes    number of likes associated with a post
     * @param numShares   number of shares associated with a post
     * @return popularity score calculated given input parameters.
     */
    private int calculatePopularityScore(int numComments, int numLikes, int numShares) {
        // evaluate popularity score
        int popularity = (int) (
                COMMENT_WEIGHT * numComments
                        + LIKE_WEIGHT * numLikes
                        + SHARE_WEIGHT * numShares
        );

        // handle boundary conditions
        if (popularity < 0) {
            popularity = 0;
        } else if (popularity >= Integer.MAX_VALUE) {
            popularity = Integer.MAX_VALUE;
        }

        return popularity;
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

    /**
     * Converts a list of postList entities to a single list of posts, containing the same posts
     *
     * @param postLists list of postlist entities
     * @return list of post entities comprised of the expanded postLists
     */
    private List<Post> expandPostLists(List<PostList> postLists) {
        List<Post> posts = new ArrayList<>();

        // convert postList to list of posts and append to new list of posts
        postLists.forEach(postList -> posts.addAll(postList.getPostsList()));

        return posts;
    }

    /**
     * Calculates top hashtags in data store, and updates the top hashtag list
     */
    public void updateTopHashtags() {
        List<String> allHashtags = dataSource.getAllHashTags();
        Map<String, Long> hashtagsToNumPages = new HashMap<>();

        // retrieve number of pages of hashtag posts at each hashtag display
        allHashtags.forEach(h -> hashtagsToNumPages.put(h, dataSource.getNumHashTagPostLists(h)));

        // sort hashtags by number of pages, in decreasing order
        LinkedHashMap<String, Long> sortedTopHashtagsMap = hashtagsToNumPages.entrySet().stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue,
                        (e1, e2) -> e1, LinkedHashMap::new));

        // convert to list of sorted strings, and truncate to number of desired top hashtags
        List<String> topHashtags = new ArrayList<>(sortedTopHashtagsMap.keySet());

        if (topHashtags.size() > NUM_TOP_HASHTAGS) {
            topHashtags = topHashtags.subList(0, NUM_TOP_HASHTAGS);
        }

        // add new hashtag list to top hashtags
        dataSource.addTopHashtags(topHashtags);
    }
}
