package org.example.system.http.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class HttpNettyServer {
    private final static Logger log  = LogManager.getLogger(HttpNettyServer.class);

    public static void main(String[] args) throws InterruptedException {
        HttpNettyServer httpNettyServer = new HttpNettyServer(9999);
        try {
            httpNettyServer.run();
        } catch (InterruptedException e) {
            throw e;
        }finally {
            httpNettyServer.close();
        }
    }

    private int port=8080;
    private NioEventLoopGroup boosGroup;
    private NioEventLoopGroup workGroup;

    public HttpNettyServer(int port){
        this.port = port;
    }

    private void run() throws InterruptedException {
        log.info("开始启动http服务器...");

        log.info("初始化boosGroup、workGroup 线程组");
        boosGroup = new NioEventLoopGroup(1);
        workGroup = new NioEventLoopGroup(10);

        log.info("配置服务器启动器");
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boosGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel ch) throws Exception {
                        ch.pipeline()
                                .addLast("coder",new HttpServerCodec())
                                .addLast("httpAggregator",new HttpObjectAggregator(512*1024))
                                .addLast("HttpHandler",new HttpCustomHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("监听端口：{}",port);

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("端口监听失败",e);
            throw e;
        }

        
        
        log.info("关闭服务器...");
    }

    public void  close(){
        if (boosGroup!=null) {
            boosGroup.shutdownGracefully();
        }
        if (workGroup!=null){
            workGroup.shutdownGracefully();
        }
    }
}
