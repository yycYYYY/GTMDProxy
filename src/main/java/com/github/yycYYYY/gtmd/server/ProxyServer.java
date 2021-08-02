package com.github.yycYYYY.gtmd.server;

import com.github.yycYYYY.gtmd.interceptor.InterceptorInitializer;
import com.github.yycYYYY.gtmd.model.ProxyInfo;
import com.github.yycYYYY.gtmd.handler.HttpProxyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author yyc
 */
public class ProxyServer {

    private final static Logger logger = LoggerFactory.getLogger(ProxyServer.class);

    private InterceptorInitializer interceptorInitializer;

    public ProxyServer interceptorInitializer(InterceptorInitializer interceptorInitializer){
        this.interceptorInitializer = interceptorInitializer;
        return this;
    }

    private void init(){

    }

    public void start(int port){
        ProxyInfo proxyInfo = new ProxyInfo();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .childOption(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.AUTO_READ, false)

//                    .childHandler(new ProxyChannelInitializer());
                    .childHandler(new ChannelInitializer(){
                        @Override
                        protected void initChannel(Channel ch) throws Exception {
                            ch.pipeline().addLast("httpCodec", new HttpServerCodec());
                            ch.pipeline().addLast("aggregator", new HttpObjectAggregator(65536));
                            ch.pipeline().addLast("httpHandler", new HttpProxyServerHandler(proxyInfo, interceptorInitializer));
                        }
                    });

            ChannelFuture f = b.bind(port).sync();
            logger.info("Proxy server start on port: {}", port);
            f.channel().closeFuture().sync();

        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    public static void main(String[] args) {
        ProxyServer server = new ProxyServer();
        server.start(6667);
    }
}
