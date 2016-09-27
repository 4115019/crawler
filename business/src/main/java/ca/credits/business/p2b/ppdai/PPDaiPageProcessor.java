package ca.credits.business.p2b.ppdai;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bTemplate;
import ca.credits.deep.Bootstrap;
import ca.credits.deep.IFailedListener;
import ca.credits.deep.ISiteGen;
import ca.credits.deep.RabbitSpider;
import ca.credits.deep.scheduler.RabbimqScheduler;
import ca.credits.queue.EventControlConfig;
import ca.credits.queue.EventController;
import ca.credits.queue.ExchangeEnum;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.impl.DefaultEventController;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

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

    public static void main(String[] args) throws PushFailedException {

        EventControlConfig config  = null;
        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.P2B.PPDAI.getCode()).exchangeName(PlatformCodeEnum.P2B.PPDAI.getCode()).exchangeType(ExchangeEnum.DIRECT).build();
        Bootstrap.startTest(queueInfo,new PPDaiPageProcessor()).start();
//        Spider.create(new PPDaiPageProcessor())
//                //从"https://github.com/code4craft"开始抓
//                .addUrl("http://www.ppdai.com/blacklist")
//                //开启5个线程抓取
//                .thread(5)
//                //启动爬虫
//                .run();
    }
}
