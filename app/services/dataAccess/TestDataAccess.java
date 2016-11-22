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

    public Optional<Post> popOldestPost(String keyString) {
        return testOptPost;
    }

    public Optional<PostList> peekAtPostList(String keyString) {
        return testOptPostList;
    }

    public long addNewPostList(String keyString, PostList postList) {
        return x;
    }

    public List<Post> getAllPosts(String keyString) {
        return testPostList;
    }

    @Override
    protected Optional<Post> popFirstPost(String keyString) {
        return null;
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
    public Optional<PostList> getDisplayPostList(String keyString, Integer index) {
        return Optional.of(TestDataGenerator.generatePostList(50));
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

    @Override
    protected long getListSize(String keyString) {
        return 0;
    }

    @Override
    protected List<String> getStringList(String keyString, long length) {
        return null;
    }

    @Override
    protected long replaceStringList(String keyString, List<String> stringList) {
        return 0;
    }

    @Override
    protected long replacePostLists(String keyString, List<PostList> postLists) {
        return 0;
    }

}
