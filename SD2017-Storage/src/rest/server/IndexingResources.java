package rest.server;

import static javax.ws.rs.core.Response.Status.CONFLICT;
import static javax.ws.rs.core.Response.Status.NOT_FOUND;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.URI;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;

import org.glassfish.jersey.client.ClientConfig;

import com.google.common.net.InetAddresses;

import api.Document;
import api.Endpoint;
import api.IndexerService;
import sys.storage.LocalVolatileStorage;;

@Path("/indexer")
public class IndexingResources implements IndexerService {

	private Map<String, Document> db = new ConcurrentHashMap<>();
	LocalVolatileStorage storage = new LocalVolatileStorage();

	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	public List<Document> search(@QueryParam("query") String keywords) {// List<String>
		// TODO Auto-generated method stub
		String[] kwords = keywords.split("+");
		List<Document> docs = storage.search(Arrays.asList(kwords));
		return docs;
	}

	@Override
	public void add(String id, Document doc) {
		// TODO Auto-generated method stub
		System.err.printf("add document: %s <%s>\n", id, doc);

		if (storage.store(id, doc))
			System.out.println("document added successfully\n");
		else
			System.out.println("document could not be stored\n");

		// if (db.containsKey(id))
		// throw new WebApplicationException(CONFLICT);
		// else
		// db.put(id, doc);
	}

	@DELETE
	@Path("/{id}")
	public void remove(@PathParam("id") String id) throws Exception {
		// TODO Auto-generated method stub
		// o delete tem que apagar o documento de todos os servidores de
		// indexação
		// temos que contactar o rendevousserver para obter a lista de todos os
		// endpoints que este conhece
		// neste metodo teos que ser clientes de outro servidor
		// para fazer isto precisamos do url do rendevousserver
		// depois de termos a lista de endpoints em array vamos iterar sobre
		// cada um dos endpoints e fazer uma operação de remove para apagar o
		// documento
		// temos que acrescentar à interface dos indexing services outra
		// opreação (nome e path à nossa descrição). vai ser usada num servidor
		// para pedir a outro servidor para apagar o ficheiro- opreção A
		// é esta a opreção que o cliente chama para apagar o documento e onde
		// vamos buscar a lista de endpoints, que estiver a iterar os endpoints
		// invocamos a operação A para remover o documento destes, vai à local
		// storage e remove o documento se ele existir
		//

		
		ClientConfig config = new ClientConfig();
		Client client = ClientBuilder.newClient(config);

		// multicast
		String hostname=null;
		String multAddress = "229.229.229.229";// igual no servidor de indexação
		int multPort = 9999;// igual no servidor de indexação
		InetAddress address = null;
		MulticastSocket socket = null;
		WebTarget target=null;

		address = InetAddress.getByName(multAddress);
		socket = new MulticastSocket();
		socket.setSoTimeout(3000);

		do {
			byte[] buffer = new byte[65536];
			DatagramPacket request = new DatagramPacket(buffer, buffer.length);
			request.setAddress(address);
			request.setPort(multPort);
			socket.send(request);
			
			DatagramPacket reply = new DatagramPacket(buffer, buffer.length);
			socket.receive(reply);
			
			String serverUrl= new String(reply.getData(), 0, reply.getLength());
			hostname=serverUrl;
			
			URI baseURI = UriBuilder.fromUri(hostname).build();
			target = client.target(baseURI);
			
			Endpoint[] endpoints = target.path("/contacts").request().accept(MediaType.APPLICATION_JSON)
					.get(Endpoint[].class);
			
			for(Endpoint e: endpoints){
				//e. removeFromStorage(id);
				
			}
			
			
		} while(true);

//		if (!db.containsKey(id))
//			throw new WebApplicationException(NOT_FOUND);
//
//		else
//			db.remove(id);

	}

	@Override
	public void removeFromStorage(String id) {
		// TODO Auto-generated method stub
		storage.remove(id);
	}


}
