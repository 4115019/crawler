package us.codecraft.webmagic.downloader;

import ca.credits.common.util.HttpLogHelper;
import ca.credits.common.util.HttpMethod;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.*;
import org.apache.http.client.methods.HttpRequestWrapper;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketTimeoutException;
import java.nio.charset.Charset;
import java.util.Arrays;

/**
 * Created by cw on 15-9-15.
 */
@Slf4j
public class SimpleHttpResponseInterceptor implements HttpResponseInterceptor {
    private static SimpleHttpResponseInterceptor instance = new SimpleHttpResponseInterceptor();

    private SimpleHttpResponseInterceptor()
    {

    }
    public static SimpleHttpResponseInterceptor getInstance()
    {
        return instance;
    }
    @Override
    public void process(HttpResponse response, HttpContext context) throws HttpException, IOException
    {
        try {
            String url = null;
            String queryString = null;
            String requestBody = null;
            String header = null;
            HttpMethod.HTTP_METHOD httpMethod = null;
            InetAddress ip = InetAddress.getLocalHost();
            int code = response.getStatusLine().getStatusCode();

            HttpRequest request = (HttpRequest) context.getAttribute(ExecutionContext.HTTP_REQUEST);

            httpMethod = HttpMethod.HTTP_METHOD.valueOf("HTTP_" + request.getRequestLine().getMethod());
            header = Arrays.toString(request.getAllHeaders());
            try {
                url = ((HttpRequestWrapper) request).getTarget() + request.getRequestLine().getUri();
            }catch (Exception e){
                url = request.getRequestLine().getUri();
            }
            log.info("========= response headers =============");
            for(Header header1 : response.getAllHeaders()){
                log.info(header1.getName() +  " = " + header1.getValue());
            }
            log.info("========= response headers =============");
            String responseBody = null;
            if (response.getFirstHeader("Content-Type") != null && response.getFirstHeader("Content-Type").getValue().contains("application/octet-stream")){//application/octet-stream{
                responseBody = "下载pdf成功";
            }else if (response.getFirstHeader("Content-Type") != null && response.getFirstHeader("Content-Type").getValue().contains("image/")){
                responseBody = "保存图片成功";
            } else if (code < 500) {
                // Replace entity as an repeatable entity
                BufferedHttpEntity entity = new BufferedHttpEntity(response.getEntity());
                response.setEntity(entity);

                ContentType contentType = ContentType.getOrDefault(entity);

                Charset charset = contentType.getCharset();

                if (charset == null) {
                    charset = Consts.UTF_8;
                }
//                requestBody = "日志太多,暂时不打印返回结果";
                responseBody = EntityUtils.toString(entity, charset);
            }
            String responseLog = HttpLogHelper.logResponseOnClient(header, httpMethod, url, ip.getHostAddress(),
                    queryString, requestBody, String.valueOf(code), responseBody);
            log.info(responseLog);
        } catch (SocketTimeoutException e) {
            log.error("获取信息超时", "获取信息超时，请稍后重试", e);
        } catch (Exception e) {
            log.error("爬虫日志异常", e);
        }
    }
}
