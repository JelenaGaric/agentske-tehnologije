package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import agent.*;
import beans.MessageManagerBean;
import model.AgentCenter;
import model.MessageManager;


public class JNDILookup {

	public static final String JNDIPATH = "ejb:ChatAppEar/ChatAppJar/";
	public static final String CollectorLookup = JNDIPATH + Collector.class.getSimpleName() + "!" + CollectorRemote.class.getName() + "?stateful";
	public static final String PredictorLookup = JNDIPATH + Predictor.class.getSimpleName() + "!" + PredictorRemote.class.getName() + "?stateful";
	public static final String PingLookup = JNDIPATH + Ping.class.getSimpleName() + "!" + PingRemote.class.getName() + "?stateful";
	public static final String PongLookup = JNDIPATH + Pong.class.getSimpleName() + "!" + PongRemote.class.getName() + "?stateful";
	public static final String MessageManagerLookup = JNDIPATH + MessageManagerBean.class.getSimpleName() + "!" + MessageManager.class.getName();

	@SuppressWarnings("unchecked")
	public static <T> T lookUp(String name, Class<T> c) {
		T bean = null;
		try {
			Context context = new InitialContext();

			System.out.println("Looking up: " + name);
			bean = (T) context.lookup(name);

			context.close();

		} catch (NamingException e) {
			e.printStackTrace();
		}
		return bean;
	}
	

//	@SuppressWarnings("unchecked")
//	public static <T> T lookUpWithAgentCenter(String name, Class<T> c, AgentCenter remote) {
//		try {
//			return (T) ContextFactory.get(remote).lookup(name);
//		} catch (NamingException ex) {
//			throw new IllegalStateException("Error " + name, ex);
//		}
//	}
}