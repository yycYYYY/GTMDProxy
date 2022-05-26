package io.github.yycYYYY.gtmd.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

/**
 *
 * @author yyc
 */
public interface ProxyInterceptor {

    void beforeRequest(Channel clientChannel, HttpRequest httpRequest, InterceptotPipeline pipeline) throws Exception;

    void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, InterceptotPipeline pipeline) throws Exception;
}
