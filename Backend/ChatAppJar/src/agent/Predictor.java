package agent;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Remote;
import javax.ejb.Stateful;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Response;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.python.core.PyInstance;
import org.python.core.PyInteger;
import org.python.core.PyObject;
import org.python.core.PyString;
import org.python.util.PythonInterpreter;

import data.NetworkData;
import model.ACLMessage;
import model.AID;
import ws.WS;

@Stateful
@LocalBean
@Remote(PredictorRemote.class)

public class Predictor implements PredictorRemote{
	private AID id;
	
	@EJB WS ws;


	@EJB
	NetworkData networkData; 
	
	@Override
	public AID getId() {
		return id;
	}

	@Override
	public void setId(AID id) {
		this.id = id;		
	}
	
	public Predictor() {}

	public Predictor(AID id) {
		this.id = id;
	}

	@Override
	public void handleMessage(ACLMessage message) {
		System.out.println("Agent " + id.getName() + " received message " + message.getContent() );
		
		//predict
		
		ResteasyClient client = new ResteasyClientBuilder().build();
		String path = "http://"+networkData.getThisNode().getAddress().toString()+":5000/api/predict/";
		String encodedData;
		try {
			encodedData = URLEncoder.encode(message.getContent().toString(), "UTF-8");
			System.out.println(encodedData);
			ResteasyWebTarget target = client.target(path+encodedData);
			Response response = target.request().get();
			String result = response.readEntity(String.class);
			System.out.println();
			System.out.println(result);
			ws.echoTextMessage(result);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		 PythonInterpreter.initialize(System.getProperties(),
//                 System.getProperties(), new String[0]);
//		 PythonInterpreter interpreter = new PythonInterpreter();
//		interpreter.execfile("test.py");
//		PyInstance predictor = (PyInstance) interpreter.eval("Hello(None)");
//		predictor.invoke("run");
//		PyObject retInt2_func = predictor.invoke("ret_int2", new PyInteger(123));
//        Integer ret_int2 = (Integer) retInt2_func.__tojava__(Integer.class);
//        System.out.println("ret int = " + ret_int2);
//		PyObject retString = predictor.invoke("predict", new PyString("{gold:100}"));
//		String results = (String) retString.__tojava__(String.class);
//		System.out.print(results);
//		interpreter.close();
		
		
	}

}
