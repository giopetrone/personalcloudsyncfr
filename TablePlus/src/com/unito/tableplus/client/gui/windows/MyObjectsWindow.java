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
import com.extjs.gxt.ui.client.widget.Layout;
import com.extjs.gxt.ui.client.widget.LayoutContainer;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormButtonBinding;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextArea;
import com.extjs.gxt.ui.client.widget.grid.ColumnConfig;
import com.extjs.gxt.ui.client.widget.grid.ColumnModel;
import com.extjs.gxt.ui.client.widget.grid.ColumnData;
import com.extjs.gxt.ui.client.widget.grid.Grid;
import com.extjs.gxt.ui.client.widget.grid.GridCellRenderer;
import com.extjs.gxt.ui.client.widget.layout.CenterLayout;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.extjs.gxt.ui.client.widget.layout.FormData;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
import com.extjs.gxt.ui.client.widget.menu.Menu;
import com.extjs.gxt.ui.client.widget.menu.MenuItem;
import com.extjs.gxt.ui.client.widget.tips.ToolTipConfig;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.unito.tableplus.client.TablePlus;
import com.unito.tableplus.client.services.ServiceFactory;
import com.unito.tableplus.client.services.TableServiceAsync;
import com.unito.tableplus.client.services.UserService;
import com.unito.tableplus.client.services.UserServiceAsync;
import com.unito.tableplus.shared.model.Bookmark;
import com.unito.tableplus.shared.model.Comment;
import com.unito.tableplus.shared.model.Resource;
import com.unito.tableplus.shared.model.Table;
import com.unito.tableplus.shared.model.User;

public class MyObjectsWindow extends WindowPlus {

	private final UserServiceAsync userService = GWT.create(UserService.class);
	public static final TableServiceAsync tableService = ServiceFactory
			.getTableServiceInstance();

	private LayoutContainer mainContainer;
	private Grid<BaseModel> grid;
	private ListStore<BaseModel> objectsStore;
	private Button loadButton;
	private Button addButton;
	private Layout fitLayout = new FitLayout();
	private Layout centerLayout = new CenterLayout();
	private FormPanel inputPanel;
	
	
	/**
	 * Right-click context menu
	 */
	private Menu contextMenu;

	/*Right click menu entries*/
	private MenuItem share;
	private MenuItem open;

	private List<String> allTags = new ArrayList<String>();
	/**
	 * The list of the object contained in the grid.
	 * It is needed to know which object has been selected
	 * on a right-click.
	 */
	private List<Resource> objects;

	public MyObjectsWindow() {
		super();
		setSize(635, 350);
		setHeading("My Objects");
		setVisible(false);

		setLayout(new RowLayout(Orientation.VERTICAL));
		setButtonAlign(HorizontalAlignment.LEFT);
		mainContainer = new LayoutContainer();
		mainContainer.setLayout(fitLayout);
		mainContainer.setScrollMode(Scroll.AUTOY);
		objectsStore = new ListStore<BaseModel>();
		grid = new Grid<BaseModel>(objectsStore, getColumnModel());
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
				Resource selectedObject = getSelectedObject(id);
				if (selectedObject != null) {
					shareObject(selectedObject);

				}
			}
		});
		contextMenu.add(share);
		
		open = new MenuItem();
		open.setText("Open");
		open.setIcon(IconHelper.createStyle("go"));

		open.addSelectionListener(new SelectionListener<MenuEvent>() {
			@Override
			public void componentSelected(MenuEvent ce) {
				BaseModel selected = grid.getSelectionModel().getSelectedItem();
				String id = selected.get("url").toString();
				if (id != null)
					Window.open(id, "_blank", "");
				else
					Info.display("Open resource",
							"This resource cannot be opened.");
			}
		});

		contextMenu.add(open);

		grid.setContextMenu(contextMenu);
		mainContainer.add(grid);

		add(mainContainer, new RowData(1, 1, new Margins(4)));
		loadButton = new Button("Refresh",
				new SelectionListener<ButtonEvent>() {
					@Override
					public void componentSelected(ButtonEvent ce) {
						loadObjects();
					}
				});
		loadButton.setToolTip(new ToolTipConfig("Refresh objects"));
		loadButton.setIcon(IconHelper.createStyle("arrow_refresh"));

		addButton = new Button("New bookmark", new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				showInputPanel();
			}
		});
		addButton.setToolTip(new ToolTipConfig("Open new bookmark panel"));
		addButton.setIcon(IconHelper.createStyle("add"));

		addButton(loadButton);
		addButton(addButton);
	}

	/**
	 * Clean the objects store and refills it with
	 * the result of a remote call fetching user objects.
	 */
	private void loadObjects() {
		objectsStore.removeAll();
		mask();
		userService.loadUserObjects(TablePlus.getUser(),
				new AsyncCallback<List<Resource>>() {
					@Override
					public void onFailure(Throwable caught) {
						GWT.log("Unable to load objects for user: "
								+ TablePlus.getUser().getFirstName(), caught);
						Info.display("Error", "Unable to load objects.");
						unmask();
					}

					@Override
					public void onSuccess(List<Resource> result) {
						if (result != null) {
							objects = result;
							fillGrid(objects);
						}
						unmask();
					}
				});
	}
	
	/**
	 * Shows the input panel for inserting a new Bookmark.
	 */
	private void showInputPanel() {
		inputPanel = buildPanel();
		mainContainer.remove(grid);
		loadButton.disable();
		addButton.disable();
		mainContainer.setLayout(centerLayout);
		mainContainer.add(inputPanel);
		mainContainer.layout();

	}

	@Override
	public void updateContent() {
		loadObjects();
	}
	
	/**
	 * Determines which object has been selected
	 * after a right click.
	 * @param id The object id
	 * @return	The selected object
	 */
	private Resource getSelectedObject(String id) {
		for (Resource r : objects)
			if (r.getID().equals(id))
				return r;
		return null;
	}

	/**
	 * Makes a remote call performing the sharing operation
	 * with current table and user.
	 * @param selectedResource The resource that has been selected for sharing.
	 */
	private void shareObject(Resource selectedResource) {
		User user = TablePlus.getUser();
		Table table = TablePlus.getDesktop().getActiveTable();
		if (table == null)
			Info.display("Share resource", "Cannot share on personal table!");
		else
			tableService.addObject(selectedResource, user, table.getKey(),
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
	
	/**
	 * Builds the new bookmark input panel
	 * @return The built panel
	 */
	private FormPanel buildPanel() {
		final FormPanel panel = new FormPanel();
		panel.setLayoutData(Orientation.HORIZONTAL);
		final FormData formData = new FormData("-20");
		panel.setHeading("New Bookmark");
		panel.setFrame(true);
		panel.setWidth(350);

		final TextArea title = new TextArea();
		title.setHeight(20);
		title.setPreventScrollbars(true);
		title.setFieldLabel("Title");
		title.setAllowBlank(false);
		panel.add(title, formData);

		final TextArea url = new TextArea();
		url.setHeight(20);
		url.setPreventScrollbars(true);
		url.setFieldLabel("Url");
		url.setAllowBlank(false);
		panel.add(url, formData);

		final TextArea legend = new TextArea();
		legend.setHeight(35);
		legend.setPreventScrollbars(true);
		legend.setFieldLabel("Legend");
		legend.setAllowBlank(true);
		panel.add(legend, formData);

		final TextArea comment = new TextArea();
		comment.setHeight(35);
		comment.setPreventScrollbars(true);
		comment.setFieldLabel("Comment");
		comment.setAllowBlank(true);
		panel.add(comment, formData);

		final TextArea annotation = new TextArea();
		annotation.setHeight(20);
		annotation.setPreventScrollbars(true);
		annotation.setFieldLabel("Annotation");
		annotation.setAllowBlank(true);
		panel.add(annotation, formData);

		final TextArea tag = new TextArea();
		tag.setHeight(20);
		tag.setPreventScrollbars(true);
		tag.setFieldLabel("Tag");
		tag.setAllowBlank(true);
		panel.add(tag, formData);
		if (allTags.size() > 0)
			tag.setToolTip("All existing tags: \n" + allTags.toString());
		else
			tag.setToolTip("There are no tags");
		Button saveButton = new Button("Save");
		panel.addButton(saveButton);
		Button cancelButton = new Button("Cancel");
		panel.addButton(cancelButton);
		panel.setButtonAlign(HorizontalAlignment.CENTER);
		FormButtonBinding binding = new FormButtonBinding(panel);
		binding.addButton(saveButton);
		cancelButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				mainContainer.remove(inputPanel);
				mainContainer.setLayout(fitLayout);
				mainContainer.add(grid);
				loadButton.enable();
				addButton.enable();
				mainContainer.layout();
			}
		});
		saveButton.addSelectionListener(new SelectionListener<ButtonEvent>() {
			@Override
			public void componentSelected(ButtonEvent ce) {
				Bookmark bookmark = new Bookmark();
				bookmark.setTitle(title.getValue());
				bookmark.setUrl(url.getValue());
				bookmark.setLegend(legend.getValue());
				if (annotation.getValue() != null)
					bookmark.addAnnotation(annotation.getValue());
				if (tag.getValue() != null)
					bookmark.addTag(tag.getValue().toUpperCase());
				if (comment.getValue() != null) {
					Comment c = new Comment(comment.getValue(), TablePlus
							.getUser().getEmail());
					bookmark.addComment(c);
				}
				title.clear();
				url.clear();
				legend.clear();
				comment.clear();
				annotation.clear();
				tag.clear();
			}
		});
		return panel;
	}

	private ColumnModel getColumnModel() {
		ColumnConfig icon = new ColumnConfig("icon", "", 32);
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
		
		ColumnConfig name = new ColumnConfig("title", "Title", 70);
		final ColumnConfig url = new ColumnConfig("url", "Url", 160);
		ColumnConfig legend = new ColumnConfig("legend", "Legend", 120);
		ColumnConfig comment = new ColumnConfig("comment", "Comments", 55);
		ColumnConfig tag = new ColumnConfig("tag", "Tag", 70);
		ColumnConfig annotation = new ColumnConfig("annotation", "Annotation",
				65);
		
		List<ColumnConfig> config = new ArrayList<ColumnConfig>();
		config.add(icon);
		config.add(name);
		config.add(url);
		config.add(legend);
		config.add(comment);
		config.add(tag);
		config.add(annotation);

		return new ColumnModel(config);
	}

	private void fillGrid(List<Resource> objects) {
		for (final Resource object : objects) {
			final BaseModel model = new BaseModel();
			model.set("icon", "");
			model.set("img", object.getIcon());
			model.set("key", object.getID());
			model.set("title", object.getName());
			model.set("url", object.getURI());
			objectsStore.add(model);
		}
		contextMenu.setEnabled(objectsStore.getCount() > 0);
	}
}
