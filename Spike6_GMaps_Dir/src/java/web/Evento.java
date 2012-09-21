/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import java.util.HashMap;

/**
 *
 * @author goy
 */
public class Evento {

    private HashMap<String, Object> datiEvento = new HashMap();

    public Evento() {
    }

    public Evento(HashMap<String, Object> datiEvento) {
        this.datiEvento = datiEvento;
    }

    public HashMap<String, Object> getDatiEvento() {
        return this.datiEvento;
    }

    public void setDatiEvento(HashMap<String, Object> de) {
        this.datiEvento = de;
    }

    public String getName() {
        return (String) this.datiEvento.get("nome");
    }

    public void setName(String n) {
        this.datiEvento.put("nome", n);
    }

    public String getAddress() {
        return (String) this.datiEvento.get("indirizzo");
    }

    public void setAddress(String a) {
        this.datiEvento.put("indirizzo", a);
    }

    public String getImage() {
        return (String) this.datiEvento.get("img");
    }

    public void setImage(String i) {
        this.datiEvento.put("img", i);
    }

    public String getDescription() {
        return (String) this.datiEvento.get("descr");
    }

    public void setDescription(String d) {
        this.datiEvento.put("descr", d);
    }
}
