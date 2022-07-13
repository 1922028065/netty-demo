package org.example.system.coap;

import lombok.SneakyThrows;
import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.Endpoint;
import org.eclipse.californium.elements.exception.ConnectorException;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class JCoapClient {
    public static void main(String[] args) throws ConnectorException, IOException {
        CoapClient coapClient = new CoapClient("127.0.0.1:5683");
    }

    private final String ip;
    private final int port;

    private CoapClient coapClient;

    JCoapClient(){
        this("127.0.0.1",5683);
    }

    JCoapClient(String ip,int port){
        this.ip = ip;
        this.port = port;
        this.coapClient = new CoapClient(ip + ":" +port);
    }

    public void run(){
    }


}
