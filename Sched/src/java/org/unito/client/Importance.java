/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

/**
 *
 * @author marino
 */
public enum Importance{
    L("L"),
    M("M"),
    H("H");
    
    private Importance(final String text) {
        this.text = text;
    }

    private final String text;

    /* (non-Javadoc)
     * @see java.lang.Enum#toString()
     */
    @Override
    public String toString() {
        return text;
    }
    
    public static void main(String[] args){
        Importance i = Importance.L;
        Importance i2 = Importance.H;
        System.out.println(i.ordinal() < i2.ordinal());
        System.out.println(i);
    }
}
