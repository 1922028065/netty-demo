package org.example.system.mdns;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceInfo;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class JmdnsTest {
    public static void main(String[] args) throws IOException, InterruptedException {
        JmDNS jmdns = JmDNS.create();
        ServiceInfo serviceInfo = ServiceInfo.create("_http._tcp.local.", "example", 1234, "path=index.html");
        ServiceInfo serviceInfo1 = ServiceInfo.create("_udp.local.", "example", 2222, "path=index.html");
        ServiceInfo serviceInfo2 = ServiceInfo.create("_tcp.local.", "example1", 3333, "");
        jmdns.registerService(serviceInfo);
        jmdns.registerService(serviceInfo1);
        jmdns.registerService(serviceInfo2);

        Thread.sleep(60000);

        // Unregister all services
        jmdns.unregisterAllServices();
    }
}
