package services.content;

import services.dataAccess.AbstractDataAccess;

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

	public abstract Post fetch();

	public void store(Post post){
		// TODO
	}

	public void collect() {
		store(fetch());
	}
}
