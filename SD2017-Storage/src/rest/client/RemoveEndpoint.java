package rest.client;

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

/**
 * Created by Rui Soares n 41783
 */
public class RemoveEndpoint {

    public RemoveEndpoint(String id, String serverHost, int port) throws IOException {

        System.err.println("-----------------entrou--------------------- \n");

        ClientConfig config = new ClientConfig();
        Client client = ClientBuilder.newClient(config);

        // String serverHost = "192.168.99.1:8080";/*null;*/
        //String endpointId = "D16A0D458C240DDC52367FC370FCE615";/*null;*/


        URI baseURI = null;
        //int port = 8081;

        String  endpointId = null;
        serverHost = serverHost;
      /*
        if (args.length > 0) {
//            port = Integer.parseInt(args[0]);


            serverHost = args[0];

            serverHost = serverHost.toString();

            endpointId = args[1];

           baseURI = UriBuilder.fromUri(serverHost).port(port).build();


        }else {
            baseURI = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();
        }*/


        endpointId = id;

        System.err.println(" id = " + id);

        baseURI = UriBuilder.fromUri("http://" + serverHost +"/").port(port).build();


        System.err.println(serverHost);

        // URI baseURI = UriBuilder.fromUri("http://" + serverHost + "/").build();

        WebTarget target = client.target(baseURI);

        try {

            Endpoint[] endpoints1 = target.path("/contacts").request().accept(MediaType.APPLICATION_JSON)
                    .get(Endpoint[].class);

            //   System.err.println("as array: \n" + Arrays.asList(endpoints1));

            //System.err.println("-----------------After--------------------- \n");




            Response response = target.path("/contacts/" + endpointId).request()
                    .delete();

            System.err.println("as array: " + Arrays.asList(endpoints1));
            System.err.println(response.getStatus());

        }catch (Exception e){

            System.err.println("-----------------foda-se--------------------- \n" + e.getCause());

        }


        //  Endpoint[] endpoints = target.path("/contacts").request().accept(MediaType.APPLICATION_JSON)
        //           .get(Endpoint[].class);

        // System.err.println("as array: " + Arrays.asList(endpoints));


    }
}
