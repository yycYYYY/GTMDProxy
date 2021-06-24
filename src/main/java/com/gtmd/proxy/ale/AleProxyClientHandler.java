package com.gtmd.proxy.ale;

import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

/**
 * @Author yuyongchao
 **/
public class AleProxyClientHandler extends ChannelInboundHandlerAdapter {
    private Channel inBoundChannel;

    public AleProxyClientHandler(Channel outBoundChannel) {
        this.inBoundChannel = outBoundChannel;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {

        if ( inBoundChannel.isActive() ) {
            inBoundChannel.writeAndFlush(msg);
        } else {
            ctx.close();
        }

    }
}
