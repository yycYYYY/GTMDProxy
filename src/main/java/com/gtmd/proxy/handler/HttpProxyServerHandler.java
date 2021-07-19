package com.gtmd.proxy.handler;

import com.gtmd.proxy.model.RequestInfo;
import com.gtmd.proxy.utils.RequestUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.Attribute;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.gtmd.proxy.utils.RequestUtil.REQUEST_INFO_KEY;

public class HttpProxyServerHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(HttpProxyServerHandler.class);

    private ChannelFuture cf;
    private String host;
    private int port;

    public final static HttpResponseStatus CONNECT_SUCCESS = new HttpResponseStatus(200,
            "Connection established");
    public final static String CONNECT_METHOD_NAME = "CONNECT";

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

            this.host = requestInfo.getHost();
            this.port = requestInfo.getPort();

            String methodName = httpRequest.method().name();
            if (CONNECT_METHOD_NAME.equalsIgnoreCase(methodName)){
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
    private void handlerHttpProto(Channel channel, Object msg, boolean isHttp){
        if (isHttp){
            //连接至目标服务器
            Bootstrap bootstrap = new Bootstrap();
            //注册线程池
            bootstrap.group(channel.eventLoop())
                    // 使用NioSocketChannel来作为连接用的channel类
                    .channel(channel.getClass())
                    .handler(new ProxyChannelInitializer(channel));

            ChannelFuture cf = bootstrap.connect(host, port);
            cf.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    channel.writeAndFlush(msg);
                } else {
                    channel.close();
                }
            });

        }else {
            if (cf == null) {
                //连接至目标服务器
                Bootstrap bootstrap = new Bootstrap();
                // 复用客户端连接线程池
                bootstrap.group(channel.eventLoop())
                        // 使用NioSocketChannel来作为连接用的channel类
                        .channel(channel.getClass())
                        .handler(new ChannelInitializer() {

                            @Override
                            protected void initChannel(Channel ch) throws Exception {
                                ch.pipeline().addLast(new ChannelInboundHandlerAdapter() {
                                    @Override
                                    public void channelRead(ChannelHandlerContext ctx0, Object msg) throws Exception {
                                       channel.writeAndFlush(msg);
                                        System.out.println(msg);
                                    }
                                });
                            }
                        });
                cf = bootstrap.connect(host, port);
                cf.addListener((ChannelFutureListener) future -> {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(msg);
                    } else {
                        channel.close();
                    }
                });
            } else {
                cf.channel().writeAndFlush(msg);
            }
        }
    }

}
