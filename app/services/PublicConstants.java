package services;

public class PublicConstants {
    // runtime environment switch
    public static final String RUNTIME_ENVIRONMENT = "runtime_env";

    // http method
    public static final String HTTP_GET = "GET";

    // special characters
    public static final String HASHTAG = "%23";


    /** data collection **/

    public static final Integer MAX_TRACKED_TRENDS = 10000;

    // twitter auth
    public static final String TWITTER4J_CONSUMER_KEY = "twitter4j_consumerKey";
    public static final String TWITTER4J_CONSUMER_SECRET = "twitter4j_consumerSecret";
    public static final String TWITTER4J_ACCESS_TOKEN = "twitter4j_accessToken";
    public static final String TWITTER4J_ACCESS_TOKEN_SECRET = "twitter4j_accessTokenSecret";

    // imgur auth
    public static final String IMGUR_APP_ID = "imgur_client_id";
    public static final String IMGUR_SECRET = "imgur_client_secret";

    /** data access & sorting **/

    // data access
    public static final String REDDIT_USER = "reddit_user";
    public static final String REDDIT_PASS = "reddit_pass";
    public static final String REDDIT_CLIENTID = "reddit_client_id";
    public static final String REDDIT_SECRET = "reddit_secret";

    public static final String DATA_SOURCE = "data_source";
    public static final String REDIS_URL = "redis_url";
    public static final String REDIS_PORT = "redis_port";

    public static final String TOP = "top";
    public static final String TRENDING = "trending";
    public static final String TOP_HASHTAGS = "tophashtags";

    // sorting
    public static final String SORTING_NODE_INPUT_THRESHOLD = "sorting_threshold";
    public static final String POSTS_PER_PAGE = "posts_per_page";
    public static final int NUM_TOP_HASHTAGS = 10;
}
