package controllers;

import akka.actor.ActorSystem;
import play.mvc.*;
import scala.concurrent.ExecutionContextExecutor;
import services.dataAccess.RedisAccessObject;
import services.dataAccess.AbstractDataAccess;
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
public class HashtagContentController extends Controller {

    private final AbstractDataAccess dataSource;
    private final JsonSerializer serializer = new JsonSerializer();

    @Inject
    public HashtagContentController(AbstractDataAccess dataSource) {
        this.dataSource = dataSource;
    }
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result content(String hashtag) {
        Optional<PostList> hashtagContent = dataSource.peekAtPostList("display:" + hashtag);

        if (hashtagContent.isPresent()) {
            return ok(serializer.serialize(hashtagContent.get()));
        } else {
            return noContent();
        }
    }

}
