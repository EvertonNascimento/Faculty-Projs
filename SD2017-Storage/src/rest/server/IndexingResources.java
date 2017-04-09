package rest.server;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.*;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.crypto.dom.DOMCryptoContext;

import org.glassfish.jersey.client.ClientConfig;

import com.google.common.net.InetAddresses;

import api.Document;
import api.Endpoint;
import api.IndexerService;
import sys.storage.LocalVolatileStorage;

@Path("/indexer")
public class IndexingResources implements IndexerService {

    LocalVolatileStorage storage = new LocalVolatileStorage();

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> search(@QueryParam("query") String keywords) {
        String[] kwords = keywords.split("\\+");
        System.err.println(Arrays.toString(kwords));
        List<Document> docs = storage.search(Arrays.asList(kwords));

        List<String> docsUrl = new ArrayList<>();

        docs.forEach(document -> docsUrl.add(document.getUrl()));

        if (docs.equals(null))
            throw new WebApplicationException(NOT_FOUND);
        else {
            return docsUrl;
        }


    }

    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void add(@PathParam("id") String id, Document doc) {

        if (storage.store(id, doc))
            System.err.println("document added successfully -> " + doc + "\n");
        else
            throw new WebApplicationException(CONFLICT);
    }

    @DELETE
    @Path("/{id}")
    public void remove(@PathParam("id") String id) throws Exception {


        //obter endepoints de onde vamos remover o documento
        //////////////////////////////////////////////
        Multicast m = new Multicast();

        WebTarget target = m.GetMulticast();

        Endpoint[] endpoints = null;

        boolean executed = false;
        for (int i = 0; !executed && i < 3; i++) {
            try {
                endpoints = target.path("/contacts").request().accept(MediaType.APPLICATION_JSON)
                        .get(Endpoint[].class);
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
        //////////////////////////////////////////////


        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target2 = null;
        Response response2 = null;

        URI baseURI = null;


        for (Endpoint e : endpoints) {

            e.getUrl();

            baseURI = UriBuilder.fromUri(e.getUrl()).build();

            target2 = client.target(baseURI);


            boolean executed1 = false;
            for (int i = 0; !executed1 && i < 3; i++) {
                try {
                    response2 = target2.path("/indexer/remove/" + id).request().delete();
                    executed1 = true;
                } catch (RuntimeException e2) {
                    if (i < 2) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e3) {
                        }
                    }
                }
            }

            System.err.println("apagar documento: " + response2.getStatus());

        }

    }

    @DELETE
    @Path("/remove/{id}")
    public void removeFromStorage(@PathParam("id") String id) {
        if (storage.remove(id))
            System.err.println("document " + id + " remove");
        else
            System.err.println("delete failed");
    }


}
