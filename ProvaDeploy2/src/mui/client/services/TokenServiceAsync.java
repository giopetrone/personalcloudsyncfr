package mui.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface TokenServiceAsync {
	void getToken(String requestUri, AsyncCallback<String> callback);
	void getDocsInfo(String token, AsyncCallback<String> callback);
}
