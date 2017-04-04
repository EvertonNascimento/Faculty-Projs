package rest.server;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.MulticastSocket;
import java.net.URI;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.jdkhttp.JdkHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;

public class RendezVousServer {

	public static void main(String[] args) throws Exception {
		int port = 8080;
		if (args.length > 0)
			port = Integer.parseInt(args[0]);

		URI baseUri = UriBuilder.fromUri("http://0.0.0.0/").port(port).build();

		ResourceConfig config = new ResourceConfig();
		config.register(new RendezVousResources());

		JdkHttpServerFactory.createHttpServer(baseUri, config);

		System.err.println("REST RendezVous Server ready @ " + baseUri + " : local IP = "
				+ InetAddress.getLocalHost().getHostAddress());

		// multicast
		String multAddress = "229.229.229.229";//igual no servidor de indexa��o 
		int multPort = 9999;//igual no servidor de indexa��o 
		
		final InetAddress address = InetAddress.getByName(multAddress);
		MulticastSocket socket = new MulticastSocket(multPort);
		socket.joinGroup(address);
		
		while(true){
			byte[] buffer=new byte[65536];
			DatagramPacket request= new DatagramPacket(buffer,buffer.length);
			socket.receive(request);
			
			DatagramPacket reply= new DatagramPacket(buffer,buffer.length);
			String cena= "http://"+ InetAddress.getLocalHost().getHostAddress();
			reply.setSocketAddress(new InetSocketAddress(request.getAddress(), request.getPort()));
			reply.setData(cena.getBytes());
			socket.send(reply);
		}
		
		

	}
}

//
// //CLIENTE
//
// ClientConfig clientConfig = new ClientConfig();
// Client client = ClientBuilder.newClient(clientConfig);
//
// WebTarget target = client.target(baseUri);
//
// Document document = new Document("http://some-server-document-url",
// Collections.emptyList());
//
//
// //ADICIONAR DOCUMENTO
// Response response = target.path("/index/" + document.id())
// .request()
// .post(Entity.entity(document, MediaType.APPLICATION_JSON));
//
// System.out.println("adicionar documento: "+response.getStatus());
//
//
// //APAGAR DOCUMENTO
// Response response2 = target.path("/index/" + document.id())
// .request()
// .delete();
//
// System.out.println("apagar documento: "+response2.getStatus());