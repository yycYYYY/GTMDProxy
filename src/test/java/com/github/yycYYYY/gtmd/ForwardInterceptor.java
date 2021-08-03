package com.github.yycYYYY.gtmd;

import com.github.yycYYYY.gtmd.interceptor.DefaultForwardInterceptor;
import com.github.yycYYYY.gtmd.interceptor.DefaultRequestInterceptor;
import com.github.yycYYYY.gtmd.interceptor.InterceptotPipeline;
import com.github.yycYYYY.gtmd.utils.MatchUtil;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @Author yuyongchao
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
