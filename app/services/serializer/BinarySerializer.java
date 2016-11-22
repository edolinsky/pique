package services.serializer;

import services.dataAccess.proto.PostListProto.PostList;

import java.util.List;

public final class BinarySerializer implements Serializer{

    @Override
    public byte[] serialize(PostList postList) {
        return postList.toByteArray();
    }

    @Override
    public byte[] serialize(List<String> stringList) { return stringList.toString().getBytes(); }
}
