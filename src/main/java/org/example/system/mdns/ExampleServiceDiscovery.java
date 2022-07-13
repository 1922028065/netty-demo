package org.example.system.mdns;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import javax.jmdns.JmDNS;
import javax.jmdns.ServiceEvent;
import javax.jmdns.ServiceInfo;
import javax.jmdns.ServiceListener;

public class ExampleServiceDiscovery {

    private static class SampleListener implements ServiceListener {
        @Override
        public void serviceAdded(ServiceEvent event) {
            System.out.println("Service added: " + event.getInfo());
        }

        @Override
        public void serviceRemoved(ServiceEvent event) {
            System.out.println("Service removed: " + event.getInfo());
        }

        @Override
        public void serviceResolved(ServiceEvent event) {
            System.out.println("Service resolved: " + event.getInfo());
        }
    }

    public static void main(String[] args) throws InterruptedException {
        try {
            // Create a JmDNS instance
            JmDNS jmdns = JmDNS.create();

            // Add a service listener
            //jmdns.addServiceListener("_http._tcp.local.", new SampleListener());
            //jmdns.addServiceListener("_udp.local.", new SampleListener());
            while (true){
                System.out.println("11111111111111");
                ServiceInfo[] list = jmdns.list("_udp.local.");
                System.out.println(Arrays.asList(list));
                System.out.println("2222222222222");
                ServiceInfo[] list1 = jmdns.list("_tcp.local.");
                System.out.println(Arrays.asList(list1));
/*                System.out.println("33333333333");
                ServiceInfo[] list2 = jmdns.list("_http._tcp.local.");
                System.out.println(Arrays.asList(list2));*/
            }
            // Wait a bit
            //Thread.sleep(30000);
        } catch (UnknownHostException e) {
            System.out.println(e.getMessage());
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}
