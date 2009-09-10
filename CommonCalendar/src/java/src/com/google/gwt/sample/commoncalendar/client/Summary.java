/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package src.com.google.gwt.sample.commoncalendar.client;

import appsusersevents.client.EventDescription;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import java.util.ArrayList;

/**
 *
 * @author marino
 */
public class Summary extends VerticalPanel {

    private FlexTable meetingTable = new FlexTable();
    private Label lastMessage = new Label("LAST MESSAGE");
    private ArrayList<MeetingSession> shownSessions = new ArrayList();

    public Summary(ArrayList<MeetingSession> sessions) {
        this.shownSessions = sessions;
        setBorderWidth(2);
        meetingTable.setBorderWidth(2);
        meetingTable.setText(0, 0, "Meeting description");
        meetingTable.setText(0, 1, "" + "Total");
        meetingTable.setText(0, 2, "" + "Missing");
        for (int i = 0; i < shownSessions.size(); i++) {
            MeetingSession se = shownSessions.get(i);
            EventDescription[] des = se.getProposal();
            String msg = se.getTitle();
            meetingTable.setText(i + 1, 0, msg);
            meetingTable.setText(i + 1, 1, "" + des.length);
            meetingTable.setText(i + 1, 2, "" + se.getMissingConfirmations());
        }
        add(meetingTable);
        add(lastMessage);
    }
}
