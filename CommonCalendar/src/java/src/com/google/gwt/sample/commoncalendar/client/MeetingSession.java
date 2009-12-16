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
import com.extjs.gxt.ui.client.widget.form.TextField;
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
    private String[] freeTimes = null;
    private CommonCalendar originator;
    private String title;
    private SingleUser organizerUser;
    private int meetingLength = 1;
    private  int[] sceglibili;
    TextField<String> meetingTitle;

    public boolean addRisposta(EventDescription evt) {

        // remove a recipient each time a confirmation is received
        // template contains this user!!!

     //   CommonCalendar.debug("in meetingsession.addrisposta:");
      //  MessageBox.alert("Add risposta I", "", null);
        //   CommonCalendar.debug("\t" + evt.getDestinatario() + " " + template.getUser());        
     //   CommonCalendar.debug("\t" + evt.getSessionId() + " " + sessionId);
        // if the destination is this user AND the answer
        // relates to this meeting proposal, check if answe == yes
        //   if (evt.getDestinatari().contains(getTemplate().getUser()) &&
        if (evt.getSessionId().equals(sessionId)) {
               String answ = evt.getParameter("Answer");
                //CommonCalendar.debug("\t" + "Yes" + " " + answ);
             //   MessageBox.alert("add risposta  II", "", null);
                if (answ.equalsIgnoreCase("Yes")) {
                    return risposte.remove(evt.getUser());
                } else {
                  //  CommonCalendar.debug("utente ha rsiposto no o errore: " + answ);
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
        return getTemplate().getDescription();
    }

    public EventDescription[] getConfirmation() {
        for (int k = 0; k < publishedEvents.length; k++) {
            // MessageBox.alert("MEETING CONFIRMATION", "", null);
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
            // des.setDestinatario(tr.getMailAddress()); GIO

            des.setEventName("MeetingProposal");
            des.setUser(getTemplate().getUser());
            des.setApplication(getTemplate().getApplication());
            //commentato da gio
            des.setDestinatari(getTemplate().getDestinatari());// NUOVO, MAR
            // des.addDestinatario(tr.getMailAddress()); //??? GIO
            des.setSessionId(sessionId);
            //    des.setCorrelationId("*");
            des.setParameters(getTemplate().getParameters());
            CommonCalendar.debug("ev: " + des.getDescription());
        }
        return publishedEvents;
    }

    public int getIndexDataMeeting() {
        return rowIndex;
    }

    private void initialize(Object qwr, int hours) {
        this.setCalendari((CalendarOwner[]) qwr);
        createFreeTimes();
        createPossibleList(3, hours);
        showCompleteButton.addClickListener(new ClickListener() {

            public void onClick(Widget sender) {
                //add(calendarTable);
            }
        });
        // add(espressione);
        //   add(showCompleteButton);
        buildDateChooser(3);
    }

    public MeetingSession(CommonCalendar originator, EventDescription startingEvent, SingleUser[] selectedUsers,
            MyDate startDate, MyDate endDate, String title, int meetingLength) {
        this.originator = originator;
        this.template = startingEvent;
        this.meetingLength = meetingLength;
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
                initialize(result, getMeetingLength());
            }

            public void onFailure(Throwable caught) {
                CommonCalendar.debug("Communication failed while retrieving calendars");
                CommonCalendar.debug(caught.toString());

            }
        };
        CommonCalendar.getService().getCalendars(organizerUser, selectedUsers, startDate, endDate, callback77);
    }

    private void createPossibleList(int appointments, int hours) {

        sceglibili = new int[appointments];
        for (int i = 0, j = 0; i < freeTimes.length; i++) {
            if (freeTimes[i].equals("")) {
                // can be chosen as possible meeting date            
                boolean ok = true;
                    // long meeting?
                if (hours > 1) {
                    for (int k = 1; k < hours; k++) {
                        if (!freeTimes[i + k].equals("")) {
                            ok = false;
                            break;
                        }
                    }
                }
                if (ok) {
                    espressione.addItem(trovaGiorno(i) + ";" + ore[i]);
                    sceglibili[j] = i;
                    j++;
                }
            }
        }

    }

    private void createFreeTimes() {
        freeTimes = new String[settimane.length];
        for (int j = 0; j < freeTimes.length; j++) {
            freeTimes[j] = "";
        }
        String tutti = "";
        for (int i = 0; i < getCalendari().length - 1; i++) {
            CalendarOwner co = getCalendari()[i];
            String[] occup = co.creaMat();
            for (int j = 0; j < occup.length; j++) {
                String oc = occup[j];
                tutti += oc + "\n";
                if (!oc.equals("")) {
                    freeTimes[j] = "OCC";
                }
            }
        }
        // Window.alert(tutti);
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
        this.title = meetingTitle.getValue();
        getTemplate().setParameter("Date", messageParam);

        getTemplate().setParameter("Duration", "" + meetingLength);

        getTemplate().setParameter("Title", title);  //GIO

        originator.sendEvents(getProposal());
        //Window.alert("Proposed meeting date" + meetingIndex);
        MessageBox.alert("Proposed meeting date", "", null);
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

        meetingTitle = new TextField<String>();
        meetingTitle.setAllowBlank(false);
        meetingTitle.setFieldLabel("Meeting Titile");
        final Radio[] buttonsArray = new Radio[numberOfDates];
        final RadioGroup radioG = new RadioGroup();

        radioG.setOrientation(Style.Orientation.VERTICAL);

        radioG.setFieldLabel("options");
        final FormPanel buttonsPanel = new FormPanel();

        buttonsPanel.setHeading("CommonCalendarMgr");

        buttonsPanel.setStyleAttribute("backgroundColor", "#DFE8F6");
        // #CCFFFF

        for (int i = 0;
                i < numberOfDates;
                i++) {
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
        buttonsPanel.add(meetingTitle);
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

                risp = ((Radio) radioG.getValue()).getFieldLabel();

                //per mandare evento a giga
                int appIndex = (new Integer(risp)).intValue();
                //    createClickCall(appIndex, trovaGiorno(appIndex) + ";" + ore[appIndex]);
                createClickCall(sceglibili[appIndex], espressione.getItemText(appIndex));

            }
        });

    }

    /**
     * @return the template
     */
    public EventDescription getTemplate() {
        return template;
    }

    /**
     * @param template the template to set
     */
    public void setTemplate(EventDescription template) {
        this.template = template;
    }

    public int getMeetingLength() {
        return meetingLength;
    }
}
