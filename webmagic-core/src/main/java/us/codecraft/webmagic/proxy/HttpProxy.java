package us.codecraft.webmagic.proxy;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;

import java.io.Serializable;

/**
 * Created by cw on 15-11-26.
 */
public class HttpProxy implements Serializable {
    private static final long serialVersionUID = 1L;

    private String scheme = "http";

    private String username;

    private String password;

    private String ip;

    private int port;

    public HttpProxy(){
    }

    public HttpProxy(HttpHost httpHost){
        ip = httpHost.getHostName();
        port = httpHost.getPort();
        scheme = httpHost.getSchemeName();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getScheme() {
        return scheme;
    }

    public void setScheme(String scheme) {
        this.scheme = scheme;
    }

    public boolean validate(){
        if (StringUtils.isEmpty(ip)){
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "HttpProxy{" +
                "username='" + username + '\'' +
                ", password='" + password + '\'' +
                ", ip='" + ip + '\'' +
                ", port=" + port +
                '}';
    }
}
