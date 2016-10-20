package controllers;

import com.google.inject.Inject;
import play.mvc.*;
import play.libs.ws.*;
import java.util.concurrent.CompletionStage;
import com.fasterxml.jackson.databind.JsonNode;
import play.libs.Json;

import views.html.*;

/**
 * This controller contains an action to handle HTTP requests
 * to the application's home page.
 */
public class HashtagContentController extends Controller {

    @Inject
    private WSClient ws;
    /**
     * An action that renders an HTML page with a welcome message.
     * The configuration in the <code>routes</code> file means that
     * this method will be called when the application receives a
     * <code>GET</code> request with a path of <code>/</code>.
     */
    public Result index() {
        return ok(index.render("Pique"));
    }

}
