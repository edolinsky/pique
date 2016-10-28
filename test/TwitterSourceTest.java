import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import services.dataAccess.proto.PostProto;
import services.dataAccess.proto.PostProto.Post;
import services.sources.TwitterSource;

import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TwitterSourceTest {

	TwitterSource twitterSource;

	@Before
	public void before() {
		twitterSource = new TwitterSource();
	}

	@Test
	public void testGetTopTrending() {
		List<Post> list = twitterSource.getTopTrending();
		assertFalse(list.isEmpty());
	}
}
