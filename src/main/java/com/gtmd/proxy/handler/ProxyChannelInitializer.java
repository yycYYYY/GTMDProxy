package com.gtmd.proxy.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;

/**
 * @author yyc
 */
public class ProxyChannelInitializer extends ChannelInitializer<SocketChannel> {

    private Channel clientChannel;

    public ProxyChannelInitializer(Channel channel) {
        this.clientChannel = channel;
    }

    public ProxyChannelInitializer() {
    }
    // 这里有一个问题，就是两种情况，哪种写法比较好？还是没有区别
    // 1、把所有handler都添加到pipeline中，在管道中，由netty判断数据包类型再解析
    // 2、自己把类型判断了，然后再确定在pipeline中加哪些handler
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast("httpCodec", new HttpServerCodec());
        ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
        ch.pipeline().addLast("httpHandler", new HttpProxyServerHandle());
        ch.pipeline().addLast("httpsHandler", new HttpProxyServerHandle());
        ch.pipeline().addLast("socksHandler", new HttpProxyServerHandle());
        ch.pipeline().addLast("udpHandler", new HttpProxyServerHandle());
    }
}
