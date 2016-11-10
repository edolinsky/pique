package controllers;

import com.google.inject.Inject;
import play.mvc.*;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.RedisAccessObject;
import services.dataAccess.proto.PostListProto.PostList;
import services.serializer.BinarySerializer;
import services.serializer.JsonSerializer;
import services.serializer.Serializer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@Singleton
public class TrendingContentController extends Controller {

    private AbstractDataAccess dataSource;
    private JsonSerializer serializer = new JsonSerializer();

    @Inject
    public TrendingContentController(AbstractDataAccess dataSource) {
        this.dataSource = dataSource;
    }
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result content() {
        Optional<PostList> trendingContent = dataSource.peekAtPostList(
                AbstractDataAccess.DISPLAY_NAMESPACE + AbstractDataAccess.NAMESPACE_DELIMITER + "trending");

        if (trendingContent.isPresent()) {
            return ok(serializer.serialize(trendingContent.get()));
        } else {
            return noContent();
        }
    }

}
