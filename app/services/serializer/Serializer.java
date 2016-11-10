package services.serializer;

import services.dataAccess.proto.PostListProto.PostList;

/**
 * Created by erik on 09/11/16.
 */
public interface Serializer {

    Object serialize(PostList postList);
}
