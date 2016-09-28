package ca.credits.business.p2b.shixin;

import ca.credits.business.enums.PlatformCodeEnum;
import ca.credits.business.p2b.P2bBootstrap;
import ca.credits.business.p2b.P2bTemplate;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.scheduler.PushFailedException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

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
                    template.setPublishTime(new Date(pjson.getLong("publishDateStamp") * 1000));
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
        Collection<Request> requests = new ArrayList<>();
        String url = "https://sp0.baidu.com/8aQDcjqpAAV3otqbppnN2DJv/api.php?resource_id=6899&query=失信被执行人&pn=%s&rn=10&ie=utf-8&oe=utf-8&format=json&t=1474529801500&cb=jQuery110205811325080133857_1474526931162&_=1474526931172";
        for(int i=1 ;i < 20000;i++){
            requests.add(new Request(String.format(url,i*10)));
        }
        P2bBootstrap.start(PlatformCodeEnum.P2B.SHIXIN,new ShiXinPageProcessor(),0.5,requests);
    }
}
