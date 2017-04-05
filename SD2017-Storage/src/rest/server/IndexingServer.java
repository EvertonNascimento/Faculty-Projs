
package rest.server;

import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.util.Collections;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.server.internal.process.Endpoint;

public class IndexingServer {



	public static void main(String[] args) throws Exception {

		Multicast m = new Multicast();
		// TODO Auto-generated method stub
		int port = 8081;
		if (args.length > 0)
			port = Integer.parseInt(args[0]);

		URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();

		ResourceConfig config = new ResourceConfig();
		config.register(new IndexingResources());

		JdkHttpServerFactory.createHttpServer(baseUri, config);

		
		System.err.println("REST Indexing Server ready @ " + baseUri + " : local IP = "
				+ InetAddress.getLocalHost().getHostAddress());
		


		WebTarget target = m.GetMulticast();
		//é mesmo isto???
		String b = "http://" + InetAddress.getLocalHost().getHostAddress();
		api.Endpoint endpoint = new api.Endpoint(b ,Collections.emptyMap());
		Response response = target.path("/contacts/" + endpoint.generateId()).request()
				.post(Entity.entity(endpoint, MediaType.APPLICATION_JSON));
		System.err.println(response.getStatus());


	}

}