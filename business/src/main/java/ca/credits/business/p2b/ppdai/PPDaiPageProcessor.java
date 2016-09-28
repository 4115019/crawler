package ca.credits.business.p2b.ppdai;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bTemplate;
import ca.credits.business.pipeline.DynamodbPipeline;
import ca.credits.business.util.EventControlUtil;
import ca.credits.business.util.QueueInfoUtil;
import ca.credits.common.config.Config;
import ca.credits.common.filter.RedisBloomDuplicateFilter;
import ca.credits.common.filter.RedisHashSetDuplicateFilter;
import ca.credits.common.util.RedissonUtil;
import ca.credits.deep.RabbitSpider;
import ca.credits.deep.scheduler.RabbimqDuplicateScheduler;
import ca.credits.deep.scheduler.RedisBloomFilterDuplicateRemover;
import ca.credits.queue.*;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;
import us.codecraft.webmagic.utils.HttpProxyUtil;

import java.util.List;

/**
 * Created by chenwen on 16/9/12.
 */
@Slf4j
public class PPDaiPageProcessor implements PageProcessor {
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        List<String> detailList = page.getHtml().xpath("//input[@onclick]").regex("/blacklistdetail/pdu[0-9]+").all();
        detailList.stream().forEach(detail -> page.addTargetRequest(String.format("http://www.ppdai.com%s",detail)));
        List<String> all = page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/div/ul/li").regex("[0-9]+").all();
        all.stream().forEach(node -> page.addTargetRequest(String.format("http://www.ppdai.com/blacklist/%s_m0",node)));
        page.addTargetRequests(page.getHtml().links().regex(".*/blacklist/.*").all());

        if (page.getRequest().getUrl().startsWith("http://www.ppdai.com/blacklistdetail")) {
            String text = page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[1]/div[1]/span/text()").get();
            if (StringUtils.isNotEmpty(text)) {
                P2bTemplate template = new P2bTemplate(PlatformCodeEnum.P2B.PPDAI);
                template.setUserId(page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/ul/li/text()").regex("用户名：*(pdu[0-9]+) *姓名：",1).get());
                template.setName(page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/ul/li/text()").regex("姓名：*([^ ]+) *手机号：",1).get());
                template.setPhone(page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/ul/li/text()").regex("手机号：*([^ ]+) *身份证号：",1).get());
                template.setCustId(page.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/ul/li/text()").regex("身份证号：*([^ ]+)",1).get());
                template.setBadFlag(PlatformCodeEnum.BadReason.OVERDUE.getCode());
                page.addTemplate(template);
            }
        }

    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws PushFailedException, SendRefuseException {
        QueueInfo queueInfo = QueueInfoUtil.getQueueInfo(PlatformCodeEnum.P2B.PPDAI);
        EventController eventController = EventControlUtil.getEventController();
        RabbitSpider spider = RabbitSpider
                .create(queueInfo,new PPDaiPageProcessor(),new RabbimqDuplicateScheduler(eventController).setDuplicateRemover(new RedisBloomFilterDuplicateRemover(new RedisHashSetDuplicateFilter(PlatformCodeEnum.P2B.PPDAI.getCode(), RedissonUtil.getInstance().getRedisson()))))
                .siteGen(request -> Site.me().setRetryTimes(3).setSleepTime(100).httpProxy(HttpProxyUtil.getHttpProxy()))
                .rateLimiter(RateLimiter.create(1))
                .listener((request, site1) -> log.error("请求失败",request.getUrl(),new Exception()))
                .pipelines(new DynamodbPipeline(P2bTemplate.TABLE_NAME));
        eventController.getEventTemplate().send(queueInfo,new Request("http://www.ppdai.com/blacklist"));
        eventController.add(queueInfo,spider);
        eventController.start();
    }
}
