package rest.server;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

import api.Document;
import api.Endpoint;

import static javax.ws.rs.core.Response.Status.*;

/**
 * Implementacao do servidor de rendezvous em REST
 */
@Path("/index")
public class RendezVousResources {

    private Map<String, Endpoint> db = new ConcurrentHashMap<>();
    private Map<String, Document> dbDocument = new ConcurrentHashMap<>();

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public Endpoint[] endpoints() {
        return db.values().toArray(new Endpoint[db.size()]);
    }

//	@POST
//	@Path("/{id}")
//	@Consumes(MediaType.APPLICATION_JSON)
//	public void register( @PathParam("id") String id, Endpoint endpoint) {
//		System.err.printf("register: %s <%s>\n", id, endpoint);
//
//		if (db.containsKey(id))
//			throw new WebApplicationException( CONFLICT );
//		else
//			db.put(id, endpoint);
//	}

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void update(@PathParam("id") String id, Endpoint endpoint) {
        System.err.printf("update: %s <%s>\n", id, endpoint);

        if (!db.containsKey(id))
            throw new WebApplicationException(NOT_FOUND);
        else
            db.put(id, endpoint);
    }

//	@DELETE
//	//@Path("/{id}")
//	public void unregister(@PathParam("id") String id) {
//
//		if (!db.containsKey(id))
//			throw new WebApplicationException( NOT_FOUND );
//
//		else
//			db.remove(id);
//
//	}



/*-----------------------------------------------------------------------------------------------------*/


    @POST
    @Path("/{DocumentId}")
    @Consumes(MediaType.APPLICATION_JSON)
    public void add(@PathParam("DocumentId") String documentId, Document doc) {
        System.err.printf("add document: %s <%s>\n", documentId, doc);

        if (dbDocument.containsKey(documentId))
            throw new WebApplicationException(CONFLICT);
        else
            dbDocument.put(documentId, doc);
    }


    @DELETE
    @Path("/{DocumentId}")
    public void remove(@PathParam("DocumentId") String documentId) {

        if (!dbDocument.containsKey(documentId))
            throw new WebApplicationException(NOT_FOUND);

        else
            dbDocument.remove(documentId);

    }


}
