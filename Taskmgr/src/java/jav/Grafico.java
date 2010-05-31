/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

/**
 *
 * @author marino
 */
public class Grafico {

    Connection[] connections;
    Block[] blocks;

    public String faGrafo () {
       String ret = "";
       for (int i = 0; i < connections.length; i++){
           Connection c = connections[i];
           ret += c.source + " -> "+ c.target +"\n";
       }
       return ret;
    }

}
