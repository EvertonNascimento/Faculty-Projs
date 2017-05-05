package soap.server;

import api.Document;
import api.Endpoint;
import api.soap.IndexerAPI;
import org.glassfish.jersey.client.ClientConfig;
import rest.server.Multicast;
import sys.storage.LocalVolatileStorage;

import javax.jws.WebService;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.ws.Service;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static javax.ws.rs.core.Response.Status.NOT_FOUND;


/**
 * Created by Rui Soares n 41783
 */


@WebService(
        serviceName = IndexerAPI.NAME,
        targetNamespace = IndexerAPI.NAMESPACE,
        endpointInterface = IndexerAPI.INTERFACE)

public class IndexerServerImpl implements IndexerAPI {

    private LocalVolatileStorage storage = new LocalVolatileStorage();


    @Override
    @SuppressWarnings("Duplicates")
    public List<String> search(String keywords) throws InvalidArgumentException {

        try {
            keywords.equals(null);
        } catch (NullPointerException e) {
            throw new InvalidArgumentException();
        }

        String[] kwords = keywords.split("\\+");

        List<Document> docs = storage.search(Arrays.asList(kwords));

        List<String> docsUrl = new ArrayList<>();

        docs.forEach(document -> docsUrl.add(document.getUrl()));
        return docsUrl;

    }

    @Override
    public boolean add(Document doc) throws InvalidArgumentException {

        try {
            doc.equals(null);
        } catch (NullPointerException e) {
            throw new InvalidArgumentException();
        }

        if (storage.store(doc.id(), doc)) {
            System.err.println("document with id:" + doc.id() + " added successfully -> " + doc + "\n");
            return true;
        } else {
            return false;
        }

    }


    @Override
    @SuppressWarnings("Duplicates")
    public boolean remove(String id) throws Exception {

        boolean removalResult = false;

        try {
            id.equals(null);
        } catch (NullPointerException e) {
            throw new InvalidArgumentException();
        }

        //obter endpoints de onde vamos remover o documento
        Multicast m = new Multicast();

        WebTarget target = m.GetMulticast();

        Endpoint[] endpoints = null;

        boolean executed = false;
        for (int i = 0; !executed && i < 3; i++) {
            try {
                endpoints = target.request().accept(MediaType.APPLICATION_JSON)
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

        //parar contactar servidores REST
        ClientConfig clientConfig = new ClientConfig();
        Client client = ClientBuilder.newClient(clientConfig);
        WebTarget target2 = null;
        boolean response2 = false;
        URI baseURI = null;


        //parar contactar servidores SOAP
        QName qname = new QName(NAMESPACE, NAME);
        URL wsURL = null;
        Service service = null;
        IndexerAPI indexer = null;


        int endpointsQueExistem = endpoints.length;
        int endpointsOndeOdocumentoNaoExiste = 0;
        boolean executed1 = false;

        for (Endpoint e : endpoints) {


            e.getUrl();

            //parar contactar seridores REST
            baseURI = UriBuilder.fromUri(e.getUrl()).build();
            target2 = client.target(baseURI);


            //parar contactar seridores SOAP
            try {
                wsURL = new URL(String.format(baseURI + "/indexer?wsdl"));
                service = Service.create(wsURL, qname);
                indexer = service.getPort(IndexerAPI.class);
            } catch (Exception e3) {
            }


            try {
                if (e.getAttributes().get("type").equals("soap")) {
                    removalResult = indexer.removeFromStorage(id);
                    executed1 = true;
                } else if (e.getAttributes().get("type").equals("rest")) {
                    removalResult = target2.path("/indexer/remove/" + id).request().delete(boolean.class);
                    executed1 = true;
                }
            } catch (Exception e3) {
            }


            try {
                if (removalResult)
                    System.err.println("document removed successfully -> " + id + "\n");
                else {
                    endpointsOndeOdocumentoNaoExiste++;
                    if (endpointsOndeOdocumentoNaoExiste == endpointsQueExistem) {
                        System.err.println("document does not exist");
                        System.err.println("endpoints onde o documento não existe -> " + endpointsOndeOdocumentoNaoExiste);
                        removalResult = false;
                    } else {
                        System.err.println("document could not be removed -> " + id + "\n");
                        removalResult = true;
                    }
                }
            } catch (Exception e3) {
            }

        }

        return removalResult;


    }

    @Override
    public boolean removeFromStorage(String id) {
        if (storage.remove(id)) {
            return true;
        } else {

            return false;
        }
    }


}
