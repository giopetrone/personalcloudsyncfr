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
public class ActivityDescription extends TreeNode {

    private Condition condition = new OrCondition();
    private long startTime = -1;
    private long maxDuration = -1;
    private long deadLine = -1;
    private String comment = "No Information";
    private String link = "http://docs.google.com";
    private ArrayList <EventDescription> availableEvents = new ArrayList();
    private boolean completed = false;
    private ArrayList <EventDescription> generatedEvents = new ArrayList();

    public ArrayList <TreeElement> getChildren() {
        ArrayList ret = new ArrayList();
        ret.addAll(children);
        ret.add(condition);
        return ret;
    }

    public ActivityDescription() {
    }

    public void addEvent(String name) {
        children.add(new FlowEvent(name));
    }
    
    public boolean canStart() {
        return condition.evaluate(getAvailableEvents());
    }

    public void addAvailableEvent(EventDescription evt) {
        getAvailableEvents().add(evt);
    }

    public ActivityDescription(String name) {
       super(name);
    }

    public void propagateEvent (FlowEvent event) {
        addAvailableEvent(event);
    }
   

    /**
     * @return the condition
     */
    public Condition getCondition() {
        return condition;
    }

    /**
     * @param condition the condition to set
     */
    public void setCondition(Condition condition) {
        this.condition = condition;
    }

    /**
     * @return the startTime
     */
    public long getStartTime() {
        return startTime;
    }

    /**
     * @param startTime the startTime to set
     */
    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    /**
     * @return the maxDuration
     */
    public long getMaxDuration() {
        return maxDuration;
    }

    /**
     * @param maxDuration the maxDuration to set
     */
    public void setMaxDuration(long maxDuration) {
        this.maxDuration = maxDuration;
    }

    /**
     * @return the deadLine
     */
    public long getDeadLine() {
        return deadLine;
    }

    /**
     * @param deadLine the deadLine to set
     */
    public void setDeadLine(long deadLine) {
        this.deadLine = deadLine;
    }

    /**
     * @return the availableEvents
     */
    public ArrayList<EventDescription> getAvailableEvents() {
        return availableEvents;
    }

    /**
     * @param availableEvents the availableEvents to set
     */
    public void setAvailableEvents(ArrayList<EventDescription> availableEvents) {
        this.availableEvents = availableEvents;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }

    /**
     * @return the link
     */
    public String getLink() {
        return link;
    }

    /**
     * @param link the link to set
     */
    public void setLink(String link) {
        this.link = link;
    }

    /**
     * @return the completed
     */
    public boolean isCompleted() {
        return completed;
    }

    /**
     * @param completed the completed to set
     */
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

}
