package services.content;

import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostProto.Post;
import services.sources.Source;

import java.util.List;
import java.util.PriorityQueue;

/**
 * This class is the abstract level representation of an entity that collects data for our
 * system. It has an {@link AbstractDataAccess} object to store results in.
 *
 * A DataCollector class is responsible for moderating requests out to it's destination source,
 * notifying others of incoming data, and storing the data in the data tier. Essentially
 * it is what transitions data from being external to internal and making sure to keep
 * track of its query schedule (what and when).
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public abstract class AbstractDataCollector {
	AbstractDataAccess dataAccess;

	public AbstractDataCollector(AbstractDataAccess dataAccess) {
		this.dataAccess = dataAccess;
	}

	/**
	 * Provides the source used for this collector
	 * @return
	 */
	public abstract Source getSource();

	/**
	 * Retrieves a list of posts from the source
	 * @return
	 */
	public abstract List<Post> fetch();

	/**
	 * Store a list of posts within the data storage
	 * @param posts
	 * @return the new size of the list of posts after the list is added
	 */
	public long store(List<Post> posts){
		return dataAccess.addNewPostsFromSource(getSource().getSourceName(), posts);
	}

	/**
	 * Grabs a list of posts and puts it into the data tier
	 * @return the number of posts collected
	 */
	public int collect() {
		List<Post> posts = fetch();
        store(posts);
        return posts.size();
	}


}
