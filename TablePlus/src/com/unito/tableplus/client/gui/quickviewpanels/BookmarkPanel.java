package com.unito.tableplus.client.gui.quickviewpanels;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
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
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.unito.tableplus.client.gui.RightPanel;

public class BookmarkPanel extends ContentPanel {

	public RightPanel rightPanel;

	//componenti
	public LayoutContainer leftLayoutContainer = new LayoutContainer();
	public LayoutContainer rightLayoutContainer = new LayoutContainer();
	public TreePanel<ModelData> treePanel;
	public TreeStore<ModelData> treeStore = new TreeStore<ModelData>();
	public BaseTreeModel googleDocsRoot = new BaseTreeModel();

	/**
	 * Costruttore
	 *
	 * @return void
	 */

	public BookmarkPanel(RightPanel rightPanel_) {
		this.rightPanel = rightPanel_;
		setHeading("My Bookmark");
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
		Button refreshMyBookmark = new Button();
		refreshMyBookmark.setToolTip(new ToolTipConfig("Refresh objects list"));
		refreshMyBookmark.setIcon(IconHelper.createStyle("arrow_refresh"));
		refreshMyBookmark.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				// refreshMyBookmarkTree();
			}
		});
		leftLayoutContainer.add(refreshMyBookmark);
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
				if ("MyBookmark".equals(m.get("name"))) {
					return true;
				}
				return super.hasChildren(m);
			}
		};
		treePanel.setIconProvider(new ModelIconProvider<ModelData>() {
			public AbstractImagePrototype getIcon(ModelData model) {
				if (model.get("icon") != null) {
					return IconHelper.createStyle((String) model.get("icon"));
				} else {
					return null;
				}
			}
		});
		treePanel.setDisplayProperty("name");
		treePanel.addListener(Events.OnDoubleClick,new Listener<TreePanelEvent<ModelData>>() {
			public void handleEvent(TreePanelEvent<ModelData> be) {
				if (be.getItem().get("link") != null)
					com.google.gwt.user.client.Window.open((String) be.getItem().get("link"), "_blank", "");
			};
		});
		googleDocsRoot.set("name", "MyBookmark");
		treeStore.add(googleDocsRoot, false);
		ModelData m_son;
		treePanel.setExpanded(googleDocsRoot, true);
		rightLayoutContainer.add(treePanel);
		rightLayoutContainer.setHeight("100%");
		rightLayoutContainer.setWidth(300);
		add(rightLayoutContainer);
	}

	public void addData() {
		System.out.println("----------------------------- addData BookmarkPanel ---------------------");
	}
}