package ca.credits.queue;

/**
 * Created by chenwen on 16/9/20.
 */
public class RequestMessage extends Message{
    /**
     * serial version
     */
    private static final long serialVersionUID = 1L;

    private String url;

    public RequestMessage(String url){
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
