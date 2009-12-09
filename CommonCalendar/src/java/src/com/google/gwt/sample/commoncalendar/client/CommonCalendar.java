package src.com.google.gwt.sample.commoncalendar.client;

import appsusersevents.client.MyTreeItem;
import appsusersevents.client.EventDescription;
import appsusersevents.client.MyDate;
import appsusersevents.client.SingleUser;
import appsusersevents.client.TreeElement;
import appsusersevents.client.UserGroup;
//import ANNA& GIO
import com.extjs.gxt.ui.client.Style.HorizontalAlignment;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;
import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;

import com.extjs.gxt.ui.client.widget.form.DateField;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.TextField;
//GWT
import com.extjs.gxt.ui.client.widget.toolbar.ToolBar;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.ServiceDefTarget;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Label;
//FINE IMPORT ANNA e GIO
/*
import com.google.gwt.user.client.ui.Button;

import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;

import com.google.gwt.user.client.ui.Hyperlink;
import com.google.gwt.user.client.ui.KeyboardListenerAdapter;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
 */
import com.google.gwt.user.client.Window;

import com.google.gwt.user.client.ui.CheckBox;

import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.SourcesTableEvents;
import com.google.gwt.user.client.ui.TableListener;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;

import java.util.HashMap;
import java.util.ArrayList;
// 15-7-09 Potrebbe essere utile usare extended by com.extjs.gxt.ui.client.util.DateWrapper per evitare troppi deprecati ?
import java.util.Date;
import java.util.Iterator;

public class CommonCalendar implements EntryPoint {

    AsyncCallback putEventsCb = null;
    AsyncCallback getEventsCb = null;
    private FormPanel selectionPanel;
    //   private VerticalPanel vertPanel2 = new VerticalPanel();  Datepicker di marino
    //   private VerticalPanel panel2 = new VerticalPanel(); // MARINO
//    private VerticalPanel vertPanel3 = new VerticalPanel(); MARINO
    private Button addButton;  // Show possible meeting dates
    private DateField startDate;
    private DateField endDate;
    private TextBox duration;
//    private com.google.gwt.user.client.ui.Button creaTree = new com.google.gwt.user.client.ui.Button("get users and groups");
    private Label lblServerReply = new Label();
    // private Label msgLabel = new Label();
    private Html whoAreYou = new Html("<br/><b style='color:#15428B;'>Who Are You?</b><br/><br/>");
    private Html selectionUser = new Html("<br/><b style='color:#15428B;'>Please select users to be invited: </b><br/><br/>");
    private Html selectionDateStart = new Html("<br/><b style='color:#15428B;'>Set the meeting period:</b><br/><br/>");
    //private Html selectionDateEnd = new Html("<br/><b style='color:#15428B;'>Set the meeting period:</b><br/><br/>");
    private Html space = new Html("<br/><br/>");
    private TextField<String> userName;
    private TextField<String> userPwd;
    private static TextArea messaggio = new TextArea();
    //  private FlexTable msgTable = new FlexTable();
    private FlexTable calendarTable = new FlexTable();
    private Tree userTree = new Tree();
//    private Hyperlink changeViewLink = new Hyperlink("", "");
    private ArrayList<Widget> rimozioni = new ArrayList<Widget>();
    private static final int MSG_INTERVAL = 10000; // ms
    private MyDate oggi = null;
    private SingleUser me = null;
    private String thisApplication = "CommonCalendar";
    private ArrayList<MeetingSession> waitingSessions = new ArrayList();
    private String[] dayNumbers;
    private boolean big = true; //false
    private int settimaneInTabella = 6;
    private int mesiMostrati = 2;
    // variabili di ANNA E GIO
    private Panel panel;
    private FormPanel loginPanel;
    ToolBar buttonBar;
    MeetingSession session;
    //FINE VARIABILI DI ANNA E GIO

    public CommonCalendar() {
    }

    public static void debug(String s) {
        messaggio.setText(messaggio.getText() + "\n" + s);
    }

    public SingleUser getMe() {
        return me;
    }

    public void onModuleLoad() {

//???? che serve ???
        GWT.setUncaughtExceptionHandler(new GWT.UncaughtExceptionHandler() {

            public void onUncaughtException(Throwable throwable) {
                String text = "Uncaught exception: ";
                while (throwable != null) {
                    StackTraceElement[] stackTraceElements = throwable.getStackTrace();
                    text += throwable.toString() + "\n";
                    for (int i = 0; i < stackTraceElements.length; i++) {
                        text += "    at " + stackTraceElements[i] + "\n";
                    }
                    throwable = throwable.getCause();
                    if (throwable != null) {
                        text += "Caused by: ";
                    }
                }
                DialogBox dialogBox = new DialogBox(true);
                //       DOM.setStyleAttribute(dialogBox.getElement(), "backgroundColor", "#ABCDEF");
                DOM.setStyleAttribute(dialogBox.getElement(), "backgroundColor", "#CCFFFF");
                System.err.print(text);
                text = text.replaceAll(" ", " ");
                dialogBox.setHTML("<pre>" + text + "</pre>");
                dialogBox.center();
            }
        });
        // use a deferred command so that the handler catches onModuleLoad2() exceptions
        // A CHE SERVE ?
        /*
        DeferredCommand.addCommand(new Command() {

        public void execute() {
        //DISANBILITATO !!!!!!
        //    onModuleLoad2();

        /// FINE
        }
        });
         * */

        //   msgTable.setStyleName("tabellaMessaggi");
        calendarTable.setStyleName("calendari");
        messaggio.setCharacterWidth(40);
        messaggio.setVisibleLines(20);
        messaggio.setText("trace messages");
        addButton = new Button("Show possible meeting dates", new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                if (me != null) {
                    addCalendars();
                }
            }
        });

        //   startDate = new DatePicker();
        startDate = new DateField();
        startDate.setFieldLabel("from...");
        duration = new TextBox();
        duration.setText("1");
        endDate = new DateField();
        endDate.setFieldLabel("to...");
        // layout
        selectionPanel = new FormPanel();
        selectionPanel.add(whoAreYou);
        selectionPanel.add(selectionUser);
        selectionPanel.add(userTree);
        selectionPanel.add(selectionDateStart);
        selectionPanel.add(startDate);
//        selectionPanel.add(selectionDateEnd);
        selectionPanel.add(endDate);
        selectionPanel.add(new Label("duration"));
        selectionPanel.add(duration);
        selectionPanel.add(space);
        selectionPanel.add(addButton); //per possible meeting dates
        //   selectionPanel.add(messaggio);

        RootPanel.get().add(loginPanel()); // ANNA E GIO
        //           panel.add(selDatePanel);
        //       selDatePanel.setVisible(false);// ANNA E GIO
        selectionPanel.setVisible(false);// ANNA E GIO
        selectionPanel.setFrame(true);
        selectionPanel.setHeading("CommonCalendarMgr");
        selectionPanel.setBorders(false);
        selectionPanel.setPadding(5);
        selectionPanel.setWidth(620);
        selectionPanel.setLabelWidth(105);


        setTimers();

    }

    private void setTimers() {
        Timer msgTimer = new Timer() {

            public void run() {
                refreshMsgList();
                //    debug("in timer, me =" + me);
            }
        };
        msgTimer.scheduleRepeating(MSG_INTERVAL);

        Timer singleTimer = new Timer() {

            public void run() {
                getServerData();
            }
        };
        singleTimer.schedule(1000);
    }

    public static ICommonCalendarAsync getService() {
        // Create the client proxy. Note that although you are creating the
        // service interface proper, you cast the result to the asynchronous
        // version of
        // the interface. The cast is always safe because the generated proxy
        // implements the asynchronous interface automatically.
        ICommonCalendarAsync service = (ICommonCalendarAsync) GWT.create(ICommonCalendar.class);
        // Specify the URL at which our service implementation is running.
        // Note that the target URL must reside on the same domain and port from
        // which the host page was served.
        //
        ServiceDefTarget endpoint = (ServiceDefTarget) service;
        String moduleRelativeURL = GWT.getModuleBaseURL() + "commoncalendarservice";
        endpoint.setServiceEntryPoint(moduleRelativeURL);
        return service;
    }

    public void sendEvents(EventDescription[] eve) {
        if (putEventsCb == null) {
            putEventsCb = new AsyncCallback() {

                public void onSuccess(Object result) {
                    lblServerReply.setText("spedito evento");
                    nulla(result);
                }

                public void onFailure(Throwable caught) {
                    debug(caught.toString());
                    lblServerReply.setText("Communication failed");
                }
            };
        }
        getService().putEvents(eve, putEventsCb);
    }
    static boolean never = true;

    private void refreshMsgList() {
        if (never) {
            never = false;
            final AsyncCallback callback44 = new AsyncCallback() {

                public void onSuccess(Object result) {
                    debug("tabella success");
                    crea6Mesi(result);
                }

                public void onFailure(Throwable caught) {
                    debug(caught.toString());
                    debug("tabella Communication failed");
                }
            };
            getService().calendario6Mesi(callback44);
        }
        if (getEventsCb == null) {
            getEventsCb = new AsyncCallback() {

                public void onSuccess(Object result) {
                    lblServerReply.setText("refresh success");
                    showEvents(result);
                }

                public void onFailure(Throwable caught) {
                    debug(caught.toString());
                    lblServerReply.setText("refresh Communication failed");
                }
            };
        }
        getService().getEvents(getEventsCb);
    }

    private void getServerData() {
        final AsyncCallback callback44 = new AsyncCallback() {

            public void onSuccess(Object result) {
                debug("today success");
                settaOggi(result);
            }

            public void onFailure(Throwable caught) {
                debug(caught.toString());
                debug("today Communication failed");
            }
        };
        getService().cheGiornoOggi(callback44);
    }

    private void updatedCalendars(Object res) {
        debug("After calendar update");
    }

    private void nulla(Object res) {
        debug("Sono in NULLA");
    }
    static String[] sigleGiorni = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun", "...."};
    static String[] sigleMesi = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Now", "Dic"};
    private int row1 = -1;
    private int col1 = -1;
    private int row2 = -1;
    private int col2 = -1;
    private FlexTable aTable = new FlexTable();

    private void updateSelection(int row, int col) {
        debug("update row col" + row + " " + col);
        if (row > 1 && (col + 1) % 8 != 0) {
            Label lab = (Label) aTable.getWidget(row, col);
            if (lab.getText().trim().isEmpty()) {
                return;
            }
            //  DOM.setStyleAttribute(lab.getElement(), "backgroundColor", "#ABCDEF");
            DOM.setStyleAttribute(lab.getElement(), "backgroundColor", "#CCFFFF");

            // update selection interval
            if (row1 == -1) {
                row1 = row;
                col1 = col;
            } else if (row2 == -1) {
                row2 = row;
                col2 = col;
            } else {
                if (row == row1 && col == col1) {
                    // do not deselect
                } else if (row == row2 && col == col2) {
                    // do not deselect
                } else {
                    // delete old label color
                    lab = (Label) aTable.getWidget(row1, col1);
                    DOM.setStyleAttribute(lab.getElement(), "backgroundColor", "CCFFFF");
                    row1 = row2;
                    row2 = row;
                    col1 = col2;
                    col2 = col;
                }
            }

        }
    }

    private void crea6Mesi(Object qwr) {
        // the table contains :
        //one row for names of months
        // one for days of week
        // six for weeks
        // 48 columns: 8 for each month
        // first seven weekdays, number 8 left empty
// we assume that today is always present in the first month of the shown calendar
        dayNumbers = (String[]) qwr;
        aTable.addTableListener(new TableListener() {

            public void onCellClicked(SourcesTableEvents sender, int row, int col) {
                //  aTable.setText(row,cell,"OK");
                // cell selection only on date labels , not on text
                // tenere solo due celle selezionate, mostrare oggi
                updateSelection(row, col);
            }
        });
        int temporaneo = oggi.getMonth();// -1;
        for (int i = 0; i < mesiMostrati; i++) {
            aTable.setText(0, i * 8 + 3, sigleMesi[(temporaneo + i) % 12]);
            for (int j = 0; j < 8; j++) {
                aTable.setText(1, i * 8 + j, sigleGiorni[j]);
            }
            //   aTable.getCellFormatter().setWidth(0, i * 8 + 7, "40");
        }
        // tabella da mesiMostrati mesi in fila, a 7 colonne a mese
        //settimaneInTabella settimane a mese su righe successive
        int indice = 0;
        for (int i = 0; i < mesiMostrati; i++) {
            for (int k = 0; k < settimaneInTabella; k++) {
                for (int j = 0; j < 7; j++) {
                    String mos = dayNumbers[indice++];
                    aTable.setWidget(k + 2, i * 8 + j, new Label(mos));  // 8 per una colonna vuota 2 per titoli
                }
            }
        }
//        vertPanel2.add(aTable);
        // default: selectt calendar dates from today to 2 weeks
        String s = "" + oggi.getDayOfMonth();
        int j = 0;
        while (j < mesiMostrati * settimaneInTabella * 7) {
            String ss = dayNumbers[j];
            if (ss.equals(s)) {
                // found today position in widget
                updateSelection(getRigaWidget(j), getColonnaWidget(j));
                break;
            }
            j++;
        }
        //   int en = j + 15;
        //   debug("F"+ en +" "+ j);
        int limit = 14;
        while (limit > 0) {
            String ss = dayNumbers[++j];
            if (!ss.trim().isEmpty()) {
                //    debug("C"+ en +" "+ j + ss);
                limit--;// valid day
            }
        }
        //  debug("dueupd "+j);
        updateSelection(getRigaWidget(j), getColonnaWidget(j));
    }

    private int getColonnaWidget(int i) {
        return 8 * (i / 42) + (i % 42) % 7;
    }

    private int getRigaWidget(int i) {
        return (i % 42) / 7 + 2;
    }

    private int getColonnaGiorni(int i) {
        return i / 8 + i % 8;
    }

    private int getRigaGiorni(int i) {
        return i - 2;
    }

    private int posizGiorno(int row, int col) {
        // given row and col in widget,
        // return poszion in the days array
        row = getRigaGiorni(row);
        col = getColonnaGiorni(col);
        return 42 * (col / 7) + row * 7 + col % 7;
    }

    private void setDayOfYear(MyDate date, int row, int col) {
        String og = "" + oggi.getDayOfMonth();
        //  row -= 2;
        //  col = col / 8 + col % 8;
        int offset = 0;
        boolean found = false;
        int fine = posizGiorno(row, col);
        for (int i = 0; i < mesiMostrati; i++) {  // mesi
            for (int k = 0; k < settimaneInTabella; k++) { // settimane in un mese
                for (int j = 0; j < 7; j++) { // giorni settimana
                    int pos = i * settimaneInTabella * 7 + k * 7 + j;
                    String s = dayNumbers[pos];
                    if (!found) {
                        if (s.equals(og)) {
                            found = true;
                        }
                    } else {
                        // count good days from today
                        if (!s.trim().isEmpty()) {
                            offset++;
                        }
                        if (pos >= fine) {
                            date.setDayOfYear(oggi.getDayOfYear() + offset);
                            debug("setto data da oggi" + offset);
                            return;
                        }
                    }
                }
            }
        }
    }

    private MyDate dayOf(int row, int col) {
        if (row == -1) {
            return oggi;
        }
        // find correct rows and cols
        MyDate date = new MyDate();
        setDayOfYear(date, row, col);
        int mo = (col / 8);
        String sigla = aTable.getText(0, mo * 8 + 3);
        for (int i = 0; i < 12; i++) {
            if (sigla.equals(sigleMesi[i])) {
                date.setMonth(i + 1);
                break;
            }
        }
        String dm = ((Label) aTable.getWidget(row, col)).getText();
        Integer av = Integer.parseInt(dm);
        date.setDayOfMonth(av.intValue());
        date.setDayOfWeek(col % 8 + 1); // o +2   ???
// find if date is next year (after december)
        if (oggi.getMonth() > date.getMonth()) {
            date.setYear(oggi.getYear() + 1);
        } else {
            date.setYear(oggi.getYear());
        }
        return date;
    }

    private void showEvents(Object qwr) {
        // use this call for looking if any pending event
        // can be completed
        //   CommonCalendar.debug("show45");
        EventDescription[] eve = (EventDescription[]) qwr;

        for (int i = 0; i < eve.length; i++) {
            if (eve[i] == null) {
                return;
            }
            //   debug("showevents 0");
            // ANNA GIO commentiamo il display eventi
            //         addMsgRow(eve[i].getDescription());
            for (int j = 0; j < waitingSessions.size(); j++) {
                //  debug("showevents 00");
                MeetingSession sessio = waitingSessions.get(j);
                if (sessio.addRisposta(eve[i])) {

                    //  per ora DISATTIVATO!!!! sessio.setConfirmedColor(eve[i]);
                    //
                    //  MessageBox.alert("showevents 1 ricevo risposta!!! ",eD.getDescription(),null);
                    break;
                }
            }

        }
        //    MessageBox.alert("showevents 2 ricevo risposta!!! ",eD== null? "null":"non null",null);
        //     debug("showevents 1");
        for (int j = 0; j < waitingSessions.size(); j++) {
            MeetingSession v = waitingSessions.get(j);
            //      debug("showevents 1.1");
            if (v.confermato()) {
                sendEvents(v.getConfirmation()); // publish confirmation of meeting
                //    CalendarOwner.aggiorna(calendari, v.getDataMeeting() );
                final AsyncCallback callback33 = new AsyncCallback() {

                    public void onSuccess(Object result) {
                        lblServerReply.setText("modifico i calendari");
                        updatedCalendars(result);
                    }

                    public void onFailure(Throwable caught) {
                        debug(caught.toString());
                        lblServerReply.setText("Communication failed");
                    }
                };
                //    debug("showevents 2");
                getService().updateCalendars(v.getUserCalendars(), v.getIndexDataMeeting(), me, v.getTitle(), v.getMeetingLength(), callback33);
                //    debug("showevents 3");
                waitingSessions.remove(j);
                //  Window.alert("event confirmed: " + v.getEventDescription());

                //   String date = eD.getParameter("Date");

                MessageBox.alert("Meeting confirmed: ", v.getTemplate().getParameter("Date"), null);
            }
        }
    }

    private void settaOggi(Object qwr) {
        oggi = (MyDate) qwr;
    }

    private void addCalendars() {
        if (me == null) {
            return;
        }
        MyDate start = null;
        MyDate end = null;
        Date dStart = startDate.getValue();
        Date dEnd = endDate.getValue();
        int durata = 1;
        try {
            durata = Integer.parseInt(duration.getText());
        } catch (Exception ex) {
        }
        start = new MyDate(dStart.getTime());
        start.setDaysOfYear(0);
        start.setDayOfYear(0);
        start.setYear(dStart.getYear() + 1900);
        start.setMonth(dStart.getMonth());
        start.setDayOfWeek(dStart.getDay());
        start.setDayOfMonth(dStart.getDate());
        end = new MyDate(dEnd.getTime());
        end.setDaysOfYear(0);
        end.setDayOfYear(0);
        end.setYear(dEnd.getYear());
        end.setMonth(dEnd.getMonth());
        end.setDayOfWeek(dEnd.getDay());
        end.setDayOfMonth(dEnd.getDate());

        debug(start.getDescription() + "    " + end.getDescription());
        SingleUser[] selectedUsers = getSelectedUsers();
       
        if (selectedUsers.length > 0) {
            EventDescription template = new EventDescription("*");
            template.setUser(me.getMailAddress());
            template.setDestinatari(getDestinatari());
            template.setApplication(thisApplication);
            // set shperes (alias groups and usrs in the event
            // UserGroup.getUserMap(getDestinationUsers());
            template.setInvolvedUsers(UserGroup.getSingleUsers(getDestinationUsers()));
            template.setSpheres(UserGroup.getGroups(getDestinationUsers()));
            //MeetingSession session = new MeetingSession(this, template, selectedUsers, start, end, "Meeting proposal");
            session = new MeetingSession(this, template, selectedUsers, start, end, "Meeting proposal", durata);
            waitingSessions.add(session);
            //        panel2.add(session);  MARINO
            selectionPanel.setVisible(false);
            panel.add(session);
            session.setVisible(true);
            panel.add(backPanel());
        } else {
            Window.alert("no users selected!!!");
        }
    }

    private FormPanel backPanel() {

        final FormPanel bPanel = new FormPanel();
        bPanel.setFrame(true);

        bPanel.setBorders(false);
        bPanel.setPadding(5);
        // bPanel.setWidth(620);
        // bPanel.setLabelWidth(105);
        bPanel.setHeaderVisible(false);
        // aggiungo il pulsante "Back" per rtornare indieteo
        Button btnBack = new Button("Back");
        bPanel.add(space);
        bPanel.add(btnBack);

        bPanel.setVisible(true);

        final AsyncCallback callbackBack = new AsyncCallback() {

            public void onSuccess(Object result) {
                // lblServerReply.setText((String)result);
            }

            public void onFailure(Throwable caught) {
                //  lblServerReply.setText("Communication failed di RMI");
            }
        };

        btnBack.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                // Make remote call. Control flow will continue immediately and later
                // 'callback' will be invoked when the RPC completes.

                panel.remove(session);
                panel.remove(bPanel);
                selectionPanel.setVisible(true);


            }
        });
        return bPanel;
    }

    // per albero utenti da convocare
    private void buildUserTree() {
        final AsyncCallback callback2 = new AsyncCallback() {

            public void onSuccess(Object result) {
                debug("success");
                createItems2(result);
            }

            public void onFailure(Throwable caught) {
                debug("Communication failed");
            }
        };
        getService().getGroupTree(me, callback2);
    }

    private void createItems2(Object qwr) {
        UserGroup[] appe = (UserGroup[]) qwr;
        // create single root
        UserGroup roo = new UserGroup("gruppi", "");


        for (int i = 0; i < appe.length; i++) {
            roo.addChild(appe[i]);
        }
        roo.setMainGroup();
        MyTreeItem root = MyTreeItem.buildTree(roo, "users", MyTreeItem.STYLE_CALENDAR);

        userTree.addItem(root);
    }

    private void welcomeUser(Object qwr) {
        if (qwr != null) {
            me = (SingleUser) qwr;
            whoAreYou.setHtml("<br/><b style='color:#15428B;'>Welcome " + me.getMailAddress() + "</b><br/><br/>");
            buildUserTree();
            //    loginPanel.setVisible(false);// ANNA E GIO
            panel.remove(loginPanel);
            panel.add(selectionPanel);
            selectionPanel.setVisible(true);// ANNA E GIO
            // rendere visibile  canvasPanel o vertPanel1

        } else {
            //      userName.setText("INVALID USER");
            whoAreYou.setHtml("<br/><b style='color:#15428B;'>INVALID USER</b><br/><br/>");
            //   userPwd.setVisible(true);
            //  userName.setVisible(true);
            //  buttonBar.setVisible(true);
        }
    }

    private SingleUser findUserOld(String name) {
        Iterator<TreeItem> it = userTree.treeItemIterator();
        while (it.hasNext()) {
            MyTreeItem item = (MyTreeItem) it.next();
            TreeElement t = item.getContent();
            if (t.getClass() == SingleUser.class) {
                if (((SingleUser) t).getMailAddress().equals(name)) {
                    //  Class cl = ((Object) t).getClass();
                    // if (cl == SingleUser.class) {
                    return (SingleUser) t;
                    //  }
                }
            }
        }
        return null;
    }

    // interesting problem: how to get a minimal group
    // of destinations? maybe better
    // to set that in the tree selection process
    private ArrayList<TreeElement> getDestinationUsers() {
// return selected user and groups  with duplicates
        ArrayList ar = new ArrayList();
        Iterator<TreeItem> it = userTree.treeItemIterator();
        while (it.hasNext()) {
            MyTreeItem item = (MyTreeItem) it.next();
            CheckBox c = (CheckBox) item.getWidget();
            if (c.isChecked()) {
                TreeElement t = item.getContent();
                ar.add(t);
            }
        }
        return ar;
    }

    private ArrayList<String> getDestinatari() {
        // return a list of email for singleusers
        // name for groups
        ArrayList<TreeElement> ppp = getDestinationUsers();
        return UserGroup.getUserAndGroups(ppp);
    }

    private SingleUser[] getSelectedUsers() {
        // convenience method to return array of users
        HashMap<String, SingleUser> allUsers = UserGroup.getUserMap(getDestinationUsers());
        debug("ret length = " + allUsers.size());
        SingleUser[] ret = new SingleUser[allUsers.size()];
        Iterator<SingleUser> ite = allUsers.values().iterator();
        int j = 0;
        while (ite.hasNext()) {
            ret[j++] = ite.next();
        }
        return ret;
    }
    //iniziano metodi nuovi ANNA e GIO

    private Panel loginPanel() {
        panel = new VerticalPanel();
        loginPanel = new FormPanel();
        loginPanel.setFrame(true);
        loginPanel.setHeading("CommonCalendarMgr");
        loginPanel.setBorders(false);
        loginPanel.setPadding(5);
        loginPanel.setWidth(620);
        loginPanel.setLabelWidth(105);
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
        //  buttonBar.setCellSpacing(20);
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
     /*   buttonBar.add(new Button("Login", new SelectionListener<ButtonEvent>() {

        @Override
        public void componentSelected(ButtonEvent ce) {
        final AsyncCallback callback222 = new AsyncCallback() {

        public void onSuccess(Object result) {
        debug("success");
        welcomeUser(result);

        }

        public void onFailure(Throwable caught) {
        debug("Communication failed");
        }
        };
        getService().validateUser(userName.getValue(), userPwd.getValue(), callback222);

        }
        }));*/

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
                me = (SingleUser) result;
                whoAreYou.setHtml("<br/><b style='color:#15428B;'>Welcome " + me.getMailAddress() + "</b><br/><br/>");
                welcomeUser(result);
                // userPwd.setVisible(false);
                // userName.setVisible(false);
                // buttonBar.setVisible(false);

            }

            public void onFailure(Throwable caught) {
                //  lblServerReply.setText("Communication failed di RMI");
                MessageBox.alert("Validate user and passwd", "FAIL", null);
            }
        };
        getService().authenticate(googleId, callbackLoginGoogle);

        // fine login nuova
        Html html =
                new Html(
                "<br/><b style='color:#15428B;'>Please choose: </b><br/><br/>");

        //  loginPanel.add(userName);
        //  loginPanel.add(userPwd);
        //  loginPanel.add(buttonBar);


        panel.add(loginPanel);
        return panel;
    }
}
