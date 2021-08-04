package io.github.yycYYYY.gtmd;

import io.github.yycYYYY.gtmd.interceptor.DefaultResponseInterceptor;
import io.github.yycYYYY.gtmd.interceptor.InterceptotPipeline;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;

/**
 * @Author yuyongchao
 **/
public class MyResponseInterceptor extends DefaultResponseInterceptor {
    @Override
    public boolean match(HttpRequest httpRequest, HttpResponse httpResponse, InterceptotPipeline pipeline) {
        //如果目标服务器响应502了
        return httpResponse.status() == HttpResponseStatus.BAD_GATEWAY;
    }

    @Override
    public void handleResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, InterceptotPipeline pipeline) {
        //篡改为成功的响应
        httpResponse.setStatus(HttpResponseStatus.OK);
        httpResponse.content().writeBytes("success".getBytes());
    }
}
