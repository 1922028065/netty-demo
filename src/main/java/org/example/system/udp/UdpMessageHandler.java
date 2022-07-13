package org.example.system.udp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.DatagramPacket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.example.system.tcp.TcpServer;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class UdpMessageHandler extends SimpleChannelInboundHandler<DatagramPacket> {
    private final static Logger log  = LogManager.getLogger(UdpMessageHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DatagramPacket packet) throws Exception {
        log.debug("channelId: {} , 接受msg: {}",ctx.channel().id(),packet.content().toString(Charset.forName("GBK")));
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.debug("{} 开始连接",ctx.channel().id());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.debug("{} 断开连接",ctx.channel().id());
    }
}
