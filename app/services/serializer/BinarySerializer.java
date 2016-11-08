package services.serializer;

import services.dataAccess.proto.PostListProto.PostList;

/**
 * Created by erik on 02/11/16.
 */
public final class BinarySerializer {

    public byte[] serialize(PostList postList) {
        return postList.toByteArray();
    }
}
