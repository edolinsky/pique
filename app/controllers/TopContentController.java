package controllers;

import play.Logger;
import play.mvc.*;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostListProto.PostList;
import services.serializer.JsonSerializer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
@Singleton
public class TopContentController extends Controller {

    private AbstractDataAccess dataSource;
    private JsonSerializer serializer = new JsonSerializer();

    @Inject
    public TopContentController(AbstractDataAccess dataSource) {
        this.dataSource = dataSource;
    }

    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result content() {

        Logger.trace("Top Content Requested");
        Optional<PostList> topContent = dataSource.getDisplayPostList("top", 0); // todo: implement paging

        if (topContent.isPresent()) {
            return ok(serializer.serialize(topContent.get()));
        } else {
            return noContent();
        }

    }

}
