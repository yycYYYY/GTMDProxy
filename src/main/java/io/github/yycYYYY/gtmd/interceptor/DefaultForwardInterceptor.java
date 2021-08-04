package io.github.yycYYYY.gtmd.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 * @Author yuyongchao
 **/
public class DefaultForwardInterceptor implements ProxyInterceptor{

    @Override
    public void beforeRequest(Channel clientChannel, HttpRequest httpRequest, InterceptotPipeline pipeline) throws Exception {

    }

    @Override
    public final void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, InterceptotPipeline pipeline) throws Exception {

    }
}
