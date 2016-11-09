import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import services.content.AbstractDataCollector;
import services.content.JavaDataCollector;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sources.TwitterSource;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JavaDataCollectorTest {

	AbstractDataAccess data;
	TwitterSource source;
	AbstractDataCollector collector;

	@Before
	public void before() {
		data = new InMemoryAccessObject();
		source = new TwitterSource();
		collector = new JavaDataCollector(data, source);
	}

	@Test
	public void testFetch() {
		List<Post> posts = collector.fetch();
		assertFalse(posts.isEmpty());
	}

	@Test
	public void testPost() {
		List<Post> toStore = collector.fetch();
		collector.store(toStore);

		List<Post> retrieved = data.getAllPosts(collector.getSource().getSourceName());

		assertEquals(toStore, retrieved);

	}
}
