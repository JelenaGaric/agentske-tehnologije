package util;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import model.AgentCenter;

public class ContextFactory {
	private static Context context;
	private static Context remoteContext;

	//Podesavanje initial contexta
	static {
		try {
			Hashtable<String, Object> jndiProps = new Hashtable<>();
			jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
			context = new InitialContext(jndiProps);
		} catch (NamingException ex) {
			System.out.println("Context init err");
		}
	}

	public static Context get(AgentCenter remote) {
		if (remote != null) {
			try {
				if (remoteContext == null || !remoteContext.getEnvironment().get(Context.PROVIDER_URL).toString()
						.equals("http-remoting://" + remote.getAddress() + ":8080")) {
					Hashtable<String, Object> jndiProps = new Hashtable<>();
					jndiProps.put(Context.URL_PKG_PREFIXES, "org.jboss.ejb.client.naming");
					jndiProps.put(Context.INITIAL_CONTEXT_FACTORY,
							"org.jboss.naming.remote.client.InitialContextFactory");
					jndiProps.put(Context.PROVIDER_URL, "http-remoting://" + remote.getAddress() + ":8080");
					remoteContext = new InitialContext(jndiProps);
				}
			} catch (NamingException e) {
				e.printStackTrace();
				remoteContext = null;
			}
			return remoteContext;
		}
		return context;
	}

}
