package org.example.system.udp;

import io.netty.bootstrap.Bootstrap;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.system.tcp.MessageHandler;

import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;


public class UdpServer {
    private final static Logger log = LogManager.getLogger(UdpServer.class);

    public static void main(String[] args) throws InterruptedException {
        UdpServer tcpServer = new UdpServer(9999);
        try {
            tcpServer.run();
        } catch (InterruptedException e) {
            throw e;
        } finally {
            tcpServer.close();
        }
    }


    private int port = 8080;
    private EventLoopGroup boosGroup;


    public UdpServer() {
        this(8080);
    }

    public UdpServer(int port) {
        this.port = port;
    }


    public void run() throws InterruptedException {
        log.info("开始启动udp 服务器...");
        log.info("初始化boosGroup线程组");
        boosGroup = new NioEventLoopGroup(1);

        log.info("配置服务器启动器");
        Bootstrap serverBootstrap = new Bootstrap();
        serverBootstrap.group(boosGroup)
                .channel(NioDatagramChannel.class)
                .handler(new ChannelInitializer<NioDatagramChannel>() {
                    @Override
                    protected void initChannel(NioDatagramChannel channel) throws Exception {
                        channel.pipeline()
                                //.addLast(new StringDecoder(Charset.forName("GBK")))
                                //.addLast(new StringEncoder(Charset.forName("GBK")))
                                .addLast("udpMessageHandler", new UdpMessageHandler());
                    }
                })
                .option(ChannelOption.SO_BROADCAST, true)
                .option(ChannelOption.SO_RCVBUF, 2048 * 1024)
                .option(ChannelOption.SO_SNDBUF, 1024 * 1024);
        try {
            ChannelFuture channelFuture = serverBootstrap.bind(port).sync();
            log.info("监听端口：{}", port);

            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("端口监听失败", e);
            throw e;
        }
    }

    public void close() {
        if (boosGroup != null) {
            boosGroup.shutdownGracefully();
        }
    }
}
