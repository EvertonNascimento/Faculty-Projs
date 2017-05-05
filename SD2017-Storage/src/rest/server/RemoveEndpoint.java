package rest.server;

import api.Endpoint;
import org.glassfish.jersey.client.ClientConfig;
import rest.server.RendezVousResources;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.Collections;

public class RemoveEndpoint {

    public RemoveEndpoint(String id, String serverHost, int port) throws IOException {


        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);


        URI baseURI = null;

        String endpointId = null;

        endpointId = id;

        System.err.println(" id = " + id);

        baseURI = UriBuilder.fromUri("http://" + serverHost + "/contacts/").port(port).build();


        System.err.println(serverHost);


        WebTarget target = client.target(baseURI);

        try {

            Endpoint[] endpoints1 = target.request().accept(MediaType.APPLICATION_JSON)
                    .get(Endpoint[].class);


            Response response = target.path(endpointId).request()
                    .delete();

            System.err.println("as array: " + Arrays.asList(endpoints1));
            System.err.println(response.getStatus());

        } catch (Exception e) {
        }

    }
}
