package soap.server;

import rest.server.Multicast;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.ws.Endpoint;
import java.net.InetAddress;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by Rui Soares n 41783
 */
public class IndexerServer {

    @SuppressWarnings("Duplicates")
    public static void main(String[] args) throws Exception {


        /**
         *
         * AQUI NAO E PRECISO MULTICAST. NOS TESTES O ADRESS DO RENDEVOUS E PASSADO NOS ARGS
         *
         */
        Multicast m = new Multicast();
        int port = 9090;

        String baseURI = String.format("http://0.0.0.0:%d/indexer", port);

        Endpoint.publish(baseURI, new IndexerServerImpl());

        System.err.println("SOAP Indexer Server ready @ " + baseURI + " : local IP = "
                + InetAddress.getLocalHost().getHostAddress());


        WebTarget target = m.GetMulticast();

        //regista servidor
        String serverUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
        Map<String, Object> attributes= new HashMap<>();
        attributes.put("type","soap");

        api.Endpoint endpoint = new api.Endpoint(serverUrl, attributes);
        Response response = null;

        boolean executed = false;
        for (int i = 0; !executed && i < 3; i++) {
            try {
                response = target.path("/contacts/" + endpoint.generateId()).request()
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
    }
}
