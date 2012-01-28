package com.unito.tableplus.client.gui;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.button.ToolButton;
import com.extjs.gxt.ui.client.widget.layout.AccordionLayout;
import com.extjs.gxt.ui.client.widget.toolbar.SeparatorToolItem;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

public class ExampleAccordionWindow extends Window{

	public ExampleAccordionWindow(){
		setMinimizable(true);
		setMaximizable(true);
		setIcon(IconHelper.createStyle("accordion"));
		setHeading("Accordion Window");
		setWidth(200);
		setHeight(350);

		ToolBar toolBar = new ToolBar();
		Button item = new Button();
		item.setIcon(IconHelper.createStyle("icon-connect"));
		toolBar.add(item);

		toolBar.add(new SeparatorToolItem());
		setTopComponent(toolBar);

		item = new Button();
		item.setIcon(IconHelper.createStyle("icon-user-add"));
		toolBar.add(item);

		item = new Button();
		item.setIcon(IconHelper.createStyle("icon-user-delete"));
		toolBar.add(item);

		setLayout(new AccordionLayout());

		ContentPanel cp = new ContentPanel();
		cp.setAnimCollapse(false);
		cp.setHeading("Online Users");
		cp.setScrollMode(Scroll.AUTO);
		cp.getHeader().addTool(new ToolButton("x-tool-refresh"));

		add(cp);

		TreeStore<ModelData> store = new TreeStore<ModelData>();
		TreePanel<ModelData> tree = new TreePanel<ModelData>(store);
		tree.setIconProvider(new ModelIconProvider<ModelData>() {

			public AbstractImagePrototype getIcon(ModelData model) {
				if (model.get("icon") != null) {
					return IconHelper.createStyle((String) model.get("icon"));
				} else {
					return null;
				}
			}

		});
		tree.setDisplayProperty("name");

		ModelData m = newItem("Family", null);
		store.add(m, false);
		tree.setExpanded(m, true);

		store.add(m, newItem("Darrell", "user"), false);
		store.add(m, newItem("Maro", "user-girl"), false);
		store.add(m, newItem("Lia", "user-kid"), false);
		store.add(m, newItem("Alec", "user-kid"), false);
		store.add(m, newItem("Andrew", "user-kid"), false);

		m = newItem("Friends", null);
		store.add(m, false);
		tree.setExpanded(m, true);
		store.add(m, newItem("Bob", "user"), false);
		store.add(m, newItem("Mary", "user-girl"), false);
		store.add(m, newItem("Sally", "user-girl"), false);
		store.add(m, newItem("Jack", "user"), false);

		cp.add(tree);

		cp = new ContentPanel();
		cp.setAnimCollapse(false);
		cp.setHeading("Settings");
		cp.setBodyStyleName("pad-text");
		cp.addText("DUMMY_TEXT_SHORT");
		add(cp);

		cp = new ContentPanel();
		cp.setAnimCollapse(false);
		cp.setHeading("Stuff");
		cp.setBodyStyleName("pad-text");
		cp.addText("DUMMY_TEXT_SHORT");
		add(cp);

		cp = new ContentPanel();
		cp.setAnimCollapse(false);
		cp.setHeading("More Stuff");
		cp.setBodyStyleName("pad-text");
		cp.addText("DUMMY_TEXT_SHORT");
		add(cp);
	}
	
	private ModelData newItem(String text, String iconStyle) {
		ModelData m = new BaseModelData();
		m.set("name", text);
		m.set("icon", iconStyle);
		return m;
	}
}
