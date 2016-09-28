package ca.credits.business.p2b.jiedaip2p;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bBootstrap;
import ca.credits.business.p2b.P2bTemplate;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;
import us.codecraft.webmagic.selector.Selectable;
import us.codecraft.webmagic.utils.HttpProxyUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lzf on 16/9/22.
 */
@Slf4j
public class JieDaiP2PPageProcessor implements PageProcessor {
    private Site site = Site.me().setRetryTimes(3).setSleepTime(100);

    @Override
    public void process(Page page) throws ParseException {
        List<String> list = page.getHtml().regex("<option value=\"([\\S\\s]*?)\">").all();
        List<String> urls = new ArrayList<>();
        for (String url : list) {
            if (url.startsWith("blacklist") && !url.contains("selected")) {
                urls.add(url);
            }
        }
        page.addTargetRequests(urls);
        List<Selectable> nodes = page.getHtml().xpath("//*[@id=\"__01\"]/tbody/tr[6]/td[2]/table/tbody/tr").nodes();

        SimpleDateFormat origin = new SimpleDateFormat("yyyy/MM/dd");

        for (int index = 2; index + 3 < nodes.size(); index += 6) {
            P2bTemplate template = new P2bTemplate(PlatformCodeEnum.P2B.JIEDAIP2P);
            Selectable selectable = nodes.get(index);
            template.setName(selectable.regex("姓名：([\\S\\s]*?)</td>").toString());

            selectable = nodes.get(index + 1);
            String email = selectable.regex("Email：([\\S\\s]*?)</td>").toString();
            template.setEmail(email);

            selectable = nodes.get(index + 2);
            template.setCustId(selectable.regex("身份证号：([\\S\\s]*?)</td>").toString());
            template.setPhone(selectable.regex("电话：([\\S\\s]*?)</td>").toString());

            selectable = nodes.get(index + 3);
            template.setAddress(selectable.regex("现居住地地址：([\\S\\s]*?)</td>").toString());

            String qqNum = selectable.regex("QQ：([\\S\\s]*?)</td>").toString();
            if (StringUtils.isNotEmpty(qqNum)){
                qqNum = email.substring(0, email.indexOf("@"));
                if (StringUtils.isNotEmpty(qqNum)){
                    template.setQqNum(qqNum);
                }
            }

            String publishDate = selectable.regex("更 新 时 间：([\\S\\s]*?)</td>").toString();
            template.setPublishTime(origin.parse(publishDate));

            template.setBadFlag(PlatformCodeEnum.BadReason.OVERDUE.getCode());
            page.addTemplate(template);
        }
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) throws PushFailedException {

        P2bBootstrap.start(
                PlatformCodeEnum.P2B.JIEDAIP2P
                , new JieDaiP2PPageProcessor()
                , 0.5
                , request -> Site.me().setRetryTimes(3).setSleepTime(100).setCharset("gb2312")
                        .addHeader("Accept-Language", "zh-CN,zh;q=0.8,en-US;q=0.5,en;q=0.3")
                        .addHeader("Accept-Encoding", "gzip, deflate")
                        .addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                        .addHeader("Content-Type","text/html; charset=gb2312")
                        .httpProxy(HttpProxyUtil.getHttpProxy())
                , new Request("http://www.p2p12580.com/blacklist.asp?id=0"));
    }
}
