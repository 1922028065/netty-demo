package org.example.system.coap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.example.system.tcp.MessageHandler;

import java.text.SimpleDateFormat;
import java.util.Date;

public class JCoapServer {
    private final static Logger log  = LogManager.getLogger(JCoapServer.class);

    public static void main(String[] args) throws Exception {
        JCoapServer jCoapServer = new JCoapServer();

        try {
            jCoapServer.run();
        }catch (Exception e){
            e.printStackTrace();
            throw e;
        }finally {
            jCoapServer.close();
        }
    }

    private final CoapServer coapServer;

    private final int port;

    public JCoapServer() {
        this(5683);
    }

    public JCoapServer(int port) {
        this.port = port;
        this.coapServer = new CoapServer(port);
    }

    public void run(){
        log.debug("初始化服务器资源");

        coapServer.add(new CoapResource("hello"){
            @Override
            public void handleGET(CoapExchange exchange) {
                exchange.respond(CoAP.ResponseCode.CONTENT,"hello coap");
            }

        });


        coapServer.add(new CoapResource("time"){ //创建一个资源为time 请求格式为 主机：端口/time
            @Override
            public void handleGET(CoapExchange exchange) {
                exchange.respond(CoAP.ResponseCode.CONTENT,
                        new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
            }

        });

        coapServer.start();
        log.debug("coap 服务器启动成功，端口:{}",port);
        while (true){

        }

    }

    public void close(){
        if (coapServer!=null){
            coapServer.stop();
        }
        log.debug("coap 服务器关闭");
    }
}
