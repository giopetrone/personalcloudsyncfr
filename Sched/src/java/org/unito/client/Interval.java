/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author marino
 */
public class Interval implements IsSerializable {

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
}
