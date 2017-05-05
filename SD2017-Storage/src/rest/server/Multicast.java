package rest.server;

import org.glassfish.jersey.client.ClientConfig;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;

public class Multicast {


    public Multicast() {
    }


    public WebTarget GetMulticast() throws Exception {


        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        // multicast
        String hostname = null;
        String multAddress = "229.229.229.229";// igual no servidor de indexa��o
        int multPort = 9999;// igual no servidor de indexa��o
        InetAddress address = null;
        MulticastSocket socket = null;
        WebTarget target = null;

        address = InetAddress.getByName(multAddress);
        socket = new MulticastSocket();
        socket.setSoTimeout(3000);

                                //65536
        byte[] buffer = new byte[64000];
        String line = "rendezvous";
        DatagramPacket request = new DatagramPacket(line.getBytes(), line.length());
        request.setAddress(address);
        request.setPort(multPort);
        DatagramPacket reply = null;


        do{

            socket.send(request);
            reply = new DatagramPacket(buffer, buffer.length);
            socket.receive(reply);


        }while( reply.getData() == null );


        String serverUrl= new String(reply.getData(), 0, reply.getLength());
        hostname=serverUrl;

        URI baseURI = UriBuilder.fromUri(hostname).build();
        target = client.target(baseURI);

        return  target;

    }

}



