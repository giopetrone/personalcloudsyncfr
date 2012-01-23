package mui.client;

import mui.client.services.LoginService;
import mui.client.services.LoginServiceAsync;
import mui.client.services.TokenService;
import mui.client.services.TokenServiceAsync;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;

public class ProvaDeploy2 implements EntryPoint {

	//
	String currentUrl = "";

	// crea il servizio per il login
	private final LoginServiceAsync loginService = GWT
			.create(LoginService.class);

	// crea il servizio per il token
	private final TokenServiceAsync tokenService = GWT
			.create(TokenService.class);

	// crea del codice html
	HTML htmlLabel = new HTML();

	// crea la stringa che sarà il corpo del codice html
	String htmlLabelBody = "";

	public void onModuleLoad() {

		// crea una stringa che sia il corpo dell'etichetta
		htmlLabelBody = "Ciao, ";

		// chiama il servizio di login, aggiunge alla htmlLabelBody la stringa
		// restituita, cioè:
		//
		// A) Se siamo loggati aggiunge
		// "sembra che tu sia loggato, questi sono i tuoi dati [...]
		// clicca _qui_ per sloggarti"
		//
		// B) Se siamo sloggati aggiunge
		// "sembra che tu non sia loggato, clicca _qui_ per loggarti"
		//

		getLoginInfo();

		// aggiunge il corpo alla label
		htmlLabel.setHTML(htmlLabelBody);

		// aggiunge la label a rootpanel
		RootPanel.get().add(htmlLabel);

		// rende visibile la label
		htmlLabel.setVisible(true);

	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** getLoginInfo
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	private void getLoginInfo() {

		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {

			}

			public void onSuccess(String result) {
				htmlLabelBody = htmlLabelBody + result;
				htmlLabel.setHTML(htmlLabelBody);
				currentUrl = com.google.gwt.user.client.Window.Location
						.getHref();

				// se nell'url corrente ho la stringa "token=", significa che
				// sto passando un token
				// alla mia applicazione, quindi chiamo
				// "getDocsInfo() --> tokenService.getDocsInfo()"
				// che preleva il token, lo promuove a sessionToken e lo usa per
				// accedere ai documenti

				if (currentUrl.contains("token=")) {
					getDocsInfo();
				} else {

					// se nell'url non sto passando il token, chiamo
					// "getToken() --> tokenService.getToken()"
					// che si occupa semplicemente di mostrare il codice html
					// utile a ricevere il token:
					// "clicca _qui_ per regalarmi un token"

					getToken();
				}
			}
		};

		if (GWT.getHostPageBaseURL().contains("127.0.0.1"))
			loginService
					.isLogged(
							"http://127.0.0.1:8888/ProvaDeploy2.html?gwt.codesvr=127.0.0.1:9997",
							callback);
		else
			loginService.isLogged(GWT.getHostPageBaseURL(), callback);
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** getToken
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	private void getToken() {

		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {

			}

			public void onSuccess(String result) {
				htmlLabelBody = htmlLabelBody + result;
				htmlLabel.setHTML(htmlLabelBody);
			}
		};

		if (GWT.getHostPageBaseURL().contains("127.0.0.1"))
			tokenService
					.getToken(
							"http://127.0.0.1:8888/ProvaDeploy2.html?gwt.codesvr=127.0.0.1:9997",
							callback);
		else
			tokenService.getToken(GWT.getHostPageBaseURL(), callback);
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** getDocsInfo
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	private void getDocsInfo() {
		String token = com.google.gwt.user.client.Window.Location
				.getParameter("token");

		AsyncCallback<String> callback = new AsyncCallback<String>() {
			public void onFailure(Throwable caught) {

			}

			public void onSuccess(String result) {
				htmlLabelBody = htmlLabelBody + result;
				htmlLabel.setHTML(htmlLabelBody);
			}
		};
		tokenService.getDocsInfo(token, callback);
	}
}
