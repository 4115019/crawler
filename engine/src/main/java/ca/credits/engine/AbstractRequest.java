package ca.credits.engine;

import ca.credits.base.config.CrawlerConfigDefaults;
import ca.credits.base.event.IEvent;
import ca.credits.base.kit.Constants;
import ca.credits.base.queue.IDuplicateKey;
import ca.credits.common.util.ClassLoaderUtils;
import ca.credits.common.Properties;
import ca.credits.common.util.UserAgents;
import ca.credits.engine.parser.IResponseProcessor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;

import java.nio.charset.Charset;

/**
 * Created by chenwen on 16/8/19.
 */
@Slf4j
public abstract class AbstractRequest implements Constants.RequestParams,IDuplicateKey{
    @Getter
    private String url;

    @Getter
    private Charset charset;

    @Getter
    private HttpMethod method;

    @Getter
    private String userAgent;

    @Getter
    private String referer;

    @Getter
    private long timeout;

    @Getter
    private IEvent event;

    @Getter
    private IResponseProcessor responseProcessor;

    @Getter
    private Properties properties;

    protected AbstractRequest(IEvent event){
        this.event = event;
        this.properties = event.getNode().getProperties();
        init();
    }

    /**
     * init all params
     */
    private void init(){
        this.url = properties.getString(URL);

        final String charset = properties.getString(CHARSET);
        this.charset = StringUtils.isNotEmpty(charset) ? Charset.forName(charset) : Charset.defaultCharset();

        this.method = HttpMethod.fromValue(properties.getString(METHOD));

        final Long timeout = properties.getLong(TIMEOUT);
        this.timeout = timeout == null ? CrawlerConfigDefaults.defaultHTTPTimeout() :timeout;


        final Boolean isRandomUserAgent = properties.getBoolean(IS_RANDOM_USER_AGET);

        if (isRandomUserAgent != null && isRandomUserAgent){
            this.userAgent = UserAgents.getRandomUserAgent();
        }else {
            final String userAgent = properties.getString(USERAGENT);
            this.userAgent = StringUtils.isNotEmpty(userAgent) ? userAgent : UserAgents.getUserAgent(5);
        }

        final String referer = properties.getString(REFERER);

        this.referer = StringUtils.isNotEmpty(referer) ? referer : null;

        final String responseProcessor = properties.getString(Constants.RESPONSE_PROCESSOR);

        try {
            this.responseProcessor = StringUtils.isNotEmpty(responseProcessor) ? ClassLoaderUtils.getClassInstance(responseProcessor) : null;
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException e) {
            log.error("can not find class {}",responseProcessor,e);
            throw new IllegalArgumentException("event args is illegal");
        }
    }


    public enum HttpMethod{
        CONNECT,
        DELETE,
        GET,
        HEAD,
        OPTIONS,
        ATCH,
        POST,
        PUT,
        TRACE;

        public String getValue(){
            return this.name();
        }

        public static HttpMethod fromValue(String method){
            for(HttpMethod value : values()){
                if (value.name().equalsIgnoreCase(method)){
                    return value;
                }
            }
            return GET;
        }
    }


    public enum HttpHeader{
        USERAGENT("User-Agent"),
        REFERER("Referer");

        private final String value;

        HttpHeader(String value){
            this.value = value;
        }
        public String getValue(){
            return this.value;
        }

        public static HttpHeader fromValue(String method){
            for(HttpHeader value : values()){
                if (value.getValue().equalsIgnoreCase(method)){
                    return value;
                }
            }
            return null;
        }
    }

    @Override
    public String getDuplicateKey() {
        return event.getNode().getDuplicateKey();
    }
}
