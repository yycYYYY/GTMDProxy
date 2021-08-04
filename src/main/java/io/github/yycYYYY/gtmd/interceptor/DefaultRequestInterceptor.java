package io.github.yycYYYY.gtmd.interceptor;

import io.netty.channel.Channel;
import io.netty.handler.codec.http.*;

/**
 * @Author yuyongchao
 **/
public abstract class DefaultRequestInterceptor implements ProxyInterceptor {

    public static final int MAX_CONTENT_LENGTH = 1024 * 1024 * 8;

    @Override
    public final void beforeRequest(Channel clientChannel, HttpRequest httpRequest, InterceptotPipeline pipeline) throws Exception {
        if (httpRequest instanceof FullHttpRequest) {
            FullHttpRequest fullHttpRequest = (FullHttpRequest) httpRequest;
            handleRequest(fullHttpRequest, pipeline);
            fullHttpRequest.content().markReaderIndex();
            fullHttpRequest.content().retain();
            if (fullHttpRequest.headers().contains(HttpHeaderNames.CONTENT_LENGTH)) {
                fullHttpRequest.headers().set(HttpHeaderNames.CONTENT_LENGTH, fullHttpRequest.content().readableBytes());
            }
        } else if (match(httpRequest, pipeline)) {
            //重置拦截器
            pipeline.resetBeforeHead();
            //添加gzip解压处理
            clientChannel.pipeline().addAfter("httpCodec", "decompress", new HttpContentDecompressor());
            //添加Full request解码器
            clientChannel.pipeline().addAfter("decompress", "aggregator", new HttpObjectAggregator(MAX_CONTENT_LENGTH));
            //重新过一遍处理器链
            clientChannel.pipeline().fireChannelRead(httpRequest);
            return;
        }
        pipeline.beforeRequest(clientChannel, httpRequest);
    }

    @Override
    public final void afterResponse(Channel clientChannel, Channel proxyChannel, HttpResponse httpResponse, InterceptotPipeline pipeline) throws Exception {
        if (pipeline.getHttpRequest() instanceof FullHttpRequest) {
            if (clientChannel.pipeline().get("decompress") != null) {
                clientChannel.pipeline().remove("decompress");
            }
            if (clientChannel.pipeline().get("aggregator") != null) {
                clientChannel.pipeline().remove("aggregator");
            }
            FullHttpRequest httpRequest = (FullHttpRequest) pipeline.getHttpRequest();
            httpRequest.content().resetReaderIndex();
        }
        pipeline.afterResponse(clientChannel, proxyChannel, httpResponse);
    }

    public abstract boolean match(HttpRequest httpRequest, InterceptotPipeline pipeline);

    public abstract void handleRequest(FullHttpRequest httpRequest, InterceptotPipeline pipeline);
}
