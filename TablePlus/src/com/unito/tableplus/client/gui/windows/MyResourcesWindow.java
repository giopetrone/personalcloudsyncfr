package com.unito.tableplus.client.gui.windows;

import java.util.ArrayList;
import java.util.List;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.data.BaseModel;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.MenuEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.util.IconHelper;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.Info;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.User;

public class MyResourcesWindow extends WindowPlus {

	public static final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();
	public static final TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();
	private LayoutContainer container;
	private ListStore<BaseModel> resourcesStore;
	private List<Resource> resources; // needed for sharing
	private Grid<BaseModel> grid;
	private Button loadButton;

	Menu contextMenu;
	MenuItem share;

	public MyResourcesWindow() {
		super();
		setSize(480, 340);
		setHeading("My Resources");
		setLayout(new RowLayout(Orientation.VERTICAL));
		setButtonAlign(HorizontalAlignment.LEFT);

		container = new LayoutContainer();
		container.setLayout(new FitLayout());
		container.setScrollMode(Scroll.AUTOY);

		resourcesStore = new ListStore<BaseModel>();
		grid = new Grid<BaseModel>(resourcesStore, getColumnModel());
		grid.setBorders(true);

		contextMenu = new Menu();
		share = new MenuItem();
		share.setText("Share on Table");
		share.setIcon(IconHelper.createStyle("menu-share"));

		share.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				final BaseModel selected = grid.getSelectionModel()
						.getSelectedItem();
				String id = selected.get("id").toString();
				Resource selectedResource = getSelectedResource(id);
				if (selectedResource != null) {
					shareResource(selectedResource);

				}
			}
		});
		contextMenu.add(share);
		grid.setContextMenu(contextMenu);

		loadButton = new Button("Load Resources",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						loadResources();
					}
				});
		loadButton.setIcon(IconHelper.createStyle("arrow_refresh"));

		container.add(grid);
		addButton(loadButton);
		add(container, new RowData(1, 1, new Margins(4)));
	}

	private void loadResources() {
		resourcesStore.removeAll();
		mask();
		userService.loadResources(TablePlus.getUser(),
				new AsyncCallback<List<Resource>>() {

					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Failed loading resources.");
						unmask();
					}

					@Override
					public void onSuccess(List<Resource> result) {
						fillGrid(result);
						unmask();
					}
				});
	}

	private void fillGrid(List<Resource> resources) {
		BaseModel mdata;

		for (Resource r : resources) {
			mdata = new BaseModel();

			mdata.set("icon", "");
			mdata.set("img", r.getIcon());
			mdata.set("name", r.getName());
			mdata.set("provider", r.getProvider());
			mdata.set("id", r.getID());
			resourcesStore.add(mdata);
		}
		this.resources = resources;
	}

	private ColumnModel getColumnModel() {
		ColumnConfig icon = new ColumnConfig("icon", "", 24);
		icon.setSortable(false);
		icon.setResizable(false);
		icon.setRenderer(new GridCellRenderer<BaseModel>() {
			@Override
			public Object render(BaseModel model, String property,
					ColumnData config, int rowIndex, int colIndex,
					ListStore<BaseModel> store, Grid<BaseModel> grid) {
				config.style = "background: url(/personal/desktop/icons/"
						+ model.get("img") + ") no-repeat center !important;";
				return null;
			}
		});

		ColumnConfig name = new ColumnConfig("name", "Name", 300);
		ColumnConfig provider = new ColumnConfig("provider", "Provider", 100);
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(icon);
		config.add(name);
		config.add(provider);
		return new ColumnModel(config);
	}

	private Resource getSelectedResource(String id) {
		for (Resource r : resources)
			if (r.getID().equals(id))
				return r;
		return null;
	}

	private void shareResource(Resource selectedResource) {
		User user = TablePlus.getUser();
		Long tableKey = TablePlus.getDesktop().getActiveTableKey();
		if (tableKey.equals(0L))
			Info.display("Share resource", "Cannot share on personal table!");
		else
			tableService.addResource(selectedResource, user, tableKey,
					new AsyncCallback<Boolean>() {

						@Override
						public void onFailure(Throwable caught) {
							GWT.log("Failed to share selected resource: ",
									caught);
							Info.display("Share resource",
									"Failed to share selected resource.");
						}

						@Override
						public void onSuccess(Boolean result) {
							if (result)
								Info.display("Share resource",
										"Resource has been successfully shared.");
							else
								Info.display("Share resource",
										"Resource could not be shared.");
						}

					});
	}
}
