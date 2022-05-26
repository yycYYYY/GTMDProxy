package io.github.yycYYYY.gtmd;

import io.github.yycYYYY.gtmd.interceptor.DefaultForwardInterceptor;
import io.github.yycYYYY.gtmd.interceptor.InterceptotPipeline;
import io.github.yycYYYY.gtmd.utils.MatchUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @Author yyc
 **/
public class ForwardInterceptor extends DefaultForwardInterceptor {

    @Override
    public void beforeRequest(Channel clientChannel, HttpRequest httpRequest, InterceptotPipeline pipeline) throws Exception {
        if (MatchUtil.matchHost(httpRequest, "www.123.com")) {
            pipeline.getRequestInfo().setHost("www.456.com");
            pipeline.getRequestInfo().setPort(80);
            pipeline.getRequestInfo().setHttps(false);
        }
        pipeline.beforeRequest(clientChannel, httpRequest);
    }

}
