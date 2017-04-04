package rest.client;

import java.io.IOException;
import java.net.URI;
import java.util.List;

import api.Document;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

public class SearchDocument {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		
		ClientConfig clientConfig = new ClientConfig();
		Client client = ClientBuilder.newClient(clientConfig);

		URI baseURI = UriBuilder.fromUri("http://localhost:8080/").build();
		
		String keywords="blablabla"+"+"+"asdasdasd"+"+"+"bnmbnmbnmb";

		WebTarget target = client.target(baseURI);
		
		List<Document> documents= target.path("/indexer/")
				.queryParam("query", keywords)
				.request()
				.accept(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Document>>(){});
		
		System.err.println("document list" + documents);

	}

}
