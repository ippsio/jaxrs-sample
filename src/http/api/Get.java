package http.api;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang.StringUtils;

import com.fasterxml.jackson.databind.ObjectMapper;

import http.ws.WSEndpoint;

@Path("get")
public class Get {
	@Context
	private javax.servlet.http.HttpServletRequest requeset;

	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String get(@QueryParam("symbol") String symbol) {
		final String broadcastMessage = StringUtils.defaultString(WSEndpoint.getBroadcastMessageBySymbol(symbol));
		return broadcastMessage;
	}

}
