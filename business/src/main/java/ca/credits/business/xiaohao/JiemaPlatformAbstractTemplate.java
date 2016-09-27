package ca.credits.business.xiaohao;

import ca.credits.business.enums.PlatformCodeEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;

/**
 * Created by wl on 2016-09-23.
 */
@Slf4j
public abstract class JiemaPlatformAbstractTemplate implements PageProcessor {

    // 部分一：抓取网站的默认配置，包括编码、抓取间隔、重试次数等
    private Site site = Site.me().setRetryTimes(3).setSleepTime(3000);

    protected String loginUrl;
    protected String getPhoneUrl;
    protected String addBlackListUrl;
    protected String releasePhoneUrl;
    protected PlatformCodeEnum.INameCode nameCode;

    public JiemaPlatformAbstractTemplate(String loginUrl, String getPhoneUrl, String addBlackListUrl, String releasePhoneUrl, PlatformCodeEnum.INameCode nameCode) {
        this.loginUrl = loginUrl;
        this.getPhoneUrl = getPhoneUrl;
        this.addBlackListUrl = addBlackListUrl;
        this.releasePhoneUrl = releasePhoneUrl;
        this.nameCode = nameCode;
    }

    @Override
    public void process(Page page) {
        if(page.getRequest().getUrl().startsWith(loginUrl)){
            login(page);
        }else if(page.getRequest().getUrl().startsWith(getPhoneUrl)){
            getPhone(page);
        }else if(page.getRequest().getUrl().startsWith(addBlackListUrl)){
            addBlackList(page);
        }else if(page.getRequest().getUrl().startsWith(releasePhoneUrl)){
            releasePhone(page);
        }else {
            log.info(String.format("[%s] - xiaohao url error = %s",nameCode.getName(),page.getRequest().getUrl()));
        }
    }

    protected void login(Page page){
        log.info(String.format("[%s] - login response = %s",nameCode.getName(),page.getRawText()));
        if (StringUtils.isNotEmpty(page.getRawText()) && !page.getRawText().contains("False")){
            String[] loginReturnText = page.getRawText().split("&");
            if (loginReturnText.length > 0){
                String token = loginReturnText[0];//False:Session 过期;False:余额不足，请先充值;False:暂时没有此项目号码，请等会试试...//False:信息不完整!//False:余额不足，请先释放号码
                log.info(String.format("[%s] - token = %s",nameCode.getName(),token));
                Request getMobileRequest = new Request(String.format("%s?ItemId=%s&token=%s&Count=%s&PhoneType=0",getPhoneUrl,page.getRequest().getExtra("itemId"),token,page.getRequest().getExtra("count")));
                getMobileRequest = completeRequestBaseInfo(getMobileRequest,page);
                getMobileRequest.putExtra("token",token);
                page.addTargetRequest(getMobileRequest);
            }
        }else {
            log.info(String.format("[%s] - login error = %s",nameCode.getName(),page.getRawText()));
        }
    }

    protected void getPhone(Page page){
        log.info(String.format("[%s] - getPhone response = %s",nameCode.getName(),page.getRawText()));
        if (StringUtils.isNotEmpty(page.getRawText())){
            if (page.getRawText().contains("False")){
                if (page.getRawText().contains("Session")){
                    //session过期,重新获取token
                    Request loginRequest = new Request(String.format("%s?uName=%s&pWord=%s&Developer=%s",loginUrl,page.getRequest().getExtra("uid"),page.getRequest().getExtra("password"),page.getRequest().getExtra("developer")));
                    loginRequest = completeRequestBaseInfo(loginRequest,page);
                    page.addTargetRequest(loginRequest);
                }else if(page.getRawText().contains("释放号码")){
                    Request releaseMobileRequest = new Request(String.format("%s?token=%s",releasePhoneUrl,page.getRequest().getExtra("token")));
                    releaseMobileRequest = completeRequestBaseInfo(releaseMobileRequest,page);
                    page.addTargetRequest(releaseMobileRequest);
                }else if(page.getRawText().contains("暂时没有此项目号码")){
                    //TODO 应该尝试更换项目id
                    page.addTargetRequest(page.getRequest());
                }else{
                    log.info(String.format("[%s] - userGetPhone other error：%s",nameCode.getName(),page.getRawText()));
                }
                return;
            }
            String[] getPhoneReturnText = page.getRawText().split(";");
            String addBlackPhoneList = "";
            for (int i=0;i < getPhoneReturnText.length;i++){
                //TODO 对每个手机号进行去重存储
                XiaohaoTemplate xiaohaoTemplate = new XiaohaoTemplate(nameCode);
                xiaohaoTemplate.setPhone(getPhoneReturnText[i]);
                page.addTemplate(xiaohaoTemplate);
                //TODO 判断手机号是否存在，存在手机号数量和page.getRequest().getExtra("count")进行对比，若相等则切换项目id或停止爬取
                addBlackPhoneList += page.getRequest().getExtra("itemId")+"-"+getPhoneReturnText[i]+";";

            }
            Request addBlackListRequest = new Request(String.format("%s?token=%s&phoneList=%s",addBlackListUrl,page.getRequest().getExtra("token"),addBlackPhoneList));
            addBlackListRequest = completeRequestBaseInfo(addBlackListRequest,page);
            page.addTargetRequest(addBlackListRequest);
        }
    }

    protected void addBlackList(Page page){
        log.info(String.format("[%s] - userAddBlack response = %s",nameCode.getName(),page.getRawText()));
        if (StringUtils.isNotEmpty(page.getRawText())){
            if (page.getRawText().contains("False")){
                if (page.getRawText().contains("Session")){
                    //session过期,重新获取token
                    Request loginRequest = new Request(String.format("%s?uName=%s&pWord=%s&Developer=%s",loginUrl,page.getRequest().getExtra("uid"),page.getRequest().getExtra("password"),page.getRequest().getExtra("developer")));
                    loginRequest = completeRequestBaseInfo(loginRequest,page);
                    page.addTargetRequest(loginRequest);
                }else {
                    log.info(String.format("[%s] - userAddBlack other error：%s",nameCode.getName(),page.getRawText()));
                }
                return;
            }
            Request getMobileRequest = new Request(String.format("%s?ItemId=%s&token=%s&Count=%s&PhoneType=0",getPhoneUrl,page.getRequest().getExtra("itemId"),page.getRequest().getExtra("token"),page.getRequest().getExtra("count")));
            getMobileRequest = completeRequestBaseInfo(getMobileRequest,page);
            page.addTargetRequest(getMobileRequest);
        }
    }

    protected void releasePhone(Page page){
        log.info(String.format("[%s] - userReleaseAllPhone response = %s",nameCode.getName(),page.getRawText()));
        if (StringUtils.isNotEmpty(page.getRawText())){
            if (page.getRawText().contains("False")){
                if (page.getRawText().contains("Session")){
                    //session过期,重新获取token
                    Request loginRequest = new Request(String.format("%s?uName=%s&pWord=%s&Developer=%s",loginUrl,page.getRequest().getExtra("uid"),page.getRequest().getExtra("password"),page.getRequest().getExtra("developer")));
                    loginRequest = completeRequestBaseInfo(loginRequest,page);
                    page.addTargetRequest(loginRequest);
                }else {
                    log.info(String.format("[%s] - userReleaseAllPhone other error：%s",nameCode.getName(),page.getRawText()));
                }
                return;
            }
            Request getMobileRequest = new Request(String.format("%s?ItemId=%s&token=%s&Count=%s&PhoneType=0",getPhoneUrl,page.getRequest().getExtra("itemId"),page.getRequest().getExtra("token"),page.getRequest().getExtra("count")));
            getMobileRequest = completeRequestBaseInfo(getMobileRequest,page);
            page.addTargetRequest(getMobileRequest);
        }
    }

    protected Request completeRequestBaseInfo(Request request,Page page){
        request.putExtra("token",page.getRequest().getExtra("token"));
        request.putExtra("uid",page.getRequest().getExtra("uid"));
        request.putExtra("itemId",page.getRequest().getExtra("itemId"));
        request.putExtra("count",page.getRequest().getExtra("count"));
        request.putExtra("password",page.getRequest().getExtra("password"));
        request.putExtra("developer",page.getRequest().getExtra("developer"));
        return request;
    }

    @Override
    public Site getSite() {
        return site;
    }
}
