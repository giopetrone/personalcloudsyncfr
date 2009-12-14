/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.com.google.gwt.sample.commoncalendar.server;

import giga.GigaListener;

import appsusersevents.client.ActivityDescription;
import appsusersevents.client.AndCondition;
import appsusersevents.client.ApplicationDescription;
import appsusersevents.client.Condition;
import appsusersevents.client.OrCondition;
import appsusersevents.client.SingleUser;
import appsusersevents.client.UserGroup;
import appsusersevents.client.Appointment;
import appsusersevents.client.CalendarOwner;
import appsusersevents.client.CloudUsers;
import appsusersevents.client.MyDate;
import appsusersevents.client.TreeElement;
import appsusersevents.client.TreeNode;

import calendar.CalendarCall;

import com.google.gdata.data.DateTime;
import com.google.gdata.data.calendar.CalendarEventEntry;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.GroupMembershipInfo;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.When;
import com.thoughtworks.xstream.XStream;

import googlecontacts.ContactCall;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author marino
 */
public class ServerToClient {

    //  static GigaListener listener = null;
    GigaListener listener = new GigaListener();
    CloudUsers cloudUsers = new CloudUsers();

    private SingleUser[] createUsers(ArrayList<ContactEntry> contacts) {
        SingleUser[] si = new SingleUser[contacts.size()];
        for (int i = 0; i < contacts.size(); i++) {
            ContactEntry entry = contacts.get(i);
            String emai = "";
            for (Email email : entry.getEmailAddresses()) {
                if (email.getPrimary()) {
                    emai = email.getAddress();
                    break;
                }
            }
            String nam = entry.getTitle().getPlainText();
            String id = entry.getId();
            SingleUser s = new SingleUser(nam, emai, "", id);
            si[i] = s;
        }
        return si;
    }

    private UserGroup addGroup(ContactGroupEntry gre, ArrayList<ContactEntry> contacts, SingleUser[] users) {
        String grid = gre.getId();
        UserGroup gru = new UserGroup(gre.getTitle().getPlainText(), grid);
        for (int i = 0; i < contacts.size(); i++) {
            ContactEntry co = contacts.get(i);
            for (GroupMembershipInfo group : co.getGroupMembershipInfos()) {
                String groupHref = group.getHref();
                if (groupHref.equals(grid)) {
                    gru.addChild(users[i]);
                }
            }
        }
        return gru;
    }

    public UserGroup[] buildGoogleGroupTree(SingleUser me) {
        //  ContactCall c = new ContactCall("sgnmrn@gmail.com", "micio11");
        //  ContactCall c = new ContactCall("gio.petrone@gmail.com", "mer20ia05");
        //   ContactCall c = new ContactCall("iceMgr09@gmail.com", "sync09fr");
        ContactCall c = new ContactCall(me.getMailAddress(), me.getPwd());
        ArrayList<ContactGroupEntry> groups = new ArrayList<ContactGroupEntry>(c.getUserGroups());
        ArrayList<ContactEntry> contacts = new ArrayList<ContactEntry>(c.getUserContacts());
        System.err.println("DIM=" + groups.size() + " " + contacts.size());
        SingleUser[] users = createUsers(contacts);
        UserGroup[] ret = new UserGroup[groups.size()];

        for (int i = 0; i < groups.size(); i++) {
            ContactGroupEntry gre = groups.get(i);
            UserGroup gru = addGroup(gre, contacts, users);
            ret[i] = gru;
        }
        return ret;
    }

    public SingleUser validateUser(String name, String pwd) {
        System.out.println("ServerTocli validateUser pwd = " + pwd);
        CalendarCall c = new CalendarCall();
        if (c.validate(name, pwd) != null) {
            return new SingleUser("", name, pwd, "");
        } else {
            return null;
        }
    }

    public void updateCalendars(CalendarOwner[] calendars, int rowIndex, SingleUser me, String title, int durata) {
        //    System.out.println("modifico calendario google: "+ rowIndex + " "+ calendars.length);
        long absTime = calendars[0].absoluteApptTime(rowIndex);
        //     System.out.println("modifico calendario google: "+ absTime);
        String pwdMeetingProposeUser = (cloudUsers.getUserByEmail(me.getMailAddress())).getPwd();
        for (int i = 0; i < calendars.length; i++) {
            CalendarOwner co = calendars[i];
            System.out.println("MODIFICO CALENDARIO GOOGLE google: " + me.getMailAddress() + " " + co.getMailAddress() + " " + co.getPwd() + "; ");

            new CalendarCall(me.getMailAddress(), pwdMeetingProposeUser, co.getMailAddress()).insertEvent(absTime, title, "CONTENUTO", durata);
        }
    }

    public CalendarOwner[] getCalendars(SingleUser sU, SingleUser[] users, MyDate startDate, MyDate endDate) {
        CalendarOwner[] cw = new CalendarOwner[users.length + 1];
        for (int i = 0; i < users.length; i++) {
            CalendarOwner calOwn = new CalendarOwner(users[i], startDate, endDate);
            loadAppts(sU, calOwn);//, false);
            cw[i] = calOwn;
        }
        //      System.err.println("torno da server");
        return cw;
    }

    private void updateMidnight(Calendar one, MyDate date) {
        one.set(Calendar.YEAR, date.getYear());
        // forse inutile????   one.set(Calendar.DAY_OF_YEAR,date.getDayOfYear());
        one.set(Calendar.DAY_OF_MONTH, date.getDayOfMonth());
        //      one.set(Calendar.DAY_OF_WEEK, date.getDayOfWeek());
        one.set(Calendar.MONTH, date.getMonth());
        one.set(Calendar.HOUR_OF_DAY, 0);
        one.set(Calendar.MINUTE, 0);
        one.set(Calendar.SECOND, 0);
        one.set(Calendar.MILLISECOND, 0);
    }
    // this code is not good, but we do not want to inject
    // any code dependencies in the client module
    // from whatever library we have

    void loadAppts(SingleUser sU, CalendarOwner co) { //, boolean special) {

        int oraInizioGiornata = CalendarOwner.getStartHour();
        int oraFineGiornata = CalendarOwner.getEndHour();
        MyDate startDate = co.getStartDate();
        MyDate endDate = co.getEndDate();
      
        Calendar calS = Calendar.getInstance();
        Calendar calE = Calendar.getInstance();

        Calendar calNight = Calendar.getInstance();
        updateMidnight(calNight, startDate);
        long partenza = startDate.getTime(); //calNight.getTimeInMillis();
        co.setTodayMidNight(partenza);
        Calendar calFine = Calendar.getInstance();
        updateMidnight(calFine, endDate);
        long fine = endDate.getTime(); //calFine.getTimeInMillis();
        int offsetFromStart = calNight.get(Calendar.DAY_OF_YEAR);
        List appts = null;
        // remote call to googgle calendar !!!
        appts = new CalendarCall(sU.getMailAddress(), sU.getPwd(), co.getMailAddress()).getCalendarEvents();
        System.out.println("carico appts: " + co.getMailAddress());
        Iterator it = appts.iterator();
        while (it.hasNext()) {
            //       MyCalendarEventEntry en = new MyCalendarEventEntry((CalendarEventEntry) it.next());
            CalendarEventEntry en = (CalendarEventEntry) it.next();
            List<When> lw = en.getTimes();
            DateTime s = null;
            DateTime e = null;
            //       System.err.println("CAL1");
            if (lw.size() > 0) {
                When wh = lw.get(0);
                s = wh.getStartTime();
                e = wh.getEndTime();
            }
            //  System.err.println("CAL112");
            if (s == null | e == null) {
                System.err.println("Appointment with no time:" + en.getTitle().getPlainText());
                continue;
            }
            //    System.err.println("CAL113");
            // if appt yesterday or later than shown  days continue
            if (e.getValue() < partenza || s.getValue() > fine) {

                continue;
            }
            //   System.err.println("CAL11");
            calS.setTimeInMillis(s.getValue());
            calE.setTimeInMillis(e.getValue());
            int giornoApp = calS.get(Calendar.DAY_OF_YEAR) - offsetFromStart;
            int oraInizio = calS.get(Calendar.HOUR_OF_DAY);
            int oraFine = calE.get(Calendar.HOUR_OF_DAY);
            System.err.println("CAL2 " + giornoApp + " " + oraInizio + " " + oraFine);
            if (oraInizio == oraFine) {
                oraFine++;
            }
            if (oraInizio >= oraFineGiornata || oraFine <= oraInizioGiornata) {
                continue;
            }
            //  System.err.println("calendario, ora inizio fine e giorno" + giornoApp + " " +
            //          oraInizio + " " + oraFine);
            //   System.err.println("CAL3: " + giornoApp);
            String tit = en.getTitle().getPlainText();
            Appointment appol = new Appointment(tit, giornoApp, oraInizio, oraFine);
            co.addImpegno(appol);
            System.err.println("fine carico CALENDARIO!!!");
        }
    }

}




