package rest.server;

import java.net.URI;
import java.util.Collections;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import api.Document;
import org.glassfish.jersey.client.ClientConfig;
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

        System.err.println("REST RendezVous Server ready @ " + baseUri);


        //CLIENT
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);

        WebTarget target = client.target(baseUri);

        Document document = new Document("http://some-server-document-url", Collections.emptyList());

        //ADICIONAR DOCUMENTO
        Response response = target.path("/index/" + document.id())
                .request()
                .post(Entity.entity(document, MediaType.APPLICATION_JSON));


        System.out.println("adicionar documento: "+response.getStatus());


        //APAGAR DOCUMENTO
        Response response2 = target.path("/index/" + document.id())
                .request()
                .delete();

        System.out.println("apagar documento: "+response2.getStatus());
    }
}