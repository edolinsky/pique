package controllers;

import play.Logger;
import play.mvc.*;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostListProto.PostList;
import services.serializer.JsonSerializer;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;
import static services.PublicConstants.TRENDING;

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

    public Result content(String page) {
        Logger.debug("Trending Content Requested");

        int pageNum;

        try {
            pageNum = Integer.parseInt(page);
        } catch (NumberFormatException nfE) {
            Logger.debug("Trending Content invalid page: " + nfE.getMessage());
            pageNum = 0;    // default to page 0 if given invalid number
        }

        Optional<PostList> trendingContent = dataSource.getDisplayPostList(TRENDING, pageNum);

        if (trendingContent.isPresent()) {
            return ok(serializer.serialize(trendingContent.get()));
        } else {
            return noContent();
        }
    }

}
