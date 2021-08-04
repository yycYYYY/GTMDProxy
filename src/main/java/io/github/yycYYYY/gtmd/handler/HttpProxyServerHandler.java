package io.github.yycYYYY.gtmd.handler;


import io.github.yycYYYY.gtmd.interceptor.InterceptorInitializer;
import io.github.yycYYYY.gtmd.model.ProxyInfo;
import io.github.yycYYYY.gtmd.model.RequestInfo;
import io.github.yycYYYY.gtmd.utils.RequestUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.proxy.HttpProxyHandler;
import io.netty.handler.proxy.ProxyHandler;
import io.netty.handler.proxy.Socks4ProxyHandler;
import io.netty.handler.proxy.Socks5ProxyHandler;
import io.netty.util.Attribute;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.Deque;


public class HttpProxyServerHandler extends ChannelInboundHandlerAdapter {
    private final static Logger logger = LoggerFactory.getLogger(HttpProxyServerHandler.class);

    private ChannelFuture cf;
    private String host;
    private int port;
    private Deque<Object> pendingQueue;
    private ProxyInfo proxyInfo;
    private InterceptorInitializer interceptorInitializer;
    private boolean isConnect;
    private RequestInfo requestInfo;

    public final static HttpResponse CONNECT_SUCCESS_RESPONSE = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1,
            new HttpResponseStatus(200, "Connection established"));
    public final static String CONNECT_METHOD_NAME = "CONNECT";

    public HttpProxyServerHandler(ProxyInfo proxyInfo, InterceptorInitializer interceptorInitializer) {
        this.proxyInfo = proxyInfo;
        this.interceptorInitializer = interceptorInitializer;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        logger.debug("[HttpProxyHandler]");
        if (msg instanceof HttpRequest) {
            HttpRequest httpRequest = (HttpRequest) msg;

            logger.debug("uri:{}, method:{}",httpRequest.uri(),httpRequest.method());

            requestInfo = RequestUtil.getRequestInfoByAttr(ctx.channel());
            logger.debug("host:{}, port:{}, isHttp:{}",requestInfo.getHost(),requestInfo.getPort(),requestInfo.isHttps());
            if (requestInfo == null) {

                requestInfo = RequestUtil.getRequestInfo(httpRequest);
                Attribute<RequestInfo> requestInfoAttr = ctx.channel().attr(RequestUtil.REQUEST_INFO_KEY);
                //将requestInfo保存到channel中，这样不需要每一个请求都去解析保存请求信息
                requestInfoAttr.setIfAbsent(requestInfo);
            }

            this.host = requestInfo.getHost();
            this.port = requestInfo.getPort();

            String methodName = httpRequest.method().name();
            if (CONNECT_METHOD_NAME.equalsIgnoreCase(methodName)){
                handleConnectProto(ctx);
                ReferenceCountUtil.release(msg);
                logger.debug("connect success!");
                return;
            }
            //注意这里的赋值位置可能有问题
            proxyInfo.setRequestInfo(requestInfo);

        }else if (msg instanceof HttpContent){
            //处理HttpContent
        }else {
            //ssl和socket
            ByteBuf byteBuf = (ByteBuf) msg;
            // ssl握手
            if (byteBuf.getByte(0) == 22) {
                logger.debug("[do ssl hands]");
            }
        }

        //TODO： proxyInfo缺少初始化
        handleHttpProto(ctx.channel(), msg, proxyInfo);
    }


    /**
     * 处理连接报文
     * @param ctx 上下文
     */
    private void handleConnectProto(ChannelHandlerContext ctx) {
            ctx.writeAndFlush(CONNECT_SUCCESS_RESPONSE);
            ctx.channel().pipeline().remove("httpRequestDecoder");
            ctx.channel().pipeline().remove("httpResponseEncoder");
            ctx.channel().pipeline().remove("httpAggregator");

    }

    /**
     * 处理http/https报文转发
     * @param channel
     * @param msg
     */
    private void handleHttpProto(Channel channel, Object msg, ProxyInfo proxyInfo){

        RequestInfo requestInfo = proxyInfo.getRequestInfo();

        if (cf == null){

            ProxyHandler proxyHandler = buildProxyHandler(proxyInfo);
            Bootstrap bootstrap = new Bootstrap();

            bootstrap.group(channel.eventLoop())
                    .channel(channel.getClass())
                    .handler(proxyHandler);

            cf = bootstrap.connect(requestInfo.getHost(), requestInfo.getPort());

            cf.addListener((ChannelFutureListener) future -> {
                if (future.isSuccess()) {
                    future.channel().writeAndFlush(msg);
                    synchronized (pendingQueue) {
                        pendingQueue.forEach(obj -> future.channel().writeAndFlush(obj));
                        pendingQueue.clear();
                        isConnect = true;
                    }
                } else {
                    pendingQueue.forEach(ReferenceCountUtil::release);
                    pendingQueue.clear();
                    future.channel().close();
                    channel.close();
                }
            });

        }else {
            synchronized (pendingQueue){
                if (isConnect){
                    cf.channel().writeAndFlush(msg);
                }else {
                    pendingQueue.add(msg);
                }
            }
        }

    }

    private ProxyHandler buildProxyHandler(ProxyInfo proxyInfo){

        ProxyHandler proxyHandler;
        InetSocketAddress inetSocketAddress = new InetSocketAddress(proxyInfo.getRequestInfo().getHost(), proxyInfo.getRequestInfo().getPort());
        switch (proxyInfo.getProxyType()){
            case HTTP:
                proxyHandler = new HttpProxyHandler(inetSocketAddress);
                break;

            case SOCKET4:
                proxyHandler = new Socks4ProxyHandler(inetSocketAddress);
                break;

            case SOCKET5:
                proxyHandler = new Socks5ProxyHandler(inetSocketAddress);
                break;

            default:
                throw new RuntimeException("[unknown protocol]不支持的协议");
        }
        return proxyHandler;
    }
}
