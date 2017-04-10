package rest.client;

import java.io.IOException;
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

public class AddDocument {

	public static void main(String[] args) throws IOException {

		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);

		String serverHost = "192.168.99.1:8081"; /*null;*/
		if (args.length > 0)
			serverHost = args[0];

		System.err.println(serverHost);

		URI baseURI = UriBuilder.fromUri("http://" + serverHost + "/").build();

		WebTarget target = client.target(baseURI);

		Document document = new Document("http://polyform.di.fct.unl.pt/docs/5d905a2fea6ca89c", Collections.emptyList());

		Response response = target.path("/indexer/" + document.id()).request()
				.post(Entity.entity(document, MediaType.APPLICATION_JSON));

		System.out.println("adicionar documento: " + response.getStatus());
		System.err.println(response.getLinks().toString());
	}

}
