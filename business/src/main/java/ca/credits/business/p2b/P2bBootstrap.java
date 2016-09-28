package ca.credits.business.p2b;

import ca.credits.business.DeepCrawlerFailedListener;
import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.pipeline.DynamodbPipeline;
import ca.credits.business.util.EventControlUtil;
import ca.credits.business.util.QueueInfoUtil;
import ca.credits.common.filter.IDuplicateFilter;
import ca.credits.common.filter.RedisHashSetDuplicateFilter;
import ca.credits.common.util.RedissonUtil;
import ca.credits.deep.ISiteGen;
import ca.credits.deep.RabbitSpider;
import ca.credits.deep.scheduler.RabbitmqDuplicateScheduler;
import ca.credits.deep.scheduler.RedisDuplicateRemover;
import ca.credits.queue.EventController;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.SendRefuseException;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.utils.HttpProxyUtil;

import java.util.Collection;

/**
 * Created by chenwen on 16/9/28.
 */
@Slf4j
public class P2bBootstrap {
    public static void start(PlatformCodeEnum.INameCode nameCode, PageProcessor pageProcessor,Double permitsPerSecond, Request startRequest){
        start(nameCode,pageProcessor,permitsPerSecond,request -> Site.me().setRetryTimes(3).setSleepTime(100).httpProxy(HttpProxyUtil.getHttpProxy()),startRequest);
    }

    public static void start(PlatformCodeEnum.INameCode nameCode, PageProcessor pageProcessor,Double permitsPerSecond, Collection<Request> startRequest){
        start(nameCode,pageProcessor,permitsPerSecond,request -> Site.me().setRetryTimes(3).setSleepTime(100).httpProxy(HttpProxyUtil.getHttpProxy()),startRequest);
    }

    public static void start(PlatformCodeEnum.INameCode nameCode, PageProcessor pageProcessor, Double permitsPerSecond, ISiteGen siteGen, Request startRequest){
        start(nameCode,pageProcessor,permitsPerSecond,siteGen,new RedisHashSetDuplicateFilter(nameCode.getCode(),RedissonUtil.getInstance().getRedisson()),startRequest);
    }

    public static void start(PlatformCodeEnum.INameCode nameCode, PageProcessor pageProcessor, Double permitsPerSecond, ISiteGen siteGen, Collection<Request> startRequest){
        start(nameCode,pageProcessor,permitsPerSecond,siteGen,new RedisHashSetDuplicateFilter(nameCode.getCode(),RedissonUtil.getInstance().getRedisson()),startRequest);
    }

    public static void start(PlatformCodeEnum.INameCode nameCode, PageProcessor pageProcessor, Double permitsPerSecond, IDuplicateFilter duplicateFilter, Request startRequest){
        start(nameCode,pageProcessor,permitsPerSecond,request -> Site.me().setRetryTimes(3).setSleepTime(100).httpProxy(HttpProxyUtil.getHttpProxy()),duplicateFilter,null,startRequest);
    }

    public static void start(PlatformCodeEnum.INameCode nameCode, PageProcessor pageProcessor, Double permitsPerSecond, IDuplicateFilter duplicateFilter, Collection<Request> startRequest){
        start(nameCode,pageProcessor,permitsPerSecond,request -> Site.me().setRetryTimes(3).setSleepTime(100).httpProxy(HttpProxyUtil.getHttpProxy()),duplicateFilter,startRequest,null);
    }

    public static void start(PlatformCodeEnum.INameCode nameCode, PageProcessor pageProcessor, Double permitsPerSecond, ISiteGen siteGen, IDuplicateFilter duplicateFilter, Request startRequest){
        start(nameCode,pageProcessor,permitsPerSecond,siteGen,duplicateFilter,null,startRequest);
    }

    public static void start(PlatformCodeEnum.INameCode nameCode, PageProcessor pageProcessor, Double permitsPerSecond, ISiteGen siteGen, IDuplicateFilter duplicateFilter, Collection<Request> startRequest){
        start(nameCode,pageProcessor,permitsPerSecond,siteGen,duplicateFilter,startRequest,null);
    }

    private static void start(PlatformCodeEnum.INameCode nameCode, PageProcessor pageProcessor, Double permitsPerSecond, ISiteGen siteGen, IDuplicateFilter duplicateFilter, Collection<Request> startRequests,Request startRequest){
        try {
            QueueInfo queueInfo = QueueInfoUtil.getQueueInfo(nameCode);
            EventController eventController = EventControlUtil.getEventController();
            Scheduler scheduler = new RabbitmqDuplicateScheduler(eventController).setDuplicateRemover(new RedisDuplicateRemover(duplicateFilter));
            RabbitSpider spider = RabbitSpider
                    .create(queueInfo, pageProcessor, scheduler)
                    .siteGen(siteGen)
                    .permitsPerSecond(permitsPerSecond)
                    .listener(new DeepCrawlerFailedListener(nameCode, eventController))
                    .pipelines(new DynamodbPipeline(P2bTemplate.TABLE_NAME));
            sendStartRequest(eventController,queueInfo,startRequest);
            if (startRequests != null){
                startRequests.parallelStream().forEach(request -> {
                    try {
                        scheduler.push(request,spider);
                    } catch (PushFailedException e) {
                        log.error("推送失败",e);
                        System.exit(-1);
                    }
                });
            }
            eventController.add(queueInfo, spider);
            eventController.start();
        }catch (Exception e){
            log.error("启动失败",e);
            System.exit(-1);
        }
    }

    private static void sendStartRequest(EventController eventController , QueueInfo queueInfo , Request request) throws SendRefuseException {
        if (request != null) {
            eventController.getEventTemplate().send(queueInfo, request);
        }
    }
}
