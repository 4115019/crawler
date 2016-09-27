package ca.credits.engine.downloader;

import ca.credits.engine.AbstractResponse;
import ca.credits.engine.AbstractRequest;
import org.asynchttpclient.Response;

/**
 * Created by chenwen on 16/8/31.
 */
public class AsyncHttpClientResponse extends AbstractResponse {
    /**
     * async http request
     */
    private Response response;

    /**
     * request
     */
    private AbstractRequest request;

    public AsyncHttpClientResponse(AbstractRequest request, Response response){
        super(request,response.getStatusCode(),response.getResponseBody(request.getCharset()));
        this.request = request;
        this.response = response;
    }

    @Override
    public AbstractRequest getRequest() {
        return request;
    }

    public Response getResponse() {
        return response;
    }
}
