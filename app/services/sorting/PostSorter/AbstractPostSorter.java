package services.sorting.PostSorter;

import com.google.common.collect.Lists;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static services.PublicConstants.POSTS_PER_PAGE;

public abstract class AbstractPostSorter {

    AbstractDataAccess dataSource;
    private static final int PAGE_LIMIT = Integer.valueOf(System.getenv(POSTS_PER_PAGE));

    public AbstractPostSorter(AbstractDataAccess dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * Sorts a list of posts based on some internal logic
     *
     * @param posts a list of posts to be sorted
     * @return Map of key string(s) to list(s) of posts
     */
    public abstract Map<String, List<Post>> sort(List<Post> posts);

    /**
     * Loads list(s) of posts under keystrings into the data store
     *
     * @param sortedPosts a map of key string(s) to sorted posts
     * @return length of storage channel after insertion
     */
    public abstract long load(Map<String, List<Post>> sortedPosts);

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
     * Converts a list of postList entities to a single list of posts, containing the same posts
     *
     * @param postLists list of postlist entities
     * @return list of post entities comprised of the expanded postLists
     */
    protected List<Post> expandPostLists(List<PostList> postLists) {
        List<Post> posts = new ArrayList<>();

        // convert postList to list of posts and append to new list of posts
        postLists.forEach(postList -> posts.addAll(postList.getPostsList()));

        return posts;
    }

    /**
     * Loads a set of PostList pages into the display namespace of the data store under key
     *
     * @param key   key string
     * @param pages list of PostList objects to be added under key string
     * @return size of channel after display pages have been added
     */
    long addDisplayPages(String key, List<PostList> pages) {
        ListIterator li = pages.listIterator(pages.size());
        long pagesInChannel = 0;

        while (li.hasPrevious()) {
            pagesInChannel = dataSource.addNewDisplayPostList(key, pages.get(li.previousIndex()));
            li.previous();
        }

        return pagesInChannel;
    }

    /**
     * Loads a set of PostList pages, replacing old pages, into the display namespace of the data store under key
     *
     * @param key key string
     * @param pages list of PostList objects to replace existing pages
     * @return size of channel after display pages have been added
     */
    long replaceDisplayPages(String key, List<PostList> pages) {
        return dataSource.replaceDisplayPostLists(key, pages);
    }


    /**
     * Provides a method of filtering a list of posts (within a lambda function) for unique IDs (or any other field for
     * that matter).
     *
     * @param idExtractor
     * @param <T>
     * @return
     */
    public static <T> Predicate<T> distinctById(Function<? super T, ?> idExtractor) {
        Map<Object,Boolean> seen = new ConcurrentHashMap<>();
        return t -> seen.putIfAbsent(idExtractor.apply(t), Boolean.TRUE) == null;
    }

}
