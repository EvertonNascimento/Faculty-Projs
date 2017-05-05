
package rest.server;

import java.net.*;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import api.HeartBeat;


public class IndexerServer {

    private static HeartBeat heart;

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws Exception {


        int port = 8081;
        URI baseUri = null;
        WebTarget target;

        if(args.length < 1){
            Multicast m = new Multicast();
            target = m.GetMulticast();

            }
        else{

            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);
            URI baseURI = UriBuilder.fromUri(args[0]).build();
            target = client.target(baseURI);

        }


        baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();

        ResourceConfig config = new ResourceConfig();
        config.register(new IndexingResources());

        JdkHttpServerFactory.createHttpServer(baseUri, config);

        System.err.println("REST Indexing Server ready @ " + baseUri + " : local IP = "
                + InetAddress.getLocalHost().getHostAddress());




        //regista servidor
        String serverUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;


        Map<String, Object> attributes = new HashMap<>();
        attributes.put("type", "rest");

        api.Endpoint endpoint = new api.Endpoint(serverUrl, attributes);
        Response response = null;

        String indexerID = endpoint.generateId();


        boolean executed = false;
        for (int i = 0; !executed && i < 3; i++) {
            try {
                response = target.path( indexerID).request()
                        .post(Entity.entity(endpoint, MediaType.APPLICATION_JSON));
                executed = true;
            } catch (RuntimeException e) {
                if (i < 2) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                    }
                }
            }
        }

        System.err.println(response.getStatus() + " info: " + response.getStatusInfo());


        //heartbeat
        String multAddress = "229.229.229.229";
        MulticastSocket sock = null;
        int multPort = 9999;

        final InetAddress addr = InetAddress.getByName(multAddress);
        sock = new MulticastSocket();

        heart = new HeartBeat(sock, addr, multPort, indexerID);
        heart.run();

    }

}
