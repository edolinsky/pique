package services.sources;

import services.dataAccess.proto.PostProto.Post;
import facebook4j.Facebook;
import facebook4j.FacebookException;
import facebook4j.FacebookFactory;
import facebook4j.Reading;
import facebook4j.ResponseList;


/**
 * Class that interacts with the Facebook4j library to get data
 *
 * @author Reid Oliveira, Sammie Jiang
 */
public class FacebookSource extends AbstractJavaSource {
	public static final String FACEBOOK = "facebook";
	public static final String URL = "graph.facebook.com";
	public static final String VERSION = "2.8";

	Facebook facebook;

	public FacebookSource() {
		super(FACEBOOK);
		facebook = new FacebookFactory().getInstance();
	}

}
