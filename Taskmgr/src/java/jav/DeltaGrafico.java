/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

/**
 *
 * @author marino
 */
public class DeltaGrafico {
    private Grafico vecchio;
    private Grafico nuovo;

    public void createChangeEvents() {
        System.err.println("createChangeEventse'  da FARE!!!!!");
    }

    /**
     * @return the vecchio
     */
    public Grafico getVecchio() {
        return vecchio;
    }

    /**
     * @param vecchio the vecchio to set
     */
    public void setVecchio(Grafico vecchio) {
        this.vecchio = vecchio;
    }

    /**
     * @return the nuovo
     */
    public Grafico getNuovo() {
        return nuovo;
    }

    /**
     * @param nuovo the nuovo to set
     */
    public void setNuovo(Grafico nuovo) {
        this.nuovo = nuovo;
    }

}
