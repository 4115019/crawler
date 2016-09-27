package ca.credits.business.p2b.ledao;

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

import java.util.List;

/**
 * Created by lzf on 16/9/23.
 */
@Slf4j
public class LeDaoPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    @Override
    public void process(Page page) {
        List<Selectable> nodes = page.getHtml().xpath("//*[@class=\"table_input2\"]").css("tr").nodes();
        for(Selectable selectable : nodes){
            if(selectable.css("table").get() == null){
                continue;
            }
            P2bTemplate template = new P2bTemplate(PlatformCodeEnum.P2B.LEDAO);
            template.setName(selectable.regex("真实姓名：([\\S\\s]*?)<br />").toString());
            template.setCustId(selectable.regex("身份证号码：([\\S\\s]*?)<br />").toString());
            template.setPhone(selectable.regex("手机号码：([\\S\\s]*?)<br />").toString());
            template.setUserId(selectable.regex("</table> ([\\S\\s]*?)<br />").toString());
            String email = selectable.regex("邮箱：([\\S\\s]*?)<br />").toString();
            template.setEmail(email);
            if(email.contains("@qq.com")){
                template.setQqNum(email.substring(0,email.indexOf("@")));
            }
            template.setAddress(selectable.regex("现居住地址：([\\S\\s]*?)<br />").toString());
            template.setBadFlag(PlatformCodeEnum.BadReason.OVERDUE.getCode());
            page.addTemplate(template);
        }
        page.addTargetRequests(page.getHtml().links().regex("/blacklist/index[0-9]+.html").all());
    }

    @Override
    public Site getSite() {
        return site;
    }
    public static void main(String[] args) throws PushFailedException {
        EventControlConfig config = new EventControlConfig(Config.getString("rabbitmq.host"));
        config.setUsername(Config.getString("rabbitmq.username"));
        config.setPassword(Config.getString("rabbitmq.password"));
        config.setVirtualHost(Config.getString("rabbitmq.virtual.host"));

        EventController eventController = DefaultEventController.getInstance(config);
        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.P2B.LEDAO.getCode())
                                                .exchangeName(PlatformCodeEnum.P2B.LEDAO.getCode())
                                                .exchangeType(ExchangeEnum.DIRECT).build();
        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo,new LeDaoPageProcessor(),new RabbimqDuplicateScheduler(eventController).setDuplicateRemover(new HashSetDuplicateRemover()))
                .rateLimiter(RateLimiter.create(0.5))
                .siteGen(request -> Site.me().setRetryTimes(3).setSleepTime(1000).setCharset("GBK"))
                .pipelines(new DynamodbPipeline(P2bTemplate.TABLE_NAME));
        eventController.add(queueInfo,rabbitSpider);
        rabbitSpider.push(new Request("http://www.ledaosw.com/blacklist/index.html"));
        eventController.start();

    }
}
