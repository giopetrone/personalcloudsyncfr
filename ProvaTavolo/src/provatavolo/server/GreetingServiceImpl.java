package provatavolo.server;

import java.util.List;

import provatavolo.client.GreetingService;
import provatavolo.shared.FieldVerifier;

import com.google.gdata.data.contacts.ContactEntry;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GreetingServiceImpl extends RemoteServiceServlet implements
		GreetingService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 766042051228151430L;

	public String greetServer(String input) throws IllegalArgumentException {
		// Verify that the input is valid. 
		if (!FieldVerifier.isValidName(input)) {
			// If the input is not valid, throw an IllegalArgumentException back to
			// the client.
			throw new IllegalArgumentException(
					"Name must be at least 4 characters long");
		}

		//gio x GDATA

		try {
		String[] myArg = {"--username=annamaria.goy@gmail.com", "--password=tex_willer", "--contactfeed", "--action=update", "--id=http://www.google.com/m8/feeds/contacts/annamaria.goy%40gmail.com/base/38aa1dea099ac975", "--groupid2=32af7f8d8a54cb99"};   
		ContactsExampleParameters parameters = new ContactsExampleParameters(myArg); // X USAGE
		 ContactCall example = new ContactCall(parameters);
		 List<ContactEntry> listaContatti = example.getUserContacts();

	        System.out.println("SONO nel MAIN id del contatto = " + listaContatti.get(0).getId());
		} catch (Exception ex)   {
			ex.printStackTrace();
		}
		
		//end gio Per gdata
		
		
		
		String serverInfo = getServletContext().getServerInfo();
		String userAgent = getThreadLocalRequest().getHeader("User-Agent");

		// Escape data from the client to avoid cross-site script vulnerabilities.
		input = escapeHtml(input);
		userAgent = escapeHtml(userAgent);

		return "Hello, " + input + "!<br><br>I am running " + serverInfo
				+ ".<br><br>It looks like you are using:<br>" + userAgent;
	}

	/**
	 * Escape an html string. Escaping data received from the client helps to
	 * prevent cross-site script vulnerabilities.
	 * 
	 * @param html the html string to escape
	 * @return the escaped string
	 */
	private String escapeHtml(String html) {
		if (html == null) {
			return null;
		}
		return html.replaceAll("&", "&amp;").replaceAll("<", "&lt;")
				.replaceAll(">", "&gt;");
	}
}
