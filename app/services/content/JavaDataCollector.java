package services.content;

import services.PublicConstants;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostProto.Post;
import services.sources.Source;
import services.sources.TwitterSource;
import twitter4j.Trend;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * This class interacts with an {@link Source} to collect data through a Java
 * library and an {@link AbstractDataAccess} to place the results
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class JavaDataCollector extends AbstractDataCollector {

	private TwitterSource source;
	private Queue<Trend> trends = new LinkedList<>();
	private Map<String, Long> sinceIds = new PostIdCache();

	public JavaDataCollector(AbstractDataAccess dataAccess, TwitterSource source) {
		super(dataAccess);
		this.source = source;
	}

	@Override
	public Source getSource() {
		return source;
	}

	@Override
	public List<Post> fetch() {
		/**
		 * Unlike the RestfulDataCollector, JavaDataCollectors will have very little common
		 * behaviour and instead depend heavily on their library functions, so we offload the
		 * work to the source object.
		 */

        // if no trends exist for this collector, retrieve them
		if (trends.isEmpty()) {
            trends.addAll(source.getTrends("canada", "vancouver"));
		}

        // get the top trend not yet queried
        Trend trend = trends.poll();
        List<Post> posts;

        // if we have queried this trend before only get newer posts
        if (sinceIds.containsKey(trend.getName())) {
            posts = source.getMaxTrendingPostsSince(trend, sinceIds.get(trend.getName()));
        } else {
            posts = source.getMaxTrendingPosts(trend);
        }

        // overwrite set the newest id queried to the newest tweet retrieved
        sinceIds.put(trend.getName(), Long.parseLong(posts.get(0).getId()));
        return posts;
	}
}
