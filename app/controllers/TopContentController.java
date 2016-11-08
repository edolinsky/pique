package controllers;

import com.google.inject.Inject;
import play.mvc.*;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.RedisAccessObject;
import services.dataAccess.proto.PostListProto.PostList;
import services.serializer.BinarySerializer;

import java.util.Optional;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class TopContentController extends Controller {


    private AbstractDataAccess dataSource = new RedisAccessObject();
    private BinarySerializer serializer = new BinarySerializer();


    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */

    public Result content() {

        Optional<PostList> topContent = dataSource.peekAtPostList("display:top");

        if (topContent.isPresent()) {
            return ok(serializer.serialize(topContent.get()));
        } else {
            return noContent();
        }

    }

}
