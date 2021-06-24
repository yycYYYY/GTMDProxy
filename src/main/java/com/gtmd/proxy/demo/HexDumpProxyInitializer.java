package com.gtmd.proxy.demo;

import com.gtmd.proxy.handler.DemoHandle;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
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
