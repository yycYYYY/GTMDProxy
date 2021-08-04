package io.github.yycYYYY.gtmd;

import io.github.yycYYYY.gtmd.interceptor.InterceptorInitializer;
import io.github.yycYYYY.gtmd.interceptor.InterceptotPipeline;
import io.github.yycYYYY.gtmd.server.ProxyServer;

/**
 * @Author yuyongchao
 **/
public class ProxyTestServer {
    public static void main(String[] args) {
        new ProxyServer().interceptorInitializer(new InterceptorInitializer(){
            @Override
            public void init(InterceptotPipeline pipeline) {
                //域名转发拦截器
                pipeline.addLast(new ForwardInterceptor());
                //请求拦截器
                pipeline.addLast(new MyRequestInterceptor());
                //响应拦截器
                pipeline.addLast(new MyResponseInterceptor());
            }
        })
        .start(8889);
    }
}
