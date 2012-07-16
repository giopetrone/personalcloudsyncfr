package com.unito.tableplus.client.gui.quickviewpanels;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.BaseTreeModel;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.gui.RightPanel;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.Provider;

import java.util.List;
import com.unito.tableplus.shared.model.Resource;

public class MyResourcesPanel extends ContentPanel {

	private static final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();

	private RightPanel rightPanel;

	// componenti
	private LayoutContainer leftLayoutContainer;
	private LayoutContainer rightLayoutContainer;
	private TreePanel<ModelData> treePanel;
	private TreeStore<ModelData> treeStore;
	private BaseTreeModel resourcesRoot;

	/**
	 * Costruttore
	 * 
	 * @return void
	 */

	public MyResourcesPanel(RightPanel rightPanel) {
		this.setRightPanel(rightPanel);

		setHeading("My Resources");
		setCollapsible(true);
		setTitleCollapse(true);
		setBodyStyle("backgroundColor: white;");
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		mask("Loading...");
		leftLayoutContainer = new LayoutContainer();
		rightLayoutContainer = new LayoutContainer();
		treeStore = new TreeStore<ModelData>();
		resourcesRoot = new BaseTreeModel();
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
		Button refreshMyResources = new Button();
		refreshMyResources.setToolTip(new ToolTipConfig(
				"Refresh resources list"));
		refreshMyResources.setIcon(IconHelper.createStyle("arrow_refresh"));
		refreshMyResources
				.addSelectionListener(new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						// TODO: implement this method
						// refreshMyResourcesTree();
					}
				});
		leftLayoutContainer.add(refreshMyResources);
		add(leftLayoutContainer);
	}

	/**
	 * Popola l'area di destra, quella con le informazioni
	 * 
	 * @return void
	 */

	public void populateRightLayoutContainer() {
		rightLayoutContainer.setScrollMode(Scroll.AUTO);

		treePanel = new TreePanel<ModelData>(treeStore) {
			@Override
			protected boolean hasChildren(ModelData m) {
				if ("Resources".equals(m.get("name"))) {
					return true;
				}
				return super.hasChildren(m);
			}
		};

		treePanel.setIconProvider(new ModelIconProvider<ModelData>() {

			@Override
			public AbstractImagePrototype getIcon(ModelData model) {
				if (model.get("icon") != null) {
					return IconHelper.createStyle((String) model.get("icon"));
				} else {
					return null;
				}
			}

		});

		treePanel.setDisplayProperty("name");

		treePanel.addListener(Events.OnDoubleClick,
				new Listener<TreePanelEvent<ModelData>>() {
					@Override
					public void handleEvent(TreePanelEvent<ModelData> be) {
						if (be.getItem().get("link") != null)
							com.google.gwt.user.client.Window.open((String) be
									.getItem().get("link"), "_blank", "");
					};
				});

		resourcesRoot.set("name", "Resources");
		treeStore.add(resourcesRoot, false);

		treePanel.setExpanded(resourcesRoot, true);

		rightLayoutContainer.add(treePanel);
		rightLayoutContainer.setHeight("100%");
		rightLayoutContainer.setWidth(300);
		add(rightLayoutContainer);
		userService.loadResources(TablePlus.getUser(),
				new AsyncCallback<List<Resource>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error loading user Drive files.");
						unmask();
					}

					@Override
					public void onSuccess(List<Resource> result) {
						if (result != null) {
							ModelData model;
							for (Resource r : result) {
								if (r.getProvider().equals(Provider.DRIVE)) {
									DriveFile driveFile = (DriveFile) r;
									model = new BaseModelData();
									model.set("name", driveFile.getTitle());
									model.set("icon", "document_font");
									model.set("link", driveFile.getLink());
									model.set("docId", driveFile.getDocId());
									treeStore.add(resourcesRoot, model, false);
								}

							}
						}
						unmask();
					}
				});

	}
	
	//TODO: load all resources types
	public void loadResources() {
		treeStore.removeAll();
		userService.loadResources(TablePlus.getUser(),
				new AsyncCallback<List<Resource>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Error loading user Drive files.");
						unmask();
					}

					@Override
					public void onSuccess(List<Resource> result) {
						if (result != null) {
							ModelData model;
							for (Resource r : result) {
								if (r.getProvider().equals(Provider.DRIVE)) {
									DriveFile driveFile = (DriveFile) r;
									model = new BaseModelData();
									model.set("name", driveFile.getTitle());
									model.set("icon", "document_font");
									model.set("link", driveFile.getLink());
									model.set("docId", driveFile.getDocId());
									treeStore.add(resourcesRoot, model, false);
								}

							}
						}
						unmask();
					}
				});

	}

	/**
	 * @return the rightPanel
	 */
	public RightPanel getRightPanel() {
		return rightPanel;
	}

	/**
	 * @param rightPanel
	 *            the rightPanel to set
	 */
	public void setRightPanel(RightPanel rightPanel) {
		this.rightPanel = rightPanel;
	}

	public TreePanel<ModelData> getTreePanel() {
		return treePanel;
	}

	public void setTreePanel(TreePanel<ModelData> treePanel) {
		this.treePanel = treePanel;
	}
}
