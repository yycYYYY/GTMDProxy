package io.github.yycYYYY.gtmd.utils;

import io.github.yycYYYY.gtmd.model.RequestInfo;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.util.Attribute;
import io.netty.util.AttributeKey;

/**
 * 获取请求信息
 */
public class RequestUtil {

    public static final String HTTPS_PROTOCOL = "https";

    public static final AttributeKey<RequestInfo> REQUEST_INFO_KEY =  AttributeKey.valueOf("requestInfo");

    /**
     *
     * @param httpRequest http请求
     * @return 本次请求的请求信息
     */
    public static RequestInfo getRequestInfo(HttpRequest httpRequest){
        String host = httpRequest.headers().get("host");
        String[] hostAndPort = host.split(":");

        int port = 80;
        boolean isHttps = false;

        if (hostAndPort.length > 1) {
            port = Integer.parseInt(hostAndPort[1]);
        } else if (httpRequest.uri().startsWith(HTTPS_PROTOCOL)) {
            port = 443;
            isHttps = true;
        }

        return new RequestInfo(hostAndPort[0], port, isHttps);
    }

    /**
     * 把请求信息存放在连接中的attr中，这样可以减少重复的请求信息解析过程，不过有一点要考虑，同域名下，不同port不同协议的请求会复用同一个连接么，
     * 大概率是不会的吧，不过也得查一查，万一会复用，这个逻辑就会有问题，就需要每次都新获取请求信息或者检查请求信息是否有变更
     * @param channel 连接信道
     * @return 信道中暂存的请求信息
     */
    public static RequestInfo getRequestInfoByAttr(Channel channel){
        Attribute<RequestInfo> requestInfoAttribute = channel.attr(REQUEST_INFO_KEY);
        return requestInfoAttribute.get();
    }

}
