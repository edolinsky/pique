package services.dataAccess;

import org.springframework.stereotype.Service;
import services.dataAccess.proto.PostListProto;
import services.dataAccess.proto.PostProto;

/**
 * Created by maria on 04/11/16.
 */

public class TestDataAccess extends AbstractDataAccess {
    private byte[] testData = {(byte)0x0f, (byte)0x54};

    public long addNewPost(String keyString, PostProto.Post post) {
        long x = 1;
        return x;
    }

    public long addNewPostList(String keyString, PostListProto.PostList postList) {
        long x = 1;
        return x;
    }

    public byte[] popOldestPost(String keyString) {
        return testData;
    }

    public byte[] peekAt(String keyString) {
        return testData;
    }

    public byte[] peekAt(byte[] key) {
        return testData;
    }
}
