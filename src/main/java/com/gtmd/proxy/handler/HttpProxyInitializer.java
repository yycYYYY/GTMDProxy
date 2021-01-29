package com.gtmd.proxy.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

public class HttpProxyInitializer extends ChannelInitializer {

    private Channel clientChannel;

    public HttpProxyInitializer(Channel channel) {
        this.clientChannel = channel;
    }

    @Override
    protected void initChannel(Channel channel) throws Exception {
        channel.pipeline().addLast(new HttpClientCodec());
        channel.pipeline().addLast(new HttpObjectAggregator(6553600));
        channel.pipeline().addLast(new HttpProxyClientHandle(clientChannel));
    }
}
