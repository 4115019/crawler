package ca.credits.business.xiaohao.xiaohao51;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.pipeline.DynamodbPipeline;
import ca.credits.business.xiaohao.XiaohaoTemplate;
import ca.credits.common.config.Config;
import ca.credits.common.util.HttpMethod;
import ca.credits.deep.RabbitSpider;
import ca.credits.deep.scheduler.RabbimqScheduler;
import ca.credits.queue.EventControlConfig;
import ca.credits.queue.EventController;
import ca.credits.queue.ExchangeEnum;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.impl.DefaultEventController;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.google.common.util.concurrent.RateLimiter;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;

import java.text.ParseException;

/**
 * Created by huangpin on 16/9/23.
 */
public class XiaoHao51PageProcessor implements PageProcessor {

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    public static void main(String[] args) throws PushFailedException {
        EventControlConfig config = new EventControlConfig(Config.getString("rabbitmq.host"));
        config.setUsername(Config.getString("rabbitmq.username"));
        config.setPassword(Config.getString("rabbitmq.password"));
        config.setVirtualHost(Config.getString("rabbitmq.virtual.host"));

        EventController eventController = DefaultEventController.getInstance(config);
        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.VirtualPhone.XIAOHAO51.getCode())
                                                .exchangeName(PlatformCodeEnum.VirtualPhone.XIAOHAO51.getCode())
                                                .exchangeType(ExchangeEnum.DIRECT).build();
        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo, new XiaoHao51PageProcessor(),
                new RabbimqScheduler(eventController)).rateLimiter(RateLimiter.create(0.5))
                .pipelines(new DynamodbPipeline(XiaohaoTemplate.TABLE_NAME));
        eventController.add(queueInfo, rabbitSpider);

        Request request = new Request("http://www.5151sjh.com:8080/vpn/interfaceService/queryVirtualList?areaId=2299&busiType=3&key=8ceaf082ace08112&request=ewogICJjaGFubmVsIiA6ICJBcHBTdHJvZVNDIiwKICAiaGFzaENvZGUiIDogIjJhYjJkZmZlMTk3MmM0NzEzNTZjYTM3NTdhOTU3NDBlIiwKICAicGhvbmVPcyIgOiAiMiIsCiAgInRpbWVTdGFtcCIgOiAiMTQ3NDYyMTMyNSIsCiAgInZlcnNpb24iIDogIjMuMC40IiwKICAicmVxdWVzdElkIiA6ICIwOTNiYWM1OTdmNGU1MGVmZjg4MDlkODkyZDJlMjQ4ZGQzYTA1YmM4MTQ3NDYyMTMyNSIKfQ%3D%3D&userNo=18210036590");
        request.setMethod(HttpMethod.HTTP_METHOD.HTTP_POST.getString());
        rabbitSpider.push(request);

        eventController.start();
    }

    @Override
    public void process(Page page) throws ParseException {
        JSONObject result = JSON.parseObject(page.getRawText());
        if (result.getString("code").equals("0")) {
            JSONArray phoneList = result.getJSONArray("data");
            if (!phoneList.isEmpty()) {
                for (Object object : phoneList) {
                    XiaohaoTemplate template = new XiaohaoTemplate(PlatformCodeEnum.VirtualPhone.XIAOHAO51);
                    JSONObject one = (JSONObject) object;
                    template.setPhone(one.getString("virtualPhone"));
                    page.addTemplate(template);
                }
            }
        }
        Request request = new Request("http://www.5151sjh.com:8080/vpn/interfaceService/queryVirtualList?areaId=2299&busiType=3&key=8ceaf082ace08112&request=ewogICJjaGFubmVsIiA6ICJBcHBTdHJvZVNDIiwKICAiaGFzaENvZGUiIDogIjJhYjJkZmZlMTk3MmM0NzEzNTZjYTM3NTdhOTU3NDBlIiwKICAicGhvbmVPcyIgOiAiMiIsCiAgInRpbWVTdGFtcCIgOiAiMTQ3NDYyMTMyNSIsCiAgInZlcnNpb24iIDogIjMuMC40IiwKICAicmVxdWVzdElkIiA6ICIwOTNiYWM1OTdmNGU1MGVmZjg4MDlkODkyZDJlMjQ4ZGQzYTA1YmM4MTQ3NDYyMTMyNSIKfQ%3D%3D&userNo=18210036590");
        request.setMethod(HttpMethod.HTTP_METHOD.HTTP_POST.getString());
        page.addTargetRequest(request);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
