package com.unito.tableplus.client.gui;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.data.ModelIconProvider;
import com.extjs.gxt.ui.client.dnd.DND.Operation;
import com.extjs.gxt.ui.client.dnd.TreePanelDragSource;
import com.extjs.gxt.ui.client.dnd.TreePanelDropTarget;
import com.extjs.gxt.ui.client.event.DNDEvent;
import com.extjs.gxt.ui.client.event.DNDListener;
import com.extjs.gxt.ui.client.event.Events;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.TreePanelEvent;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.google.gwt.user.client.ui.AbstractImagePrototype;
import com.unito.tableplus.shared.model.Group;
import com.unito.tableplus.shared.model.User;

public class GroupPanel extends RightPanel {

	public ContentPanel members = new ContentPanel();
	public ContentPanel myResources = new ContentPanel();
	public ContentPanel groupResources = new ContentPanel();
	private Group group;

	public GroupPanel(DesktopPlus desktop_, User user_, Group group_) {

		super(desktop_, user_);
		this.setGroup(group_);
		addMembersPanel();

		myResources = getMyResourcesPanel();
		add(myResources);

		setGroupResources();
		add(groupResources);
		addDnd();
		
	}

	public void addDnd() {
		
		DNDListener listener = new DNDListener(){
			@SuppressWarnings({ "unchecked", "rawtypes" })
			@Override
			public void dragStart(DNDEvent e) {
				TreePanel tree = ((TreePanel) e.getComponent());
				ModelData sel = tree.getSelectionModel().getSelectedItem();
				System.out.println("PROVAAAA "+sel.get("name"));
				
				if (sel != null && tree.getStore().getParent(sel) == null) {
					e.setCancelled(true);
					e.getStatus().setStatus(false);
					return;
				}
				super.dragStart(e);
			}
		};


		TreePanelDragSource source = new TreePanelDragSource(treePanelMyDocuments);
		source.addDNDListener(listener);
//		
		TreePanelDropTarget t=new TreePanelDropTarget(treePanelGroupResources);  
		t.setOperation(Operation.COPY);
	}
	
	TreeStore<ModelData> storeGroupResources = new TreeStore<ModelData>();
	TreePanel<ModelData> treePanelGroupResources = new TreePanel<ModelData>(storeGroupResources);

	public void setGroupResources() {
		groupResources.setHeading("Group Resources");
		groupResources.setCollapsible(true);
		groupResources.setTitleCollapse(true);
		groupResources.setBodyStyle("backgroundColor: white;");
		groupResources.setScrollMode(Scroll.AUTO);

		
		treePanelGroupResources.setIconProvider(new ModelIconProvider<ModelData>() {
			public AbstractImagePrototype getIcon(ModelData model) {
				if (model.get("icon") != null) {
					return IconHelper.createStyle((String) model.get("icon"));
				} else {
					return null;
				}
			}

		});
		treePanelGroupResources.setDisplayProperty("name");
		treePanelGroupResources.addListener(Events.OnDoubleClick,
				new Listener<TreePanelEvent<ModelData>>() {
					public void handleEvent(TreePanelEvent<ModelData> be) {
						// System.out.println("CIAO " +
						// be.getItem().get("name"));
						if (be.getItem().get("link") != null)
							com.google.gwt.user.client.Window.open((String) be
									.getItem().get("link"), "_blank", "");
					};
				});

		ModelData m = new BaseModelData();
		m.set("name", "Shared Documents");
		storeGroupResources.add(m, false);
		ModelData m_son;

		for (int i = 0; i < 3; i++) {
			m_son = new BaseModelData();
			m_son.set("name", "Bogus Doc " + i);
			m_son.set("icon", "document_font");
			m_son.set("link", "http://www.google.it/");
			// System.out.println("LINK = "+document.getLink());
			storeGroupResources.add(m, m_son, false);
		}

		treePanelGroupResources.setExpanded(m, true);
		groupResources.add(treePanelGroupResources);
	}

	public void addMembersPanel() {

		members.setHeading("Members");
		members.setCollapsible(true);
		members.setTitleCollapse(true);
		members.setBodyStyle("backgroundColor: white;");
		members.setScrollMode(Scroll.AUTO);

		TreeStore<ModelData> store = new TreeStore<ModelData>();
		TreePanel<ModelData> treePanel = new TreePanel<ModelData>(store);

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

		ModelData m1 = new BaseModelData();
		m1.set("name", "Online Users");
		m1.set("icon", "lightbulb");
		store.add(m1, false);
		ModelData m1_son;

		for (int i = 1; i < 4; i++) {
			m1_son = new BaseModelData();
			m1_son.set("name", "User " + i);
			m1_son.set("icon", "user-green");
			store.add(m1, m1_son, false);
		}

		ModelData m2 = new BaseModelData();
		m2.set("name", "Offline Users");
		m2.set("icon", "lightbulb_off");
		store.add(m2, false);
		ModelData m2_son;

		for (int i = 4; i < 6; i++) {
			m2_son = new BaseModelData();
			m2_son.set("name", "User " + i);
			m2_son.set("icon", "user-silhouette");
			store.add(m2, m2_son, false);
		}

		treePanel.setExpanded(m1, true);
		treePanel.setExpanded(m2, true);
		members.add(treePanel);
		add(members);

	}

	public Group getGroup() {
		return group;
	}

	public void setGroup(Group group) {
		this.group = group;
	}

}
