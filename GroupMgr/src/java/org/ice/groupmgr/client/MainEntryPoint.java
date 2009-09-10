/*
 * MainEntryPoint.java
 *
 * Created on May 19, 2009, 12:41 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package org.ice.groupmgr.client;

import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.Style.Orientation;
import com.extjs.gxt.ui.client.dnd.ListViewDragSource;
import com.extjs.gxt.ui.client.dnd.ListViewDropTarget;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.ComponentEvent;
import com.extjs.gxt.ui.client.event.Listener;
import com.extjs.gxt.ui.client.event.SelectionChangedEvent;
import com.extjs.gxt.ui.client.event.SelectionChangedListener;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.store.ListStore;
import com.extjs.gxt.ui.client.store.StoreSorter;
import com.extjs.gxt.ui.client.util.Margins;
import com.extjs.gxt.ui.client.widget.ContentPanel;
import com.extjs.gxt.ui.client.widget.Dialog;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.ListView;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
//import com.extjs.gxt.ui.client.widget.button.ButtonBar;
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.extjs.gxt.ui.client.widget.form.ComboBox;
import com.extjs.gxt.ui.client.widget.form.ComboBox.TriggerAction;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
import com.extjs.gxt.ui.client.widget.layout.RowData;
import com.extjs.gxt.ui.client.widget.layout.RowLayout;
//GWT
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;


import java.util.List;

/**
 *
 * @author giovanna
 */
public class MainEntryPoint implements EntryPoint {

    private ContentPanel buttonPanel;
    private MyServiceProvider provider = new MyServiceProvider();
    private ListView<ContattoModelData> listaContatti;
    private ListStore<ContattoModelData> storeContatti;
    private ListView<ContattoModelData> listaContattiGruppo;
    private ListStore<ContattoModelData> storeContattiGruppo;
    private ComboBox<GruppoModelData> comboGrouppi;
    private ListStore<GruppoModelData> storeGruppi;
    private TextField<String> nomeGruppoField;
    private Panel panel;
    final private Label whoAreYou = new Label(""); // per mostrare chi e' loggato al
    private TextField<String> userName;
    private TextField<String> userPwd;
    ToolBar buttonBar;
    //   String iceMgrLogin = "gio.petrone@gmail.com";
    String groupMakerLogin = "";  // fino a che dalla UI di groupMgr non si riesce a fare login
    String groupMakerPwd = "";

    /** Creates a new instance of MainEntryPoint */
    public MainEntryPoint() {
    }

    /**
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {

        provider.getParameter("p");
        RootPanel.get().add(loginPanel());

//per prendere eventi
        Timer msgTimer = new Timer() {

            public void run() {
                provider.getEvents(groupMakerLogin);
            }
        };
        msgTimer.scheduleRepeating(10000);


        //     RootPanel.get().aodd(buttonPanel);
    }

    private Panel loginPanel() {
        panel = new VerticalPanel();
        FormPanel formPanel = new FormPanel();
        formPanel.setFrame(true);
        formPanel.setHeading("GroupMgr");
        formPanel.setBorders(false);
        formPanel.setPadding(5);
        formPanel.setWidth(620);
        formPanel.setLabelWidth(105);
        //login area
        userName = new TextField<String>();
        userName.setAllowBlank(false);
        userName.setFieldLabel("Who Are You?");
        userPwd = new TextField<String>();
        userPwd.setAllowBlank(false);
        userPwd.setFieldLabel("Password");
        userPwd.setPassword(true);
        // creo la barra dei pulsanti del form
        buttonBar = new ToolBar();
        buttonBar.setAlignment(HorizontalAlignment.LEFT);

        // buttonBar.setButtonAlign(HorizontalAlignment.LEFT);
        // buttonBar.setCellSpacing(20);   // SERVE ????????
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
//inizio di v
        buttonBar.add(new Button("Login", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                groupMakerLogin = userName.getValue();
                groupMakerPwd = userPwd.getValue();

                if (groupMakerLogin != null) {
                    whoAreYou.setText("Welcome " + groupMakerLogin);
                    userPwd.setVisible(false);
                    userName.setVisible(false);
                    buttonBar.setVisible(false);
                    provider.validateUser(groupMakerLogin, groupMakerPwd);
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

        formPanel.add(userName);
        formPanel.add(userPwd);
        formPanel.add(whoAreYou);
        formPanel.add(buttonBar);
        // end login area        
        formPanel.add(initialSelectionButtons());
        panel.add(formPanel);
        return panel;
    }

    private ContentPanel initialSelectionButtons() {
        Html html = new Html("<br/><b style='color:#15428B;'>Please choose: </b><br/><br/>");
        buttonPanel = new ContentPanel();
        buttonPanel.setWidth(340);
        buttonPanel.setHeading("group manager");
        buttonPanel.setHeaderVisible(false);
        buttonPanel.setVisible(false);
        buttonPanel.add(html);
        Button nuovoGruppoButton = new Button("New group", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                RootPanel.get().remove(panel);
                RootPanel.get().add(nuovoGruppo());
                //  buttonPanel.setVisible(false);
            }
        });

        Button modificaGruppoButton = new Button("Modify/Delete a group", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                RootPanel.get().remove(panel);
                RootPanel.get().add(modificaEliminaGruppo());
                //  buttonPanel.setVisible(false);
            }
        });
        buttonPanel.addButton(nuovoGruppoButton);
        buttonPanel.addButton(modificaGruppoButton);
        return buttonPanel;
    }

    private Panel back() {
        panel = new VerticalPanel();
        FormPanel formPanel = new FormPanel();
        formPanel.setFrame(true);
        formPanel.setHeading("GroupMgr");
        formPanel.setBorders(false);
        formPanel.setPadding(5);
        formPanel.setWidth(620);
        formPanel.setLabelWidth(105);
        formPanel.add(initialSelectionButtons());
        buttonPanel.setVisible(true);
        panel.add(formPanel);
        return panel;
    }

    private Panel nuovoGruppo() {
        panel = new VerticalPanel();

        FormPanel formPanel = new FormPanel();
        formPanel.setFrame(true);
        formPanel.setHeading("New Group");
        formPanel.setBorders(false);
        formPanel.setPadding(5);
        formPanel.setWidth(520);
        formPanel.setLabelWidth(105);
        nomeGruppoField = new TextField<String>();
        nomeGruppoField.setAllowBlank(false);
        nomeGruppoField.setFieldLabel("Group name");
        ContentPanel cp = new ContentPanel();
//      cp.setHeading("Nuovo Gruppo");
        cp.setHeaderVisible(false);
        cp.setSize(500, 225);
        cp.setFrame(true);
        cp.setBorders(false);
        cp.setLayout(new RowLayout(Orientation.HORIZONTAL));
        listaContatti = new ListView<ContattoModelData>();
        listaContatti.setSimpleTemplate("{cognome} {nome}");
        listaContatti.setTitle("Contact list");
        storeContatti = new ListStore<ContattoModelData>();
        storeContatti.setStoreSorter(new StoreSorter<ContattoModelData>());
        provider.getContatti();

        listaContattiGruppo = new ListView<ContattoModelData>();
        listaContattiGruppo.setSimpleTemplate("<img src='images/statoConferma/{statoConferma}.png'/> " +
                "{cognome} {nome}");
        listaContattiGruppo.setTitle("Membri del gruppo");
        storeContattiGruppo = new ListStore<ContattoModelData>();
        storeContattiGruppo.setStoreSorter(new StoreSorter<ContattoModelData>());
        listaContattiGruppo.setStore(storeContattiGruppo);

        new ListViewDragSource(listaContatti);
        new ListViewDragSource(listaContattiGruppo);
        new ListViewDropTarget(listaContatti);
        new ListViewDropTarget(listaContattiGruppo);

        RowData data = new RowData(.5, 0.99);
        data.setMargins(new Margins(5));
        data.setHeight(210);
        cp.add(listaContatti, data);
        cp.add(listaContattiGruppo, data);

        // creo la barra dei pulsanti del form
        ToolBar buttonBar = new ToolBar();
        buttonBar.setAlignment(HorizontalAlignment.CENTER);
        /*
        buttonBar.add(new Button("Close", new SelectionListener<ButtonEvent>() {
        @Override
        public void componentSelected(ButtonEvent ce) {
        RootPanel.get().remove(panel);
        buttonPanel.setVisible(true);
        }
        }));
         */
        // aggiungo il pulsante "Save" per inviare i dati del gruppo nuovo
        // al server
        buttonBar.add(new Button("Save", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!storeContattiGruppo.getModels().isEmpty()) {
                    provider.creaGruppo(nomeGruppoField.getValue(), storeContattiGruppo.getModels());
                } else {
                    MessageBox.alert("New group",
                            "Add at least 1 contact to the group!", null);
                }
            }
        }));

        // aggiungo il pulsante "Cancella" per resettare il form
        buttonBar.add(new Button("Cancel", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                nomeGruppoField.reset();
                provider.getContatti();
            }
        }));

        // aggiungo il pulsante "Back" per rtornare indieteo
        buttonBar.add(new Button("Back", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                RootPanel.get().remove(panel);
                RootPanel.get().add(back());
            }
        }));

        Html html =
                new Html(
                "<br/><b style='color:#15428B;'>To add a contact to the group , please drag it from the list on the left to the one on the right </b><br/><br/>");

        formPanel.add(nomeGruppoField);
        formPanel.add(html);
        formPanel.add(cp);
        formPanel.setBottomComponent(buttonBar);
        panel.add(formPanel);
        return panel;
    }

    private Panel modificaEliminaGruppo() {
        panel = new VerticalPanel();
        FormPanel formPanel = new FormPanel();
        formPanel.setFrame(true);
        formPanel.setHeading("Modify/Delete group");
        formPanel.setBorders(false);
        formPanel.setPadding(5);
        formPanel.setWidth(520);
        formPanel.setLabelWidth(120);
        ContentPanel cp = new ContentPanel();
        cp.setHeaderVisible(false);
        cp.setSize(500, 225);
        cp.setFrame(true);
        cp.setBorders(false);
        cp.setLayout(new RowLayout(Orientation.HORIZONTAL));
        listaContatti = new ListView<ContattoModelData>();
        listaContatti.setSimpleTemplate("{cognome} {nome}");
        listaContatti.setTitle("Contact list");
        storeContatti = new ListStore<ContattoModelData>();
        storeContatti.setStoreSorter(new StoreSorter<ContattoModelData>());
        listaContattiGruppo = new ListView<ContattoModelData>();
        listaContattiGruppo.setSimpleTemplate(
                "<img src='images/statoConferma/{statoConferma}.png'/> " +
                "{cognome} {nome}");
        listaContattiGruppo.setTitle("Group members");
        storeContattiGruppo = new ListStore<ContattoModelData>();
        storeContattiGruppo.setStoreSorter(new StoreSorter<ContattoModelData>());
        listaContattiGruppo.setStore(storeContattiGruppo);

        new ListViewDragSource(listaContatti);
        new ListViewDragSource(listaContattiGruppo);

        new ListViewDropTarget(listaContatti);
        new ListViewDropTarget(listaContattiGruppo);

        RowData data = new RowData(.5, 0.99);
        data.setMargins(new Margins(5));
        data.setHeight(210);

        cp.add(listaContatti, data);
        cp.add(listaContattiGruppo, data);
        storeGruppi = new ListStore<GruppoModelData>();
        comboGrouppi = new ComboBox<GruppoModelData>();
        comboGrouppi.setFieldLabel("Select a group");
        comboGrouppi.setDisplayField("nome");  //FIORE
        comboGrouppi.setWidth(300); //era 150 di fiore
        comboGrouppi.setStore(storeGruppi);
        comboGrouppi.setTriggerAction(TriggerAction.ALL);

        final Listener eliminaGruppoListener = new Listener<ComponentEvent>() {

            public void handleEvent(ComponentEvent ce) {
                provider.eliminaGruppo(comboGrouppi.getValue().getNome(), comboGrouppi.getValue().getId());
                /* PROVVISORIO gio 27-8-09
                if (((Dialog) ce.getComponent()).getButtonPressed().getText().equals("Yes")) {
                provider.eliminaGruppo(comboGrouppi.getValue().getNome(), comboGrouppi.getValue().getId());
                } else {
                }
                 */
            }
        };

        final Button eliminaGruppoButton = new Button("Delete group", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                MessageBox.confirm("Delete group", "Confirm deletion of the group?", eliminaGruppoListener);
            }
        });
        eliminaGruppoButton.setEnabled(false);

        // creo la barra dei pulsanti del form
        ToolBar buttonBar = new ToolBar();
        buttonBar.setAlignment(HorizontalAlignment.CENTER);
        // buttonBar.setCellSpacing(20);

        /*     buttonBar.add(new Button("Close", new SelectionListener<ButtonEvent>() {

        @Override
        public void componentSelected(ButtonEvent ce) {
        RootPanel.get().remove(panel);
        buttonPanel.setVisible(true);
        }
        }));
         */
        buttonBar.add(eliminaGruppoButton);
        // aggiungo il pulsante "Invia" per inviare i dati di autenticazione
        // al server
        buttonBar.add(new Button("Save changes", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (!storeContattiGruppo.getModels().isEmpty()) {
                    provider.modificaGruppo(comboGrouppi.getValue().getNome(), comboGrouppi.getValue().getId(), storeContattiGruppo.getModels());
                    //         Integer.parseInt(comboGrouppi.getValue().getId()),  // DA MODIFICARE IN STRING
                } else {
                    MessageBox.alert("Modify group",
                            "Add at least one contact to the group!", null);
                }
            }
        }));
        // aggiungo il pulsante "Cancella" per resettare il form
        buttonBar.add(new Button("Cancel", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (comboGrouppi.getValue().getId() != null) {

                    provider.getMembriGruppo(comboGrouppi.getValue().getId());
                }
            }
        }));

        // aggiungo il pulsante "Back" per rtornare indieteo
        buttonBar.add(new Button("Back", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                RootPanel.get().remove(panel);
                RootPanel.get().add(back());
            }
        }));


        comboGrouppi.addSelectionChangedListener(new SelectionChangedListener<GruppoModelData>() {

            public void selectionChanged(
                    SelectionChangedEvent<GruppoModelData> se) {
                if (se.getSelection().size() > 0) {
                    provider.getMembriGruppo(se.getSelectedItem().getId());
                    listaContatti.setEnabled(true);
                    listaContattiGruppo.setEnabled(true);
                    eliminaGruppoButton.setEnabled(true);
                }
            }
        });
        provider.getGruppi();

        Html html =
                new Html(
                "<br/><b style='color:#15428B;'>To add a contact to the group, please drag it from the list on the left to the one on the right; to delete a contact  from the group drag it from the list to the right to the on on the left </b><br/><br/>");
        formPanel.add(comboGrouppi);
        formPanel.add(html);
        formPanel.add(cp);
        formPanel.setBottomComponent(buttonBar);
        panel.add(formPanel);
        return panel;
    }

    /**
     * Classe che permette di gestire tutte le RPC del gadget.
     */
    public class MyServiceProvider {

        public GWTServiceAsync groupManagerService;

        public MyServiceProvider() {
            groupManagerService = (GWTServiceAsync) GWT.create(GWTService.class);
            ServiceDefTarget groupManagerTarget = (ServiceDefTarget) groupManagerService;
            String moduleRelativeURL = GWT.getModuleBaseURL() + "gwtservice";
            groupManagerTarget.setServiceEntryPoint(moduleRelativeURL);
        }

        public  void getParameter(String par) {
            groupManagerService.getParameter(par, new AsyncCallback() {

                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void onSuccess(Object result) {
                    // qui il codice per gestire la getEvents
                    //   MessageBox.alert("getEvents", "nella onSuccess  " , null);
                }
            });
        }

        public void getEvents(String user) {
            groupManagerService.getEvents(user, new AsyncCallback() {

                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void onSuccess(Object result) {
                    // qui il codice per gestire la getEvents
                    //   MessageBox.alert("getEvents", "nella onSuccess  " , null);
                }
            });
        }

        public void validateUser(String name, String pwd) {
            groupManagerService.validateUser(name, pwd, new AsyncCallback() {

                public void onSuccess(Object result) {
                    // lblServerReply.setText((String)result);
                    //            MessageBox.alert("Validate user and passwd", (String) result, null);
                    if (((Boolean) result).booleanValue()) {
                        whoAreYou.setText("Welcome " + groupMakerLogin);
                        userPwd.setVisible(false);
                        userName.setVisible(false);
                        buttonBar.setVisible(false);
                        buttonPanel.setVisible(true);

                    } else {
                        whoAreYou.setText("INVALID USER");
                        userPwd.setVisible(true);
                        userName.setVisible(true);
                        buttonBar.setVisible(true);
                    }
                }

                public void onFailure(Throwable caught) {
                    //  lblServerReply.setText("Communication failed di RMI");

                    whoAreYou.setText("INVALID USER");
                    userPwd.setVisible(true);
                    userName.setVisible(true);
                    buttonBar.setVisible(true);
                }
            });

        }

        public void getContatti() {
            groupManagerService.getContatti(new AsyncCallback() {

                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void onSuccess(Object result) {
                    storeContatti.removeAll();
                    storeContatti.add((List<ContattoModelData>) result);
                    listaContatti.setStore(storeContatti);
                    storeContattiGruppo.removeAll();
                    listaContattiGruppo.setStore(storeContattiGruppo);
                }
            });
        }

        public void getMembriGruppo(String groupId) {
            //     groupManagerService.getMembriGruppo(Integer.parseInt(groupId), new AsyncCallback() { FIORE
            groupManagerService.getMembriGruppo(groupId, new AsyncCallback() {

                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void onSuccess(Object result) {
                    List[] liste = (List[]) result;
                    storeContatti.removeAll();
                    storeContatti.add(liste[1]);
                    listaContatti.setStore(storeContatti);
                    storeContattiGruppo.removeAll();
                    storeContattiGruppo.add(liste[0]);
                    //   MessageBox.alert("getMembriGruppo", "size contatti gruppo  " + liste[0].size(), null);
                    listaContattiGruppo.setStore(storeContattiGruppo);
                }
            });
        }

        public void getGruppi() {
            groupManagerService.getGruppi(new AsyncCallback() {

                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void onSuccess(Object result) {

                    /*
                    for ( GruppoModelData gmd : (List<GruppoModelData>)result ) {
                    MessageBox.alert("Error",                          "gmd name =  " +                         gmd.get("nome") + "    gmd id =    " +gmd.get("id") , null);
                    } */
                    comboGrouppi.setValue(null);
                    storeGruppi.removeAll();
                    storeGruppi.add((List<GruppoModelData>) result);
                }
            });
        }

        public void creaGruppo(String nomeGruppo, List<ContattoModelData> contatti) {
            groupManagerService.creaGruppo(nomeGruppo, contatti, new AsyncCallback() {

                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void onSuccess(Object result) {

                    if ((Boolean) result) {
                        nomeGruppoField.reset();
                        getContatti();
                        MessageBox.info("New group", "Group proposed to members.", null);
                    } else {
                        MessageBox.alert("Error",
                                "An error occurred! " +
                                "A group with the same name might exist.", null);
                    }
                }
            });
        }
        // public void eliminaGruppo(String nomeGruppo, int idGruppo) {

        public void eliminaGruppo(String nomeGruppo, String idGruppo) {
            groupManagerService.eliminaGruppo(nomeGruppo, idGruppo, true, new AsyncCallback() {

                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void onSuccess(Object result) {
                    storeContatti.removeAll();
                    listaContatti.setStore(storeContatti);
                    storeContattiGruppo.removeAll();
                    listaContattiGruppo.setStore(storeContattiGruppo);

                    if (!(Boolean) result) {
                        MessageBox.alert("Error",
                                "An error occurred!", null);
                    }
                    getGruppi();
                }
            });
        }

        //        public void modificaGruppo(String nomeGruppo, int idGruppo, FIORE
        public void modificaGruppo(String nomeGruppo, String idGruppo, List<ContattoModelData> contatti) {
            groupManagerService.modificaGruppo(nomeGruppo, idGruppo, contatti, new AsyncCallback() {

                public void onFailure(Throwable caught) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }

                public void onSuccess(Object result) {
                    if ((Boolean) result) {
                        getGruppi();
                        storeContatti.removeAll();
                        listaContatti.setStore(storeContatti);
                        storeContattiGruppo.removeAll();
                        listaContattiGruppo.setStore(storeContattiGruppo);
                    } else {
                        MessageBox.alert("Error",
                                "An error occurred!", null);
                    }
                }
            });
        }
    }
}
