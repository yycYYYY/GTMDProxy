package io.github.yycYYYY.gtmd.interceptor;

import io.github.yycYYYY.gtmd.utils.MatchUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author yyc
 **/
public class DefaultForwardInterceptor implements ProxyInterceptor{

    private final Map<String, String> hostMap = new HashMap<>();

    public void addHostMap(String origin, String target) {
        hostMap.put(origin, target);
    }

    @Override
    public void beforeRequest(Channel clientChannel, HttpRequest httpRequest, InterceptotPipeline pipeline) throws Exception {
        String host = httpRequest.headers().get(HttpHeaderNames.HOST);
        if (hostMap.containsKey(host)) {
            String targetHost = hostMap.get(host);
            pipeline.getRequestInfo().setHost(targetHost);
            httpRequest.setUri(targetHost);
            httpRequest.headers().set(HttpHeaderNames.HOST,targetHost);
        }
        pipeline.beforeRequest(clientChannel, httpRequest);
    }

    @Override
    public final void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, InterceptotPipeline pipeline) throws Exception {

    }
}
