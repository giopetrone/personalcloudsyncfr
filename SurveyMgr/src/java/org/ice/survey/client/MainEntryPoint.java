/*
 * MainEntryPoint.java
 *
 * Created on March 18, 2009, 3:11 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.ice.survey.client;

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

import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;

import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
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

//    private VerticalPanel mainPanel = new VerticalPanel(); //gio
    private Panel panel;
    final private Label whoAreYou = new Label(""); // per mostrare chi e' loggato al
    private TextField<String> userName;
    private TextField<String> userPwd;
    private ContentPanel cp;
    private Html html;
//    private static TextArea messaggio = new TextArea();  // area che permette di stampare  eventuali stackTrace di errore
    private Label msgLabel = new Label();  // label introduttiva, Please answer
    // private TextBox txtUserInput = new TextBox();   //input Text  area sotto la label "Input yor text" x inviare eventi a Giga
    private FlexTable msgTable = new FlexTable();  //area per visualizzazione eventi  di Giga
    private ArrayList<Widget> rimozioni = new ArrayList<Widget>(); //area di widgets per eliminare eventi  di Giga
    //da modified COmmCAl
    private String me = null;
    private String mePasswd = null;
    private boolean debugOn = true;  // per mostrare pannello con stampe di Debug

    // End CommCal
    /** Creates a new instance of MainEntryPoint */
    public MainEntryPoint() {
    }
    /*
    public static void debug(String s) {
    messaggio.setText(messaggio.getText() + "\n" + s);
    }
     */

    /**
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {

        RootPanel.get().add(survey());


        if (debugOn) {
//            mainPanel.add(messaggio);  // area che permette di stampare  eventuali stackTrace di errore
        }
        //       RootPanel.get().add(mainPanel);
        // setup timer to refresh list automatically MARINO
        Timer msgTimer = new Timer() {

            public void run() {
                refreshMsgList();
            }
        };
        msgTimer.scheduleRepeating(10000);

    }

    private Panel survey() {
        panel = new VerticalPanel();
        FormPanel formPanel = new FormPanel();
        formPanel.setFrame(true);
        formPanel.setHeading("SurveyMgr");
        formPanel.setBorders(false);
        formPanel.setPadding(5);
        formPanel.setWidth(280);
        formPanel.setLabelWidth(105);
        //login
        userName = new TextField<String>();
        userName.setAllowBlank(false);
        userName.setFieldLabel("Who Are You?");
        userPwd = new TextField<String>();
        userPwd.setAllowBlank(false);
        userPwd.setFieldLabel("Password");
        userPwd.setPassword(true);
        //end login fields
        cp = new ContentPanel();
        cp.setHeading("Survey Manager");
        cp.setHeaderVisible(false);
        //    msgTable.setBorderWidth(1);
        cp.add(msgTable);
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
        /*
        buttonBar.add(new Button("Close", new SelectionListener<ButtonEvent>() {

        @Override
        public void componentSelected(ButtonEvent ce) {
        RootPanel.get().remove(panel);

        }
        }));

         */
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
                userPwd.reset();
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
        html =
                new Html(
                "<br/><b style='color:#15428B;'>Please answer: </b><br/><br/>");
// gestione login, sostituito da openId
//        formPanel.add(userName);
//        formPanel.add(userPwd);
        formPanel.add(whoAreYou);
//        formPanel.add(buttonBar);

        //    html.setVisible(false);
        //    cp.setVisible(false);
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
//        msgTable.setWidth("550px");
        (msgTable.getFlexCellFormatter()).setWidth(row, 0, "80");
        msgTable.setHTML(row, 0, msg);
        //     msgTable.setText(row, 0, msg.substring(0, 25));
        final String[] answers = new String[2];
        //  final RadioButton radioButton0;
        //  final RadioButton radioButton1;
        final Radio radioButton0;
        final Radio radioButton1;
        answers[0] = "Yes";
        answers[1] = "No";
        //      radioButton0 = new RadioButton("answer", answers[0]);
        //    radioButton1 = new RadioButton("answer", answers[1]);
        radioButton0 = new Radio();
        radioButton0.setBoxLabel(answers[0]);
        radioButton0.setName(answers[0]);
        radioButton0.setValue(false);
        radioButton1 = new Radio();
        radioButton1.setBoxLabel(answers[1]);
        radioButton1.setName(answers[1]);
        radioButton1.setValue(false);

        final Button btnSendAnswer = new Button("Answer");
        rimozioni.add(btnSendAnswer);
        //fine gestione radiobuttons
        final AsyncCallback callback = new AsyncCallback() {

            public void onSuccess(Object result) {
                // lblServerReply.setText((String)result);
            }

            public void onFailure(Throwable caught) {
                //  lblServerReply.setText("Communication failed di RMI");
            }
        };
        // Listen for the button clicks : per inviare risposta utente TEMP GIO


        btnSendAnswer.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                // Make remote call. Control flow will continue immediately and later
                // 'callback' will be invoked when the RPC completes.
                boolean selected = false;
                int i = 0;
                String risp = "";
                // MARINO: il while dovrebbe scorrere la lista dei radiobutton : COME FARE ??
                while (!selected && i < answers.length) {
                    if (radioButton0.getValue()) {
                        selected = true;
                        risp = radioButton0.getName();
                    }
                    if (radioButton1.getValue()) {
                        selected = true;
                        risp = radioButton1.getName();
                    }
                }
                //debug("SURVEY: sono prima di sendEventToGiga");
                getService().sendEventToGiga(evId, risp, me, callback); //mandiamo risposta utente a GIga
                //            getService().sendEventToGiga(quest, risp, me, callback); //mandiamo risposta utente a GIga
                //rimozione riga dell'evento dopo aver risposto
                int removedIndex = rimozioni.indexOf(btnSendAnswer);
                rimozioni.remove(removedIndex);
                msgTable.removeRow(removedIndex);
                // fine rimozione
            }
        });
        (msgTable.getFlexCellFormatter()).setWidth(row, 1, "30");
        (msgTable.getFlexCellFormatter()).setWidth(row, 2, "30");
        (msgTable.getFlexCellFormatter()).setWidth(row, 3, "40");
        msgTable.setWidget(row, 1, radioButton0);
        msgTable.setWidget(row, 2, radioButton1);
        msgTable.setWidget(row, 3, btnSendAnswer);
    }

    private void refreshMsgList() {
        final AsyncCallback callback4 = new AsyncCallback() {

            public void onSuccess(Object result) {
                msgLabel.setText("Please, answer :");
                showEvents(result);
            }

            public void onFailure(Throwable caught) {
                msgLabel.setText("Communication failed con Giga");
                //   debug(caught.getMessage());
            }
        };
        getService().getEvents(me, callback4);
    }

    private void showEvents(Object qwr) {
        EventDescription[] eve = (EventDescription[]) qwr;
        //    debug("in showEvents   me = " + me + " leng = " + eve.length);
        for (int i = 0; i < eve.length; i++) {
            if (eve[i] == null) {
                return;
            }
            //      debug("SURVEY: sono in showtEvents prima di addMsgROW " + eve[i].getEventName() + " dest = " + eve[i].getDestinatario() + " eventId =  " + eve[i].getEventId());
            if (eve[i].getDestinatari().contains(me)) {
                if (eve[i].getEventName().equals("MeetingProposal")) {
                    addMsgRow(eve[i].getEventName() + "  " +eve[i].getParameter("Title")  + " when: " + eve[i].getParameter("Date") + "<br />", eve[i].getEventId());
                }
                if ((eve[i].getEventName().equals("MembershipProposal"))) {
                    addMsgRow(eve[i].getEventName() + "  group: " + eve[i].getParameter("groupName") + "<br />", eve[i].getEventId());
                }

            }
        }
    }

    public static GWTServiceSurveyAsync getService() {
        // Create the client proxy. Note that although you are creating the
        // service interface proper, you cast the result to the asynchronous
        // version of
        // the interface. The cast is always safe because the generated proxy
        // implements the asynchronous interface automatically.
        GWTServiceSurveyAsync service = (GWTServiceSurveyAsync) GWT.create(GWTServiceSurvey.class);
        // Specify the URL at which our service implementation is running.
        // Note that the target URL must reside on the same domain and port from
        // which the host page was served.
        //
        ServiceDefTarget endpoint = (ServiceDefTarget) service;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "gwtservicesurvey";
        endpoint.setServiceEntryPoint(moduleRelativeURL);
        return service;
    }
}
