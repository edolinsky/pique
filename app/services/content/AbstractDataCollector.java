package services.content;

import services.sources.AbstractSource;
import services.storage.AbstractDataAccess;

public abstract class AbstractDataCollector {
	AbstractDataAccess dataAccess;
	AbstractSource source;

	public abstract AbstractPost fetch();

	public abstract void post(AbstractPost post);
}
