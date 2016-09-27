package ca.credits.engine.downloader;

import ca.credits.base.event.IEvent;
import ca.credits.common.Properties;
import ca.credits.engine.AbstractRequest;
import org.apache.commons.lang.StringUtils;
import org.asynchttpclient.Request;
import org.asynchttpclient.RequestBuilder;
import org.asynchttpclient.RequestBuilderBase;

/**
 * Created by chenwen on 16/8/31.
 */
public class AsyncHttpClientRequest extends AbstractRequest {
    /**
     * async http request
     */
    private Request request;

    public AsyncHttpClientRequest(IEvent event){
        super(event);
        buildRequest();
    }

    /**
     * build async http request
     */
    private void buildRequest(){
        RequestBuilderBase<RequestBuilder> requestBuilder = new RequestBuilder(getMethod().getValue());

        requestBuilder.setUrl(getUrl());

        requestBuilder.setHeader(HttpHeader.USERAGENT.getValue(),getUserAgent());

        if (StringUtils.isNotEmpty(getReferer())) {
            requestBuilder.setHeader(HttpHeader.REFERER.getValue(), getReferer());
        }


        request = requestBuilder.build();
    }

    /**
     * get request
     * @return request
     */
    public Request getRequest() {
        return request;
    }
}
