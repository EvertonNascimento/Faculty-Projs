
package rest.server;

import java.net.*;
import java.util.Collections;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;


public class IndexerServer {


    public static void main(String[] args) throws Exception {

        Multicast m = new Multicast();
        // TODO Auto-generated method stub
        int port = 8081;
        URI baseUri = null;

        baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();

        ResourceConfig config = new ResourceConfig();
        config.register(new IndexingResources());

        JdkHttpServerFactory.createHttpServer(baseUri, config);


        System.err.println("REST Indexing Server ready @ " + baseUri + " : local IP = "
                + InetAddress.getLocalHost().getHostAddress());


        WebTarget target = m.GetMulticast();

        String serverUrl = "http://" + InetAddress.getLocalHost().getHostAddress() + ":" + port;
        api.Endpoint endpoint = new api.Endpoint(serverUrl, Collections.emptyMap());
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
