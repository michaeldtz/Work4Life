package de.dietzm.alphazone.api.publik;

import java.util.List;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;

import org.codehaus.jettison.json.JSONException;

import de.dietzm.alphazone.dex.JSExecutor;
import de.dietzm.alphazone.repository.model.RepositoryItem;
import de.dietzm.foundation.db.base.DAO;
import de.dietzm.foundation.db.base.DAOFactory;

@Path("/cron")
public class CronExternalAPI {

	
	
	@GET
	@Path("/morningservice")
	@Produces("application/json")
	public Response morningService(@Context ServletContext ctx, @Context HttpServletRequest req, @Context HttpServletResponse resp) throws JSONException {

		DAO<RepositoryItem> itemDAO = DAOFactory.getEntityManager("RepositoryItem");
		List<RepositoryItem> items = itemDAO.query("name", "CRONmorningservice.sjs");

		JSExecutor exec = new JSExecutor();
		
		for (RepositoryItem item : items) {
			exec.execute(ctx, item.getName(), req, resp, item);			
		}
		
		return Response.ok("OK").build();
	}
	
	
	
	@GET
	@Path("/everyFifteenMinutes")
	@Produces("application/json")
	public Response everyFifteenMinutes(@Context ServletContext ctx, @Context HttpServletRequest req, @Context HttpServletResponse resp) throws JSONException {

		DAO<RepositoryItem> itemDAO = DAOFactory.getEntityManager("RepositoryItem");;
		List<RepositoryItem> items = itemDAO.query("name", "CRONeveryFifteenMinutes.sjs");

		JSExecutor exec = new JSExecutor();
		
		for (RepositoryItem item : items) {
			exec.execute(ctx, item.getName(), req, resp, item);			
		}
		
		return Response.ok("OK").build();
	}
	
	@GET
	@Path("/everyHour")
	@Produces("application/json")
	public Response everyHour(@Context ServletContext ctx, @Context HttpServletRequest req, @Context HttpServletResponse resp) throws JSONException {

		DAO<RepositoryItem> itemDAO = DAOFactory.getEntityManager("RepositoryItem");;
		List<RepositoryItem> items = itemDAO.query("name", "CRONeveryHour.sjs");

		JSExecutor exec = new JSExecutor();
		
		for (RepositoryItem item : items) {
			exec.execute(ctx, item.getName(), req, resp, item);			
		}
		
		return Response.ok("OK").build();
	}


}
