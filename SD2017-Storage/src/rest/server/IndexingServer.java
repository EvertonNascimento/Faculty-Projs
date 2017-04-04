
package rest.server;

import java.net.InetAddress;
import java.net.URI;

import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class IndexingServer {

	public static void main(String[] args) throws Exception {
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

	}

}
