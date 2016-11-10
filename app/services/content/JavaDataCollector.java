package services.content;

import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostProto.Post;
import services.sources.Source;
import services.sources.TwitterSource;
import twitter4j.Trend;

import java.util.LinkedList;
import java.util.List;
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

		if (trends.isEmpty()) {
			trends.addAll(source.getTrends("canada", "vancouver"));
		}

		notifySubscribers();
		return source.getMaxTrendingPosts(trends.poll());

	}
}
