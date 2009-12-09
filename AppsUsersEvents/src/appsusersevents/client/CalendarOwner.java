/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class CalendarOwner implements Serializable, IsSerializable {

    SingleUser user;
    private ArrayList<Appointment> impegni = new ArrayList();
    private static CalendarOwner[] tutti = null;
    private long todayMidNight = 0;
    private MyDate startDate;
    private MyDate endDate;
    private static int SlotsPerDay = 10;
    private static int Weeks = 2;
    private static int startHour = 8;
    private static int endHour = 18;
    private int offsetfromGMT = 2;
    private static String[] giorniSettimana = {"Dom", "Lun", " Mar", "Mer", "Gio", "Ven", "Sab"};

    public String getName() {
        return user.getName();
    }

    public String getPwd() {
        return user.getPwd();
    }

    public String getMailAddress() {
        return user.getMailAddress();
    }

    public static CalendarOwner[] getTutti() {
        return tutti;
    }

    public static int getTotalSlots() {
        return 7 * Weeks * SlotsPerDay;
    }

    public static int getTotalDays() {
        return 7 * getWeeks();
    }

    public static int getStartHour() {
        return startHour;
    }

    public static int getEndHour() {
        return endHour;
    }

    /**
     * @param aTutti the tutti to set
     */
    public static void setTutti(CalendarOwner[] aTutti) {
        setTutti(aTutti);
    }

    /**
     * @return the SlotsPerDay
     */
    public static int getSlotsPerDay() {
        return SlotsPerDay;
    }

    /**
     * @param aSlotsPerDay the SlotsPerDay to set
     */
    public static void setSlotsPerDay(int aSlotsPerDay) {
        SlotsPerDay = aSlotsPerDay;
    }

    /**
     * @return the Weeks
     */
    public static int getWeeks() {
        return Weeks;
    }

    /**
     * @param aWeeks the Weeks to set
     */
    public static void setWeeks(int aWeeks) {
        Weeks = aWeeks;
    }

    /**
     * @param aTotalSlots the TotalSlots to set
     */
    /**
     * @param aStartHour the startHour to set
     */
    public static void setStartHour(int aStartHour) {
        startHour = aStartHour;
    }

    /**
     * @param aEndHour the endHour to set
     */
    public static void setEndHour(int aEndHour) {
        endHour = aEndHour;
    }

    /**
     * @return the giorniSettimana
     */
    public static String[] getGiorniSettimana() {
        return giorniSettimana;
    }

    /**
     * @param aGiorniSettimana the giorniSettimana to set
     */
    public static void setGiorniSettimana(String[] aGiorniSettimana) {
        giorniSettimana = aGiorniSettimana;
    }

    public CalendarOwner() {
    }

    /*  public CalendarOwner(String na, String pw, MyDate startDate,  MyDate endDate) {
    email = na;
    pwd = pw;
    this.startDate = startDate;
    this.endDate = endDate;
    }
     */
    public CalendarOwner(SingleUser u, MyDate startDate, MyDate endDate) {
        this.user = u;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public long absoluteApptTime(int rowIndex) {
        // count how many millisecs from today's midnight
        int ore = startHour + (rowIndex / SlotsPerDay) * 24 + rowIndex % SlotsPerDay;
        // count how many missisecs from jan 1 midnight
        ore += offsetfromGMT;
        return todayMidNight + ore * 3600 * 1000;
    }

    public static String[] creaSettimane(MyDate startDate, MyDate endDate) {

        setWeeks(startDate.daysTo(endDate) / 7 + 1);
        String[] ret = new String[getTotalSlots()];
        for (int j = 0; j < ret.length; j++) {
            ret[j] = "     ";
        }
        //   System.err.println("GIORNI  "+ (startDate.daysTo(endDate)));
        for (int i = 0; i < getGiorniSettimana().length * getWeeks(); i++) {
            ret[i * SlotsPerDay] = getGiorniSettimana()[(startDate.getDayOfWeek() + i) % 7];
        }
        /* ret[1] = "" + (startDate.daysTo(endDate));
        ret[2] = "" + getGiorniSettimana().length;
        ret[3] = "" + getWeeks();*/
        return ret;
    }

    public static String[] creaOre() {
        String[] ret = new String[getTotalSlots()];
        for (int j = 0; j < ret.length; j++) {
            ret[j] = (startHour + (j % SlotsPerDay)) + ".00";
        }

        return ret;
    }

    public String[] creaMat() {
        String[] ret = new String[getTotalSlots()];
        for (int j = 0; j < ret.length; j++) {
            ret[j] = "";
        }
        for (int j = 0; j < getImpegni().size(); j++) {
            Appointment ap = (Appointment) getImpegni().get(j);
            int posto = ap.getPosto(startHour, SlotsPerDay);
            // appointments longer than 1 hour ?
            for (int i = 0; i < endHour - startHour; i++) {
                ret[posto + i] = ap.getTitolo();
            }
        }
        return ret;
    }

    public void addImpegno(Appointment a) {
        getImpegni().add(a);
    }

    /**
     * @return the impegni
     */
    public ArrayList<Appointment> getImpegni() {
        return impegni;
    }

    /**
     * @param impegni the impegni to set
     */
    public void setImpegni(ArrayList<Appointment> impegni) {
        this.impegni = impegni;
    }

    /**
     * @return the today
     */
    public long getTodayMidNight() {
        return todayMidNight;
    }

    /**
     * @param today the today to set
     */
    public void setTodayMidNight(long todayMidNight) {
        this.todayMidNight = todayMidNight;
    }

    /**
     * @return the startDate
     */
    public MyDate getStartDate() {
        return startDate;
    }

    /**
     * @return the endDate
     */
    public MyDate getEndDate() {
        return endDate;
    }
}
