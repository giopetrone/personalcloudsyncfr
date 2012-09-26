package aa.vv.cc.client;

import java.util.HashMap;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client side stub for the RPC service.
 */
@RemoteServiceRelativePath("greet")
public interface GreetingService extends RemoteService {
	String greetServer(String name) throws IllegalArgumentException;
	String subscribe(String name) throws IllegalArgumentException;
	String publish(String name) throws IllegalArgumentException;
	String feedAsString(String name) throws IllegalArgumentException;
	String storeAtom(HashMap<String,String> args) throws IllegalArgumentException;
}
