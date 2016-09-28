package ca.credits.business;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.util.QueueInfoUtil;
import ca.credits.deep.IFailedListener;
import ca.credits.queue.EventController;
import ca.credits.queue.SendRefuseException;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;

/**
 * Created by chenwen on 16/9/28.
 */
@Slf4j
public class DeepCrawlerFailedListener implements IFailedListener {
    private PlatformCodeEnum.INameCode nameCode;
    private EventController eventController;

    public DeepCrawlerFailedListener(PlatformCodeEnum.INameCode nameCode, EventController eventController){
        this.nameCode = nameCode;
        this.eventController = eventController;
    }
    @Override
    public void onError(Request request, Site site) {
        log.error("请求失败 {} ",request.getUrl(),new Exception());
        try {
            eventController.getEventTemplate().send(QueueInfoUtil.getFailedQueueInfo(nameCode),request);
        } catch (SendRefuseException e) {
            log.error("推送failed queue发生异常 url = {}", request.getUrl(), e);
        }
    }
}
