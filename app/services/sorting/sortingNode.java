package services.sorting;

import com.google.inject.Singleton;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.RedisAccessObject;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto.Post;

import java.util.*;

/**
 * Created by erik on 08/11/16.
 */

@Singleton
public class sortingNode {

    private static final Long PROCESS_INPUT_THRESHOLD = 100L;
    private AbstractDataAccess dataSource;
    private Object sortNotification= new Object();

    public void sortingNode(Map<String,String> args) {
        String mode = args.get("mode");

        if (mode.equals("test")) {
            dataSource = new InMemoryAccessObject();
        } else {
            dataSource = new RedisAccessObject();
        }

        startBackgroundThread();
    }

    private void startBackgroundThread() {
        new Thread(() -> {

            while (true) {

                try {
                    synchronized (sortNotification) {
                        sortNotification.wait();
                    }
                } catch (InterruptedException Ie){
                    System.out.println("Sorting Node Thread Exiting"); // todo: handle better
                }

                run();
            }
        });
    }

    private void run() {
        List<Post> newPosts = new ArrayList<>();

        // Obtain number of posts available for processing and exit if this does not meet the threshold
        Long numAvailablePosts = dataSource.getNumPostsInNameSpace(AbstractDataAccess.SOURCE_NAMESPACE);
        if (numAvailablePosts < PROCESS_INPUT_THRESHOLD) {
            return;
        }

        // Obtain all source channels, obtain all posts from each, and delete these posts from the source channels
        List<String> sourceKeys = dataSource.getKeysInNameSpace(AbstractDataAccess.SOURCE_NAMESPACE);
        for (String key : sourceKeys) {
            List<Post> postsFromSource = dataSource.getAllPosts(key);   // get all posts provided by source

            newPosts.addAll(postsFromSource);                           // load posts into memory
            dataSource.deleteFirstNPosts(key, postsFromSource.size());  // delete all posts that have been gathered
        }

        dataSource.addNewPostList(
                AbstractDataAccess.DISPLAY_NAMESPACE + AbstractDataAccess.NAMESPACE_DELIMITER + "top",
                preparePostList(sortTopPosts(newPosts))
        );


        /*
        // Sort trending posts, process into postList, and add to trending channel
        dataSource.addNewPostList(
                AbstractDataAccess.DISPLAY_NAMESPACE + AbstractDataAccess.NAMESPACE_DELIMITER + "trending",
                preparePostList(sortTrendingPosts(newPosts))
        );

        // Bin posts containing particular hashtags together, and add to individual channels
        Map<String, List<Post>> postsByHashTag = sortPostsByHashTag(newPosts);

        for (Map.Entry<String, List<Post>> e : postsByHashTag.entrySet()) {
            dataSource.addNewPostList(
                    AbstractDataAccess.HASHTAG_NAMESPACE + AbstractDataAccess.NAMESPACE_DELIMITER + e.getKey(),
                    preparePostList(e.getValue())
            );
        }
        */

    }

    private PostList preparePostList(List<Post> listOfPosts) {
        PostList.Builder postListBuilder = PostList.newBuilder();
        postListBuilder.addAllPosts(listOfPosts);

        return postListBuilder.build();
    }

    private List<Post> sortTopPosts(List<Post> listOfPosts) {
        // todo: implement
        return listOfPosts;
    }

    private List<Post> sortTrendingPosts(List<Post> listOfPosts) {
        // todo: implement
        return listOfPosts;
    }

    private Map<String, List<Post>> sortPostsByHashTag(List<Post> listOfPosts) {
        // todo: implement
        return Collections.emptyMap();
    }
}
