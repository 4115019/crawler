package ca.credits.deep;

import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;

/**
 * Created by chenwen on 16/9/20.
 */
public interface IFailedListener {
    void onError(Request request, Site site);
}
