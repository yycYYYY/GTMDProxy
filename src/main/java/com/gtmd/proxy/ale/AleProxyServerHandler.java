package com.gtmd.proxy.ale;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;

/**
 * @Author yuyongchao
 **/
public class AleProxyServerHandler extends ChannelInboundHandlerAdapter {
    private String remoteHost = "localhost";
    private int remotePort = 80;

    private Channel outBoundChannel;

    public AleProxyServerHandler() {
        super();
    }
    public AleProxyServerHandler(String remoteHost, int remotePort) {
        super();
        this.remoteHost = remoteHost;
        this.remotePort = remotePort;
    }

    @Override
    public void channelRead(final ChannelHandlerContext ctx, final Object msg) throws Exception {

        if(outBoundChannel==null || !ctx.channel().isActive()) {
            /* 创建netty client,连接到远程地址 */
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(ctx.channel().eventLoop())
                    .channel(ctx.channel().getClass())
                    .handler(new ChannelInitializer<SocketChannel>(){
                        @Override
                        protected void initChannel(SocketChannel ch)
                                throws Exception {
                            ChannelPipeline pipeline = ch.pipeline();
                            pipeline.addLast("codec", new HttpClientCodec());
                            pipeline.addLast("aggregator", new HttpObjectAggregator(1048576));
                            pipeline.addLast(new AleProxyClientHandler(ctx.channel()));
                        }});
            ChannelFuture future = bootstrap.connect(remoteHost,remotePort);
            outBoundChannel = future.channel();

            /* channel建立成功后,将请求发送给远程主机 */
            future.addListener(new ChannelFutureListener(){
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        future.channel().writeAndFlush(msg);
                    } else {
                        future.channel().close();
                    }
                }

            });
        } else {
            outBoundChannel.writeAndFlush(msg);
        }
    }
}
