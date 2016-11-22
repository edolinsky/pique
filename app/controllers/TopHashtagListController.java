package controllers;

import play.Logger;
import play.mvc.*;
import services.dataAccess.AbstractDataAccess;
import services.serializer.JsonSerializer;
import static services.PublicConstants.NUM_TOP_HASHTAGS;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class TopHashtagListController extends Controller{

    private AbstractDataAccess dataSource;
    private JsonSerializer serializer = new JsonSerializer();

    @Inject
    public TopHashtagListController(AbstractDataAccess dataSource) { this.dataSource = dataSource; }

    public Result content() {
        Logger.debug("Top Hashtags requested");

        List<String> topHashtags = dataSource.getTopHashTags(NUM_TOP_HASHTAGS);

        if (topHashtags.size() > 0) {
            return ok(serializer.serialize(topHashtags));
        } else {
            return noContent();
        }
    }



}
