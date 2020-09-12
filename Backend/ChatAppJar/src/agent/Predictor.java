package agent;

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

import model.ACLMessage;
import model.AID;
import ws.WS;

@Stateful
@LocalBean
@Remote(PredictorRemote.class)

public class Predictor implements PredictorRemote{
	private AID id;
	
	@EJB WS ws;

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
		ResteasyWebTarget target = client.target("http://localhost:5000/api/predict/"+message.getContent());
		Response response = target.request().get();
		System.out.print(response);
		
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
		
		//npr:
		ws.echoTextMessage("{\"certainty\": \"88.00\", \"result\": \"1\"}");
	}

}
