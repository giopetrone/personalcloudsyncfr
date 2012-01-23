package mui.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

@RemoteServiceRelativePath("token-service")
public interface TokenService extends RemoteService{

	String getToken(String requestUri);
	String getDocsInfo(String token);
}
