package com.unito.tableplus.server.services;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.unito.tableplus.client.services.LoginService;
import com.unito.tableplus.shared.*;

public class LoginServiceImpl extends RemoteServiceServlet implements
		LoginService {

	private static final long serialVersionUID = 1L;

	@Override
	public Utente isLogged(String requestUri) {
		Utente utente = new Utente();

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		// l'utente è loggato, la sessione è aperta
		if (user != null) {
			
			
			System.out.println("sono loggato");
			utente.setLoggedIn(true);
			utente.setEmail(user.getEmail());
			utente.setLogoutUrl(userService
					.createLogoutURL(requestUri));
			utente.setWallet(new Wallet());

			// Il fatto che sia loggato imlica che sia
			// anche registrato al sito, per cui dovrei
			// recuperare dal db il suo wallet. Al momento
			// recupero solo, se c'è, il suo gdocSessionToken
			// dalla session

			HttpServletRequest request = getThreadLocalRequest();
			HttpSession session = request.getSession();
			String gdocSessionToken = (String) session.getAttribute("gdocSessionToken");
			if(gdocSessionToken!=null){
				utente.getWallet().setGoogleDocSessionToken(gdocSessionToken);
				System.out.println("LSI: un token nella session");
				}
			else
				System.out.println("LSI: Nessun token nella session perchè sono appena rientrato, ancora" +
						"non ho estratto il token dall'url!!");
			
		} else {
			utente.setLoggedIn(false);
			utente.setLoginUrl(userService.createLoginURL(requestUri));
		}

		return utente;

	}

}