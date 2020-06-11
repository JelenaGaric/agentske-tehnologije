package ws;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.ejb.LocalBean;
import javax.ejb.Singleton;
import javax.websocket.EncodeException;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;


@Singleton
@LocalBean
@ServerEndpoint("/ws/{clientId}")
//salje svim klijentima 3 korak
public class WS {

	public static Map<String, Session> sessions = new ConcurrentHashMap<>();
	
	@OnOpen
	public void onOpen(@PathParam("clientId") String clientId, Session session) {
//		if(!sessions.contains(session)) {
//			sessions.add(session);
//			}
		sessions.put(clientId, session);
		System.out.println("opened "+ clientId);
	}

	@OnMessage
	public void echoTextMessage(String msg)  {

				for (Entry<String, Session> entry : sessions.entrySet()) {

							try {
								((Session) entry.getValue()).getBasicRemote().sendText(msg);
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
				}
					if(!msg.equals("loggedIn")) {
						String[] parts=msg.split(":");
						sessions.remove(parts[1]);
					}
	 }
}
