package com.gtmd.proxy.demo;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class HexDumpProxyInitializer extends ChannelInitializer<SocketChannel> {

    static final String remoteHost = "localhost";
    static final int remotePort = 1083;

    @Override
    public void initChannel(SocketChannel ch) {
//        ch.pipeline().addLast("httpCodec",new HttpServerCodec());
//        ch.pipeline().addLast("httpObject",new HttpObjectAggregator(65536));
//        ch.pipeline().addLast("serverHandle",new DemoHandle());
        ch.pipeline().addLast(
                new LoggingHandler(LogLevel.DEBUG),
                new HexDumpProxyFrontendHandler());
    }
}
