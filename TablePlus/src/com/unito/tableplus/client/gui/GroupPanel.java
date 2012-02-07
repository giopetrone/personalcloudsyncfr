package com.unito.tableplus.client.gui;

import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModelData;
import com.extjs.gxt.ui.client.data.ModelData;
import com.extjs.gxt.ui.client.store.TreeStore;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Text;
import com.extjs.gxt.ui.client.widget.treepanel.TreePanel;
import com.unito.tableplus.shared.model.User;

public class GroupPanel extends RightPanel{
	
	public ContentPanel members = new ContentPanel();
	public ContentPanel myResources = new ContentPanel();
	public ContentPanel groupResources = new ContentPanel();
	
	public GroupPanel(DesktopPlus desktop, User user){
		
		super(desktop,user);
		
		addMembersPanel();
		
		myResources=getMyResourcesPanel();
	    add(myResources);
		
		groupResources.setHeading("Group Resources");
		groupResources.add(new Text("Panel 3 Text"));
		groupResources.setCollapsible(true);
		groupResources.setTitleCollapse(true);
		groupResources.setBodyStyle("backgroundColor: white;");
		groupResources.setScrollMode(Scroll.AUTO);
		add(groupResources);
		
	}
	
	public void addMembersPanel(){
		
		members.setHeading("Members");
		members.setCollapsible(true);
		members.setTitleCollapse(true);
		members.setBodyStyle("backgroundColor: white;");
		members.setScrollMode(Scroll.AUTO);
		
		TreeStore<ModelData> store = new TreeStore<ModelData>();
		TreePanel<ModelData> tree = new TreePanel<ModelData>(store);
		
		tree.setDisplayProperty("name");
		
		ModelData m1 = new BaseModelData();
		m1.set("name", "Online Users");
		store.add(m1, false);
		ModelData m1_son;
		
		for (int i=1;i<4;i++) {
			m1_son = new BaseModelData();
			m1_son.set("name", "User "+i);
			store.add(m1, m1_son, false);
		}
		
		ModelData m2 = new BaseModelData();
		m2.set("name", "Offline Users");
		store.add(m2, false);
		ModelData m2_son;
		
		for (int i=4;i<6;i++) {
			m2_son = new BaseModelData();
			m2_son.set("name", "User "+i);
			store.add(m2, m2_son, false);
		}
		
		members.add(tree);
		add(members);
		
	}

}
