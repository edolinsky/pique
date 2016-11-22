package services.serializer;

import services.dataAccess.proto.PostListProto.PostList;
import com.google.gson.Gson;

import java.util.List;

public final class JsonSerializer implements Serializer {

    private Gson gson = new Gson();

    @Override
    public String serialize(PostList postList) {
        return gson.toJson(postList);
    }

    @Override
    public String serialize(List<String> stringList) { return gson.toJson(stringList); }
}
