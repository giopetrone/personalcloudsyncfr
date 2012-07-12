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

    private ArrayList<String> users = new ArrayList();
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
    
    public boolean esterno(Interval i) {
        return !(i.min >= min && i.min < max ||
           i.max >= min && i.max < max) ; 
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
        this.users = users;
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
}
