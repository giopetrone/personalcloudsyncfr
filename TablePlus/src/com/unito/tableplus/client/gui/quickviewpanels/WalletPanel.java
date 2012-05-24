package com.unito.tableplus.client.gui.quickviewpanels;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.RightPanel;
import com.unito.tableplus.client.services.TokenService;
import com.unito.tableplus.client.services.TokenServiceAsync;

public class WalletPanel extends ContentPanel {

	public RightPanel rightPanel;
	
	// componenti
	public LayoutContainer leftLayoutContainer = new LayoutContainer();
	public LayoutContainer rightLayoutContainer = new LayoutContainer();

	// servizi
	public final TokenServiceAsync tokenService = GWT
			.create(TokenService.class);
	
	// token che verrà inserito manualmente
	TextField<String> manualSessionToken = null;

	/**
	 * Costruttore
	 * 
	 * @return void
	 */

	public WalletPanel(RightPanel rightPanel_) {
		this.rightPanel = rightPanel_;
		setHeading("Wallet");
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: white;");
		setLayout(new RowLayout(Orientation.HORIZONTAL));

		populateLeftLayoutContainer();
		populateRightLayoutContainer();
	}

	/**
	 * Popola l'area di sinistra, quella con i pulsanti in verticale
	 * 
	 * @return void
	 */

	public void populateLeftLayoutContainer() {
		// button per il refresh dei miei documenti
		Button refresh = new Button();
		refresh.setToolTip(new ToolTipConfig("Refresh Wallet list"));
		refresh.setIcon(IconHelper.createStyle("arrow_refresh"));
		refresh.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// refreshMyResourcesTree();
			}
		});
		leftLayoutContainer.add(refresh);
		add(leftLayoutContainer);
	}

	/**
	 * Popola l'area di destra, quella con le informazioni
	 * 
	 * @return void
	 */

	public void populateRightLayoutContainer() {
		rightLayoutContainer.add(new Text(
				"Click here to allow docs access: (new token request)"));

		Button toGdocTokenRequestButton = new Button("GDocs");

		final String homepageURL;
		if (GWT.getHostPageBaseURL().contains("127.0.0.1"))
			homepageURL = "http://127.0.0.1:8888/TablePlus.html?gwt.codesvr=127.0.0.1:9997";
		else
			homepageURL = GWT.getHostPageBaseURL();

		toGdocTokenRequestButton
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					public void componentSelected(ButtonEvent ce) {
						// Azioni da eseguire alla pressione del button
						tokenService.getRequestTokenURL(homepageURL, new AsyncCallback<String>() {
							public void onFailure(Throwable caught) {
							}

							public void onSuccess(String result) {
								redirect(result);
							}
						});
						
					}
				});
		// item (1)
		rightLayoutContainer.add(toGdocTokenRequestButton);

		// richiesta manuale
		// item (2)
		rightLayoutContainer
				.add(new Text(
						"or manually copy here an old token (not managing wrong tokens right now)"));
		manualSessionToken = new TextField<String>();
		manualSessionToken.setFieldLabel("Session Token");
		manualSessionToken.setAllowBlank(false);
		Button go = new Button("GO");
		
		// item (3)
		rightLayoutContainer.add(manualSessionToken);
		// item (4)
		rightLayoutContainer.add(go);

		// se abbiamo già il token, il button sarà inattivo
		if (TablePlus.user.getToken() != null) {
			toGdocTokenRequestButton.setEnabled(false);
			manualSessionToken.setEnabled(false);
			go.setEnabled(false);
			rightLayoutContainer.add(new Text(TablePlus.user.getToken()));
		}
		
		rightLayoutContainer.setScrollMode(Scroll.AUTO);
		rightLayoutContainer.setHeight("100%");
		rightLayoutContainer.setWidth(300);
		
		add(rightLayoutContainer);
	}

	/**
	 * Funzione che reindirizza a un'altra pagina
	 * 
	 * @return void
	 */
	
	public static native void redirect(String url)
	/*-{
		$wnd.location = url;
	}-*/;

}
