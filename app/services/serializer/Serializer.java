package services.serializer;

import services.dataAccess.proto.PostListProto.PostList;
import java.util.List;


public interface Serializer {

    Object serialize(PostList postList);
    Object serialize(List<String> stringList);
}
