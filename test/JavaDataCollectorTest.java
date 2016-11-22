import com.sun.corba.se.spi.oa.OADestroyed;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.oauth.OAuthException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import services.content.AbstractDataCollector;
import services.content.JavaDataCollector;
import services.dataAccess.AbstractDataAccess;
import services.dataAccess.InMemoryAccessObject;
import services.dataAccess.proto.PostProto.Post;
import services.sources.TwitterSource;
import services.sources.RedditSource;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class JavaDataCollectorTest {

	AbstractDataAccess data;
	TwitterSource twitterSource;
	RedditSource redditSource;
	AbstractDataCollector twitterCollector;
	AbstractDataCollector redditCollector;

	@Before
	public void before() throws NetworkException, OAuthException {
		data = new InMemoryAccessObject();
		twitterSource = new TwitterSource();
		redditSource = new RedditSource();
		twitterCollector = new JavaDataCollector(data, twitterSource);
		redditCollector = new JavaDataCollector(data, redditSource);
	}

	@Test
	public void testTwitterFetch() {
		List<Post> posts = twitterCollector.fetch();
		assertFalse(posts.isEmpty());
	}

	@Test
	public void testRedditFetch() {
		List<Post> posts = redditCollector.fetch();
		assertFalse(posts.isEmpty());
	}

	@Test
	public void testTwitterPost() {
		List<Post> toStore = twitterCollector.fetch();
		twitterCollector.store(toStore);

		List<Post> retrieved = data.getAllPostsFromSource(twitterCollector.getSource().getSourceName());

		assertEquals(toStore, retrieved);

	}

	@Test
	public void testRedditPost() {
		List<Post> toStore = redditCollector.fetch();
		redditCollector.store(toStore);

		List<Post> retrieved = data.getAllPostsFromSource(redditCollector.getSource().getSourceName());

		assertEquals(toStore, retrieved);

	}
}
