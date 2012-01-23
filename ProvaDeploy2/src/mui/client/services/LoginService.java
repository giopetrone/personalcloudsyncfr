package mui.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("login-service")
public interface LoginService extends RemoteService {
	public String isLogged(String requestUri);
}
