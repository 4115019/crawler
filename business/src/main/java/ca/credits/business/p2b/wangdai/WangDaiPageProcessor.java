package ca.credits.business.p2b.wangdai;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bTemplate;
import ca.credits.common.config.Config;
import ca.credits.queue.EventControlConfig;
import ca.credits.queue.ExchangeEnum;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.SendRefuseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.http.HttpHost;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.RegexSelector;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by chenwen on 16/9/22.
 */
@Slf4j
public class WangDaiPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().xpath("//*[@id=\"threadlisttableid\"]").links().regex("http://www.5dai5.com/thread-[0-9]{5}-.*\\.html").all());
        page.addTargetRequests(page.getHtml().links().regex("http://www.5dai5.com/forum-.*\\.html").all());
        if (page.getUrl().get().startsWith("http://www.5dai5.com/thread-")){
            String userId = page.getHtml().select(new RegexSelector("fixed_avatar\\(\\[([0-9]+)\\]",1)).get();
            if (StringUtils.isNotEmpty(userId)) {
                P2bTemplate template = new P2bTemplate(PlatformCodeEnum.P2B.WANGDAI);
                template.setName(page.getHtml().xpath("//*[@id=\"pid" + userId + "\"]/tbody/tr[1]/td[2]/div[3]/div/div[1]/table/tbody/tr[3]/td/text()").get());
                if (StringUtils.isNotEmpty(template.getName())) {
                    template.setCustId(page.getHtml().xpath("//*[@id=\"pid" + userId + "\"]/tbody/tr[1]/td[2]/div[3]/div/div[1]/table/tbody/tr[4]/td/text()").get());
                    template.setPhone(page.getHtml().xpath("//*[@id=\"pid" + userId + "\"]/tbody/tr[1]/td[2]/div[3]/div/div[1]/table/tbody/tr[5]/td/text()").regex("[^-]+").get());
                    template.setQqNum(page.getHtml().xpath("//*[@id=\"pid" + userId + "\"]/tbody/tr[1]/td[2]/div[3]/div/div[1]/table/tbody/tr[6]/td/text()").regex("[^-]+").get());
                    template.setSchool(page.getHtml().xpath("//*[@id=\"postmessage_" + userId + "\"]").regex("([^>]*)" + template.getName() + "，", 1).get());
                    try {
                        template.setPublishTime(new SimpleDateFormat("yyyy-mm-dd HH:mm:ss").parse(page.getHtml().xpath("//*[@id=\"authorposton" + userId + "\"]/text()").regex("发表于 (.*)", 1).get()));
                    } catch (ParseException e) {
                        log.error("解析失败", e);
                    }
                    template.setBadFlag(PlatformCodeEnum.BadReason.OVERDUE.getCode());
                    page.addTemplate(template);
                }
            }
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws SendRefuseException {
        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.P2B.WANGDAI.getCode()).exchangeName(PlatformCodeEnum.P2B.WANGDAI.getCode()).exchangeType(ExchangeEnum.DIRECT).build();
        EventControlConfig config = new EventControlConfig(Config.getString("rabbitmq.host"));
        config.setUsername(Config.getString("rabbitmq.username"));
        config.setPassword(Config.getString("rabbitmq.password"));
        config.setVirtualHost(Config.getString("rabbitmq.virtual.host"));
        new HttpClientDownloader().download(new Request("http://www.baidu.com/s?ie=utf-8&f=8&rsv_bp=0&rsv_idx=1&tn=baidu&wd=ip&rsv_pq=df3f2fe200004c61&rsv_t=6ef3fx3oacHs0FJOpiYwmBZHY7St90A9v1O%2BAZHsCp1pNh5drIE64y6yL%2Fo&rsv_enter=1&rsv_sug3=3&rsv_sug2=0&inputT=445&rsv_sug4=1120"),
                Site.me().setHttpHost(new HttpHost("218.74.83.254",8888)));

//        Bootstrap.startTest(queueInfo,new WangDaiPageProcessor()).getEventTemplate().send(queueInfo,new Request("http://www.5dai5.com/forum-44-1.html"));

//        EventControlConfig config = new EventControlConfig("open-test.wecash.net");
//        config.setUsername("wecash-admin");
//        config.setPassword("wecash2015");
//
//        EventController eventController = DefaultEventController.getInstance(config);
//
//        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo, new WangDaiPageProcessor(),
//                new RabbimqScheduler(eventController).setDuplicateRemover(new RedisBloomFilterDuplicateRemover(new RedisBloomDuplicateFilter(
//                        100000,0.01,PlatformCodeEnum.P2B.WANGDAI.getCode(), RedissonUtil.getInstance().getRedisson()
//                )))).rateLimiter(RateLimiter.create(10));
//
//        eventController.add(queueInfo,rabbitSpider);
//
//        eventController.start();;
    }
}
