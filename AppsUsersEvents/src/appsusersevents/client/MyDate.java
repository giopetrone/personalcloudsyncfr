/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.io.Serializable;

/**
 *
 * @author marino
 */
public class MyDate implements Serializable, IsSerializable {

    private int year;  // si
    private int month; //si
    private int dayOfYear;
    private int daysOfYear; // 365 || 366
    private int dayOfMonth; //si
    private int dayOfWeek;  //si
    private long time;

    public MyDate() {
    }

    public MyDate(long time) {
        this.time = time;
    }

    public MyDate(MyDate startDate, int daysFromNow) {

        // un po' una schifezza, da migliorare
        if (daysFromNow > 28) {
            System.err.println("ERROE DATA TROPPO DISTANTE");
            daysFromNow = 28;
        }
        dayOfWeek = (startDate.dayOfWeek + daysFromNow) % 7;
        daysOfYear = startDate.daysOfYear;
        dayOfYear = (startDate.dayOfYear + daysFromNow) % daysOfYear;
        dayOfMonth = (startDate.dayOfMonth + daysFromNow);
        month = startDate.month;
        year = startDate.year;
        int max = daysOfMonth();
        if (dayOfMonth > max) {
            month++;
            dayOfMonth -= max;
            if (month > 12) {
                year++;
                month = 1;
            }
        }
    }

    public String stampina() {
        return "" + dayOfMonth + "/" + (month + 1)+ "/" + year;
    }

    private int daysOfMonth() {
        int ret = 31;
        switch (month) {
            case 2:
                ret = bises() ? 29 : 28;
                break;
            case 4:
            case 6:
            case 9:
            case 11:
                ret = 30;
                break;
            //  deafult: break;
            }
        return ret;
    }

    private boolean bises() {
        return daysOfYear == 366;
    }

    public String getDescription() {
        return ("" + year + "." + month + "." + dayOfMonth);
    }

    public int compareTo(MyDate date) {
        if (this.year != date.year) {
            return this.year < date.year ? -1 : 1;
        }
        if (this.month != date.month) {
            return this.month < date.month ? -1 : 1;
        }
        if (this.dayOfMonth != date.dayOfMonth) {
            return this.dayOfMonth < date.dayOfMonth ? -1 : 1;
        }
        return 0;
    }

    public int daysTo(MyDate date) {
        // errore non conto bissetile
        if (this.year == date.year) {
            return date.dayOfYear - this.dayOfYear;
        } else {
            // per ora approssimato
            return date.dayOfYear + (date.daysOfYear - this.dayOfYear);
        }

    }

    /**
     * @return the month
     */
    public int getMonth() {
        return month;
    }

    /**
     * @param month the month to set
     */
    public void setMonth(int month) {
        this.month = month;
    }

    /**
     * @return the dayOfMonth
     */
    public int getDayOfMonth() {
        return dayOfMonth;
    }

    /**
     * @param dayOfMonth the dayOfMonth to set
     */
    public void setDayOfMonth(int dayOfMonth) {
        this.dayOfMonth = dayOfMonth;
    }

    /**
     * @return the dayOfWeek
     */
    public int getDayOfWeek() {
        return dayOfWeek;
    }

    /**
     * @param dayOfWeek the dayOfWeek to set
     */
    public void setDayOfWeek(int dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    /**
     * @return the year
     */
    public int getYear() {
        return year;
    }

    /**
     * @param year the year to set
     */
    public void setYear(int year) {
        this.year = year;
    }

    /**
     * @return the dayOfYear
     */
    public int getDayOfYear() {
        return dayOfYear;
    }

    /**
     * @param dayOfYear the dayOfYear to set
     */
    public void setDayOfYear(int dayOfYear) {
        this.dayOfYear = dayOfYear;
        if (this.dayOfYear > daysOfYear) {
            this.dayOfYear -= daysOfYear; // subtract 365 or 366
        }
    }

    /**
     * @return the daysOfYear
     */
    public int getDaysOfYear() {
        return daysOfYear;
    }

    /**
     * @param daysOfYear the daysOfYear to set
     */
    public void setDaysOfYear(int daysOfYear) {
        this.daysOfYear = daysOfYear;
    }

    /**
     * @return the time
     */
    public long getTime() {
        return time;
    }

    /**
     * @param time the time to set
     */
    public void setTime(long time) {
        this.time = time;
    }
}
