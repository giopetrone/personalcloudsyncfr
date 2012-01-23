package mui.server.services;

import mui.client.services.LoginService;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class LoginServiceImpl extends RemoteServiceServlet implements LoginService{

	private static final long serialVersionUID = 1L;

	@Override
	public String isLogged(String requestUri) {
		String info="";
		
		UserService userService = UserServiceFactory.getUserService();
        User user = userService.getCurrentUser();
        
        if(user != null) {
        	info=info+"sembra che tu sia loggato, questi sono i tuoi dati:<br><br>" +
				"    email: "+user.getEmail()+"<br>" +
				"    nickname = "+user.getNickname()+"<br>" +
				"    userId = "+user.getUserId()+"<br>" +
				"    FederatedIdentity = "+user.getFederatedIdentity()+"<br>" +
				"    AuthDomain = "+user.getAuthDomain()+"<br><br>" +
				"clicca <a href='"+userService.createLogoutURL(requestUri)+"'>qui</a> per sloggarti";
  		}
		else{
			info=info+"sembra che tu non sia loggato, clicca <a href='"+userService.createLoginURL(requestUri)+"'>qui</a> per loggarti!";
		} 
        
		return info;
		
	}

}
