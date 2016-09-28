package ca.credits.business.xiaohao.shenhua;

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
import us.codecraft.webmagic.scheduler.PushFailedException;

/**
 * Created by chenwen on 16/9/12.
 */
@Slf4j
public class ShenhuaPlatformPageProcessor extends JiemaPlatformAbstractTemplate {

    public ShenhuaPlatformPageProcessor(String loginUrl, String getPhoneUrl, String addBlackListUrl, String releasePhoneUrl, PlatformCodeEnum.INameCode nameCode) {
        super(loginUrl, getPhoneUrl, addBlackListUrl, releasePhoneUrl, nameCode);
    }

    public static void main(String[] args) throws PushFailedException {
        String loginUrl = "http://api.shjmpt.com:9002/pubApi/uLogin";
        String getPhoneUrl = "http://api.shjmpt.com:9002/pubApi/GetPhone";
        String addBlackListUrl = "http://api.shjmpt.com:9002/pubApi/AddBlack";
        String releasePhoneUrl = "http://api.shjmpt.com:9002/pubApi/ReleaseAllPhone";
        PlatformCodeEnum.INameCode nameCode = PlatformCodeEnum.VirtualPhone.SHENHUA;

        EventControlConfig config = new EventControlConfig(Config.getString("rabbitmq.host"));
        config.setUsername(Config.getString("rabbitmq.username"));
        config.setPassword(Config.getString("rabbitmq.password"));
        config.setVirtualHost(Config.getString("rabbitmq.virtual.host"));

        EventController eventController = DefaultEventController.getInstance(config);

        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.VirtualPhone.SHENHUA.getCode()).exchangeName(PlatformCodeEnum.VirtualPhone.SHENHUA.getCode()).exchangeType(ExchangeEnum.DIRECT).build();

        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo, new ShenhuaPlatformPageProcessor(loginUrl, getPhoneUrl, addBlackListUrl, releasePhoneUrl, nameCode),
                new RabbitmqScheduler(eventController)).permitsPerSecond(0.1)
                .pipelines(new DynamodbPipeline(XiaohaoTemplate.TABLE_NAME));

        eventController.add(queueInfo, rabbitSpider);

//        Request loginRequest = new Request("http://api.shjmpt.com:9002/pubApi/uLogin?uName=neimeng2830&pWord=shenhua2830&Developer=DL6Gf%2bgxIdQEYNFhLwkUtg%3d%3d");
//        loginRequest.putExtra("uid", "neimeng2830");
//        loginRequest.putExtra("password", "shenhua2830");
//        loginRequest.putExtra("developer", "DL6Gf%2bgxIdQEYNFhLwkUtg%3d%3d");
//        loginRequest.putExtra("itemId", "76");
//        loginRequest.putExtra("count", "30");
//        rabbitSpider.push(loginRequest);

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
