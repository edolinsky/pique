package services.content;

import services.dataAccess.AbstractDataAccess;
import services.dataAccess.proto.PostProto.Post;
import services.sources.AbstractSource;

import java.util.List;

/**
 * This class is the abstract level representation of an entity that collects data for our
 * system. It has an {@link AbstractDataAccess} object to store results in.
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public abstract class AbstractDataCollector {
	AbstractDataAccess dataAccess;

	public AbstractDataCollector(AbstractDataAccess dataAccess) {
		this.dataAccess = dataAccess;
	}

	public abstract AbstractSource getSource();
	public abstract List<Post> fetch();

	private void store(List<Post> posts){
//		dataAccess.addNewPosts(getSource().getSourceName(), posts);
	}

	public void collect() {
		store(fetch());
	}
}
