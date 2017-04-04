package rest.client;

import java.net.URI;
import java.util.Collections;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response;

import org.glassfish.jersey.client.ClientConfig;

import api.Document;

public class RemoveDocument {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);

		String serverHost = null;
		if (args.length > 0)
			serverHost = args[0];

		System.err.println(serverHost);

		URI baseURI = UriBuilder.fromUri("http://" + serverHost + "/").build();
		
		//URI baseURI = UriBuilder.fromUri("http://localhost:8080/").build();

		Document document = new Document("http://some-server-document-url", Collections.emptyList());

		WebTarget target = client.target(baseURI);
		Response response2 = target.path("/indexer/" + document.id()).request().delete();

		System.out.println("apagar documento: " + response2.getStatus());

	}

}
