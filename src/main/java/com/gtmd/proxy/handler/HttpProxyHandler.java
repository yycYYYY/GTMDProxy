package com.gtmd.proxy.handler;

import com.gtmd.proxy.model.RequestInfo;
import com.gtmd.proxy.utils.RequestUtil;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.Attribute;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gtmd.proxy.utils.RequestUtil.REQUEST_INFO_KEY;

public class HttpProxyHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(HttpProxyHandler.class);

    public final static HttpResponseStatus CONNECT_SUCCESS = new HttpResponseStatus(200,
            "Connection established");

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("[HttpProxyHandler]");
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;

            RequestInfo requestInfo = RequestUtil.getRequestInfoByAttr(ctx.channel());
            if (requestInfo == null) {

                requestInfo = RequestUtil.getRequestInfo(httpRequest);
                Attribute<RequestInfo> requestInfoAttr = ctx.channel().attr(REQUEST_INFO_KEY);
                //将requestInfo保存到channel中，这样不需要每一个请求都去解析保存请求信息
                requestInfoAttr.setIfAbsent(requestInfo);
            }

            String methodName = httpRequest.method().name();
            if ("CONNECT".equalsIgnoreCase(methodName)){
                handlerConnectProto(ctx);
                ReferenceCountUtil.release(msg);
                return;
            }

            handlerHttpProto(ctx.channel(), msg, requestInfo.isHttps());

        }
    }


    /**
     * 处理连接报文
     * @param ctx 上下文
     */
    private void handlerConnectProto(ChannelHandlerContext ctx) {
            HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, CONNECT_SUCCESS);
            ctx.writeAndFlush(response);
            ctx.channel().pipeline().remove("httpRequestDecoder");
            ctx.channel().pipeline().remove("httpResponseEncoder");
            ctx.channel().pipeline().remove("httpAggregator");

    }

    /**
     * 处理http/https报文转发
     * @param channel
     * @param msg
     * @param isHttp
     */
    private void handlerHttpProto(Channel channel, Object msg, boolean isHttp){}

}
