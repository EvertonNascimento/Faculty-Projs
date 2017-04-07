package rest.server;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.google.common.net.InetAddresses;

import api.Document;
import api.Endpoint;
import api.IndexerService;
import sys.storage.LocalVolatileStorage;;

@Path("/indexer")
public class IndexingResources implements IndexerService {

    private Map<String, Document> db = new ConcurrentHashMap<>();
    LocalVolatileStorage storage = new LocalVolatileStorage();

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<Document> search(@QueryParam("query") String keywords) {// List<String>
        // TODO Auto-generated method stub
        String[] kwords = keywords.split("\\+");
        List<Document> docs = storage.search(Arrays.asList(kwords));
        return docs;
    }

    @Override
    public void add(String id, Document doc) {
        // TODO Auto-generated method stub
        System.err.printf("add document: %s <%s>\n", id, doc);

        if (storage.store(id, doc))
            System.out.println("document added successfully\n");
        else
            System.out.println("document could not be stored\n");

        // if (db.containsKey(id))
        // throw new WebApplicationException(CONFLICT);
        // else
        // db.put(id, doc);
    }

    @DELETE
    @Path("/{id}")
    public void remove(@PathParam("id") String id) throws Exception {
        // TODO Auto-generated method stub
        //


        //obter endepoints de onde vamos remover o documento
        //////////////////////////////////////////////
        Multicast m = new Multicast();

        WebTarget target = m.GetMulticast();

        Endpoint[] endpoints = target.path("/contacts").request().accept(MediaType.APPLICATION_JSON)
                .get(Endpoint[].class);
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

            response2 = target2.path("/indexer/" + id).request().delete();

            System.out.println("apagar documento: " + response2.getStatus());

        }

    }

    @Override
    public void removeFromStorage(String id) {
        // TODO Auto-generated method stub
        storage.remove(id);
    }


}
