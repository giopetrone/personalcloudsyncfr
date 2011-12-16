package com.mui.myproject.client.windows;

import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Window;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.layout.FitLayout;
import com.mui.myproject.client.forms.FeedForm;
import com.mui.myproject.shared.model.Feed;
import com.google.gwt.user.client.Element;

public class FeedWindow extends Window {

	private final FeedForm feedForm = new FeedForm();

	public FeedWindow(final Feed feed, String ret) {
		setHeading("Feed "+ret);
		setWidth(350);
		setHeight(200);
		setResizable(false);
		setLayout(new FitLayout());

		final Button btnSave = new Button("Save");
		btnSave.setIconStyle("save");
		btnSave.addSelectionListener(new SelectionListener<ButtonEvent>() {
			public void componentSelected(ButtonEvent ce) {
				btnSave.setEnabled(false);
				if (feedForm.isValid()) {
					hide(btnSave);
					feedForm.save(feed);
				} else {
					btnSave.setEnabled(true);
				}
			}
		});
		addButton(btnSave);
	}

	@Override
	protected void onRender(Element parent, int pos) {
		super.onRender(parent, pos);
		add(feedForm);
	}

}
