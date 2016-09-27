package us.codecraft.webmagic.utils;

import ca.credits.common.config.Config;
import ca.credits.common.util.HttpMethod;
import ca.credits.common.util.UserAgents;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Request;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.downloader.Downloader;
import us.codecraft.webmagic.downloader.HttpClientDownloader;
import us.codecraft.webmagic.proxy.HttpProxy;

import java.net.InetAddress;

/**
 * Created by chenwen on 16/9/27.
 */
@Slf4j
public class HttpProxyUtil {
    private static Downloader downloader = new HttpClientDownloader();
    public static HttpProxy getHttpProxy(){
        return getHttpProxy(0);
    }

    public static HttpProxy getHttpProxy(int count){
        if (count > 2){
            return null;
        }else {
            try {
                Request request = new Request(Config.getString("proxy.pool.url"));
                request.setMethod(HttpMethod.HTTP_METHOD.HTTP_POST.getString());
                Site site = Site.me().contentType("application/json").addHeader("User-Agent", UserAgents.getUserAgent(5)).setTimeOut(3000);
                JSONObject body = new JSONObject();
                body.put("userName", Config.getString("proxy.pool.username"));
                body.put("password", Config.getString("proxy.pool.password"));
                body.put("webType", Config.getString("proxy.pool.webtype"));
                site.body(body.toJSONString());
                Page page = downloader.download(request, site);
                JSONObject json = JSON.parseObject(page.getRawText());
                HttpProxy httpProxy = new HttpProxy();
                httpProxy.setIp(json.getString("ip"));
                httpProxy.setPort(json.getInteger("squid_port"));
                httpProxy.setUsername(json.containsKey("name") ? json.getString("name").trim() : null);
                httpProxy.setPassword(json.getString("squid_password"));
                if (!httpProxy.validate() || !isReachable(httpProxy.getIp())) {
                    return getHttpProxy(++count);
                }
                return httpProxy;
            } catch (Exception e) {
                log.error("获取代理异常", e);
                return getHttpProxy(++count);
            }
        }
    }

    public static boolean isReachable(String ip){
        try{
            InetAddress addr = InetAddress.getByName(ip);
            if(addr.isReachable(2000)){
                log.info("成功获取代理 ip = " + ip);
                return true;
            }
        }catch(Exception ex){
            log.error("代理ip超时,切换代理",ex);
        }
        log.info("失败获取代理 ip = " + ip);
        return false;
    }
}
