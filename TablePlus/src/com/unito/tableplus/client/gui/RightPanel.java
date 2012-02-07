package com.unito.tableplus.client.gui;

import java.util.List;

import com.extjs.gxt.desktop.client.Desktop;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.layout.FillLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.services.TokenService;
import com.unito.tableplus.client.services.TokenServiceAsync;
import com.unito.tableplus.shared.model.Document;
import com.unito.tableplus.shared.model.User;

public class RightPanel extends ContentPanel {

	private Desktop desktop;
	private ToolBar toolBar = null;
	public User user = null;
	public List<Document> myDocuments = null;

	public RightPanel(Desktop desktop, User user) {
		this.setDesktop(desktop);
		this.user = user;

		setLayout(new FillLayout(Orientation.VERTICAL));
		setHeading("Quick View");
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: lightgray;");
		setFrame(true);

		addToolBar();
	}

	public User getUser() {
		return user;
	}

	public ToolBar getToolBar() {
		return toolBar;
	}

	public void setToolBar(ToolBar toolBar) {
		this.toolBar = toolBar;
	}

	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************
	// ******
	// ****** updateMyGdocsFolder(String gdocSessionToken)
	// ******
	// ******************************************************************************
	// ******************************************************************************
	// ******************************************************************************

	// crea il servizio per il token
	public final TokenServiceAsync tokenService = GWT
			.create(TokenService.class);

	ContentPanel tmpDocContainer;

	public void updateMyDocuments(List<Document> documentsList,
			ContentPanel docContainer) {
		TreeStore<ModelData> store = new TreeStore<ModelData>();
		TreePanel<ModelData> tree = new TreePanel<ModelData>(store);

		tree.setDisplayProperty("name");

		ModelData m = new BaseModelData();
		m.set("name", "MyGoogleDocs");
		store.add(m, false);
		ModelData m_son;

		for (Document document : documentsList) {
			m_son = new BaseModelData();
			m_son.set("name", document.getTitle());
			store.add(m, m_son, false);
		}
		// aggiunge l'albero al secondo contentPanel
		docContainer.add(tree);
		docContainer.layout();
	}

	public void updateMyDocuments(String gdocSessionToken,
			ContentPanel docContainer) {

		tmpDocContainer = docContainer;
		AsyncCallback<List<Document>> callback = new AsyncCallback<List<Document>>() {
			public void onFailure(Throwable caught) {

			}

			@Override
			public void onSuccess(List<Document> result) {
				myDocuments = result;
				System.out.println("Sono qui " + myDocuments.get(0).getTitle());
				loadMyDocuments();
			}
		};
		tokenService.getDocumentList(gdocSessionToken, callback);
	}

	public void loadMyDocuments() {
		System.out.println(myDocuments.get(0).getTitle());
		// crea un tree per il secondo contentpanel
		TreeStore<ModelData> store = new TreeStore<ModelData>();
		TreePanel<ModelData> tree = new TreePanel<ModelData>(store);

		tree.setDisplayProperty("name");

		ModelData m = new BaseModelData();
		m.set("name", "MyGoogleDocs");
		store.add(m, false);
		ModelData m_son;

		for (Document document : myDocuments) {
			m_son = new BaseModelData();
			m_son.set("name", document.getTitle());
			store.add(m, m_son, false);
		}
		// aggiunge l'albero al secondo contentPanel
		tmpDocContainer.add(tree);
		tmpDocContainer.layout();
	}

	public void addToolBar() {
		ToolBar toolBar = new ToolBar();
		Button styleButton = new Button("Style");

		MenuItem accordionItem = new MenuItem("Accordion Layout",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						setLayout(new AccordionLayout());
						layout();
					}
				});

		MenuItem fillItem = new MenuItem("Fill Layout",
				new SelectionListener<MenuEvent>() {
					@Override
					public void componentSelected(MenuEvent ce) {
						setLayout(new FillLayout(Orientation.VERTICAL));
						layout();
					}
				});

		Menu menu = new Menu();
		menu.add(accordionItem);
		menu.add(fillItem);

		styleButton.setMenu(menu);

		Button button2 = new Button("Menu2");
		toolBar.add(styleButton);
		toolBar.add(button2);
		setTopComponent(toolBar);

	}

	public Desktop getDesktop() {
		return desktop;
	}

	public void setDesktop(Desktop desktop) {
		this.desktop = desktop;
	}

	public ContentPanel getMyResourcesPanel() {
		ContentPanel myResources = new ContentPanel();
		myResources.setHeading("My Resources");
		myResources.setCollapsible(true);
		myResources.setTitleCollapse(true);
		myResources.setBodyStyle("backgroundColor: white;");
		myResources.setScrollMode(Scroll.AUTO);

		if (user.getToken() != null)
			updateMyDocuments(user.getToken(), myResources);

		return myResources;
	}

}
