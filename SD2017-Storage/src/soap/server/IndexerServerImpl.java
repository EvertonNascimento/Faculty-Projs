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
import java.net.URI;
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
        String[] kwords = keywords.split("\\+");

        List<Document> docs = storage.search(Arrays.asList(kwords));

        List<String> docsUrl = new ArrayList<>();

        docs.forEach(document -> docsUrl.add(document.getUrl()));

        if (docs.equals(null))
            throw new InvalidArgumentException();
        else {
            return docsUrl;
        }
    }

    @Override
    public boolean add(Document doc) throws InvalidArgumentException {

        if (doc.equals(null)) {
            throw new InvalidArgumentException();
        } else if (storage.store(doc.id(), doc)) {
            System.err.println("document added successfully -> " + doc + "\n");
            return true;
        } else {
            return false;
        }

    }

    /**
     * AINDA NAO TRATA DO CASO EM QUE DA FALSE (DOCUMENTO NAO EXISTE NO SISTEMA)
     * @param id
     * @return
     * @throws Exception
     */
    @Override
    @SuppressWarnings("Duplicates")
    public boolean remove(String id) throws Exception {


        if(id.equals(null))
            throw new InvalidArgumentException();

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


        boolean executed1 = false;
        for (Endpoint e : endpoints) {

            e.getUrl();

            baseURI = UriBuilder.fromUri(e.getUrl()).build();

            target2 = client.target(baseURI);


            for (int i = 0; !executed1 && i < 3; i++) {
                try {
                    response2 = target2.path("/indexer/remove/" + id).request().delete();
                    executed1 = true;
                    return true; ///////////////////////////////////////////////////////////ISTO PODE TAR MAL FEITO. RESPOSTA PODE NAO SER SEMPRE TRUE
                } catch (RuntimeException e2) {
                    if (i < 2) {
                        try {
                            Thread.sleep(3000);
                        } catch (InterruptedException e3) {
                            return false;///////////////////////////////////////////////////////////ISTO PODE TAR MAL FEITO. RESPOSTA PODE NAO SER SEMPRE FALSE
                        }
                    }
                }
            }

            System.err.println("apagar documento: " + response2.getStatus());

        }

        return executed1;
    }

    @Override
    public boolean removeFromStorage(String id) { //mudar return para bool
        if (storage.remove(id)) {
            System.err.println("document " + id + " remove");
            return true;
        } else {
            System.err.println("delete failed");
            return false;
        }
    }


}
