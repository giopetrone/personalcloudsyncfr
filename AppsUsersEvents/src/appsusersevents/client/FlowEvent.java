/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class FlowEvent extends EventDescription {

    private boolean eventValue = false;

    public boolean evaluate(ArrayList<EventDescription> availableEvents) {
        for (EventDescription el : availableEvents) {
            System.err.println(el.getEventName());
            if (el.getEventName().equals(getEventName())) {
                /*   if (negated) {
                System.err.println("evento negated non funziona!!!");
                return false;
                } */
                return true;
            }
        }
        return false;
    }

    public FlowEvent(String n) {
        super(n);
        setEventName(n);
    }

    public FlowEvent() {
    }

    public void setValue(boolean value) {
        this.eventValue = value;
    }

    public void propagate(boolean value) {
    }

    public boolean findPath(ArrayList a, TreeElement b) {
        return (b == this);
    }

    /**
     * @return the eventValue
     */
    public boolean isEventValue() {
        return eventValue;
    }

    /**
     * @param eventValue the eventValue to set
     */
    public void setEventValue(boolean eventValue) {
        this.eventValue = eventValue;
    }
}
