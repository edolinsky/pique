package services.sorting;

import services.ThreadNotification;
import com.google.common.collect.Lists;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import play.Logger;

/**
 * Created by erik on 08/11/16.
 */

public class SortingNode implements Runnable {

    private static final Long PROCESS_INPUT_THRESHOLD = 100L;
    private static final int POPULARITY_THRESHOLD = 300;

    private static final Double LIKE_WEIGHT = 0.9;
    private static final Double COMMENT_WEIGHT = 0.5;
    private static final Double SHARE_WEIGHT = 1.1;

    private AbstractDataAccess dataSource;
    private ThreadNotification sortNotification;

    public SortingNode(AbstractDataAccess dataSource, ThreadNotification sortNotification) {
        this.dataSource = dataSource;
        this.sortNotification = sortNotification;
    }

    @Override
    public void run() {

        while (true) {
            synchronized (sortNotification) {
                try {
                    Logger.info("Sorter is waiting at " + new Date());
                    sortNotification.wait();
                } catch (InterruptedException e) {
                    Logger.error("Sorting Node Thread Exiting");
                }
            }

            sort();

        }
    }

    private void sort() {

        Logger.info("Sorting posts at " + new Date());
        List<Post> newPosts = new ArrayList<>();

        // Obtain number of posts available for processing and exit if this does not meet the threshold
        Long numAvailablePosts = dataSource.getNumPostsInSources();
        if (numAvailablePosts < PROCESS_INPUT_THRESHOLD) {
            return;
        }

        // Obtain all source channels, obtain all posts from each, and delete these posts from the source channels
        List<String> sourceKeys = dataSource.getSources();
        for (String key : sourceKeys) {
            List<Post> postsFromSource = dataSource.getAllPostsFromSource(key);   // get all posts provided by source

            newPosts.addAll(postsFromSource);                           // load posts into memory
            // delete all posts that have been gathered
            dataSource.deleteFirstNPostsFromSourceQueue(key, postsFromSource.size());
        }

        dataSource.addNewDisplayPostList("top", preparePostList(sortTopPosts(newPosts))
        );


        /*
        // Sort trending posts, process into postList, and add to trending channel
        dataSource.addNewDisplayPostList("trending", preparePostList(sortTrendingPosts(newPosts))
        );

        // Bin posts containing particular hashtags together, and add to individual channels
        Map<String, List<Post>> postsByHashTag = sortPostsByHashTag(newPosts);

        for (Map.Entry<String, List<Post>> e : postsByHashTag.entrySet()) {
            dataSource.addNewHashTagPostList(e.getKey(), preparePostList(e.getValue())
            );
        }
        */


    }

    public PostList preparePostList(List<Post> listOfPosts) {
        PostList.Builder postListBuilder = PostList.newBuilder();
        postListBuilder.addAllPosts(listOfPosts);

        return postListBuilder.build();
    }

    public List<Post> sortTopPosts(List<Post> listOfPosts) {
        List<Post> topPosts = new ArrayList<>();

        for (Post post : listOfPosts) {
            post = calculatePopularity(post);
            int popularity = post.getPopularityScore();
            if (popularity > POPULARITY_THRESHOLD) {
                topPosts.add(post);
            }
        }

        topPosts.addAll(Lists.reverse(listOfPosts.stream()
                .sorted(Comparator.comparingInt(Post::getPopularityScore))
                .collect(Collectors.toList())));

        return topPosts;
    }

    public List<Post> sortTrendingPosts(List<Post> listOfPosts) {
        // todo: implement
        return listOfPosts;
    }

    public Map<String, List<Post>> sortPostsByHashTag(List<Post> listOfPosts) {
        // todo: implement
        return Collections.emptyMap();
    }

    public Post calculatePopularity(Post post) {
        int popularity = (int) (
                COMMENT_WEIGHT * post.getNumComments()
                        + LIKE_WEIGHT * post.getNumLikes()
                        + SHARE_WEIGHT * post.getNumShares()
        );

        if (popularity < 0) {
            popularity = 0;
        } else if (popularity >= Integer.MAX_VALUE) {
            popularity = Integer.MAX_VALUE;
        }

        post = post.toBuilder().setPopularityScore(popularity).build();

        return post;
    }
}
