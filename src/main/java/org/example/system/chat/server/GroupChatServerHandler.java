package org.example.system.chat.server;


import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.text.SimpleDateFormat;

/**
 * 群聊处理器
 */
public class GroupChatServerHandler extends SimpleChannelInboundHandler<String> {
    public static final Logger log = LogManager.getLogger(GroupChatServerHandler.class);

    /**
     * 所有用户的channel
     */
    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    private static final SimpleDateFormat FORMAT=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("[{}] 上线了",ctx.channel().remoteAddress());
        tellAllExcludeSelf(ctx.channel().remoteAddress() + " 上线\n",ctx.channel());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("[{}] 下线了",ctx.channel().remoteAddress());
        tellAllExcludeSelf(ctx.channel().remoteAddress() + " 下线\n",ctx.channel());
    }

    /**
     * 连接建立立即执行
     * @param ctx
     * @throws Exception
     */
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        log.info("[{}] 加入聊天室",ctx.channel().remoteAddress());
        channelGroup.add(ctx.channel());
        ctx.channel().writeAndFlush(ctx.channel().remoteAddress() + " 加入聊天室\n");
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("[{}] 离开聊天室",ctx.channel().remoteAddress());
        log.info("当前在线人数：{}",channelGroup.size());
        channelGroup.remove(ctx.channel());
        ctx.channel().writeAndFlush(ctx.channel().remoteAddress() + " 离开聊天室\n");
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        tellAllExcludeSelf("[客户端 "+ctx.channel().remoteAddress()+"] "+msg + "\n",ctx.channel());
    }

    private void tellAllExcludeSelf(String msg,Channel self){
        channelGroup.stream().filter(ch->ch!=self).forEach(ch->ch.writeAndFlush(msg));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("服务器发生异常",cause);
    }
}
