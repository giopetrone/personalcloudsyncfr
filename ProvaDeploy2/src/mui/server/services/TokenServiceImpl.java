package mui.server.services;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.GeneralSecurityException;

import mui.client.services.TokenService;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.client.http.AuthSubUtil;
import com.google.gdata.data.Person;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

public class TokenServiceImpl extends RemoteServiceServlet implements
		TokenService {

	private static final long serialVersionUID = 1L;

	String nextUrl = "";
	String scope = "https://docs.google.com/feeds/";
	boolean secure = false; // set secure=true to request secure AuthSub tokens
	boolean session = true;
	String authSubUrl = "";

	@Override
	public String getToken(String requestUri) {
		nextUrl = requestUri;
		authSubUrl = AuthSubUtil.getRequestUrl(nextUrl, scope, secure, session);

		return "<br><br>Clicca <a href='" + authSubUrl
				+ "'>qui</a> per regalarmi un token! (esempio Google Docs)";

	}

	@Override
	public String getDocsInfo(String token) {

		String elenco = "<br>Grazie del token, eccolo qui:<br><br> " + token
				+ "<br><br>";

		String sessionToken = "inizializzata";

		//
		// Promuovo il token a "sessionToken"
		//
		
		try {
			sessionToken = AuthSubUtil.exchangeForSessionToken(token, null);
		} catch (AuthenticationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (GeneralSecurityException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

		elenco = elenco
				+ "mentre questa è la sua versione <i>session</i> <br><br>"
				+ sessionToken + "<br><br>";

		DocsService client = new DocsService("yourCo-yourAppName-v1");
		client.setAuthSubToken(sessionToken);
		client.setProtocolVersion(DocsService.Versions.V2);

		try {
			URL feedUri = new URL(
					"https://docs.google.com/feeds/documents/private/full/");
			DocumentListFeed feed = client.getFeed(feedUri,
					DocumentListFeed.class);

			elenco = elenco + "Adesso posso vedere i tuoi documenti:";

			//
			// Scorre l'elenco dei documenti
			//

			for (DocumentListEntry entry : feed.getEntries()) {
				elenco = elenco + "<br><br>-- "
						+ entry.getTitle().getPlainText() + "<br>"
						+ "------ Description: " + entry.getDescription()
						+ "<br>" + "------ DocId: " + entry.getDocId() + "<br>"
						+ "------ Etag: " + entry.getEtag() + "<br>";

				//
				// Stampa l'elenco degli autori
				//
				
				elenco = elenco + "------ # Autori: "
						+ entry.getAuthors().size() + "<br>";
				for (Person p : entry.getAuthors())
					elenco = elenco + "---------- Author: " + p.getEmail()
							+ "<br>";

				//
				// Stampa l'elenco dei contributors
				//
				
				elenco = elenco + "------ # Contributors: "
						+ entry.getContributors().size() + "<br>";
				for (Person p : entry.getContributors())
					elenco = elenco + "---------- Contributor: " + p.getEmail()
							+ "<br>";
				
				//
				// Recupera l'ACL relativo al documento corrente e ne stampa
				// ogni entry, una per ogni utente avente qualche tipo di
				// diritto sul documento
				//

				AclFeed aclFeed = client.getFeed(new URL(entry.getAclFeedLink()
						.getHref()), AclFeed.class);
				elenco = elenco + "------ # aclEntries: "
						+ aclFeed.getEntries().size() + "<br>";
				for (AclEntry aclEntry : aclFeed.getEntries())
					elenco = elenco + "---------- aclEntry: "
							+ aclEntry.getScope().getValue() + " ("
							+ aclEntry.getScope().getType() + ") : "
							+ aclEntry.getRole().getValue() + "<br>";
			}

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ServiceException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return elenco;
	}

}
