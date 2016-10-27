package controllers;

import com.google.inject.Inject;
import play.mvc.*;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.RedisAccessObject;

import java.util.Optional;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class TrendingContentController extends Controller {

    @Inject
    private InMemoryAccessObject dataSource = new InMemoryAccessObject();
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result content() {
        Optional<byte[]> trendingContent = dataSource.peekAtByte("display:trending");

        if (trendingContent.isPresent()) {
            return ok(trendingContent.get());
        } else {
            return noContent();
        }
    }

}
