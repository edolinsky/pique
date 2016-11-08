package controllers;

import com.google.inject.Inject;
import play.mvc.*;
import services.dataAccess.TestDataAccess;
import services.dataAccess.RedisAccessObject;
import services.dataAccess.InMemoryAccessObject;
/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HashtagContentController extends Controller {

    @Inject
    private TestDataAccess dataSource = new TestDataAccess();
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result content(String hashtag) {
        byte[] hashtagContent = dataSource.peekAt(hashtag);

        if (hashtagContent.length != 0) {
            return ok(hashtagContent);
        } else {
            return noContent();
        }
    }

}
