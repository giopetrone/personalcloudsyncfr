package myCLasses;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author liliana
 */
public class ActivityDegree {
    private String sphere;
    private int count;

    public ActivityDegree(String sphere, int count) {
        this.sphere = sphere;
        this.count = count;
    }

    public int getCount() {
        return count;
    }

    public String getSphere() {
        return sphere;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setSphere(String sphere) {
        this.sphere = sphere;
    }

    public void incrementCount() {
       this.count++;
    }

    public String toString() {
        return this.sphere + ": " + this.count;
    }
}

