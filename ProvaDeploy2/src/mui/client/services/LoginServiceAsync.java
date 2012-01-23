package mui.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

public interface LoginServiceAsync {
	public void isLogged(String requestUri, AsyncCallback<String> callback);
}
