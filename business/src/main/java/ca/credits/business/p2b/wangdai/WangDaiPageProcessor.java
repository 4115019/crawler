package ca.credits.business.p2b.wangdai;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bBootstrap;
import ca.credits.business.p2b.P2bTemplate;
import ca.credits.queue.SendRefuseException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.RegexSelector;

import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by chenwen on 16/9/22.
 */
@Slf4j
public class WangDaiPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) {
        page.addTargetRequests(page.getHtml().xpath("//*[@id=\"threadlisttableid\"]").xpath("//*[@target=\"_blank\"]").links().regex("http://www.5dai5.com/thread-[0-9]{5}.*\\.html").all());
        page.addTargetRequests(page.getHtml().xpath("//*[@id=\"fd_page_bottom\"]").links().regex("http://www.5dai5.com/forum-.*\\.html").all());
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
        P2bBootstrap.start(PlatformCodeEnum.P2B.WANGDAI,new WangDaiPageProcessor(),0.5,new Request("http://www.5dai5.com/forum-44-1.html"));
    }
}
