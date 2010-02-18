/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package myCLasses;

import java.util.ArrayList;

/**
 *
 * @author liliana
 */
public class Sphere {

    String name;
    ArrayList<String> members;

    public Sphere(String name, ArrayList<String> members) {
        this.name = name;
        this.members = members;
    }

    public ArrayList<String> getMembers() {
        return members;
    }

    public String getName() {
        return name;
    }

} // end class
