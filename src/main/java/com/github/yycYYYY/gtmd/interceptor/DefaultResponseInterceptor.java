package com.github.yycYYYY.gtmd.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;

import static com.github.yycYYYY.gtmd.interceptor.DefaultRequestInterceptor.MAX_CONTENT_LENGTH;

/**
 * @Author yuyongchao
 **/
public abstract class DefaultResponseInterceptor implements ProxyInterceptor {

    private boolean isMatch;

    @Override
    public final void beforeRequest(Channel clientChannel, HttpRequest httpRequest, InterceptotPipeline pipeline) throws Exception {

    }

    @Override
    public final void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, InterceptotPipeline pipeline) throws Exception {
        if (httpResponse instanceof FullHttpResponse) {
            FullHttpResponse fullHttpResponse = (FullHttpResponse) httpResponse;
            // 判断是第一个处理FullResponse的拦截器是否匹配
            boolean isFirstMatch = isMatch;
            // 判断后续的拦截器是否匹配
            boolean isAfterMatch = !isFirstMatch && match(pipeline.getHttpRequest(), pipeline.getHttpResponse(), pipeline);
            if (isFirstMatch || isAfterMatch) {
                handleResponse(pipeline.getHttpRequest(), fullHttpResponse, pipeline);
                if (fullHttpResponse.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
                    httpResponse.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpResponse.content().readableBytes());
                }
                if (pipeline.getHttpRequest() instanceof FullHttpRequest) {
                    FullHttpRequest fullHttpRequest = (FullHttpRequest) pipeline.getHttpRequest();
                    if (fullHttpRequest.content().refCnt() > 0) {
                        ReferenceCountUtil.release(fullHttpRequest);
                    }
                }
            }
            if (isFirstMatch) {
                proxyChannel.pipeline().remove("decompress");
                proxyChannel.pipeline().remove("aggregator");
            }
        } else {
            this.isMatch = match(pipeline.getHttpRequest(), pipeline.getHttpResponse(), pipeline);
            if (this.isMatch) {
                proxyChannel.pipeline().addAfter("httpCodec", "decompress", new HttpContentDecompressor());
                proxyChannel.pipeline()
                        .addAfter("decompress", "aggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH));
                proxyChannel.pipeline().fireChannelRead(httpResponse);
                return;
            }
        }
        pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
    }

    public abstract boolean match(HttpRequest httpRequest, HttpResponse httpResponse,
                                  InterceptotPipeline pipeline);

    public abstract void handleResponse(HttpRequest httpRequest, FullHttpResponse httpResponse, InterceptotPipeline pipeline);
}
