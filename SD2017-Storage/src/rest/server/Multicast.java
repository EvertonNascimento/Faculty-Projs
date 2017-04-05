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

/**
 * Created by GOD on 4/4/2017.
 */
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

        DatagramPacket reply = null;
                                //65536
        byte[] buffer = new byte[64000];
        DatagramPacket request = new DatagramPacket(buffer, buffer.length);
        request.setAddress(address);
        request.setPort(multPort);

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



