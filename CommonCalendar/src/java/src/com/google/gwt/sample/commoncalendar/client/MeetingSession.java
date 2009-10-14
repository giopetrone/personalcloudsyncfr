/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.com.google.gwt.sample.commoncalendar.client;

import appsusersevents.client.CalendarOwner;
import appsusersevents.client.EventDescription;
import appsusersevents.client.MyDate;
import appsusersevents.client.SingleUser;
import com.extjs.gxt.ui.client.Style;
import com.extjs.gxt.ui.client.event.ButtonEvent;
import com.extjs.gxt.ui.client.event.SelectionListener;
import com.extjs.gxt.ui.client.widget.Html;

import com.extjs.gxt.ui.client.widget.MessageBox;
import com.extjs.gxt.ui.client.widget.button.Button;
import com.extjs.gxt.ui.client.widget.form.FormPanel;
import com.extjs.gxt.ui.client.widget.form.Radio;
import com.extjs.gxt.ui.client.widget.form.RadioGroup;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.rpc.AsyncCallback;
//import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;
import java.util.HashSet;

/* formato eventi
 *
 * inviati:
 *   Proposta di riunione
 *     Destinatario
EventName("MeetingProposal")
User
Application("CommonCalendar")
CorrelationId("CorrelationId")
Parameters("Date")
 *
 * conferma di riunione
 *
 * EventName("MeetingConfirmation")
 *
 *
 * ricevuti:
 *
 *  Application("SurveyMgr")
 *  EventName("MeetingAnswer")
 * User( chi ha mandato la risposta )
 *  Destinatario( io)
 * Paramaters( "Yes" oppure "No" seguiro da quelli vecchi
 *
 *
 * */
/**
 *
 * @author marino
 */
public class MeetingSession extends VerticalPanel {

    final ListBox espressione = new ListBox();
    private com.google.gwt.user.client.ui.Button showCompleteButton = new com.google.gwt.user.client.ui.Button("Show complete calendars");
    private FlexTable calendarTable = new FlexTable();
    private CalendarOwner[] calendari = null;
    private String sessionId = "" + hashCode();
    private HashSet<String> risposte = new HashSet();
    private SingleUser[] receivers = null;
    private EventDescription template = null;
    private EventDescription[] publishedEvents = null;
    private int rowIndex = 0;
    private MyDate startDate = null;
    private MyDate endDate = null;
    private String[] settimane = null;
    private String[] ore = null;
    private CommonCalendar originator;
    private String title;
    private SingleUser organizerUser;

    public boolean addRisposta(EventDescription evt) {

        // remove a recipient each time a confirmation is received
        // template contains this user!!!

        CommonCalendar.debug("in meetingsession.addrisposta:");
        CommonCalendar.debug("\t" + evt.getDestinatario() + " " + template.getUser());
        CommonCalendar.debug("\t" + evt.getSessionId() + " " + sessionId);
        // if the destination is this user AND the answer
        // relates to this meeting proposal, check if answe == yes
        if (evt.getDestinatario().equals(template.getUser()) &&
                evt.getSessionId().equals(sessionId)) {
            ArrayList params = evt.getParameters();
            if (!params.isEmpty()) {
                String answ = evt.getParameter("Answer");
                CommonCalendar.debug("\t" + "Yes" + " " + answ);
                if (answ.equalsIgnoreCase("Yes")) {
                    return risposte.remove(evt.getUser());
                } else {
                    CommonCalendar.debug("utente ha rsiposto no o errore: " + answ);
                }
            }
        }
        return false;
    }

    public boolean confermato() {
        //   return true;
        return risposte.size() == 0;
    }

    public int getMissingConfirmations() {
        return risposte.size();
    }

    public String getEventDescription() {
        return template.getDescription();
    }

    public EventDescription[] getConfirmation() {
        for (int k = 0; k < publishedEvents.length; k++) {
            publishedEvents[k].setEventName("MeetingConfirmation");
        }
        return publishedEvents;
    }

    public EventDescription[] getProposal() {
        // at startup, create list of events
        // new version; only a single event is published
        // and the destinatari arraylist
        // contains the string of all the users and groups
        // to wich the proposal is sent
     //   publishedEvents = new EventDescription[receivers.length];
        publishedEvents = new EventDescription[1]; // NUOVO, MAR
     //   for (int k = 0; k < receivers.length; k++) {
        for (int k = 0; k < 1; k++) { // NUOVO, MAR
            SingleUser tr = receivers[k];
            EventDescription des = new EventDescription("*");
            publishedEvents[k] = des;
            des.setDestinatario(tr.getMailAddress());
            des.setEventName("MeetingProposal");
            des.setUser(template.getUser());
            des.setApplication(template.getApplication());
            des.setDestinatari(template.getDestinatari());// NUOVO, MAR
            des.setSessionId(sessionId);
            //    des.setCorrelationId("*");
            des.setParameters(template.getParameters());
            CommonCalendar.debug("ev: " + des.getDescription());
        }
        return publishedEvents;
    }

    public int getIndexDataMeeting() {
        return rowIndex;
    }

    private void initialize(Object qwr) {
        this.setCalendari((CalendarOwner[]) qwr);
        createCalendarTable();
        createPossibleList(5);
        showCompleteButton.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                //add(calendarTable);
            }
        });
        // add(espressione);
        //   add(showCompleteButton);
        buildDateChooser(5);
    }

    public MeetingSession(CommonCalendar originator, EventDescription startingEvent, SingleUser[] selectedUsers,
            MyDate startDate, MyDate endDate, String title) {
        this.originator = originator;
        this.template = startingEvent;
        this.receivers = selectedUsers;
        this.startDate = startDate;
        this.endDate = endDate;
        this.settimane = CalendarOwner.creaSettimane(startDate, endDate);
        this.ore = CalendarOwner.creaOre();
        this.title = title;
        this.organizerUser = originator.getMe();
        for (int i = 0; i < selectedUsers.length; i++) {
            SingleUser su = selectedUsers[i];
            risposte.add(su.getMailAddress());
        }


        // Create an asynchronous callback to handle the result.
        final AsyncCallback callback77 = new AsyncCallback() {

            public void onSuccess(Object result) {
                CommonCalendar.debug("success");
                initialize(result);
            }

            public void onFailure(Throwable caught) {
                CommonCalendar.debug("Communication failed while retrieving calendars");
                CommonCalendar.debug(caught.toString());

            }
        };
        CommonCalendar.getService().getCalendars(organizerUser, selectedUsers, startDate, endDate, callback77);
    }

    private void createPossibleList(int appointments) {
        int rows = calendarTable.getRowCount();
        int cols = calendarTable.getCellCount(0);


        final int[] sceglibili = new int[appointments];
        for (int i = 1, j = 0; i < rows && j < appointments; i++) {
            Widget w = calendarTable.getWidget(i, cols);
            if (w != null) {
                // can be chosen as possible meeting date
                espressione.addItem(trovaGiorno(i) + ";" + ore[i]);
                sceglibili[j] = i;
                j++;
            }
        }
        espressione.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                int index = espressione.getSelectedIndex();

                int appIndex = sceglibili[index];
                //  debug("click0" + rowIndex);
                createClickCall(appIndex, trovaGiorno(appIndex) + ";" + ore[appIndex]);
            }
        });
        espressione.setVisibleItemCount(appointments);
    }

    private void createCalendarTable() {
        for (int j = 0; j < settimane.length; j++) {
            String s1 = settimane[j];
            String s2 = ore[j];
            calendarTable.setText(j + 1, 0, s1);
            calendarTable.setText(j + 1, 1, s2);
        }

        calendarTable.setBorderWidth(2);
        String[] common = new String[settimane.length];

        for (int j = 0; j < common.length; j++) {

            common[j] = "________";
        }
        // last position is common calendar
        // fill column for each user
        for (int i = 0; i < getCalendari().length - 1; i++) {
            CalendarOwner co = getCalendari()[i];
            Label lab = new Label(co.getName());
            calendarTable.setWidget(0, i + 2, lab);
            String[] occup = co.creaMat();
            for (int j = 0; j < occup.length; j++) {
                String oc = occup[j];
                if (!oc.equals(" ")) {
                    common[j] = "OCC";
                }
                calendarTable.setText(j + 1, i + 2, oc);
            }
        }
        // fill in common calendar
        for (int j = 0; j < common.length; j++) {
            String ss = common[j];
            final int meetingIndex = j;
            if (ss.equals("OCC")) {
                calendarTable.setText(j + 1, (getCalendari().length - 1) + 2, ss);
            } else {
                com.google.gwt.user.client.ui.Button cho = new com.google.gwt.user.client.ui.Button("Choose");
                cho.addClickListener(new ClickListener() {

                    public void onClick(Widget sender) {
                        createClickCall(meetingIndex, trovaGiorno(meetingIndex) + ";" + ore[meetingIndex]);
                    }
                });
                calendarTable.setWidget(j + 1, (getCalendari().length - 1) + 2, cho);
            }
        }
    }

    private String trovaGiorno(int index) {
        for (; index >= 0; index--) {
            //   debug("'"+ settimane[index]+"'");
            if (!settimane[index].trim().isEmpty()) {
                // now get month and day of month and yaer!!!!
                MyDate data = new MyDate(startDate, index / 10);
                String datina = data.stampina();
                return settimane[index] + " " + datina;
            }
        }
        return "UNKNOWN DAY";
    }

    private void createClickCall(int meetingIndex, String messageParam) {

        if (receivers.length == 0) {
            return;
        }
        espressione.setEnabled(false);
        // set the position of appointment in the calendar table for retrieving later day and time
        this.rowIndex = meetingIndex;

        template.setParameter("Date", messageParam);
        originator.sendEvents(getProposal());
         MessageBox.alert("Proposed meeting date",  "", null);
    }

    public void setConfirmedColor(EventDescription eve) {
        CommonCalendar.debug("colori 0");
        for (int i = 0; i < getCalendari().length - 1; i++) {
            CalendarOwner co = getCalendari()[i];
            // controllare!!!!
            CommonCalendar.debug("NICK " + co.getName() + " " + eve.getUser());
            if (co.getMailAddress().equals(eve.getUser())) {
                Label lab = (Label) calendarTable.getWidget(0, i + 2);
                CommonCalendar.debug("colori 1");
                //DOM.setStyleAttribute(lab.getElement(), "backgroundColor", "#ABCDEF");
                DOM.setStyleAttribute(lab.getElement(), "backgroundColor", "#DFE8F6");
                return;
            }
        }
    }

    /**
     * @return the calendari
     */
    public CalendarOwner[] getCalendari() {
        return calendari;
    }

    public CalendarOwner[] getUserCalendars() {
        CalendarOwner[] ret = new CalendarOwner[calendari.length - 1];
        for (int i = 0; i < ret.length; i++) {
            ret[i] = calendari[i];
        }
        return ret;
    }

    /**
     * @param calendari the calendari to set
     */
    public void setCalendari(CalendarOwner[] calendari) {
        this.calendari = calendari;
    }

    public String getTitle() {
        return title;
    }

    //per radio button di scelte data
    private void buildDateChooser(int numberOfDates) {
        //    final String quest = msg;
        Html space = new Html("<br/>");

        final Radio[] buttonsArray = new Radio[numberOfDates];
        final RadioGroup radioG = new RadioGroup();
        radioG.setOrientation(Style.Orientation.VERTICAL);
        radioG.setFieldLabel("options");

        final FormPanel buttonsPanel = new FormPanel();
        buttonsPanel.setHeading("CommonCalendarMgr");
        buttonsPanel.setStyleAttribute("backgroundColor", "#DFE8F6");
        // #CCFFFF
        for (int i = 0; i < numberOfDates; i++) {
            buttonsArray[i] = new Radio();
            buttonsArray[i].setFieldLabel((new Integer(i)).toString());
          //       buttonsArray[i].setFieldLabel("opzione " + (i + 1));
            buttonsArray[i].setName((new Integer(i)).toString());
            buttonsArray[i].setBoxLabel(espressione.getItemText(i));
            buttonsArray[i].setValue(false);
            //buttonsPanel.add(buttonsArray[i]);
            radioG.add(buttonsArray[i]);
            buttonsPanel.add(radioG);
        }

        final Button btnSendDate = new Button("Send date");
        buttonsPanel.add(space);
        buttonsPanel.add(btnSendDate);
        add(buttonsPanel);
        buttonsPanel.setVisible(true);
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

        btnSendDate.addSelectionListener(new SelectionListener<ButtonEvent>() {

            @Override
            public void componentSelected(ButtonEvent ce) {
                // Make remote call. Control flow will continue immediately and later
                // 'callback' will be invoked when the RPC completes.
               String risp = "";

              risp = ((Radio)radioG.getValue()).getFieldLabel();

                //per mandare evento a giga
                int appIndex = (new Integer(risp)).intValue();
                createClickCall(appIndex, trovaGiorno(appIndex) + ";" + ore[appIndex]);

            }
        });


    }
}
