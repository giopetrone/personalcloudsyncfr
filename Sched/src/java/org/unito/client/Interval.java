/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.user.client.rpc.IsSerializable;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class Interval implements IsSerializable {

    /**
     * @return the giorni
     */
    public static String[] getGiorni() {
        return giorni;
    }

    /**
     * @param aGiorni the giorni to set
     */
    public static void setGiorni(String[] aGiorni) {
        setGiorni(aGiorni);
    }

   

    private ArrayList<String> users = new ArrayList();
    private ArrayList<String> impacts = new ArrayList();
    private ArrayList<Update> updates = new ArrayList();
    private String taskName = "";
    private int min = -1;
    private int max = -1;

    public Interval() {
    }

    public Interval(String taskName, int min, int max) {
        this.min = min;
        this.max = max;
        this.taskName = taskName;
    }

    private String trad(String s) {
        if (s.equals("L")) {
            return "Low";
        }
        if (s.equals("M")) {
            return "Medium";
        }
        if (s.equals("H")) {
            return "High";
        }
        return "?";
    }

    public String impatto(){
        // trova max impatto fra H, L, M
        String ret = "L";
        for ( String s: impacts){
            if (s.equals("H"))
                return s;
            if (s.equals("M"))
                ret = s;
        }
        return ret;
    }
    
    public String stringaImpatto() {
        String ret = "Impatto: ";
        for (int i = 0; i < users.size(); i++) {
            ret += users.get(i) + ": " + (trad(impacts.get(i))) + ",   ";
        }
        return ret;
    }
    
    private static String[] giorni = {"lun","mar","mer","gio","ven","sab","dom"};
    
    public String giornoOra(){
        String ret = getGiorni()[min/12];
        ret+= " h. ";
        int o = min%12;
        o +=8;
        ret += ""+o;
        return ret;
    }

    public String leggibile() {
        String ret = giornoOra()+" Impatto: ";
        for (int i = 0; i < users.size(); i++) {
            ret += users.get(i) + ": " + (trad(impacts.get(i))) + ",   ";
        }
        return ret;
    }
    
    public boolean esterno(Interval i) {
        return !(i.min >= min && i.min < max
                || i.max >= min && i.max < max);
    }

    public String getName() {
        return getTaskName();
    }

    /**
     * @return the min
     */
    public int getMin() {
        return min;
    }

    /**
     * @param min the min to set
     */
    public void setMin(int min) {
        this.min = min;
    }

    /**
     * @return the max
     */
    public int getMax() {
        return max;
    }

    /**
     * @param max the max to set
     */
    public void setMax(int max) {
        this.max = max;
    }

    public String toRequest() {
        return "(" + getMin() + "," + getMax() + ")";
    }

    
    public String toRequestSchedule() {
        String ret = "<interval impacts=\"";
        for (int i=0; i < impacts.size(); i++){
          ret += impacts.get(i);
          if (i < impacts.size() -1){
              ret+=",";
          } 
        }
       ret +="\" start=\"";
       ret += toRequest();
       ret += "\" users=\"";
        for (int i=0; i < users.size(); i++){
          ret += users.get(i);
          if (i < users.size() -1){
              ret+=",";
          } 
        }
       ret +="\">";
       for (Update up :updates){
           ret += up.toRequest();
       }
       ret += "</interval>";
       return ret;
    }

    /**
     * @return the taskName
     */
    public String getTaskName() {
        return taskName;
    }

    /**
     * @param taskName the taskName to set
     */
    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public void addUser(String n) {
        getUsers().add(n);
    }

    public void addImpact(String n) {
        impacts.add(n);
    }

    public void addUpdate(Update n) {
        getUpdates().add(n);
    }

    /**
     * @return the users
     */
    public ArrayList<String> getUsers() {
        return users;
    }

    /**
     * @param users the users to set
     */
    public void setUsers(ArrayList<String> users) {
        this.setUsers(users);
    }

    public static ArrayList<Interval> sort(Interval[] param) {
        ArrayList<Interval> ret = new ArrayList();
        for (int i = 0; i < param.length; i++) {
            Interval intMin = param[i];
            int quale = i;
            for (int j = i + 1; j < param.length; j++) {
                Interval intCurr = param[j];
                if (intCurr.min < intMin.min) {
                    quale = j;
                }
            }
            ret.add(param[quale]);
        }
        return ret;
    }

    /**
     * @return the updates
     */
    public ArrayList<Update> getUpdates() {
        return updates;
    }

    /**
     * @param updates the updates to set
     */
    public void setUpdates(ArrayList<Update> updates) {
        this.setUpdates(updates);
    }

    
    /**
     * @param impacts the impacts to set
     */
    public void setImpacts(ArrayList<String> impacts) {
        this.setImpacts(impacts);
    }



}
