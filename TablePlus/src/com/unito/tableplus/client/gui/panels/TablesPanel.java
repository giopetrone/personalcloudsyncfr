package com.unito.tableplus.client.gui.panels;

import java.util.Map;

import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
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
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.shared.model.Table;

public class TablesPanel extends ContentPanel {

	private LayoutContainer leftLayoutContainer;
	private LayoutContainer rightLayoutContainer;
	private TreePanel<ModelData> treePanel;
	private TreeStore<ModelData> treeStore;

	public TablesPanel() {
		this.leftLayoutContainer = new LayoutContainer();
		this.rightLayoutContainer = new LayoutContainer();
		this.treeStore = new TreeStore<ModelData>();
		setHeading("My Tables");
		setCollapsible(true);
		setTitleCollapse(false);
		setBodyStyle("backgroundColor: white;");
		setLayout(new RowLayout(Orientation.HORIZONTAL));
		populateLeftLayoutContainer();
		populateRightLayoutContainer();
	}
	
	private void populateLeftLayoutContainer() {

		Button addTable = new Button();
		addTable.setToolTip(new ToolTipConfig("Create new Table"));
		addTable.setIcon(IconHelper.createStyle("monitor_add"));
		addTable.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				TablePlus.getDesktop().createNewTable();
			}
		});
		leftLayoutContainer.add(addTable);
		add(leftLayoutContainer);
	}
	
	private void populateRightLayoutContainer() {
		rightLayoutContainer.setScrollMode(Scroll.AUTO);

		treePanel = new TreePanel<ModelData>(treeStore);

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
						Long tableKey = Long.parseLong(be.getItem().get("key").toString());
						TablePlus.getDesktop().switchToTable(tableKey);
					};
				});

		rightLayoutContainer.add(treePanel);
		rightLayoutContainer.setHeight("100%");
		rightLayoutContainer.setWidth(300);
		add(rightLayoutContainer);
	}

	public void updateContent() {
		mask("Loading...");
		treeStore.removeAll();
		ModelData m;
		Map<Long, Table> tablesMap = TablePlus.getDesktop().getTables();
		if (!tablesMap.isEmpty())
			for (Table t : tablesMap.values()) {
				m = new BaseModelData();
				m.set("key", t.getKey());
				m.set("name", t.getName());
				m.set("icon", "monitor");
				treeStore.add(m, false);
			}
		unmask();
	}
}
