package ca.credits.engine.parser.sample;

import ca.credits.engine.AbstractResponse;
import ca.credits.engine.parser.IResponseProcessor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * Created by chenwen on 16/9/18.
 */
@Slf4j
public class PPDaiPageProcessor implements IResponseProcessor {

    @Override
    public void process(AbstractResponse response) {
//        log.info(response.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/div/ul/li").regex("[0-9]+").all().toString());

//        log.info(response.getHtml().links().regex(".*/blacklist/.*").all().toString());

//        log.info(response.getHtml().xpath("//input[@onclick]").regex("/blacklistdetail/pdu[0-9]+").all().toString());

        List<String> detailList = response.getHtml().xpath("//input[@onclick]").regex("/blacklistdetail/pdu[0-9]+").all();
        detailList.stream().forEach(detail -> response.addTargetUrl(String.format("http://www.ppdai.com%s",detail)));
        List<String> all = response.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/div/ul/li").regex("[0-9]+").all();
        all.stream().forEach(node -> response.addTargetUrl(String.format("http://www.ppdai.com/blacklist/%s_m0",node)));
        response.addTargetRequestUrls(response.getHtml().links().regex(".*/blacklist/.*").all());

        if (response.getRequest().getUrl().startsWith("http://www.ppdai.com/blacklistdetail")){
            final String name = response.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[2]/ul/li/text()[2]").get();

            final String start_time = response.getHtml().xpath("//*[@id=\"content_nav\"]/div[2]/div[1]/div[2]/table/tbody/tr[3]/td/div/table/tbody/tr[2]/td[3]").get();

            if (name == null){
                log.info("response = {}" , response.getRawText());
            }else {
                log.info("------------------ name = {}", name);
                log.info("------------------ start_time = {}", start_time);
            }
        }
    }
}
