package util;

import javax.naming.NamingException;

import agent.Predictor;
import agent.PredictorRemote;
import beans.MessageManagerBean;
import model.AgentCenter;
import model.MessageManager;

public class ObjectFactory {
	public static final String JNDIPATH = "ejb:ChatAppEar/ChatAppJar/";
	
	public static final String MessageManagerLookup = JNDIPATH + MessageManagerBean.class.getSimpleName() + "!" + MessageManager.class.getName();

	public static MessageManager getMessageManager(AgentCenter remote) {
		return lookup(MessageManagerLookup, MessageManager.class, remote);
	}

	@SuppressWarnings("unchecked")
	public static <T> T lookup(String name, Class<T> c, AgentCenter remote) {
		try {
			return (T) ContextFactory.get(remote).lookup(name);
		} catch (NamingException ex) {
			throw new IllegalStateException("Failed to lookup " + name, ex);
		}
	}
}
