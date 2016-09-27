package ca.credits.business.p2b.haomatong;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bTemplate;
import ca.credits.business.pipeline.DynamodbPipeline;
import ca.credits.common.config.Config;
import ca.credits.deep.ISiteGen;
import ca.credits.deep.RabbitSpider;
import ca.credits.deep.scheduler.RabbimqDuplicateScheduler;
import ca.credits.deep.scheduler.RabbimqScheduler;
import ca.credits.queue.EventControlConfig;
import ca.credits.queue.EventController;
import ca.credits.queue.ExchangeEnum;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.impl.DefaultEventController;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import us.codecraft.webmagic.selector.Selectable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by lzf on 16/9/21.
 */
@Slf4j
public class HaoMaTongPageProcessor implements PageProcessor {
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        log.info(page.getHtml().xpath("//*[@id=\"MainPnl\"]/div/div[2]").css("li[class=GlbBtmLn]").all().toString());
        List<Selectable> nodes = page.getHtml().xpath("//*[@id=\"MainPnl\"]/div/div[2]").css("li[class=GlbBtmLn]").nodes();
        for(Selectable selectable : nodes){
            P2bTemplate template = new P2bTemplate(PlatformCodeEnum.P2B.HAOMAOTONG);
            template.setPhone(selectable.xpath("//div[@class=Num]/text()").get());
            String dateStr = selectable.xpath("//div[@class=UpTm]/text()").get();
            try {
                template.setPublishTime(new SimpleDateFormat("yy-MM-dd HH:mm:ss").parse(dateStr.replace("提交于","")));
            } catch (ParseException e) {
                e.printStackTrace();
            }
            String txt = selectable.xpath("//div[@class=Txt]/text()").get();
            if(txt.contains("逾期") || txt.contains("欠款")){
                template.setBadFlag(PlatformCodeEnum.BadReason.OVERDUE.getCode());
            }else {
                template.setBadFlag(PlatformCodeEnum.BadReason.OTHER.getCode());
            }
            page.addTemplate(template);
        }
        log.info(page.getHtml().links().regex("ReportList_Mob_[0-9]+").all().toString());
        page.addTargetRequests(page.getHtml().links().regex("ReportList_Mob_[0-9]+").all());
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws PushFailedException {
        EventControlConfig config  = new EventControlConfig(Config.getString("rabbitmq.host"));
        config.setUsername(Config.getString("rabbitmq.username"));
        config.setPassword(Config.getString("rabbitmq.password"));
        config.setVirtualHost(Config.getString("rabbitmq.virtual.host"));

        EventController eventController = DefaultEventController.getInstance(config);
        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.P2B.HAOMAOTONG.getCode())
                                                .exchangeName(PlatformCodeEnum.P2B.HAOMAOTONG.getCode())
                                                .exchangeType(ExchangeEnum.DIRECT).build();
        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo, new HaoMaTongPageProcessor(),
                new RabbimqDuplicateScheduler(eventController).setDuplicateRemover(new HashSetDuplicateRemover()))
                .rateLimiter(RateLimiter.create(0.5))
                .siteGen(request -> Site.me().setRetryTimes(3).setSleepTime(1000).setCharset("UTF-8"))
                .pipelines(new DynamodbPipeline(P2bTemplate.TABLE_NAME));
        eventController.add(queueInfo,rabbitSpider);
        rabbitSpider.push(new Request("http://www.139018.com/ReportList_Mob"));
        eventController.start();
    }
}
