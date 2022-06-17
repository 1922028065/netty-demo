package org.example.system.chat.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class ChatClient {
    private static final Logger log = LogManager.getLogger(ChatClient.class);

    public static void main(String[] args) {
        ChatClient chatClient = new ChatClient("127.0.0.1", 7777);
        chatClient.run();
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            if (msg.equals("exit")){
                break;
            }
            chatClient.sendMsg(msg);
        }

        chatClient.close();
    }

    private final String host;
    private final int port;

    private NioEventLoopGroup group;
    private Channel channel;

    ChatClient(String host, int port) {
        this.host = host;
        this.port = port;


    }

    private void run() {
        log.info("开始启动客户端...");

        log.info("创建group线程组");
        group = new NioEventLoopGroup();

        log.info("创建启动器");
        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(group)
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) throws Exception {
                        log.info("初始化处理器链");
                        ChannelPipeline pipeline = ch.pipeline();
                        pipeline.addLast("decoder", new StringDecoder())
                                .addLast("encoder", new StringEncoder())
                                .addLast("groupChatClientHandler", new GroupChatClientHandler());
                    }
                });
        ChannelFuture channelFuture = null;
        try {
            channelFuture = bootstrap.connect(host, port).sync();
        } catch (InterruptedException e) {
            log.error("连接服务器 url：[{}:{}] 失败", host, port, e);
        }
        this.channel = channelFuture.channel();
        log.info("连接到服务器:[{}:{}] 成功",host,port);
        System.out.println("连接到服务器: " + host + ":" + port + " ...");

    }

    public void close(){
        if (group!=null) {
            group.shutdownGracefully();
        }
    }

    public void sendMsg(String msg) {
        if (channel != null && channel.isActive()) {
            channel.writeAndFlush(msg);
        }
    }
}
