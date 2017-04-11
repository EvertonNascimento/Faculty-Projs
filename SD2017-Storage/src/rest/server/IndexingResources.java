package rest.server;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.IOException;
import java.net.*;
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
import javax.xml.namespace.QName;
import javax.xml.ws.Service;

import api.soap.IndexerAPI;
import org.glassfish.jersey.client.ClientConfig;

import com.google.common.net.InetAddresses;

import api.Document;
import api.Endpoint;
import api.IndexerService;
import sys.storage.LocalVolatileStorage;

@Path("/indexer")
@SuppressWarnings("Duplicates")
public class IndexingResources implements IndexerService {

    LocalVolatileStorage storage = new LocalVolatileStorage();

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public List<String> search(@QueryParam("query") String keywords) {
        String[] kwords = keywords.split("\\+");

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

        if (storage.store(id, doc)) {
            System.err.println("document added successfully -> " + doc + "\n");
        } else
            throw new WebApplicationException(CONFLICT);
    }

    @DELETE
    @Path("/{id}")
    public void remove(@PathParam("id") String id) throws Exception {

        boolean removalResult = false;

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
                /*endpoints = target.request().accept(MediaType.APPLICATION_JSON)
                        .get(Endpoint[].class);*/
                executed = true;
            } catch (RuntimeException e) {
                if (i < 2) {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e1) {
                        System.err.println("EXPLOSION 1");
                    }
                }
            }
        }
        //////////////////////////////////////////////

        //parar contactar seridores REST
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target2 = null;
        boolean response2 = false;

        URI baseURI = null;


        //parar contactar seridores SOAP
        String NAME = "IndexerService";
        String NAMESPACE = "http://sd2017";
        String INTERFACE = "api.soap.IndexerAPI";
        QName qname = new QName(NAMESPACE, NAME);
        URL wsURL = null;
        Service service = null;
        IndexerAPI indexer = null;

        int endpointsQueExistem = endpoints.length;
        int endpointsOndeOdocumentoNaoExiste = 0;

        for (Endpoint e : endpoints) {

            try {
                e.getUrl();

                //parar contactar seridores REST
                baseURI = UriBuilder.fromUri(e.getUrl()).build();
                target2 = client.target(baseURI);
            } catch (Exception e1) {
            }

            //parar contactar seridores SOAP
            try {
                wsURL = new URL(String.format(baseURI + "/indexer?wsdl"));
                service = Service.create(wsURL, qname);
                indexer = service.getPort(IndexerAPI.class);
            } catch (Exception e3) {
            }


            boolean executed1 = false;
            for (int i = 0; !executed1 && i < 3; i++) {


                try {
                    if (e.getAttributes().get("type").equals("soap")) {
                        removalResult = indexer.removeFromStorage(id);
                        executed1 = true;
                    } else if (e.getAttributes().get("type").equals("rest")) {
                        try {
                            removalResult = target2.path("/indexer/remove/" + id).request().delete(boolean.class);
                        } catch (Exception e2) {
                        }
                        executed1 = true;
                    }
                } catch (Exception e2) {
                }

            }


            if (removalResult)
                System.err.println("document removed successfully -> " + id + "\n");
            else {
                endpointsOndeOdocumentoNaoExiste++;
                if (endpointsOndeOdocumentoNaoExiste == endpointsQueExistem) {
                    System.err.println("document does not exist");
                    System.err.println("endpoints onde o documento não existe -> " + endpointsOndeOdocumentoNaoExiste);
                    removalResult = false;
                    throw new WebApplicationException(NOT_FOUND);
                } else {
                    System.err.println("document could not be removed -> " + id + "\n");
                    removalResult = true;
                }
            }

        }
    }


    @DELETE
    @Path("/remove/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean removeFromStorage(@PathParam("id") String id) {
        if (storage.remove(id)) {
            return true;
        } else {
            return false;
        }
    }


}
