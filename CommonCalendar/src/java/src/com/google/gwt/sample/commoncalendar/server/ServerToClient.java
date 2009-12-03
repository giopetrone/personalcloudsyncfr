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
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

/**
 *
 * @author marino
 */
public class ServerToClient {

    //  static GigaListener listener = null;
    GigaListener listener = new GigaListener();
    CloudUsers cloudUsers = new CloudUsers();

    public ApplicationDescription[] buildAppTree(String str) {
        ArrayList applications = new ArrayList();
        if (str != null) {
            XStream xstream = new XStream();
            applications = (ArrayList) xstream.fromXML(str);
        } else {
            ApplicationDescription a = new ApplicationDescription("Calendar");
            applications.add(a);
            ActivityDescription ad = new ActivityDescription("insertAppt");
            a.addChild(ad);
            ad.addEvent("completed");
            ad.setCondition(buildFakeCondition(null));
            a = new ApplicationDescription("Calendar1");
            applications.add(a);
            ad = new ActivityDescription("insertAppt1");
            a.addChild(ad);
            ad.addEvent("completed1");
            a = new ApplicationDescription("Calendar2");
            applications.add(a);
            ad = new ActivityDescription("insertAppt2");
            a.addChild(ad);
            ad.addEvent("completed2");

        }
        XStream xstream = new XStream();
        String appStrings = xstream.toXML(applications);
        System.out.println("applications:" + "\n\n" + appStrings + "\n\n");
        // conversione ad array 
        int sz = applications.size();
        ApplicationDescription[] ret = new ApplicationDescription[sz];
        for (int i = 0; i < sz; i++) {
            ret[i] = (ApplicationDescription) applications.get(i);
        }
        return ret;
    }

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

    public UserGroup[] buildGroupTree(String str) {
        ArrayList groups = new ArrayList();

        Collections s;
        if (str != null) {
            XStream xstream = new XStream();
            groups = (ArrayList) xstream.fromXML(str);
        } else {
            UserGroup a = new UserGroup("seta", "");
            groups.add(a);
            SingleUser ad = new SingleUser("mar", "sgnmrn@gmail.com", "micio11", "");
            a.addChild(ad);

            ad = new SingleUser("gio", "gio.petrone@gmail.com", "mer20ia05", "");
            a.addChild(ad);

            a = new UserGroup("diamond", "");
            groups.add(a);
            ad = new SingleUser("luca");

            a.addChild(ad);
            ad = new SingleUser("anna");

            a.addChild(ad);
            a = new UserGroup("prin", "");
            groups.add(a);
            ad = new SingleUser("claud");
            a.addChild(ad);
            ad = new SingleUser("furn");
            a.addChild(ad);

        }
        sortGroups(groups);
        XStream xstream = new XStream();
        String appStrings = xstream.toXML(groups);
        System.out.println("groups:" + "\n\n" + appStrings + "\n\n");
        // conversione ad array
        int sz = groups.size();
        UserGroup[] ret = new UserGroup[sz];
        for (int i = 0; i < sz; i++) {
            ret[i] = (UserGroup) groups.get(i);
        }
        return ret;
    }

    private void sortGroups(ArrayList groups) {
        UserComparator comparator = new UserComparator();
        Collections.sort(groups, comparator);
        HashMap<String, TreeElement> allUs = new HashMap();
        boolean foundAll = false;
        for (int i = 0; i < groups.size(); i++) {
            TreeNode t = (TreeNode) groups.get(i);
            if (t.getName().equals("ALL")) {
                foundAll = true;
            }
            ArrayList<TreeElement> chil = t.getChildren();
            Collections.sort(chil, comparator);
            for (int j = 0; j < chil.size(); j++) {
                TreeElement elem = chil.get(j);
                allUs.put(elem.getName(), elem);
            }
        }
        // if group "All" misssing, create it
        if (!foundAll) {
            ArrayList us = new ArrayList(allUs.values());
            Collections.sort(us, comparator);
            UserGroup a = new UserGroup("ALL", "");
            a.setChildren(us);
            groups.add(a);
        }
    }

    public Condition buildFakeCondition(String str) {
        OrCondition orcond = null; //= new Condition();
        if (str != null) {
            XStream xstream = new XStream();
            orcond = (OrCondition) xstream.fromXML(str);
        } else {
            orcond = new OrCondition();
            AndCondition andc = new AndCondition();
            orcond.addCondition(andc);
            andc.addEvent("completed");

            andc = new AndCondition();
            orcond.addCondition(andc);
            andc.addEvent("completed3");
            andc.addEvent("completed4");
        }
        return orcond;
    }

    public void updateCalendars(CalendarOwner[] calendars, int rowIndex, SingleUser me) {
        //    System.out.println("modifico calendario google: "+ rowIndex + " "+ calendars.length);
        long absTime = calendars[0].absoluteApptTime(rowIndex);
        //     System.out.println("modifico calendario google: "+ absTime);
        String pwdMeetingProposeUser = (cloudUsers.getUserByEmail(me.getMailAddress())).getPwd();
        for (int i = 0; i < calendars.length; i++) {
            CalendarOwner co = calendars[i];
            System.out.println("modifico calendario google: " + co.getMailAddress() + " " + co.getPwd());

            new CalendarCall(me.getMailAddress(), pwdMeetingProposeUser, co.getMailAddress()).insertEvent(absTime, "TITOLO", "CONTENUTO");
        }
    }

    public CalendarOwner[] getCalendars(SingleUser sU, SingleUser[] users, MyDate startDate, MyDate endDate) {
        CalendarOwner[] cw = new CalendarOwner[users.length + 1];
        for (int i = 0; i < users.length; i++) {
            CalendarOwner calOwn = new CalendarOwner(users[i], startDate, endDate);
            loadAppts(sU, calOwn, false);
            cw[i] = calOwn;
        }
        CalendarOwner calo = new CalendarOwner(null, startDate, endDate);
        loadAppts(sU, calo, true);
        cw[users.length] = calo;
        System.err.println("torno da server");
        return cw;
    }

    /*
    public CalendarOwner[] getCalendarsFissiPerProvaOLD(SingleUser[] users) {
    CalendarOwner[] cw = new CalendarOwner[3];
    CalendarOwner calo = new CalendarOwner("Libero", "Orario", null);
    CalendarOwner cal1 = new CalendarOwner("sgnmrn@gmail.com", "micio11");
    CalendarOwner cal2 = new CalendarOwner("gio.petrone@gmail.com", "mer20ia05");
    cw[0] = cal1;
    cw[1] = cal2;
    cw[2] = calo;
    loadAppts(calo, true);
    loadAppts(cal1, false);
    loadAppts(cal2, false);
    return cw;
    }
     */

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

    void loadAppts(SingleUser sU, CalendarOwner co, boolean special) {

        /*  if (special) {
        return;

        }*/
        int oraInizioGiornata = CalendarOwner.getStartHour();
        int oraFineGiornata = CalendarOwner.getEndHour();
        MyDate startDate = co.getStartDate();
        MyDate endDate = co.getEndDate();
        //    Calendar calNow = Calendar.getInstance();


        //  co.setToday(oggi * 24 * 3600 * 1000);
        //     int giornoSett = calNow.get(Calendar.DAY_OF_WEEK);
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
        if (special) {
            appts = new ArrayList();
        } else {
            // remote call to googgle calendar !!!
          //  appts = new CalendarCall(sU.getMailAddress(), sU.getPwd()).getCalendarEvents();
   // CloudUsers cloud = new CloudUsers();
   // SingleUser uss = cloud.getUserByEmail(co.getMailAddress());
            
         //   appts = new CalendarCall(co.getMailAddress(), uss.getPwd()).getCalendarEvents();
             appts = new CalendarCall(sU.getMailAddress(), sU.getPwd(),co.getMailAddress()).getCalendarEvents();
        }
        Iterator it = appts.iterator();

        while (it.hasNext()) {
            //       MyCalendarEventEntry en = new MyCalendarEventEntry((CalendarEventEntry) it.next());
            CalendarEventEntry en = (CalendarEventEntry) it.next();
            List<When> lw = en.getTimes();
            DateTime s = null;
            DateTime e = null;
            System.err.println("CAL1");
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
            //   System.err.println("CAL2");
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

 void loadApptsSAVE(SingleUser sU, CalendarOwner co, boolean special) {

        /*  if (special) {
        return;

        }*/
        int oraInizioGiornata = CalendarOwner.getStartHour();
        int oraFineGiornata = CalendarOwner.getEndHour();
        MyDate startDate = co.getStartDate();
        MyDate endDate = co.getEndDate();
        //    Calendar calNow = Calendar.getInstance();


        //  co.setToday(oggi * 24 * 3600 * 1000);
        //     int giornoSett = calNow.get(Calendar.DAY_OF_WEEK);
        Calendar calS = Calendar.getInstance();
        Calendar calE = Calendar.getInstance();

        Calendar calNight = Calendar.getInstance();
        updateMidnight(calNight, startDate);
        long partenza = calNight.getTimeInMillis();
        co.setTodayMidNight(partenza);
        Calendar calFine = Calendar.getInstance();
        updateMidnight(calFine, endDate);
        long fine = calFine.getTimeInMillis();
        int offsetFromStart = calNight.get(Calendar.DAY_OF_YEAR);
        List appts = null;
        if (special) {
            appts = new ArrayList();
        } else {
            // remote call to googgle calendar !!!
            appts = new CalendarCall(sU.getMailAddress(), sU.getPwd()).getCalendarEvents();
        }
        Iterator it = appts.iterator();

        while (it.hasNext()) {
            //       MyCalendarEventEntry en = new MyCalendarEventEntry((CalendarEventEntry) it.next());
            CalendarEventEntry en = (CalendarEventEntry) it.next();
            List<When> lw = en.getTimes();
            DateTime s = null;
            DateTime e = null;
            System.err.println("CAL1");
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
            //   System.err.println("CAL2");
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
             System.err.println("creo app giornoorainorafin" +
                     giornoApp + "/" + oraInizio + "/" +oraFine);
            Appointment appol = new Appointment(tit, giornoApp, oraInizio, oraFine);
            co.addImpegno(appol);
            System.err.println("fine carico CALENDARIO!!!");
        }
    }

    void loadApptsOLD(CalendarOwner co, boolean special) {

        int oraInizioGiornata = CalendarOwner.getStartHour();
        int oraFineGiornata = CalendarOwner.getEndHour();

        Calendar calNow = Calendar.getInstance();

        int oggi = calNow.get(Calendar.DAY_OF_YEAR);
        //  co.setToday(oggi * 24 * 3600 * 1000);
        int giornoSett = calNow.get(Calendar.DAY_OF_WEEK);
        Calendar calS = Calendar.getInstance();
        Calendar calE = Calendar.getInstance();

        Calendar calNight = Calendar.getInstance();
        calNight.set(Calendar.HOUR_OF_DAY, 0);
        calNight.set(Calendar.MINUTE, 0);
        calNight.set(Calendar.SECOND, 0);
        calNight.set(Calendar.MILLISECOND, 0);
        long partenza = calNight.getTimeInMillis();
        co.setTodayMidNight(partenza);
        long fine = partenza + CalendarOwner.getTotalDays() * 24 * 3600 * 1000;

        List appts = null;
        if (special) {
            appts = new ArrayList();
        } else {
            // remote call to googgle calendar !!!
            appts = new CalendarCall(co.getMailAddress(), co.getPwd()).getCalendarEvents();
        }
        Iterator it = appts.iterator();

        while (it.hasNext()) {
            //       MyCalendarEventEntry en = new MyCalendarEventEntry((CalendarEventEntry) it.next());
            CalendarEventEntry en = (CalendarEventEntry) it.next();
            List<When> lw = en.getTimes();
            DateTime s = null;
            DateTime e = null;
            if (lw.size() > 0) {
                When wh = lw.get(0);
                s = wh.getStartTime();
                e = wh.getEndTime();
            }
            if (s == null | e == null) {
                System.err.println("Appointment with no time:" + en.getTitle().getPlainText());
                continue;
            }
            // if appt yesterday or later than shown  days continue
            if (e.getValue() < partenza || s.getValue() > fine) {
                continue;
            }
            calS.setTimeInMillis(s.getValue());
            calE.setTimeInMillis(e.getValue());
            int giornoApp = calS.get(Calendar.DAY_OF_YEAR) - oggi;
            int oraInizio = calS.get(Calendar.HOUR_OF_DAY);
            int oraFine = calE.get(Calendar.HOUR_OF_DAY);
            if (oraInizio == oraFine) {
                oraFine++;
            }
            if (oraInizio >= oraFineGiornata || oraFine <= oraInizioGiornata) {
                continue;
            }
            //  System.err.println("calendario, ora inizio fine e giorno" + giornoApp + " " +
            //          oraInizio + " " + oraFine);

            String tit = en.getTitle().getPlainText();
            Appointment appol = new Appointment(tit, giornoApp, oraInizio, oraFine);
            co.addImpegno(appol);
        }
    }

    void creaMese(String[] unMese, int mese, int giornoSettInizio, int giorniMese) {

        int slotPerMese = 6 * 7;
        int i = mese * slotPerMese;
        //  fill with blank initial part up to day of week of day 1 in month
        while (i < (mese * slotPerMese) + giornoSettInizio) {
            unMese[i++] = " ";
        }
        // now  i == giornoSettInizio
        int j = 0;
        // crea tabella rimepinedo coi numeri
        // le caseliine dei giorni del mese
        while (j < giorniMese) {
            unMese[i++] = "" + (j + 1);
            j++;
        }
        // now  fill with blank last part
        while (i < (mese + 1) * slotPerMese) {
            unMese[i++] = "";
        }
    }

    String[] creaTabellina() {
        int mesiCostruiti = 6;
        int settimaneDelMese = 6; // inlucsi scampoli
        Calendar calNow = Calendar.getInstance();
        String[] ret = new String[mesiCostruiti * settimaneDelMese * 7];
        calNow.set(Calendar.DAY_OF_MONTH, 1);
        // sembra non funzionare!!!!   calNow.setFirstDayOfWeek(Calendar.MONDAY);
        // fill next 6 months !!! boy!!
        for (int i = 0; i < mesiCostruiti; i++) {
            //    calNow.set(Calendar.DAY_OF_MONTH, 1);
            int oggiSett = calNow.get(Calendar.DAY_OF_WEEK) - 2; // chissa' perche'
            int giorniMese = calNow.getActualMaximum(Calendar.DAY_OF_MONTH);
            creaMese(ret, i, oggiSett, giorniMese);
            calNow.add(Calendar.MONTH, 1);
        }
        return ret;
    }
}

class UserComparator implements Comparator<TreeElement> {

    public int compare(TreeElement t1, TreeElement t2) {
        return t1.getName().compareTo(t2.getName());

    }
}

