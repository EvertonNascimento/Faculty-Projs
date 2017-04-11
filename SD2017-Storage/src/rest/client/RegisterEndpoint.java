package rest.client;

import java.io.IOException;
import java.net.InetAddress;
import java.net.URI;
import java.util.Collections;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import api.Endpoint;

public class RegisterEndpoint {

	public static void main(String[] args) throws IOException {
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);

		String serverHost = "192.168.99.1:8080/contacts";/*null;*/
		String endpointUrl = "http://indexer-1";/*null;*/
		if (args.length > 0) {
			serverHost = args[0];
			endpointUrl = args[1];
		}

//		System.err.println(serverHost);

		URI baseURI = UriBuilder.fromUri("http://" + serverHost + "/").build();

//		URI baseURI = UriBuilder.fromUri("http://localhost:8080/").build();

		WebTarget target = client.target(baseURI);

		// Endpoint endpoint = new Endpoint("http://some-server-endpoint-url",
		// Collections.emptyMap());

		Endpoint endpoint = new Endpoint(endpointUrl, Collections.emptyMap());

		String endpointId= endpoint.generateId();

		Response response = target.path(/*"/contacts/" + */endpointId).request()
				.post(Entity.entity(endpoint, MediaType.APPLICATION_JSON));

		System.err.println("Endepoint added with response: "+response.getStatus()+" info: "+response.getStatusInfo());
		System.err.println("Endpoint ready @ "+endpointUrl+" local ip: "+ InetAddress.getLocalHost().getHostAddress());
		System.err.println("Endepoints id:"+endpointId);
	}
}
