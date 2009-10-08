/*
 * MainEntryPoint.java
 *
 * Created on June 29, 2009, 11:40 AM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.notificationMgr.client;

import appsusersevents.client.EventDescription;
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.Style.Scroll;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;

import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;

import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;

/**
 *
 * @author giovanna
 */
public class MainEntryPoint implements EntryPoint {

//    private MyServiceProvider provider = new MyServiceProvider();
    private Panel panel;
    //gio
    private String me = null;
    private TextField<String> userName;
    private FlexTable msgTable = new FlexTable();  //area per visualizzazione eventi  di Giga
    private ArrayList<Widget> rimozioni = new ArrayList<Widget>(); //area di widgets per eliminare eventi  di Giga
    final private Label whoAreYou = new Label(""); // per mostrare chi e' loggato al NotificationMgr
    private TextField<String> userPwd;
    private String mePasswd = null;
    private boolean alreadyLoggedIn = false;
    private ContentPanel cp;
    private Html html;

    /** Creates a new instance of MainEntryPoint */
    public MainEntryPoint() {
    }

    /**
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {

        RootPanel.get().add(notif());
//per prendere eventi
        Timer msgTimer = new Timer() {

            public void run() {
                refreshMsgList();
            }
        };
        msgTimer.scheduleRepeating(10000);
    }

    private Panel notif() {
        panel = new VerticalPanel();
        FormPanel formPanel = new FormPanel();
        formPanel.setFrame(true);
        formPanel.setHeading("NotificationMgr");
        formPanel.setBorders(false);
        formPanel.setPadding(5); 
        formPanel.setWidth(280);
        formPanel.setLabelWidth(105);
        userName = new TextField<String>();
        userName.setAllowBlank(false);
        userName.setFieldLabel("Who Are You?");
        userPwd = new TextField<String>();
        userPwd.setAllowBlank(false);
        userPwd.setFieldLabel("Password");
        userPwd.setPassword(true);
        cp = new ContentPanel();
        cp.setHeading("Notification Manager");
        cp.setHeaderVisible(false);

        //    msgTable.setBorderWidth(1);
        cp.add(msgTable);
//        cp.setWidth(240);
//        cp.setHeight(100);
        cp.setSize(260, 200);
        cp.setScrollMode(Scroll.AUTO);
        cp.setFrame(true);
        cp.setBorders(false);
        cp.setLayout(new RowLayout(Orientation.HORIZONTAL));
        RowData data = new RowData(.5, 0.99);
        data.setMargins(new Margins(5));
        data.setHeight(210);
        // creo la barra dei pulsanti del form
        final ToolBar buttonBar = new ToolBar();
        buttonBar.setAlignment(HorizontalAlignment.LEFT);
        //   buttonBar.setCellSpacing(20);
       /* buttonBar.add(new Button("Close", new SelectionListener<ButtonEvent>() {

        @Override
        public void componentSelected(ButtonEvent ce) {
        RootPanel.get().remove(panel);

        }
        })); */



        // aggiungo il pulsante "Invia" per inviare i dati di autenticazione
        // al server


        buttonBar.add(new Button("Login", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                me = userName.getValue();
                mePasswd = userPwd.getValue();
                if (me != null) {
                    final AsyncCallback callback222 = new AsyncCallback() {

                        public void onSuccess(Object result) {
                            // lblServerReply.setText((String)result);
                            //            MessageBox.alert("Validate user and passwd", (String) result, null);
                            if (((Boolean) result).booleanValue()) {
                                whoAreYou.setText("Welcome " + me);
                                userPwd.setVisible(false);
                                userName.setVisible(false);
                                buttonBar.setVisible(false);
                                html.setVisible(true);
                                cp.setVisible(true);
                            } else {
                                whoAreYou.setText("INVALID USER");
                            }
                        }

                        public void onFailure(Throwable caught) {
                            //  lblServerReply.setText("Communication failed di RMI");
                            MessageBox.alert("Validate user and passwd", "FAIL", null);
                            whoAreYou.setText("INVALID USER");
                        }
                    };
                    getService().validateUser(me, mePasswd, callback222);
                } else {
                    whoAreYou.setText("INVALID USER");
                }
            }
        }));

        // aggiungo il pulsante "Cancella" per resettare il form
        buttonBar.add(new Button("Cancel", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                userName.reset();
            }
        }));

        // gestione login nuova 1-10-09
        String googleId = Window.Location.getParameter("gId");
        // String em = Window.Location.getParameter("openid.ext1.value.email");
        // MessageBox.alert("p = ", p, null);
        // MessageBox.alert("email = ", em, null);
        final AsyncCallback callbackLoginGoogle = new AsyncCallback() {

            public void onSuccess(Object result) {

                me = (String) result;
                whoAreYou.setText("Welcome " + me);
                // userPwd.setVisible(false);
                // userName.setVisible(false);
                // buttonBar.setVisible(false);
                html.setVisible(true);
                cp.setVisible(true);

            }

            public void onFailure(Throwable caught) {
                //  lblServerReply.setText("Communication failed di RMI");
                MessageBox.alert("Validate user and passwd", "FAIL", null);
                whoAreYou.setText("INVALID USER");
            }
        };
        getService().authenticate(googleId, callbackLoginGoogle);

        // fine login nuova

        html = new Html("<br/><b style='color:#15428B;'>Notifications: </b><br/><br/>");

        //   formPanel.add(userName);
        //   if (!alreadyLoggedIn) {
        //      formPanel.add(userPwd);
        //    } else {
        //       userPwd.setVisible(false);
        //   }
        formPanel.add(whoAreYou);
        //   formPanel.add(buttonBar);
        //   cp.setVisible(false);
        //   html.setVisible(false);
        formPanel.add(html);
        formPanel.add(cp);
        panel.add(formPanel);
        return panel;
    }
// aggiunge una riga x evento di Giga con annesso Confirm e Remove buttons

    void addMsgRow(String msg, String eventId) {
        //    final String quest = msg;
        final String evId = eventId;
        int row = msgTable.getRowCount();
        // msgTable.setWidth("550px");
        (msgTable.getFlexCellFormatter()).setWidth(row, 0, "120");
        //    msgTable.setText(row, 0, msg);
        //  msgTable.setText(row, 0, msg.substring(0, 200));
        msgTable.setHTML(row, 0, msg);

        final Button btnDelete = new Button("Delete");
        rimozioni.add(btnDelete);
        //fine gestione radiobuttons
        final AsyncCallback callback = new AsyncCallback() {

            public void onSuccess(Object result) {
                // lblServerReply.setText((String)result);
            }

            public void onFailure(Throwable caught) {
                //  lblServerReply.setText("Communication failed di RMI");
            }
        };
        // Listen for the button clicks : per inviare risposta utente

        btnDelete.addSelectionListener(new SelectionListener<ButtonEvent>() {
            //    btnDelete.addClickListener(new ClickListener() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                boolean selected = false;
                int i = 0;
                String risp = "";
                getService().sendEventToGiga(evId, risp, me, callback); //mandiamo risposta utente a GIga
                int removedIndex = rimozioni.indexOf(btnDelete);
                rimozioni.remove(removedIndex);
                msgTable.removeRow(removedIndex);
                // fine tentativo
            }
        });
        (msgTable.getFlexCellFormatter()).setWidth(row, 1, "30");
        msgTable.setWidget(row, 1, btnDelete);

    }
    /* forse da cancellare
    private void addLink(Object qwr) {
    int row = msgTable.getRowCount() - 1;
    final HTMLLink editLink = new HTMLLink();
    editLink.setTarget("_blank");
    editLink.setId("goglegroup");
    //  editLink.setHref("http://groups.google.com/group/Google-Web-Toolkit/topics");
    //  editLink.setHref(GWT.getHostPageBaseURL());
    editLink.setHref((String) qwr);
    editLink.setHTML("Action");
    // editLink.setHTML((String) qwr);
    msgTable.setWidget(row, 6, editLink);
    }
     */

    private void showEvents(Object qwr) {

        EventDescription[] eve = (EventDescription[]) qwr;

        for (int i = 0; i < eve.length; i++) {
            if (eve[i] == null) {
                return;
            }
            String linkSurvey = "<a href='http://localhost:8080/SurveyMgr/' target='_blank'>SurveyMgr</a>";
            if (eve[i].getDestinatario().equals(me)) {
                if ((eve[i].getEventName().equals("MeetingProposal"))) {
                    //addMsgRow(eve[i].getEventName() + "  " + eve[i].getParameter("Date") + "<br />Please connect to: " + linkSurvey + "<br />", eve[i].getEventId());
                    addMsgRow(eve[i].getEventName() + "  " + eve[i].getParameter("Date") + "<br />", eve[i].getEventId());
                }
                if ((eve[i].getEventName().equals("MeetingConfirmation"))) {
                    addMsgRow(eve[i].getEventName() + "  " + eve[i].getParameter("Date") + "<br />", eve[i].getEventId());
                }
                if ((eve[i].getEventName().equals("MembershipProposal"))) {
                    // addMsgRow(eve[i].getEventName() + "  group: " + eve[i].getParameter("groupName") + "<br />Please connect to: " + linkSurvey + "<br />", eve[i].getEventId());
                    addMsgRow(eve[i].getEventName() + "  group: " + eve[i].getParameter("groupName") + "<br />", eve[i].getEventId());
                }
                if ((eve[i].getEventName().equals("GroupCreated")) || (eve[i].getEventName().equals("GroupModified")) || (eve[i].getEventName().equals("GroupDeleted"))) {
                    addMsgRow(eve[i].getEventName() + "  group: " + eve[i].getParameter("groupName") + "<br />", eve[i].getEventId());
                }

                if ((eve[i].getEventName().equals("DocCreated")) || (eve[i].getEventName().equals("DocUpdated")) || (eve[i].getEventName().equals("DocRemoved"))) {
                    String linkDoc = "<a href='" + eve[i].getParameter("docLink") + "' target='_blank'>Document</a>";
                    //  addMsgRow(eve[i].getEventName() + " doc:  " + eve[i].getParameter("docName") + "  " + eve[i].getParameter("date") + "<br />Please connect to: " + linkDoc, eve[i].getEventId());
                    addMsgRow(eve[i].getEventName() + " doc:  " + eve[i].getParameter("docName") + "  " + eve[i].getParameter("date") + "<br />Please connect to: " + linkDoc, eve[i].getEventId());
                }

            }
        }

    }

    private void refreshMsgList() {
        final AsyncCallback callback4 = new AsyncCallback() {

            public void onSuccess(Object result) {
                showEvents(result);
            }

            public void onFailure(Throwable caught) {
                //   MessageBox.confirm("refreshMsgList ", "Communication failed con Giga", null);
            }
        };

        //   MessageBox.confirm("refreshMsgList ", "prima di getEvents , me = " + me, null);
        getService().getEvents(me, callback4);
    }

    public static GWTServiceAsync getService() {
        // Create the client proxy. Note that although you are creating the
        // service interface proper, you cast the result to the asynchronous
        // version of
        // the interface. The cast is always safe because the generated proxy
        // implements the asynchronous interface automatically.
        GWTServiceAsync service = (GWTServiceAsync) GWT.create(GWTService.class);

        // Specify the URL at which our service implementation is running.
        // Note that the target URL must reside on the same domain and port from
        // which the host page was served.
        //
        ServiceDefTarget endpoint = (ServiceDefTarget) service;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "gwtservice";
        //      groupManagerTarget.setServiceEntryPoint(moduleRelativeURL);


        endpoint.setServiceEntryPoint(moduleRelativeURL);
        return service;
    }
}
