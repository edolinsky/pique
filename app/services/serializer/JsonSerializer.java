package services.serializer;

import services.dataAccess.proto.PostListProto.PostList;
import com.google.gson.Gson;

/**
 * Created by erik on 09/11/16.
 */
public final class JsonSerializer implements Serializer {

    private Gson gson = new Gson();

    @Override
    public String serialize(PostList postList) {
        return gson.toJson(postList);
    }
}
