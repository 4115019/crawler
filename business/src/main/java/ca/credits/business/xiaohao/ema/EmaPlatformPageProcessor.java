package ca.credits.business.xiaohao.ema;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.pipeline.DynamodbPipeline;
import ca.credits.business.xiaohao.JiemaPlatformAbstractTemplate;
import ca.credits.business.xiaohao.XiaohaoTemplate;
import ca.credits.common.config.Config;
import ca.credits.deep.RabbitSpider;
import ca.credits.deep.scheduler.RabbitmqScheduler;
import ca.credits.queue.EventControlConfig;
import ca.credits.queue.EventController;
import ca.credits.queue.ExchangeEnum;
import ca.credits.queue.QueueInfo;
import ca.credits.queue.impl.DefaultEventController;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.scheduler.PushFailedException;

/**
 * Created by chenwen on 16/9/12.
 */
@Slf4j
public class EmaPlatformPageProcessor extends JiemaPlatformAbstractTemplate {

    public EmaPlatformPageProcessor(String loginUrl, String getPhoneUrl, String addBlackListUrl, String releasePhoneUrl, PlatformCodeEnum.INameCode nameCode) {
        super(loginUrl, getPhoneUrl, addBlackListUrl, releasePhoneUrl, nameCode);
    }

    public static void main(String[] args) throws PushFailedException {
        String loginUrl = "http://api.ema6.com:20161/Api/userLogin";
        String getPhoneUrl = "http://api.ema6.com:20161/Api/userGetPhone";
        String addBlackListUrl = "http://api.ema6.com:20161/Api/userAddBlack";
        String releasePhoneUrl = "http://api.ema6.com:20161/Api/userReleaseAllPhone";
        PlatformCodeEnum.INameCode nameCode = PlatformCodeEnum.VirtualPhone.EMA;

        EventControlConfig config = new EventControlConfig(Config.getString("rabbitmq.host"));
        config.setUsername(Config.getString("rabbitmq.username"));
        config.setPassword(Config.getString("rabbitmq.password"));
        config.setVirtualHost(Config.getString("rabbitmq.virtual.host"));

        EventController eventController = DefaultEventController.getInstance(config);

        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.VirtualPhone.EMA.getCode()).exchangeName(PlatformCodeEnum.VirtualPhone.EMA.getCode()).exchangeType(ExchangeEnum.DIRECT).build();

        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo, new EmaPlatformPageProcessor(loginUrl, getPhoneUrl, addBlackListUrl, releasePhoneUrl, nameCode),
                new RabbitmqScheduler(eventController)).permitsPerSecond(0.1)
                .pipelines(new DynamodbPipeline(XiaohaoTemplate.TABLE_NAME));

        eventController.add(queueInfo,rabbitSpider);

        Request loginRequest = new Request("http://api.ema6.com:20161/Api/userLogin?uName=neimeng2830&pWord=ema2830&Developer=KN4W9lxeXWSHQnO8h95rKg%3d%3d");
        loginRequest.putExtra("uid","neimeng2830");
        loginRequest.putExtra("password","ema2830");
        loginRequest.putExtra("developer","KN4W9lxeXWSHQnO8h95rKg%3d%3d");
        loginRequest.putExtra("itemId","718");
        loginRequest.putExtra("count","30");
        rabbitSpider.push(loginRequest);

        eventController.start();


//        Spider.create(new PPDaiPageProcessor())
//                //从"https://github.com/code4craft"开始抓
//                .addUrl("http://www.ppdai.com/blacklist")
//                //开启5个线程抓取
//                .thread(5)
//                //启动爬虫
//                .run();
    }
}
