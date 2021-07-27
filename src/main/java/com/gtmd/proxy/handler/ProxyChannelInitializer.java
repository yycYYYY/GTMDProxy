package com.gtmd.proxy.handler;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author yyc
 */
public class ProxyChannelInitializer extends ChannelInitializer<SocketChannel> {

    public ProxyChannelInitializer() {
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("httpCodec", new HttpServerCodec());
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("httpHandler", new HttpProxyServerHandler());
//        ch.pipeline().addLast("httpsHandler", new HttpProxyServerHandler());
//        ch.pipeline().addLast("socksHandler", new HttpProxyServerHandler());
//        ch.pipeline().addLast("udpHandler", new HttpProxyServerHandler());
    }
}
