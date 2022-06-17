package org.example.system.chat.server;


import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 聊天服务器
 */
public class ChatSystemServer {
    public static final Logger log = LogManager.getLogger(ChatSystemServer.class);

    private final int port;
    private NioEventLoopGroup boosGroup;
    private NioEventLoopGroup workGroup;


    public static void main(String[] args) {
        int port=7777;

        log.info("开始启动聊天系统...");
        ChatSystemServer chatSystemServer = new ChatSystemServer(port);

        chatSystemServer.run();
        chatSystemServer.close();
    }

    ChatSystemServer(int port){
        this.port = port;
    }

    private void run(){

        log.info("创建boosGroup、workGroup线程组成功");
        boosGroup = new NioEventLoopGroup(5);
        workGroup = new NioEventLoopGroup(10);

        log.info("配置netty server 启动器");
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(boosGroup,workGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG,128)
                .childOption(ChannelOption.SO_KEEPALIVE,true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) throws Exception {
                        ChannelPipeline pipeline = sc.pipeline();
                        //添加数据编解码处理器
                        pipeline.addLast("decoder",new StringDecoder())
                                .addLast("encoder",new StringEncoder())
                                //自己的处理器
                                .addLast("myHandler",new GroupChatServerHandler());
                    }
                });

        log.info("开始绑定端口: [{}]",port);
        ChannelFuture channelFuture=null;
        try {
            channelFuture = serverBootstrap.bind(port).sync();




        } catch (InterruptedException e) {
            log.error("服务器绑定端口失败",e);
        }

        log.info("聊天系统启动成功!!!");

        try {
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("聊天系统关闭线程中断",e);
        }


    }

    private void close(){
        if (boosGroup!=null) {
            log.info("关闭boosGroup线程组");
            boosGroup.shutdownGracefully();
        }
        if (workGroup!=null){
            log.info("关闭workGroup线程组");
            workGroup.shutdownGracefully();
        }
    }
}
