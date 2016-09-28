package ca.credits.business.xiaohao.yma;

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
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.scheduler.PushFailedException;

/**
 * Created by chenwen on 16/9/12.
 */
@Slf4j
public class YmaPlatformPageProcessor extends JiemaPlatformAbstractTemplate {

    public YmaPlatformPageProcessor(String loginUrl, String getPhoneUrl, String addBlackListUrl, String releasePhoneUrl, PlatformCodeEnum.INameCode nameCode) {
        super(loginUrl, getPhoneUrl, addBlackListUrl, releasePhoneUrl, nameCode);
    }

    @Override
    protected void login(Page page) {
        log.info(String.format("[%s] - login response = %s", nameCode.getName(), page.getRawText()));
        if (StringUtils.isNotEmpty(page.getRawText())) {
            if (page.getRawText().contains("unknow_error") || page.getRawText().contains("please try again later")) {
                page.addTargetRequest(page.getRequest());
                return;
            }
            String[] loginReturnText = page.getRawText().split("\\|");
            if (loginReturnText.length > 1) {
                String token = loginReturnText[1];
                log.info(String.format("[%s] - token = %s", nameCode.getName(), token));
                Request getMobileRequest = new Request(String.format("%s&pid=%s&uid=%s&token=%s&size=%s", getPhoneUrl, page.getRequest().getExtra("itemId"), page.getRequest().getExtra("uid"), token, page.getRequest().getExtra("count")));
                getMobileRequest = completeRequestBaseInfo(getMobileRequest, page);
                getMobileRequest.putExtra("token", token);
                page.addTargetRequest(getMobileRequest);
            }
        }
    }

    @Override
    protected void getPhone(Page page) {
        log.info(String.format("[%s] - getPhone response = %s", nameCode.getName(), page.getRawText()));
        if (StringUtils.isNotEmpty(page.getRawText())) {
            if (!isGetPhoneSuccess(page.getRawText())) {
                log.info(String.format("[%s] - userGetPhone other error：%s", nameCode.getName(), page.getRawText()));
                if (page.getRawText().contains("unknow_error") || page.getRawText().contains("please try again later")) {
                    page.addTargetRequest(page.getRequest());
                } else if (page.getRawText().contains("no_data") || page.getRawText().contains("max_count_disable") || page.getRawText().contains("恶意")) {
                    Request releaseMobileRequest = new Request(String.format("%s&token=%s&uid=%s", releasePhoneUrl, page.getRequest().getExtra("token"), page.getRequest().getExtra("uid")));
                    releaseMobileRequest = completeRequestBaseInfo(releaseMobileRequest, page);
                    page.addTargetRequest(releaseMobileRequest);
                }
                return;
            }
            String[] getPhoneReturnText = page.getRawText().split(";");
            String addBlackPhoneList = "";
            for (int i = 0; i < getPhoneReturnText.length; i++) {
                //TODO 对每个手机号进行去重存储
                if (i == getPhoneReturnText.length - 1) {
                    getPhoneReturnText[i] = getPhoneReturnText[i].split("\\|")[0];
                }
                XiaohaoTemplate ymaTemplate = new XiaohaoTemplate(PlatformCodeEnum.VirtualPhone.YMA);
                ymaTemplate.setPhone(getPhoneReturnText[i]);
                page.addTemplate(ymaTemplate);
                addBlackPhoneList += getPhoneReturnText[i] + ",";

            }
            Request addBlackListRequest = new Request(String.format("%s&uid=%s&token=%s&pid=%s&mobiles=%s", addBlackListUrl, page.getRequest().getExtra("uid"), page.getRequest().getExtra("token"), page.getRequest().getExtra("itemId"), addBlackPhoneList));
            addBlackListRequest = completeRequestBaseInfo(addBlackListRequest, page);
            page.addTargetRequest(addBlackListRequest);
        }
    }

    @Override
    protected void addBlackList(Page page) {
        log.info(String.format("[%s] - userAddBlack response = %s", nameCode.getName(), page.getRawText()));
        if (StringUtils.isNotEmpty(page.getRawText())) {
            if (!isAddBlackSuccess(page.getRawText())) {
                log.info(String.format("[%s] - userAddBlack other error：%s", nameCode.getName(), page.getRawText()));
                if (page.getRawText().contains("message|please try again later") || page.getRawText().contains("unknow_error")) {
                    page.addTargetRequest(page.getRequest());
                }
                return;
            }
            Request getMobileRequest = new Request(String.format("%s&pid=%s&uid=%s&token=%s&size=%s", getPhoneUrl, page.getRequest().getExtra("itemId"), page.getRequest().getExtra("uid"), page.getRequest().getExtra("token"), page.getRequest().getExtra("count")));
            getMobileRequest = completeRequestBaseInfo(getMobileRequest, page);
            page.addTargetRequest(getMobileRequest);
        } else {
            log.info(String.format("[%s] - userAddBlack response is null：%s", nameCode.getName(), page.getRawText()));
        }
    }

    @Override
    protected void releasePhone(Page page) {
        log.info(String.format("[%s] - userReleaseMobile response = %s", nameCode.getName(), page.getRawText()));
        if (StringUtils.isNotEmpty(page.getRawText()) && page.getRawText().contains("OK")) {
            Request getMobileRequest = new Request(String.format("%s&pid=%s&uid=%s&token=%s&size=%s", getPhoneUrl, page.getRequest().getExtra("itemId"), page.getRequest().getExtra("uid"), page.getRequest().getExtra("token"), page.getRequest().getExtra("count")));
            getMobileRequest = completeRequestBaseInfo(getMobileRequest, page);
            page.addTargetRequest(getMobileRequest);
        } else {
            log.info(String.format("[%s] - userReleaseMobile other error：%s", nameCode.getName(), page.getRawText()));
            if (page.getRawText().contains("message|please try again later") || page.getRawText().contains("unknow_error")) {
                page.addTargetRequest(page.getRequest());
            }
        }
    }

    @Override
    protected Request completeRequestBaseInfo(Request request, Page page) {
        request.putExtra("token", page.getRequest().getExtra("token"));
        request.putExtra("uid", page.getRequest().getExtra("uid"));
        request.putExtra("itemId", page.getRequest().getExtra("itemId"));
        request.putExtra("count", page.getRequest().getExtra("count"));
        return request;
    }

    private boolean isAddBlackSuccess(String responseText) {
        boolean flag = false;
//        parameter_error	传入参数错误
//        not_login	没有登录,在没有登录下去访问需要登录的资源，忘记传入uid,token
//        message|please try again later	访问速度过快，建议休眠50毫秒后再试
//        account_is_locked	账号被锁定
//        unknow_error	未知错误,再次请求就会正确返回
        if (StringUtils.isNotEmpty(responseText)) {
            if (!(responseText.contains("account_is_locked") || responseText.contains("not_login") || responseText.contains("parameter_error")
                    || responseText.contains("message|please try again later") || responseText.contains("unknow_error")))
                flag = true;
        }
        return flag;
    }

    private boolean isGetPhoneSuccess(String responseText) {
        boolean flag = false;
//        no_data	系统暂时没有可用号码了
//        max_count_disable	已达到用户可获取号码上限，可通过调用ReleaseMobile方法释放号码并终止任务
//        parameter_error	传入参数错误
//        not_login	没有登录,在没有登录下去访问需要登录的资源，忘记传入uid,token
//        message|please try again later	访问速度过快，建议休眠50毫秒后再试
//        account_is_locked	账号被锁定
//        mobile_notexists	指定的号码不存在
//        mobile_busy	指定的号码繁忙
//        unknow_error	未知错误,再次请求就会正确返回
        if (StringUtils.isNotEmpty(responseText)) {
            if (!(responseText.contains("no_data") || responseText.contains("max_count_disable") || responseText.contains("parameter_error")
                    || responseText.contains("not_login") || responseText.contains("message") || responseText.contains("account_is_locked")
                    || responseText.contains("mobile_notexists") || responseText.contains("mobile_busy") || responseText.contains("unknow_error")))
                flag = true;
        }
        return flag;

    }

    public static void main(String[] args) throws PushFailedException {

        String loginUrl = "http://api.yma0.com/http.aspx?action=loginIn";
        String getPhoneUrl = "http://api.yma0.com/http.aspx?action=getMobilenum";
        String addBlackListUrl = "http://api.yma0.com/http.aspx?action=addIgnoreList";
        String releasePhoneUrl = "http://api.yma0.com/http.aspx?action=ReleaseMobile";
        PlatformCodeEnum.INameCode nameCode = PlatformCodeEnum.VirtualPhone.YMA;


        EventControlConfig config = new EventControlConfig(Config.getString("rabbitmq.host"));
        config.setUsername(Config.getString("rabbitmq.username"));
        config.setPassword(Config.getString("rabbitmq.password"));
        config.setVirtualHost(Config.getString("rabbitmq.virtual.host"));

        EventController eventController = DefaultEventController.getInstance(config);

        QueueInfo queueInfo = QueueInfo.builder().queueName(PlatformCodeEnum.VirtualPhone.YMA.getCode()).exchangeName(PlatformCodeEnum.VirtualPhone.YMA.getCode()).exchangeType(ExchangeEnum.DIRECT).build();

        RabbitSpider rabbitSpider = RabbitSpider.create(queueInfo, new YmaPlatformPageProcessor(loginUrl, getPhoneUrl, addBlackListUrl, releasePhoneUrl, nameCode),
                new RabbitmqScheduler(eventController)).permitsPerSecond(0.04)
                .pipelines(new DynamodbPipeline(XiaohaoTemplate.TABLE_NAME));

        eventController.add(queueInfo, rabbitSpider);

//        Request loginRequest = new Request("http://api.yma0.com/http.aspx?action=loginIn&uid=neimeng2830&pwd=yma2830");
//        loginRequest.putExtra("uid", "neimeng2830");
//        loginRequest.putExtra("pwd", "yma2830");
//        loginRequest.putExtra("itemId", "33937");
//        loginRequest.putExtra("count", "10");
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
