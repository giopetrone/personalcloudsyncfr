/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package giga;

import appsusersevents.client.EventDescription;

/**
 *
 * @author giovanna
 */
public class Subscription extends Filter {

      // modificata da ANNA+GIO
    public boolean checkEvent(EventDescription des) {

    //    System.out.println("siamo in SUBSCRIPTION: desc  eventName (evento sottoscritto)  = " + desc.getEventName() + "  Destinatario = " + desc.getDestinatario() );
    //    System.out.println("siamo in SUBSCRIPTION: des  eventName (evento corrente)  = " + des.getEventName()+ "  Destinatario = " + des.getDestinatario());
      //   if ((des.getEventName().equals(desc.getEventName())) && (des.getApplication().equals(desc.getApplication())) && (des.getDestinatario().equals(desc.getDestinatario()))) {
        String de = (desc.getDestinatari()).get(0);
        if ((des.getEventName().equals(desc.getEventName())) && (des.getApplication().equals(desc.getApplication())) && (des.getDestinatari().contains(de))) {
            return true;
        }
        /*
        String prova = desc.getUser();
        System.out.println("user = " + prova +" vs " +des.getUser());
        return desc != null && des.getUser().equals(user); */
        return false;

    }


  public boolean checkEventMAR(EventDescription evt) {

        System.out.println("sono in subscription.checkEvent");
        return evt.compatibleWith(desc);
    //   return desc.match(evt) == 0;
    // return true;
    }

}


