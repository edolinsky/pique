package controllers;

import com.google.inject.Inject;
import play.mvc.*;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.RedisAccessObject;
import services.dataAccess.InMemoryAccessObject;

import java.util.Optional;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HashtagContentController extends Controller {

    @Inject
    private InMemoryAccessObject dataSource = new InMemoryAccessObject();
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result content(String hashtag) {
        Optional<byte[]> hashtagContent = dataSource.peekAtByte("display:" + hashtag);

        if (hashtagContent.isPresent()) {
            return ok(hashtagContent.get());
        } else {
            return noContent();
        }
    }

}
