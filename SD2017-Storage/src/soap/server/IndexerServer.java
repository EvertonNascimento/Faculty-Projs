package soap.server;

import api.HeartBeat;
import org.glassfish.jersey.client.ClientConfig;
import rest.server.Multicast;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.ws.Endpoint;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rui Soares n 41783
 */
public class IndexerServer {

    private static HeartBeat heart;


    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws Exception {


        /**
         *
         * AQUI NAO E PRECISO MULTICAST. NOS TESTES O ADRESS DO RENDEVOUS E PASSADO NOS ARGS
         *
         */

        int port;
        WebTarget target;

        if(args.length < 1){
            Multicast m = new Multicast();
            target = m.GetMulticast();

        port = 9090;}
        else{

            ClientConfig config = new ClientConfig();
            Client client = ClientBuilder.newClient(config);
            URI baseURI = UriBuilder.fromUri(args[0]).build();
            target = client.target(baseURI);

        port =9090;
        }

        String baseURI = String.format("http://0.0.0.0:%d/indexer", port);

        Endpoint.publish(baseURI, new IndexerServerImpl());

        System.err.println("SOAP Indexer Server ready @ " + baseURI + " : local IP = "
                + InetAddress.getLocalHost().getHostAddress());





        //regista servidor
        String serverUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;


        Map<String, Object> attributes = new HashMap<>();
        attributes.put("type", "soap");

        api.Endpoint endpoint = new api.Endpoint(serverUrl, attributes);
        Response response = null;

        String indexerID = endpoint.generateId();

        System.err.println("target ->"+target);

        boolean executed = false;
        for (int i = 0; !executed && i < 3; i++) {
            try {
                response = target.path(indexerID).request()
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
