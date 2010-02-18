/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package myCLasses;

import java.util.ArrayList;
import java.util.Random;

/**
 *
 * @author liliana
 */
public class GroupCalendar {

                // generates a couple of possible dates for a meeting
                // in the specified time interval (assume month is fixed)
    public static String[] getPossibleDates(String initDay, String finDay,
                                   String month, String year, String hours) {
        String[] proposals = new String[2];
        if (initDay==null || finDay==null || month==null || year==null || hours==null) {
            proposals[0]=" ";
            proposals[1]=" ";
        } else {
            int first = Integer.parseInt(initDay);
            int last = Integer.parseInt(finDay);
            int interval = last-first;
            Random r = new Random();
                            // set hours
            int hour1 = r.nextInt(8)+9;
            int hour2 = r.nextInt(8)+9;
                            // set days
            int day1;
            int day2;
            if (first==last) { // only one day selected for meeting
                day1 = first;
                day2 = last;
            } else {
                day1 = r.nextInt(interval)+first;
                day2 = r.nextInt(interval)+first;
                while (day1==day2) // try again...
                    day2 = r.nextInt(interval)+first;
                }
            if (day1>day2) { // swap dates to respect calendar order
                int tmp = day1;
                day1=day2;
                day2=tmp;
            }
            String middle = "/" + month + "/" + year + " at ";
            String firstProposal = new Integer(day1).toString() + middle +
                                new Integer(hour1).toString();
            String secondProposal = new Integer(day2).toString() + middle +
                                new Integer(hour2).toString();
        
            proposals[0] = firstProposal;
            proposals[1] = secondProposal;
        }
        return proposals;
    }

           // creates list of user spheres, without "unknown"
    public static ArrayList<String> getSpheres(ArrayList<String>sphs) {
        ArrayList<String> out = new ArrayList();
        for (int i=0; i<sphs.size(); i++) {
            String sph = sphs.get(i);
            if (!sph.equalsIgnoreCase("unknown"))
                out.add(sph);
        }
    return out;
    }

    
} //end class
