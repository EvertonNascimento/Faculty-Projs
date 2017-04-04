package api;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;

/**
* Interface do servidor que mantem lista de servidores de indexacao.
*/
@Path("/contacts")
public interface RendezVousService {

	/**
	* Devolve array com a lista de servidores registados.
	*/
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    Endpoint[] endpoints();

    /**
    * Regista novo servidor.
    */
    @POST
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    void register( @PathParam("id") String id, Endpoint endpoint);

    /**
    * De-regista servidor, dado o seu id.
    */
    @DELETE
    @Path("/{id}")
    void unregister(String id);
}