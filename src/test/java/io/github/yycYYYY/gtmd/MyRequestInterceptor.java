package io.github.yycYYYY.gtmd;

import io.github.yycYYYY.gtmd.interceptor.DefaultRequestInterceptor;
import io.github.yycYYYY.gtmd.interceptor.InterceptotPipeline;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.HttpRequest;

/**
 * @Author yuyongchao
 **/
public class MyRequestInterceptor extends DefaultRequestInterceptor {
    @Override
    public boolean match(HttpRequest httpRequest, InterceptotPipeline pipeline) {
        boolean res = false;
//        res = 判断逻辑
        return res;
    }

    @Override
    public void handleRequest(FullHttpRequest httpRequest, InterceptotPipeline pipeline) {

        //针对httpRequest请求的篡改
        httpRequest.setUri(httpRequest.uri()+"/test=1");
        httpRequest.headers().add("test","test");
    }
}
