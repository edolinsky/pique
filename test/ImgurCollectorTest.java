import org.junit.Before;
import org.junit.Test;
import services.content.RestfulDataCollector;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sources.ImgurSource;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class ImgurCollectorTest {

    AbstractDataAccess data;
    ImgurSource source;
    RestfulDataCollector collector;

    @Before
    public void before() {
        data = new InMemoryAccessObject();
        source = new ImgurSource();
        collector = new RestfulDataCollector(data, source);
    }

    @Test
    public void testRequest() {
        String response = collector.makeRequest();
        assertFalse(response.isEmpty());
    }

    @Test
    public void testParse() {
        String response = ImgurTestData.getTestJson();
        List<Post> posts = source.getPostsSince(response);
        assertFalse(posts.isEmpty());

    }

    private static class ImgurTestData {

        public static String getTestJson() {
            return "{\"data\":[{\"id\":\"abc1\",\"title\":\"title of post1\","
                    + "\"description\":null,\"datetime\":1479660371,\"cover\":\"haSvc8a\","
                    + "\"cover_width\":594,\"cover_height\":686,\"account_url\":\"fakeAccount1\","
                    + "\"account_id\":121,\"privacy\":\"hidden\",\"layout\":\"blog\","
                    + "\"views\":121,\"link\":\"http:\\/\\/imgur.com\\/a\\/abc1\",\"ups\":1111,"
                    + "\"downs\":111,\"points\":1234,\"score\":1234,\"is_album\":true,"
                    + "\"vote\":null,\"favorite\":false,\"nsfw\":false,\"section\":\"\","
                    + "\"comment_count\":123,\"topic\":\"Funny\",\"topic_id\":2,"
                    + "\"images_count\":1,\"in_gallery\":true,\"is_ad\":false},\n"
                    + "{\"id\":\"abc2\",\"title\":\"title of post2\",\"description\":null,"
                    + "\"datetime\":1479674704,\"cover\":\"9EoadVD\",\"cover_width\":1440,"
                    + "\"cover_height\":2560,\"account_url\":\"fakeAccount2\",\"account_id\":122,"
                    + "\"privacy\":\"hidden\",\"layout\":\"blog\",\"views\":122,"
                    + "\"link\":\"http:\\/\\/imgur.com\\/a\\/abc2\",\"ups\":222,\"downs\":111,"
                    + "\"points\":333,\"score\":4321,\"is_album\":true,\"vote\":null,"
                    + "\"favorite\":false,\"nsfw\":false,\"section\":\"someSection\","
                    + "\"comment_count\":14,\"topic\":\"No Topic\",\"topic_id\":29,"
                    + "\"images_count\":1,\"in_gallery\":true,\"is_ad\":false},{\"id\":\"abc3\","
                    + "\"title\":\"title of post3\",\"description\":null,\"datetime\":1479662463,"
                    + "\"cover\":\"Fu08CGh\",\"cover_width\":623,\"cover_height\":473,"
                    + "\"account_url\":\"fakeAccount3\",\"account_id\":123,"
                    + "\"privacy\":\"hidden\",\"layout\":\"blog\",\"views\":123,"
                    + "\"link\":\"http:\\/\\/imgur.com\\/a\\/abc3\",\"ups\":333,\"downs\":111,"
                    + "\"points\":1010,\"score\":11111,\"is_album\":true,\"vote\":null,"
                    + "\"favorite\":false,\"nsfw\":false,\"section\":\"\",\"comment_count\":1016,"
                    + "\"topic\":\"Current Events\",\"topic_id\":17,\"images_count\":1,"
                    + "\"in_gallery\":true,\"is_ad\":false}],\"success\":true,\"status\":200}";
        }

        public static List<Post> expectedResult() {
            return null;
        }
    }
}
