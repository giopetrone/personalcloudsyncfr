/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 *
 * @author marino
 */
public class Appointment implements IsSerializable {

    private String titolo;
    private int giorno = 0;
    private int oraInizio = 0;
    private int oraFine = 0;

    public Appointment() {
    }

    public Appointment(String tit, int giorno, int oraS, int oraF) {
        this.titolo = tit;
        this.giorno = giorno;
        this.oraInizio = oraS;
        this.oraFine = oraF;
    }

    public int getPosto(int offset, int slotsPerDay) {
        //this is the place in the table
        //could be moved to the GUI
        //offset = 8; usually we do not show meetings before 8 am
        return giorno * slotsPerDay + oraInizio - offset;
    }

    /**
     * @return the titolo
     */
    public String getTitolo() {
        return titolo;
    }

    /**
     * @param titolo the titolo to set
     */
    public void setTitolo(String titolo) {
        this.titolo = titolo;
    }

    /**
     * @return the giorno
     */
    public int getGiorno() {
        return giorno;
    }

    /**
     * @param giorno the giorno to set
     */
    public void setGiorno(int giorno) {
        this.giorno = giorno;
    }

    /**
     * @return the oraInizio
     */
    public int getOraInizio() {
        return oraInizio;
    }

    /**
     * @param oraInizio the oraInizio to set
     */
    public void setOraInizio(int oraInizio) {
        this.oraInizio = oraInizio;
    }

    /**
     * @return the oraFine
     */
    public int getOraFine() {
        return oraFine;
    }

    /**
     * @param oraFine the oraFine to set
     */
    public void setOraFine(int oraFine) {
        this.oraFine = oraFine;
    }
}
