package services.dataAccess;

import org.joda.time.DateTime;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostListProto.PostList;
import services.dataAccess.proto.PostProto;
import services.dataAccess.proto.PostProto.Post;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * Created by maria on 04/11/16.
 */

public class TestDataAccess extends AbstractDataAccess {

    private long x = 1;

    private List<Post> testPostList;
    private Optional<Post> testOptPost;
    private Optional<PostList> testOptPostList;

    public TestDataAccess() {
        testOptPostList = Optional.of(TestDataGenerator.generatePostList(50));
    }

    public long addNewPost(String keyString, Post post) {
        return x;
    }

    public long addNewPosts(String keyString, List<Post> listOfPosts) {
        return x;
    }

    @Override
    protected Optional<Post> popFirstPost(String keyString) {
        return null;
    }

    public long addNewPostList(String keyString, PostList postList) {
        return x;
    }

    public List<Post> getAllPosts(String keyString) {
        return testPostList;
    }

    @Override
    protected Optional<PostList> getPostList(String keyString, Integer index) {
        return null;
    }

    @Override
    protected List<PostList> getAllPostLists(String keyString) {
        return Collections.singletonList(testOptPostList.get());
    }

    @Override
    protected String deleteFirstNPosts(String keyString, Integer numPosts) {
        return null;
    }

    @Override
    public long getNumPostsInNameSpace(String nameSpace) {
        return 0;
    }

    @Override
    public List<String> getKeysInNameSpace(String nameSpace) {
        return null;
    }

    public Optional<Post> popOldestPost(String keyString) {
        return testOptPost;
    }

    public Optional<PostList> peekAtPostList(String keyString) {
        return testOptPostList;
    }

}
