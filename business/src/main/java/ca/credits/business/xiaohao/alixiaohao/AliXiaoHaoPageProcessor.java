package ca.credits.business.xiaohao.alixiaohao;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.pipeline.DynamodbPipeline;
import ca.credits.business.xiaohao.XiaohaoTemplate;
import ca.credits.common.config.Config;
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
public class AliXiaoHaoPageProcessor implements PageProcessor{

    private Site site = Site.me().setRetryTimes(3).setSleepTime(1000);

    public static void main(String[] args) throws PushFailedException {
        EventControlConfig config = new EventControlConfig(Config.getString("rabbitmq.host"));
        config.setUsername(Config.getString("rabbitmq.username"));
        config.setPassword(Config.getString("rabbitmq.password"));
        config.setVirtualHost(Config.getString("rabbitmq.virtual.host"));

        EventController eventController = DefaultEventController.getInstance(config);
        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.VirtualPhone.ALIPAY.getCode())
                                                .exchangeName(PlatformCodeEnum.VirtualPhone.ALIPAY.getCode())
                                                .exchangeType(ExchangeEnum.DIRECT).build();
        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo, new AliXiaoHaoPageProcessor(),
                new RabbimqScheduler(eventController)).rateLimiter(RateLimiter.create(0.5)).siteGen(request -> Site.me()
                .addCookie("WAPFDFDTGFG","+4cMKKP+8PI+KKw/edQO2jpaC3Wi/y3mlsu77T4opzq5SpHEXB8=")
                .addCookie("_w_tb_nick","tao443818435")
                .addCookie("imewweoriw","3c6yk5Ak6YQpTxF0Di5KD/7ik6Ax83h6NyNNb1EZShg=")
                .addCookie("_l_g_","Ug==")
                .addCookie("_nk_","tao443818435")
                .addCookie("_tb_token_","RL4bhzIbztokTiA")
                .addCookie("cookie1","U+M/fGlsXyBIqX2L6wef5GF/VSVXH1MYrB/wG0RVM6E=")
                .addCookie("cookie17","UoncgJHvxaQnOg==")
                .addCookie("cookie2","3ccb2d40046e1710af2bd95587ee5b0d")
                .addCookie("isg","12B7851ADC4BC006C5E36B3FA8BCCB6C")
                .addCookie("lgc","tao443818435")
                .addCookie("login","true")
                .addCookie("munb","1879086519")
                .addCookie("sg","59c")
                .addCookie("skt","05bfa46c36d26875")               // TODO: 16/9/27 变化的
                .addCookie("t","6d4ee39b38c13a8ea9d2c89bf88740c3")// TODO: 16/9/27 t是变化的
                .addCookie("tracknick","tao443818435")
                .addCookie("lg2","UIHiLt3xD8xYTw==")
                .addCookie("uc1","U+GCWk/75gdr5Q==")
                .addCookie("cookie15","U+GCWk/75gdr5Q==")
                .addCookie("uc3","F5fT5K6M0lusR8kW")
                .addCookie("nk2","F5fT5K6M0lusR8kW")
                .addCookie("id2","UoncgJHvxaQnOg===")
                .addCookie("vt3","F8dAS1HtoaHjwuf96TE=")
                .addCookie("unb","1879086519")
                .addCookie("uss","UoM/3gygRTCcYF4dmdfYZslBWNxdd3K7hK2VupvMtLWQZUA/xpMdZoSbtQ==")
                .addCookie("_cc_","UtASsssmfA==")

                .addHeader("Host","aliqin.m.tmall.com")
                .addHeader("Accept","*/*")
                .addHeader("User-Agent","Mozilla/5.0 (iPhone; CPU iPhone OS 9_3_2 like Mac OS X) AppleWebKit/601.1.46 (KHTML, like Gecko) Mobile/13F69 AliApp(KB/2.1.6)  WindVane/6.1.0 alicom 750x1334")
                .addHeader("Accept-Language","zh-cn")
                .addHeader("Referer","http://h5.m.taobao.com/app/qinxin/www/select-number.html").setDomain("aliqin.m.tmall.com"))
                .pipelines(new DynamodbPipeline(XiaohaoTemplate.TABLE_NAME));
        eventController.add(queueInfo, rabbitSpider);

//        rabbitSpider.push(new Request("http://aliqin.m.tmall.com/usercenter/secret_info.do?ver=2.0&_input_charset=UTF-8&m=GetSecretNos&phone_no=18210036590&num=8&like=&callback=jsonp3"));

        eventController.start();
    }

    @Override
    public void process(Page page) throws ParseException {
        JSONObject result = JSON.parseObject(page.getHtml().regex("jsonp3\\(([\\S\\s]*?)\\)").toString());
        if (result.getString("code").equals("0000")) {
            JSONArray phoneList = result.getJSONObject("data").getJSONArray("numList");
            if (!phoneList.isEmpty()) {
                for (Object object : phoneList) {
                    XiaohaoTemplate template = new XiaohaoTemplate(PlatformCodeEnum.VirtualPhone.ALIPAY);
                    JSONObject one = (JSONObject) object;
                    template.setPhone(one.getString("number"));
                    page.addTemplate(template);
                }
            }
        }

        Request request = new Request("http://aliqin.m.tmall.com/usercenter/secret_info.do?ver=2.0&_input_charset=UTF-8&m=GetSecretNos&phone_no=18210036590&num=20&like=&callback=jsonp3");
        // TODO: 16/9/26 动态改变site
        page.addTargetRequest(request);
    }

    @Override
    public Site getSite() {
        return site;
    }
}
