package services.storage;

import services.content.Post;

public abstract class AbstractDataAccess {

	public abstract void store(Post post);
}
