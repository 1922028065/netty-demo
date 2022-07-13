package org.example.system.tcp;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MessageHandler extends SimpleChannelInboundHandler<String> {
    private final static Logger log  = LogManager.getLogger(MessageHandler.class);
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String s) throws Exception {
        log.debug("channelId: {} , 接受msg: {}",ctx.channel().id(),s);
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
