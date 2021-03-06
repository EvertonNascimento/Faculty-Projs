package rest.client;

import java.io.IOException;
import java.net.URI;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import api.Endpoint;

public class ListEndpoints {

	public static void main(String[] args) throws IOException {

		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);

		String serverHost = "192.168.99.1:8080/contacts/";/*null;*/
		if (args.length > 0)
			serverHost = args[0];


		URI baseURI = UriBuilder.fromUri("http://" + serverHost).build();

		System.err.println(baseURI);

		//URI baseURI = UriBuilder.fromUri("http://localhost:8080/").build();

		WebTarget target = client.target(baseURI);

		/* Obtém os servidores registados sob a forma de um array */
		Endpoint[] endpoints = target.request().accept(MediaType.APPLICATION_JSON)
				.get(Endpoint[].class);

		System.err.println("as array: " + Arrays.asList(endpoints));

		/*
		 * Em alternativa, obtém os servidores registados sob a forma de uma
		 * lista...
		 */
		List<Endpoint> endpoints2 = target.request().accept(MediaType.APPLICATION_JSON)
				.get(new GenericType<List<Endpoint>>() {
				});

		System.err.println("as list: " + endpoints2);
	}
}
