package services.sources;

import services.dataAccess.proto.PostProto;

import java.util.List;

/**
 * Class representing a source where data is obtained through calls to a Java Library.
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public abstract class AbstractJavaSource extends AbstractSource {

	public AbstractJavaSource(String sourceName) {
		super(sourceName);
	}

	public abstract List<PostProto.Post> getTopTrending();
}
