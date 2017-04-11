package api;

import java.util.List;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;


/**
* Interface do servidor de indexacao.
*/
@Path("/indexer")
public interface IndexerService {

	/**
	* Devolve lista de URLs dadas as palavras chave keywords codificadas como:
	* palavra_1+palavra_2+...+palavra_n.
	*/
	@GET
	@Path("/search")
	@Produces(MediaType.APPLICATION_JSON)
	List<String> search(@QueryParam("query") String keywords);

	/**
	* Adiciona informacao sobre documento doc com identificador id.
	*/
	@POST
	@Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	void add(@PathParam("id") String id, Document doc);

	/**
	* Remove informacao sobre documento com identificador id em todos os servidores.
	 * @throws Exception 
	*/
	@DELETE
	@Path("/{id}")
	void remove(@PathParam("id") String id) throws Exception;
	
	
	/**
	 * Apaga o documento do storage local
	 */
	@DELETE
	@Path("/remove/{id}")
	@Produces(MediaType.APPLICATION_JSON)
	boolean removeFromStorage(@PathParam("id") String id);

}
