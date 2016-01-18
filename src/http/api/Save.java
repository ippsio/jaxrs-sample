package http.api;

import java.util.Date;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import cache.PriceCache;
import data.Prices;
import http.ws.WSEndpoint;

@Path("save")
public class Save {
	private static ObjectMapper om = new ObjectMapper();

	@Context private javax.servlet.http.HttpServletRequest requeset;
	@GET
	@Produces(MediaType.TEXT_PLAIN)
	public String save(@QueryParam("broaker") String broaker, @QueryParam("symbol") String symbol,
			@QueryParam("bid") double bid, @QueryParam("ask") double ask, @QueryParam("digits") double digits) {

		// TODO: Requester validation checking.
		//System.out.println(requeset.getRemoteAddr());

		try {
			// キャッシュに保存
			PriceCache cache = PriceCache.getInstance();
			cache.save(broaker, symbol, bid, ask, digits, new Date());

			// キャッシュの内容をJSONに
			Prices prices = new Prices(symbol, cache.findBySymbol(symbol));
			String json = om.writerWithDefaultPrettyPrinter().writeValueAsString(prices);

			// WebSocketのブロードキャストメッセージとして設定し、その後ブロードキャスト
			WSEndpoint.updateBroadcastMessage(json);
			WSEndpoint.broadcast(symbol);

			return json;
		} catch (JsonProcessingException e) {
			throw new RuntimeException(e);
		}
	}
}
