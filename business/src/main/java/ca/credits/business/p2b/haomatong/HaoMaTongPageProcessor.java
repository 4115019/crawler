package ca.credits.business.p2b.haomatong;

import ca.credits.business.DeepCrawlerFailedListener;
import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bBootstrap;
import ca.credits.business.p2b.P2bTemplate;
import ca.credits.business.pipeline.DynamodbPipeline;
import ca.credits.business.util.EventControlUtil;
import ca.credits.business.util.QueueInfoUtil;
import ca.credits.common.config.Config;
import ca.credits.common.filter.RedisHashSetDuplicateFilter;
import ca.credits.common.util.Md5Util;
import ca.credits.common.util.RedissonUtil;
import ca.credits.deep.RabbitSpider;
import ca.credits.deep.scheduler.RabbitmqDuplicateScheduler;
import ca.credits.deep.scheduler.RedisDuplicateRemover;
import ca.credits.queue.EventControlConfig;
import ca.credits.queue.EventController;
import ca.credits.queue.ExchangeEnum;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.impl.DefaultEventController;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpProxyUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
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
            template.setPrimaryKey(Md5Util.toMd5(template.getPhone()+PlatformCodeEnum.P2B.HAOMAOTONG.getCode()));
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
        P2bBootstrap.start(PlatformCodeEnum.P2B.HAOMAOTONG,new HaoMaTongPageProcessor(),0.5,new Request("http://www.139018.com/ReportList_Mob"));
    }
}
