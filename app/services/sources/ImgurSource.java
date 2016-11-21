package services.sources;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import services.dataAccess.proto.PostProto.Post;
import twitter4j.Status;

import java.net.HttpURLConnection;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static services.PublicConstants.IMGUR_APP_ID;

public class ImgurSource implements RestfulSource {

    private static final String SOURCE_NAME = "imgur";
    private static final String AUTH_URL = "https://api.imgur.com/oauth2/authorize?";
    private static final String REQUEST_URL = "https://api.imgur.com/";
    private static final String VERSION = "3";
    private static final Integer MAX_SEARCH_PER_WINDOW = 12500;
    private static final Long WINDOW_LENGTH = TimeUnit.DAYS.toMillis(1);

    @Override
    public String getVersion() {
        return VERSION;
    }

    @Override
    public String getUrl() {
        return REQUEST_URL;
    }

    @Override
    public String generateRequestUrl(String trend) {
        StringBuilder requestBuilder = new StringBuilder();
        requestBuilder.append(REQUEST_URL).append(VERSION).append('/').append(trend)
                .append("0.json");
        return requestBuilder.toString();
    }

    @Override
    public void addRequestHeaders(HttpURLConnection connection) {
        connection.setRequestProperty ("Authorization",
                "Client-ID " + System.getenv(IMGUR_APP_ID));
    }

    @Override
    public String getSourceName() {
        return "source:" + SOURCE_NAME;
    }

    /**
     * imgur has harsher penalties for an overage so we are going to run this at a slightly
     * slower rate of 75% max.
     * @return
     */
    @Override
    public long getQueryDelta() {
        return WINDOW_LENGTH/(MAX_SEARCH_PER_WINDOW * 3/4);
    }

    @Override
    public List<String> getTrends(String country, String city) {
        return Collections.singletonList("gallery/hot/viral/");
    }


    @Override
    public List<Post> parseResponse(String response) {
        // response from imgur is json
        JsonElement jelement = new JsonParser().parse(response);
        JsonArray data = jelement.getAsJsonObject().getAsJsonArray("data");

        List<Post> posts = new ArrayList<>();
        for (JsonElement e : data) {
            posts.add(createPost(e.getAsJsonObject()));
        }

        return posts;
    }

    private Post createPost(JsonObject object) {
        Post.Builder builder = Post.newBuilder();
        DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        builder.setId(object.getAsJsonPrimitive("id").getAsString());

        Date time = new Date(object.getAsJsonPrimitive("datetime").getAsLong());
        builder.setTimestamp(df.format(time));
        builder.addSource(object.getAsJsonPrimitive("account_url").getAsString());
        builder.addSourceLink(object.getAsJsonPrimitive("link").getAsString());
        builder.setPopularityScore(0);
        builder.setPopularityVelocity(0);
        builder.setNumComments(object.getAsJsonPrimitive("comment_count").getAsInt());
        builder.setNumShares(object.getAsJsonPrimitive("views").getAsInt());
        builder.setNumLikes(object.getAsJsonPrimitive("points").getAsInt());
        builder.addText(object.getAsJsonPrimitive("title").getAsString());
        builder.addHashtag(object.getAsJsonPrimitive("topic").getAsString());
        builder.addImgLink(object.getAsJsonPrimitive("link").getAsString());
        return builder.build();
    }

}
