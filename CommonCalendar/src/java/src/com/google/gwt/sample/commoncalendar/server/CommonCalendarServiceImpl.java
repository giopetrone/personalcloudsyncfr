/*
 * CommonCalendarServiceImpl.java
 *
 * Created on December 22, 2008, 1:23 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */
package src.com.google.gwt.sample.commoncalendar.server;

import giga.GigaListener;

import appsusersevents.client.ApplicationDescription;
import appsusersevents.client.EventDescription;
import appsusersevents.client.UserGroup;
import appsusersevents.client.CalendarOwner;
import appsusersevents.client.CloudUsers;
import appsusersevents.client.MyDate;
import appsusersevents.client.SingleUser;

import java.util.Calendar;

import src.com.google.gwt.sample.commoncalendar.client.ICommonCalendar;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import javax.servlet.http.HttpSession;

/**
 *
 * @author marino
 */
public class CommonCalendarServiceImpl extends RemoteServiceServlet implements
        ICommonCalendar {

    //  GigaListener listener = new GigaListener(false);
    // One gigalistener for each session
    HashMap<String, GigaListener> sessionListeners = new HashMap();
    CloudUsers cloudUsers = new CloudUsers();
    //   boolean doneFilters = false;

    /*
    public StockPriceOld[] getPrices(String[] symbols) {


    Random rnd = new Random();

    StockPriceOld[] prices = new StockPriceOld[symbols.length];
    for (int i = 0; i < symbols.length; i++) {
    double price = rnd.nextDouble() * MAX_PRICE;
    double change = price * MAX_PRICE_CHANGE * (rnd.nextDouble() * 2f - 1f);

    prices[i] = new StockPriceOld(symbols[i], price, change);
    }

    return prices;
    }
     */
    public String[] calendario6Mesi() {
        return new ServerToClient().creaTabellina();
    }

    private String readAppsold(String xmlFile) {

        InputStream is = getClass().getResourceAsStream(xmlFile);
        if (is == null) {
            return "";
        }
        //   System.out.println("in readapps, is == " + is);
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return sb.toString();


    }

    /*
    public String myMethod(String s) {
    // Do something interesting with 's' here on the server.

    String daGiga = listener.pollOld();
    return "Server says: " + (daGiga == null ? "null" : daGiga) + " " + s;
    }

    public String[] myMethodS(String[] s) {
    // Do something interesting with 's[]' here on the server.
    String[] re = new String[2];
    re[0] = "server says";
    re[1] = s[0];
    return re;
    }
     */
    public ApplicationDescription[] getApplicationTree() {


        // if called with null, generate a fake example tree
        //  return ServerToClient.buildAppTree(readApps("applicationList.xml"));
        //  InputStream is = getClass().getResourceAsStream("applicationList.xml");
        // return ServerToClient.buildAppTree(readApps(is));
        return new ServerToClient().buildAppTree(null);
    }

    public UserGroup[] getGroupTree(SingleUser me) {

        // if called with null, generate a fake example tree
        //   return ServerToClient.buildGroupTree(readApps("userList.xml"));
        //  InputStream is = getClass().getResourceAsStream("userList.xml");
        // return ServerToClient.buildGroupTree(readApps(is));
        //    return new ServerToClient().buildGroupTree(null);
        return new ServerToClient().buildGoogleGroupTree(me);

    }

    public EventDescription[] getEvents() {
        //  return new EventDescription[0];
        return getListener().getEvents();
    }

    public void putEvents(EventDescription[] events) {
        getListener().putEvents(events);
    }

    public CalendarOwner[] getCalendars(SingleUser sU, SingleUser[] users, MyDate startDate, MyDate endDate) {
        return new ServerToClient().getCalendars(sU, users, startDate, endDate);
    }

    public void updateCalendars(CalendarOwner[] users, int rowIndex, SingleUser me, String title, int durata) {
        new ServerToClient().updateCalendars(users, rowIndex,  me, title, durata);
    }

    public MyDate cheGiornoOggi() {
        Calendar calNow = Calendar.getInstance();
        MyDate date = new MyDate();
        date.setMonth(calNow.get(Calendar.MONTH));
        date.setYear(calNow.get(Calendar.YEAR));
        date.setDayOfYear(calNow.get(Calendar.DAY_OF_YEAR));
        date.setDayOfMonth(calNow.get(Calendar.DAY_OF_MONTH));
        date.setDayOfWeek(calNow.get(Calendar.DAY_OF_WEEK) - 2);
        date.setDaysOfYear(calNow.getActualMaximum(Calendar.DAY_OF_YEAR));
        // strano vero?
      /*  HttpSession sess = getSession();
        Enumeration enu = sess.getAttributeNames();
        System.out.println("this: " + this + " SESSIONE: " + sess.getId());
        while (enu.hasMoreElements()) {
        String s = (String) enu.nextElement();
        Object o = sess.getAttribute(s);
        System.out.println(s + "  " + o.toString());

        }*/

        return date;
    }

    private GigaListener getListener() {
        String sId = getSession().getId();
        GigaListener ret = sessionListeners.get(sId);
        if (ret == null) {
            ret = new GigaListener(false, false);
            sessionListeners.put(sId, ret);
            setFilters(ret);
        }
        //   System.out.println("retlistener  " + ret);
        return ret;
    }

    private void setFilters(GigaListener listener) {
        EventDescription desc = new EventDescription("*");
        // Commentato da GIO
       // desc.setApplication("SurveyMgr");
        desc.setEventName("MeetingAnswer");
        listener.addEvent(desc);
    }

    /**
     * Returns the current session
     *
     * @return  The current Session
     */
    private HttpSession getSession() {
        // Get the current request and then return its session
        return this.getThreadLocalRequest().getSession();
    }

    public SingleUser validateUser(String name, String pwd) {
        System.out.println("Commoncal impl Client in ValidateUSer pwd+ " + pwd);
        return new ServerToClient().validateUser(name, pwd);
    }

    public SingleUser authenticate(String s) {
        System.out.println("@@@@@@@@@@ AUTHENTICATE: s= " + s);
        SingleUser sU = cloudUsers.getUser(s);
        if (sU == null)
        System.out.println("@@@@@@@@@@ AUTHENTICATE: sU NULL!!!");
        return sU;
    }
}
