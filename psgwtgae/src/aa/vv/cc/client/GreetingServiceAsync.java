package aa.vv.cc.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * The async counterpart of <code>GreetingService</code>.
 */
public interface GreetingServiceAsync {
	void greetServer(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void subscribe(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void publish(String input, AsyncCallback<String> callback)
			throws IllegalArgumentException;
	void storeAtom(HashMap<String,String> args, AsyncCallback<String> callback) 
			throws IllegalArgumentException;
	void feedAsString(String name, AsyncCallback<String> callback) throws IllegalArgumentException;
}
