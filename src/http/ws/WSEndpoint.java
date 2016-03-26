package http.ws;


import java.util.HashMap;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import javax.websocket.OnClose;
import javax.websocket.OnError;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import org.apache.commons.lang.StringUtils;

import lombok.Data;

/**
 * WebSocketリクエストを受付ける
 *
 */
@ServerEndpoint(value = "/ws/{symbol}")
public class WSEndpoint {
	private static HashMap<String, Queue<Session>> sessions = new HashMap<>();

	private static HashMap<String, String> broadcastMessages = new HashMap<>();
//	static {
//		ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//		service.scheduleWithFixedDelay(WSEndpoint::broadcast, 5, 5, TimeUnit.SECONDS);
//	}

	/**
	 * ブロードキャストするメッセージを更新する。
	 * @param broadcastMessage ブロードキャストするメッセージ
	 */
	public static void updateBroadcastMessage(String symbol, String _broadcastMessage) {
		broadcastMessages.put(symbol, _broadcastMessage);
	}

	@OnOpen
	public void onOpen(@PathParam("symbol") String symbol, Session session) {
		if (symbol == null || symbol.length()==0) {
			return;
		}
		Queue<Session> qsessions = sessions.get(symbol);
		if (qsessions == null) {
			sessions.put(symbol, new ConcurrentLinkedQueue<Session>());
			qsessions = sessions.get(symbol);
		}
		qsessions.add(session);
		final String broadcastMessage = StringUtils.defaultString(broadcastMessages.get(symbol), "[]");
		session.getAsyncRemote().sendText(broadcastMessage);
	}

	@OnClose
	public void onClose(@PathParam("symbol") String symbol, Session session) {
		if (symbol == null || symbol.length()==0) {
			return;
		}
		System.out.println("closed. " + session.getId());
		Queue<Session> qsessions = sessions.get(symbol);
		if (qsessions == null) {
			sessions.put(symbol, new ConcurrentLinkedQueue<Session>());
			qsessions = sessions.get(symbol);
		}
		qsessions.remove(session);
	}

	@OnError
	public void onError(@PathParam("symbol") String symbol, Session session, Throwable cause) {
		System.out.println("error : " + session.getId() + ", " + cause.getMessage());
	}

	public static void broadcast(String symbol) {
		Queue<Session> qsessions = sessions.get(symbol);
		if (qsessions == null) {
			return;
		}
		final String broadcastMessage = StringUtils.defaultString(broadcastMessages.get(symbol), "[]");
		qsessions.forEach(s -> {
				s.getAsyncRemote().sendText(broadcastMessage);
		});
	}

	public static void broadcast2(String symbol, String msg) {
		Queue<Session> qsessions = sessions.get(symbol);
		if (qsessions == null) {
			return;
		}
		qsessions.forEach(s -> {
				s.getAsyncRemote().sendText(msg);
		});
	}

	public static String getBroadcastMessageBySymbol(String symbol) {
		return broadcastMessages.get(symbol);
	}
}
