package soap.client;

import api.Document;
import api.soap.IndexerAPI;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static api.soap.IndexerAPI.*;

public class AddDocument {

    public static void main(String[] args) throws Exception {

//        try {
            QName QNAME = new QName(NAMESPACE, NAME);

            URL wsURL = new URL("http://localhost:9090/indexer?wsdl");


       /* } catch (Exception e) {
            System.err.println("Erro :" + e.getMessage());
        }*/


        List<String> keywords= new ArrayList<>();

        keywords.add("carapau");



        boolean executed = false;
        for (int i = 0; !executed && i < 3; i++) {
            try {
                Service service = Service.create(wsURL, QNAME);

                IndexerAPI indexer = service.getPort(IndexerAPI.class);

                Document document = new Document("http://polyform.di.fct.unl.pt/docs/5d905a2fea6ca89c", keywords);

                indexer.add(document);

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




    }
}
