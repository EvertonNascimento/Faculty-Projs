package rest.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import api.Endpoint;
import api.RendezVousService;

import static javax.ws.rs.core.Response.Status.*;

/**
 * Implementacao do servidor de rendezvous em REST
 */
@Path("/contacts")
public class RendezVousResources implements RendezVousService {

	private Map<String, Endpoint> db = new ConcurrentHashMap<>();

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Endpoint[] endpoints() {
		return db.values().toArray(new Endpoint[db.size()]);
	}

	@POST
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void register(@PathParam("id") String id, Endpoint endpoint) {


		if (db.containsKey(id))
			throw new WebApplicationException(CONFLICT);

		else
			System.err.printf("register: %s <%s>\n", id, endpoint);
		    db.put(id, endpoint);

	}

	@PUT
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	public void update(@PathParam("id") String id, Endpoint endpoint) {


		if (!db.containsKey(id))
			throw new WebApplicationException(NOT_FOUND);
		else
			System.err.printf("update: %s <%s>\n", id, endpoint);
			db.put(id, endpoint);
	}

	@DELETE
	@Path("/{id}")
	public void unregister(@PathParam("id") String id) {

		if (!db.containsKey(id))
			throw new WebApplicationException(NOT_FOUND);

		else
			System.err.printf("delete: %s \n", id);

		      db.remove(id,db.get(id).getUrl());


	}

}
