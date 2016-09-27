package ca.credits.base.kit;

/**
 * Created by chenwen on 16/9/14.
 */
public final class Constants {
    public final static String DYNAMIC_NODES = "dynamic.nodes";

    public final static String RESPONSE_PROCESSOR = "response.processor";

    public final static String DUPLICATE_KEY = "duplicate.key";

    public interface RequestParams{
        public final static String URL = "url";
        public final static String METHOD = "method";
        public final static String CHARSET = "charset";
        public final static String TIMEOUT = "timeout";
        public final static String IS_RANDOM_USER_AGET = "is.random.user.agent";
        public final static String USERAGENT = "user.agent";
        public final static String REFERER = "referer";

        interface Headers{
            public final static String REFERER = "referer";
        }
    }
}
