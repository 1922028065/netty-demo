package org.example.system.http.server;


import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.handler.codec.http.multipart.DefaultHttpDataFactory;
import io.netty.handler.codec.http.multipart.HttpPostRequestDecoder;
import io.netty.handler.codec.http.multipart.InterfaceHttpData;
import io.netty.handler.codec.http.multipart.MemoryAttribute;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HttpCustomHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private final static Logger log = LogManager.getLogger(HttpCustomHandler.class);

    private Gson gson = new Gson();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        if (request.uri().equals("/favicon.ico")){
            return;
        }
        log.info("请求地址: [{},{}],请求方法类型: [{}]",ctx.channel().remoteAddress(),request.uri(),request.method().name());


        HttpMethod method = request.method();

        Map<String, Object> params = new HashMap<>();
        if (method == HttpMethod.GET){
            QueryStringDecoder decoder = new QueryStringDecoder(request.uri());
            decoder.parameters().forEach((key, value) -> params.put(key, value.get(0)));
        }else if (method == HttpMethod.POST){
            String contentType = request.headers().get("Content-Type").trim();
            if(contentType.contains("x-www-form-urlencoded")){
                HttpPostRequestDecoder decoder = new HttpPostRequestDecoder(new DefaultHttpDataFactory(false), request);
                List<InterfaceHttpData> postData = decoder.getBodyHttpDatas();
                for (InterfaceHttpData data : postData) {
                    if (data.getHttpDataType() == InterfaceHttpData.HttpDataType.Attribute) {
                        MemoryAttribute attribute = (MemoryAttribute) data;
                        params.put(attribute.getName(), attribute.getValue());
                    }
                }
            }else if(contentType.contains("application/json")){
                JsonObject jsonObject = gson.fromJson(request.content().toString(), JsonObject.class);
                jsonObject.entrySet().forEach(e->params.put(e.getKey(),e.getValue()));
            }
        }

        log.info("请求参数:[{}]",params);

        log.info("包装响应...");
        DefaultFullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_0, HttpResponseStatus.OK);


        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/html; charset=UTF-8");
        log.info("返回响应：[{}]",response);
        ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }
}
