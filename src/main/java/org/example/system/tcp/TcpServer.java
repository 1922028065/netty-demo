package org.example.system.tcp;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.epoll.EpollEventLoopGroup;
import io.netty.channel.epoll.EpollServerSocketChannel;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.Buffer;
import java.nio.charset.Charset;
import java.util.concurrent.TimeUnit;


public class TcpServer {
    private final static Logger log = LogManager.getLogger(TcpServer.class);

    public static void main(String[] args) throws InterruptedException {
        TcpServer tcpServer = new TcpServer(9999);
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
    private EventLoopGroup workGroup;

    /**
     * 传输模式linux上开启会有更高的性能
     */
    private boolean useEpoll;

    /**
     * 数据分隔符
     */
    private ByteBuf delimiter;

    public TcpServer() {
        this(8080,false,"\n");
    }

    public TcpServer(int port) {
        this(port,false,"\n");
    }

    public TcpServer(int port, boolean useEpoll,String delimiter) {
        this.port = port;
        this.useEpoll = useEpoll;
        this.delimiter = Unpooled.copiedBuffer(delimiter.getBytes());
    }



    public void run() throws InterruptedException {
        log.info("开始启动tcp服务器...");
        log.info("初始化boosGroup、workGroup 线程组");
        boosGroup = useEpoll ? new EpollEventLoopGroup() : new NioEventLoopGroup(1);
        workGroup = useEpoll ? new EpollEventLoopGroup() : new NioEventLoopGroup(10);

        log.info("配置服务器启动器");
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boosGroup, workGroup)
                .channel(useEpoll ? EpollServerSocketChannel.class : NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<NioSocketChannel>() {
                    @Override
                    protected void initChannel(NioSocketChannel channel) throws Exception {
                        channel.pipeline()
                                .addLast("idle", new IdleStateHandler(0, 0, 60, TimeUnit.SECONDS))
                                .addLast(new DelimiterBasedFrameDecoder(1024,delimiter))
                                .addLast(new StringDecoder(Charset.forName("GBK")))
                                .addLast(new StringEncoder(Charset.forName("GBK")))

                                .addLast("messageHandler", new MessageHandler());
                    }
                })
                .option(ChannelOption.SO_BACKLOG, 128)
                .childOption(ChannelOption.TCP_NODELAY, false)
                .childOption(ChannelOption.SO_KEEPALIVE, true);

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
        if (workGroup != null) {
            workGroup.shutdownGracefully();
        }
    }
}
