package rest.client;

import api.Endpoint;
import org.glassfish.jersey.client.ClientConfig;

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

/**
 * Created by Rui Soares n 41783
 */
public class RemoveEndpoint {

    public static void main(String[] args) throws IOException {
        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        String serverHost = /*"192.168.99.1:8080";*/null;
        String endpointId = /*"D16A0D458C240DDC52367FC370FCE615";*/null;
        if (args.length > 0)

        {
            serverHost = args[0];
            endpointId = args[1];
        }

        System.err.println(serverHost);

        URI baseURI = UriBuilder.fromUri("http://" + serverHost + "/").build();

        WebTarget target = client.target(baseURI);

        Response response = target.path("/contacts/" + endpointId).request()
                .delete();


        Endpoint[] endpoints = target.path("/contacts").request().accept(MediaType.APPLICATION_JSON)
                .get(Endpoint[].class);

        System.err.println("as array: " + Arrays.asList(endpoints));

        System.err.println(response.getStatus());
    }
}
