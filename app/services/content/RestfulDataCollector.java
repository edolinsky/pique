package services.content;

import services.dataAccess.AbstractDataAccess;
import services.sources.AbstractRestfulSource;

/**
 * This class is capable of making RESTful API calls to collect data, given an
 * {@link AbstractRestfulSource} to collect from and an {@link AbstractDataAccess} to place the
 * results
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class RestfulDataCollector extends AbstractDataCollector {

	AbstractRestfulSource source;

	public RestfulDataCollector(AbstractDataAccess dataAccess, AbstractRestfulSource source) {
		super(dataAccess);
		this.source = source;
	}

	@Override
	public Post fetch() {
		return null;
	}

	@Override
	public void store(Post post) {

	}
}
