package rest.server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class RendezVousServer {

    public static void main(String[] args) throws Exception {
        int port = 8080;
        if (args.length > 0)
            port = Integer.parseInt(args[0]);

        URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();

        ResourceConfig config = new ResourceConfig();
        config.register(new RendezVousResources());

        JdkHttpServerFactory.createHttpServer(baseUri, config);

        System.err.println("REST RendezVous Server ready @ " + baseUri + " : local IP = "
                + InetAddress.getLocalHost().getHostAddress());

        // multicast
        String multAddress = "229.229.229.229";//igual no servidor de indexa��o
        int multPort = 9999;//igual no servidor de indexa��o

        final InetAddress address = InetAddress.getByName(multAddress);
        MulticastSocket socket = new MulticastSocket(multPort);
        socket.joinGroup(address);
        byte[] buffer = new byte[65536];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
        String serverUrl=null;


        while (true) {
            socket.receive(request);
            serverUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
            reply.setSocketAddress(new InetSocketAddress(request.getAddress(), request.getPort()));
            reply.setData(serverUrl.getBytes());
            socket.send(reply);
        }


    }
}