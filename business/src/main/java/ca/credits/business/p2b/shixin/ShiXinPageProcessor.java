package ca.credits.business.p2b.shixin;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bTemplate;
import ca.credits.business.pipeline.DynamodbPipeline;
import ca.credits.business.xiaohao.XiaohaoTemplate;
import ca.credits.common.config.Config;
import ca.credits.deep.RabbitSpider;
import ca.credits.deep.scheduler.RabbimqDuplicateScheduler;
import ca.credits.deep.scheduler.RabbimqScheduler;
import ca.credits.queue.EventControlConfig;
import ca.credits.queue.EventController;
import ca.credits.queue.ExchangeEnum;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.impl.DefaultEventController;
import ch.qos.logback.core.pattern.util.RegularEscapeUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;
import us.codecraft.webmagic.scheduler.component.HashSetDuplicateRemover;

/**
 * Created by lzf on 16/9/22.
 */
@Slf4j
public class ShiXinPageProcessor implements PageProcessor{
    // 部分一：抓取网站的相关配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);
    @Override
    public void process(Page page) {
        log.info(page.getHtml().toString());
        String content = page.getHtml().regex("\\(\\{([\\S\\s]*?)\\}\\);").toString();
        log.info("regex={"+content+"}");
        JSONObject json = JSON.parseObject("{"+content+"}");
        if(json.containsKey("data") && json.getJSONArray("data").size() != 0){
            JSONObject jsonObject = json.getJSONArray("data").getJSONObject(0);
            if(jsonObject.containsKey("result") && jsonObject.getJSONArray("result").size() != 0){
                JSONArray results = jsonObject.getJSONArray("result");
                for (Object object : results){
                    JSONObject pjson = JSON.parseObject(object.toString());
                    if(!pjson.getString("cardNum").contains("****")){
                        continue;
                    }
                    P2bTemplate template = new P2bTemplate(PlatformCodeEnum.P2B.SHIXIN);
                    template.setName(pjson.getString("iname"));
                    template.setCustId(pjson.getString("cardNum"));
                    template.setBadFlag(PlatformCodeEnum.BadReason.OVERDUE.getCode());
                    template.setPublishTime(pjson.getDate("_update_time"));
                    page.addTemplate(template);
                }
            }
        }
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
        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.P2B.SHIXIN.getCode()).exchangeName(PlatformCodeEnum.P2B.SHIXIN.getCode()).exchangeType(ExchangeEnum.DIRECT).build();
        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo,new ShiXinPageProcessor(),new RabbimqDuplicateScheduler(eventController).setDuplicateRemover(new HashSetDuplicateRemover()))
                .rateLimiter(RateLimiter.create(0.5))
                .pipelines(new DynamodbPipeline(P2bTemplate.TABLE_NAME));
        eventController.add(queueInfo,rabbitSpider);
        String url = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=6899&query=失信被执行人&pn=%s&rn=10&ie=utf-8&oe=utf-8&format=json&t=1474529801500&cb=jQuery110205811325080133857_1474526931162&_=1474526931172";
        for(int i=1;i<5;i++){
            rabbitSpider.push(new Request(String.format(url,i*10)));
        }

        rabbitSpider.push(new Request("https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=6899&query=%E5%A4%B1%E4%BF%A1%E8%A2%AB%E6%89%A7%E8%A1%8C%E4%BA%BA&pn=180&rn=10&ie=utf-8&oe=utf-8&format=json&t=1474529801500&cb=jQuery110205811325080133857_1474526931162&_=1474526931172"));

        eventController.start();
    }
}
