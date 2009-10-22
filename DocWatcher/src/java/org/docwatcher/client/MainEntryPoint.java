/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.docwatcher.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Main entry point.
 *
 * @author giovanna
 */

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.Label;

public class MainEntryPoint implements EntryPoint {
    private String me = null;
    private Label label = null;

    /**
     * Creates a new instance of MainEntryPoint
     */
    public MainEntryPoint() {
    }

    /**
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {
        FormPanel formPanel = new FormPanel();
        formPanel.setFrame(true);
        formPanel.setHeading("DocWatcher");
        formPanel.setBorders(false);
        formPanel.setPadding(5);
        formPanel.setWidth(280);
        formPanel.setLabelWidth(105);
        label = new Label("");
        formPanel.add(label);

        RootPanel.get().add(formPanel);
        activateDoc();
    }

    private void activateDoc() {

        // callback per autenticazione utente
        final String googleId = Window.Location.getParameter("gId");
        final AsyncCallback callback4 = new AsyncCallback() {

            public void onSuccess(Object result) {
                me = (String) result;
                label.setText("activated " + me);

            }

            public void onFailure(Throwable caught) {
                MessageBox.confirm("activateDoc authenticate ", "FAIL", null);
            }
        };

        //   MessageBox.confirm("refreshMsgList ", "prima di getEvents , me = " + me, null);
        getService().authenticate(googleId, callback4);
        // callback per far partire docWatcher

         final AsyncCallback callback5 = new AsyncCallback() {

            public void onSuccess(Object result) {


            }

            public void onFailure(Throwable caught) {
                MessageBox.confirm("activateDoc, startDocWatcher ", "FAIL", null);
            }
        };

        //   MessageBox.confirm("refreshMsgList ", "prima di getEvents , me = " + me, null);
        getService().startDocWatcher( callback5);

    }


     public static GWTDocWatcherServiceAsync getService() {
        // Create the client proxy. Note that although you are creating the
        // service interface proper, you cast the result to the asynchronous
        // version of the interface. The cast is always safe because the
        // generated proxy implements the asynchronous interface automatically.

        return GWT.create(GWTDocWatcherService.class);
    }
}
