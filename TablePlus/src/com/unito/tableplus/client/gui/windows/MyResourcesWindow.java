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
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.DriveFile;
import com.unito.tableplus.shared.model.DropBoxFile;
import com.unito.tableplus.shared.model.Provider;
import com.unito.tableplus.shared.model.Resource;

public class MyResourcesWindow extends WindowPlus {

	public static final UserServiceAsync userService = ServiceFactory
			.getUserServiceInstance();

	private LayoutContainer container;
	private ListStore<BaseModel> resourcesStore;
	private Grid<BaseModel> grid;
	private Button loadButton;

	Menu contextMenu;
	MenuItem insert;

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
		insert = new MenuItem();
		insert.setText("Share on Table");

		insert.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				Info.display("Share resource", "Not implemented yet...");
			}
		});
		contextMenu.add(insert);
		grid.setContextMenu(contextMenu);
		
		loadButton = new Button("Load Resources", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				loadResources();
			}
		});
		
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
			if (r.getProvider().equals(Provider.DRIVE)) {
				DriveFile df = (DriveFile) r;
				mdata.set("icon", "");
				mdata.set("img", "drive-file.png");
				mdata.set("name", df.getTitle());
				mdata.set("provider", "Drive");
			}
			if (r.getProvider().equals(Provider.DROPBOX)) {
				DropBoxFile dbf = (DropBoxFile) r;
				mdata.set("icon", "");
				mdata.set("img", dbf.getIcon() + ".gif");
				mdata.set("name", dbf.getPath().substring(1));
				mdata.set("provider", "Dropbox");
			}
			resourcesStore.add(mdata);
		}
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
}
