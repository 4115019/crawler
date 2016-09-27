package ca.credits.deep;

import ca.credits.queue.EventProcessor;
import ca.credits.queue.QueueInfo;
import com.google.common.util.concurrent.RateLimiter;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.time.StopWatch;
import us.codecraft.webmagic.*;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.pipeline.ConsolePipeline;
import us.codecraft.webmagic.pipeline.Pipeline;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;
import us.codecraft.webmagic.scheduler.Scheduler;
import us.codecraft.webmagic.utils.UrlUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Created by chenwen on 16/9/20.
 */
@Slf4j
public class RabbitSpider implements EventProcessor<Request>,Task{
    protected Downloader downloader;

    protected List<Pipeline> pipelines = new ArrayList<>();

    protected PageProcessor pageProcessor;

    protected List<Request> startRequests;

    protected Scheduler scheduler;

    protected Site site;

    protected String uuid;

    @Getter
    protected QueueInfo queueInfo;

    protected ISiteGen siteGen;

    protected RateLimiter rateLimiter;

    protected List<IFailedListener> listeners = new ArrayList<>();

    public static RabbitSpider create(QueueInfo queueInfo,PageProcessor pageProcessor,Scheduler scheduler){
        return new RabbitSpider(queueInfo,pageProcessor,scheduler);
    }

    /**
     * create a spider with pageProcessor.
     *
     * @param pageProcessor
     */
    public RabbitSpider(QueueInfo queueInfo,PageProcessor pageProcessor,Scheduler scheduler) {
        this.queueInfo = queueInfo;
        this.pageProcessor = pageProcessor;
        this.scheduler = scheduler;
        this.startRequests = pageProcessor.getSite().getStartRequests();
        this.site = pageProcessor.getSite();
    }

    public RabbitSpider listener(IFailedListener... listeners){
        for(IFailedListener listener : listeners){
            this.listeners.add(listener);
        }
        return this;
    }

    public RabbitSpider rateLimiter(RateLimiter rateLimiter){
        this.rateLimiter = rateLimiter;
        return this;
    }

    public RabbitSpider siteGen(ISiteGen siteGen){
        this.siteGen = siteGen;
        return this;
    }

    public RabbitSpider startRequest(List<Request> startRequests){
        this.startRequests = startRequests;
        return this;
    }

    public RabbitSpider download(Downloader download){
        this.downloader = download;
        return this;
    }

    public RabbitSpider pipelines(Pipeline... pipelines){
        for(Pipeline pipeline : pipelines){
            this.pipelines.add(pipeline);
        }
        return this;
    }

    protected void initComponent() throws PushFailedException {
        if (downloader == null){
            synchronized (RabbitSpider.class){
                if (downloader == null){
                    downloader = new HttpClientDownloader();
                }
            }
        }

        if (pipelines.isEmpty()){
            synchronized (RabbitSpider.class){
                if (pipelines.isEmpty()){
                    pipelines.add(new ConsolePipeline());
                }
            }
        }

        if (CollectionUtils.isNotEmpty(startRequests)){
            synchronized (RabbitSpider.class){
                if (CollectionUtils.isNotEmpty(startRequests)) {
                    for (Request request : startRequests) {
                        this.push(request);
                    }
                    startRequests.clear();
                }
            }
        }
    }

    @Override
    public void process(Request request) throws Exception {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        log.info("start request url = {}",request.getUrl());
        boolean isSuccess = false;
        long retryTimes = request.getExtras().getLong(Request.RETRY_TIMES,0L);
        Site site = null;
        try {
            /**
             * step 1: if rate limiter not null , rate limiter acquire
             */
            if (rateLimiter != null){
                rateLimiter.acquire();
            }

            /**
             * step 2: init component
             */
            initComponent();

            if (siteGen != null){
                site = siteGen.gen(request);
            }

            if (site == null){
                site = getSite();
            }

            /**
             * step 3: start download page
             */
            Page page = downloader.download(request,site);

            if (page == null){
                sleep(site.getRetrySleepTime());
            }else {
                pageProcessor.process(page);
                extractAndAddRequests(page);
                if (!page.getResultItems().isSkip()) {
                    for (Pipeline pipeline : pipelines) {
                        pipeline.process(page.getResultItems(), this);
                    }
                }
                sleep(site.getSleepTime());
                isSuccess = true;
            }
        }catch (Throwable e){
            log.error("exception request url = {} retryTimes = {} ",request.getUrl(),retryTimes,e);
        }finally {
            if (!isSuccess){
                try {
                    if (retryTimes < site.getRetryTimes()) {
                        request.putExtra(Request.RETRY_TIMES,retryTimes+1);
                        push(request);
                    } else {
                        onError(request,site);
                    }
                }catch (Throwable e){
                    onError(request,site);
                }
            }
            stopWatch.stop();
            log.info("finally request url = {} isSuccess = {} retryTimes = {} costTime = {} ms",request.getUrl(),isSuccess,retryTimes,stopWatch.getTime());
        }
    }

    public void push(Request request) throws PushFailedException {
        if (site.getDomain() == null && request != null && request.getUrl() != null) {
            site.setDomain(UrlUtils.getDomain(request.getUrl()));
        }
        scheduler.push(request,this);
    }

    public void extractAndAddRequests(Page page) throws PushFailedException {
        if (CollectionUtils.isNotEmpty(page.getTargetRequests())){
            for (Request request : page.getTargetRequests()) {
                push(request);
            }
        }
    }

    protected void onError(Request request,Site site) {
        if (CollectionUtils.isNotEmpty(listeners)) {
            for (IFailedListener spiderListener : listeners) {
                spiderListener.onError(request,site);
            }
        }
    }

    @Override
    public String getUUID() {
        return uuid == null ? UUID.randomUUID().toString() : uuid;
    }

    @Override
    public Site getSite() {
        return site;
    }

    public void sleep(long mis){
        try {
            Thread.sleep(mis);
        } catch (InterruptedException e) {
            log.error("sleep InterruptedException",e);
        }
    }
}
