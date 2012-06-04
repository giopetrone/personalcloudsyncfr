/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import java.util.ArrayList;

/**
 * Main entry point.
 *
 * @author marino
 */
public class MainEntryPoint implements EntryPoint {

    final FlexTable taskTable = new FlexTable();
    final FlexTable taskDefTable = new FlexTable();
    FlexTable userTable = new FlexTable();
    FlexTable legendaTable = new FlexTable();
    private Label lblServerReply = new Label();
    DatePicker dayStart = new DatePicker("giorni", 0);
    DatePicker timeStart = new DatePicker("ore", 0);
    DatePicker dayEnd = new DatePicker("giorni", 4);
    DatePicker timeEnd = new DatePicker("ore", 12);
    DatePicker daySchedule = new DatePicker("giorni", 0);
    DatePicker timeSchedule = new DatePicker("ore", 0);
    TextBox tName = new TextBox();
    TextBox tDuration = new TextBox();
    TextBox tBefore = new TextBox();
    TextBox tAfter = new TextBox();
    TextBox tUsers = new TextBox();
    TextArea tDescription = new TextArea();
    TextBox userName = new TextBox();
    CheckBox cOverlap = new CheckBox("Can overlap other tasks ");
    private String multiUser = "";
    int partenza = 8;
    int fine = 20;
    int orario = fine - partenza;
    ArrayList<String> usersToShow = new ArrayList();

    /**
     * Creates a new instance of MainEntryPoint
     */
    public MainEntryPoint() {
    }

    /** 
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {
        RootPanel.get().addStyleName("gwt-root");
        // content creation
        HorizontalPanel uroz = new HorizontalPanel();
       // uroz.addStyleName("paddedHorizontalPanel");
        //  uroz.setSpacing(10);
        Label ll1 = new Label("Week from March 7 to March 13");
        ll1.addStyleName("labella");
        uroz.add(ll1);
        Label spazio = new Label("         ");
        spazio.setWidth("100px");
        uroz.add(spazio);
        uroz.add(new Label(" You are logged as:"));
        uroz.add(userName);
        RootPanel.get().add(uroz);
        TaskGroup.addTaskGroup();
        TaskGroup.esempioTest();
        iniziaTable(taskTable, TaskGroup.current(), false);
        VerticalPanel pannVertTask = pannelloTask();
        HorizontalPanel buttonPanel = pannelloBottoni();
        VerticalPanel pannVertSched = pannelloScheduler();

        iniziaUtenti(userTable);
        iniziaLegenda(legendaTable);
        // layout creation
        VerticalPanel utentiLegenda = new VerticalPanel();
        utentiLegenda.setSpacing(10);
        utentiLegenda.add(userTable);
        utentiLegenda.add(legendaTable);

        HorizontalPanel weekUser = new HorizontalPanel();
        weekUser.add(taskTable);
        weekUser.add(utentiLegenda);
        RootPanel.get().add(weekUser);
        HorizontalPanel taskSched = new HorizontalPanel();
        //    taskSched.setSpacing(5);
        taskSched.add(pannVertTask);
        taskSched.add(pannVertSched);
        RootPanel.get().add(taskSched);
        RootPanel.get().add(buttonPanel);
        RootPanel.get().add(lblServerReply);

    }

    private VerticalPanel pannelloScheduler() {
        VerticalPanel retPanel = new VerticalPanel();
        retPanel.setBorderWidth(1);
        retPanel.setSpacing(30);
        retPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        final Button schedule1Button = new Button("Schedule early tasks first ");
        retPanel.add(schedule1Button);
        schedule1Button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                // Window.alert("bottonr cliccato size " + TaskGroup.current().getTasks().size());

                getService().schedule(new ViaVai(TaskGroup.current()), "start", "new", callbackTask);
                // getService().schedule(new TaskGroup(), "start", callbackTask);
                //   getService().myMethod("mar", prova);

            }
        });
        final Button schedule2Button = new Button("Schedule urgent tasks first");
        retPanel.add(schedule2Button);
        schedule2Button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                getService().schedule(new ViaVai(TaskGroup.current()), "end", "new", callbackTask);

            }
        });
        final Button showListButton = new Button("Show full task list");
        retPanel.add(showListButton);
        showListButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {


                DialogBox dlg = new MyDialog("Specification of all tasks", null, "alltasks");
                dlg.center();
            }
        });
        return retPanel;
    }

    private VerticalPanel pannelloTask() {
        VerticalPanel retPanel = new VerticalPanel();
        retPanel.setBorderWidth(1);
        retPanel.setSpacing(1);
        HorizontalPanel h = new HorizontalPanel();
        Label label1 = new Label("Task name:");
        label1.setWidth("100px");
        h.add(label1);
        h.add(tName);
        retPanel.add(h);
        h = new HorizontalPanel();
        Label label2 = new Label("Duration:");
        label2.setWidth("100px");
        h.add(label2);
        h.add(tDuration);
        retPanel.add(h);
        h = new HorizontalPanel();
        Label label3 = new Label("Start:");
        Label label4 = new Label("End:");
        Label label5 = new Label("Before:");
        label3.setWidth("100px");
        h.add(label3);
        h.add(dayStart);
        h.add(timeStart);
        retPanel.add(h);
        h = new HorizontalPanel();
        label4.setWidth("100px");
        h.add(label4);
        h.add(dayEnd);
        h.add(timeEnd); //buttonPanel.add(te4);
        retPanel.add(h);
        h = new HorizontalPanel();
        label5.setWidth("100px");
        h.add(label5);
        h.add(tBefore);
        retPanel.add(h);
        h = new HorizontalPanel();
        Label label6 = new Label("After:");
        Label label7 = new Label("Schedule:");
        Label label8 = new Label("Attendees:");
        Label label9 = new Label("Description:");
        label6.setWidth("100px");
        h.add(label6);
        h.add(tAfter);
        retPanel.add(h);
        h = new HorizontalPanel();
        label7.setWidth("100px");
        h.add(label7);
        h.add(daySchedule);
        h.add(timeSchedule); // buttonPanel.add(te7);
        retPanel.add(h);
        h = new HorizontalPanel();
        label8.setWidth("100px");
        h.add(label8);
        h.add(tUsers);
        retPanel.add(h);
        h = new HorizontalPanel();
        cOverlap.setWidth("100px");
        cOverlap.addStyleName("overlapping");
        h.add(cOverlap);
        retPanel.add(h);
        h = new HorizontalPanel();
        label9.setWidth("100px");
        h.add(label9);
        tDescription.setVisibleLines(2);
        h.add(tDescription);
        retPanel.add(h);
        return retPanel;
    }

    private HorizontalPanel pannelloBottoni() {
        HorizontalPanel retPanel = new HorizontalPanel();
        retPanel.setBorderWidth(1);
        final Button addButton = new Button("Add Task");
        retPanel.add(addButton);
        addButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (TaskGroup.current().get(tName.getText()) != null) {
                    Window.alert("task already existent: " + tName.getText());
                    return;
                }
                //  Window.alert("giornoSelezionato: " + dayStart.getSelectedIndex());
                String vStart = stringVal(dayStart, timeStart, 0);
                String vEnd = stringVal(dayEnd, timeEnd, 0);
                String vSched = stringVal(daySchedule, timeSchedule, 0);
                String msg = TaskGroup.checkTask(tName.getText(), vStart, vEnd, tDuration.getText(), tBefore.getText(), tAfter.getText(), vSched, tUsers.getText(), cOverlap.getValue());
                if (!msg.equals("")) {
                    Window.alert(msg);
                    return;
                }
                int sch = TaskGroup.addScheduleTask(tName.getText(), vStart, vEnd, tDuration.getText(), tBefore.getText(), tAfter.getText(), vSched, tUsers.getText(), cOverlap.getValue(), tDescription.getText());
                if (sch == -1) {
                    boolean callScheduler = Window.confirm("task has not been scheduled, do you want to change something?");
                    if (callScheduler) {
                        Window.alert("still to be done");
                    }
                    return;
                }
                riempi(taskTable, false, TaskGroup.current());
                updateText(tName.getText());
                updateTasks(taskDefTable);
            }
        });
        final Button changeButton = new Button("Change Task");
        retPanel.add(changeButton);
        changeButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                String vStart = stringVal(dayStart, timeStart, 0);
                String vEnd = stringVal(dayEnd, timeEnd, 0);
                String vSched = stringVal(daySchedule, timeSchedule, 0);
                String msg = TaskGroup.checkTask(tName.getText(), vStart, vEnd, tDuration.getText(), tBefore.getText(), tAfter.getText(), vSched, tUsers.getText(), cOverlap.getValue());
                if (!msg.equals("")) {
                    Window.alert(msg);
                }
                Task tat = new Task(tName.getText(), vStart, vEnd, tDuration.getText(), tBefore.getText(), tAfter.getText(), vSched, tUsers.getText(), cOverlap.getValue(), tDescription.getText());
                msg = TaskGroup.change(tat);
                if (!msg.equals("")) {
                    Window.alert(msg);
                }
                //      Window.alert("add task "+ Task.get(tName.getText()).toString());
                // INUTILE???     TaskGroup.current().setSchedule(tat);
                riempi(taskTable, false, TaskGroup.current());
                updateText(tName.getText());
                updateTasks(taskDefTable);
            }
        });
        final Button removeButton = new Button("Remove Task");
        retPanel.add(removeButton);
        removeButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                getService().removeTask(tName.getText(), prova);
                TaskGroup.remove(tName.getText());
                riempi(taskTable, false, TaskGroup.current());
                updateText("");
                updateTasks(taskDefTable);
            }
        });
        final Button moveButton = new Button("Where can I place the task? ");
        //    moveButton.setEnabled(false);
        retPanel.add(moveButton);
        moveButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                //  if (selectedUser() != null) {
                String tName = MainEntryPoint.this.tName.getText();
                if (TaskGroup.exists(tName)) {
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "startintervals", "mu/", "move", selectedUser(), callbackTaskSuggest);

                    //   getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "startintervals", multiUser, "move", "" /* RIMOSSO: selectedUser()*/, callbackTaskSuggest);
                } else {
                    Window.alert("task NOT found:" + tName);
                }
            }
        });
        final Button insertButton = new Button("Insert(startIntervals)");
        insertButton.setEnabled(false);
        retPanel.add(insertButton);
        insertButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final String tName = MainEntryPoint.this.tName.getText();
                if (TaskGroup.exists(tName) && selectedUser() != null) {
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "startintervals", multiUser, "insert", selectedUser(), callbackTaskSuggest);
                } else {
                    Window.alert("task=NOT found:" + tName);
                }
            }
        });
        final Button moveNetButton = new Button("move(taskNet) ");
        moveNetButton.setEnabled(false);
        retPanel.add(moveNetButton);
        moveNetButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final String tName = MainEntryPoint.this.tName.getText();
                if (TaskGroup.exists(tName) && selectedUser() != null) {
                    TaskGroup.current().setChoiceForTask(tName);
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "tasknet", multiUser, "move", selectedUser(), callbackTaskSuggest);
                } else {
                    Window.alert("task=NOT found:" + tName);
                }
            }
        });
        final Button insertNetButton = new Button("Insert(taskNet)");
        insertNetButton.setEnabled(false);
        retPanel.add(insertNetButton);
        insertNetButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final String tName = MainEntryPoint.this.tName.getText();
                if (TaskGroup.exists(tName) && selectedUser() != null) {
                    TaskGroup.current().setChoiceForTask(tName);
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "tasknet", multiUser, "insert", selectedUser(), callbackTaskSuggest);
                } else {
                    Window.alert("task=NOT found:" + tName);
                }
            }
        });
        final Button muButton = new Button("mu");
        //   retPanel.add(muButton);
        muButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (selectedUser() != null) {
                    final String tName = MainEntryPoint.this.tName.getText();
                    if (TaskGroup.exists(tName)) {
                        // INUTILE??     TaskGroup.current().setChoiceForTask(tName);
                        getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "startintervals", "mu/", "move", selectedUser(), callbackTaskSuggest);
                    } else {
                        Window.alert("task NOT found:" + tName);
                    }
                }
            }
        });
        final Button lunchButton = new Button("Lunch");
        //    retPanel.add(lunchButton);
        lunchButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                TaskGroup.lunch();
                riempi(taskTable, false, TaskGroup.current());
                updateTasks(taskDefTable);
            }
        });
        final Button liliButton = new Button("Esempio Liliana ");
        //       retPanel.add(liliButton);
        liliButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                TaskGroup.reset();
                TaskGroup.addTaskGroup();
                TaskGroup.esempioLili();
                riempi(taskTable, false, TaskGroup.current());
                updateTasks(taskDefTable);
            }
        });
        return retPanel;
    }

    private void iniziaTable(final FlexTable t, TaskGroup tg, boolean showIntervals) {
        FlexTable.CellFormatter form = t.getCellFormatter();
        t.setText(0, 0, "Time");
        form.addStyleName(0, 0, "style1");
        //   t.addStyleName("MainLabel");
        t.setText(0, 1, "Monday");
        t.setText(0, 2, "Tuesday");
        t.setText(0, 3, "Wednesday");
        t.setText(0, 4, "Thursday");
        t.setText(0, 5, "Friday");
        t.setText(0, 6, "Saturday");
        t.setText(0, 7, "Sunday");
        for (int i = 1; i <= orario; i++) {
            t.setText(i, 0, " " + (partenza + i - 1));
        }
        riempi(t, showIntervals, tg);

        // Let's put a button in the middle...
        //  t.setWidget(1, 0, new Button("Wide Button"));

        // ...and set it's column span so that it takes up the whole row.
        //  t.getFlexCellFormatter().setColSpan(1, 0, 3);

        t.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                HTMLTable.Cell clickedCell = t.getCellForEvent(event);
                int rowIndex = clickedCell.getRowIndex();
                int colIndex = clickedCell.getCellIndex();
                String s = t.getText(rowIndex, colIndex);
                //    Window.alert("DataTable -> onClick ::: " + s);
              /*  if (t != taskTable && s.equals("")){
                Window.alert("in click: " + s);
                // siamo in nuovo schedule, ,ostraimaolo!!
                t.setWidget(rowIndex,colIndex,new Label("X"));                } */
                updateText(s);
            }
        });
    }

    private void initDefTable(final FlexTable t) {
        FlexTable.CellFormatter form = t.getCellFormatter();
        t.setBorderWidth(1);
        t.setText(0, 0, "Task name");
        form.addStyleName(0, 0, "style1");
        //   t.addStyleName("MainLabel");
        t.setText(0, 1, "Duration");
        t.setText(0, 2, "Start");
        t.setText(0, 3, "End");
        t.setText(0, 4, "Before");
        t.setText(0, 5, "After");
        t.setText(0, 6, "Overlap");
        t.setText(0, 7, "Schedule");
        t.setText(0, 8, "Users");
        updateTasks(t);    // taskDefTable

        // Let's put a button in the middle...
        //  t.setWidget(1, 0, new Button("Wide Button"));

        // ...and set it's column span so that it takes up the whole row.
        //  t.getFlexCellFormatter().setColSpan(1, 0, 3);

        t.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                HTMLTable.Cell clickedCell = t.getCellForEvent(event);
                int rowIndex = clickedCell.getRowIndex();
                int colIndex = clickedCell.getCellIndex();
                String s = t.getText(rowIndex, colIndex);
                //    Window.alert("DataTable -> onClick ::: " + s);
                updateText(s);
            }
        });

    }

    void addRow(FlexTable f, Task ta) {
        FlexTable.CellFormatter form = f.getCellFormatter();
        int row = f.getRowCount();
        f.setText(row, 0, ta == null ? "" : ta.getName());
        f.setText(row, 1, ta == null ? "" : "" + ta.getDuration());
        f.setText(row, 2, ta == null ? "" : "" + ta.getMinStartHour());
        f.setText(row, 3, ta == null ? "" : "" + ta.getMaxEndHour());
        f.setText(row, 4, ta == null ? "" : ta.beforeString());
        f.setText(row, 5, ta == null ? "" : ta.afterString());
        f.setText(row, 6, ta == null ? "" : "" + ta.getOverlap());
        f.setText(row, 7, ta == null ? "" : "" + ta.getOfficialScheduleAsString());
        if (ta != null && ta.getOfficialSchedule() == -1) {
            form.setStyleName(row, 7, "styleBusy");
        }
        f.setText(row, 8, ta == null ? "" : "" + ta.userString());
    }

    private void updateTasks(FlexTable f) {

        while (taskDefTable.getRowCount() > 1) {
            taskDefTable.removeRow(taskDefTable.getRowCount() - 1);
        }

        for (Task ta : TaskGroup.current().getTasks()) {
            addRow(f, ta);
        }

        addRow(f, null);

    }

    private void updateText(String s) {
        if (!s.equals("")) {
            Task ta = TaskGroup.get(s);
            tName.setText(ta.getName());
            tDuration.setText("" + ta.getDuration());
            dayStart.setSelectedIndex(Task.dayOf(ta.getMinStartHour()));
            timeStart.setSelectedIndex(Task.timeOf(ta.getMinStartHour()));
            // te4.setText("" + ta.getMaxEndHour());
            dayEnd.setSelectedIndex(Task.dayOf(ta.getMaxEndHour() - 1));
            //    Window.alert("timeof="+);
            timeEnd.setSelectedIndex(Task.timeOf(ta.getMaxEndHour() - 1) + 1);
            tBefore.setText(ta.beforeString());
            tAfter.setText(ta.afterString());

            daySchedule.setSelectedIndex(Task.dayOf(ta.getOfficialSchedule()));
            timeSchedule.setSelectedIndex(Task.timeOf(ta.getOfficialSchedule()));

            //  te7.setText("" + ta.getOfficialScheduleAsString());
            tUsers.setText(ta.userString());
            cOverlap.setValue(ta.getOverlap());
            tDescription.setText(ta.getDescription());
        } else {
            tName.setText("");
            tDuration.setText("");
            dayStart.setSelectedIndex(0);
            timeStart.setSelectedIndex(0);
            // te4.setText("");
            dayEnd.setSelectedIndex(0);
            timeEnd.setSelectedIndex(0);
            tBefore.setText("");
            tAfter.setText("");
            daySchedule.setSelectedIndex(0);
            timeSchedule.setSelectedIndex(0);
            //  te7.setText("");
            tUsers.setText("");
            cOverlap.setValue(false);
            tDescription.setText("");
        }
    }

    private boolean checkUserConflicts(TaskGroup te) {

        ArrayList<Interval> inters = te.getTaskSchedule();
        String msg = "";
        if (inters.isEmpty()) {
            msg = "Task conflicts everywhere";
        } else {
            for (Interval inte : inters) {
                ArrayList<String> use = inte.getUsers();
                // Window.alert("utenti di confilitti: " + use.size());
                if (!use.isEmpty()) {
                    msg += "schedule: " + inte.getMin() + " conflicts with users: ";
                    for (String s : use) {
                        msg += s + " ";
                    }
                    msg += "\n";
                }
            }
        }
        if (!msg.equals("")) {
            Window.alert(msg);
            return true;
        }
        return false;
    }

    private void riempi(FlexTable t, boolean showPossibleSchedules, TaskGroup tg) {

        FlexTable.CellFormatter form = t.getCellFormatter();
        ArrayList<String>[] nomiCaselle = TaskGroup.nomiCaselle(tg);
        ArrayList<String>[] stiliCaselle = TaskGroup.stiliCaselle(showPossibleSchedules, tg);
        int k = 0;
        for (int j = 0; j < 7 + 1; j++) {
            for (int i = 0; i < orario && k < nomiCaselle.length; i++) {
                ArrayList<String> currTasks = nomiCaselle[k];
                ArrayList<String> currStyles = stiliCaselle[k];
                if (currStyles.isEmpty()) {
                    Window.alert("empystile:" + k);
                }
                if (currTasks.isEmpty()) {
                    // t.setText(i + 1, j + 1, "");
                    t.setWidget(i + 1, j + 1, new Label(""));
                } else {
                    t.setWidget(i + 1, j + 1, bottoni(currTasks, j * 12 + i));
                }
                if (currStyles.get(0).equals("styleAvailable")) {
                    //   Window.alert("seeto a availanble:"+k);
                }
                form.setStyleName(i + 1, j + 1, currStyles.get(0));
                k++;
            }
        }
        for (int j = 5; j < 7 + 1; j++) {
            for (int i = 0; i < orario; i++) {
                form.setStyleName(i + 1, j + 1, "styleHoliday");
            }
        }
    }

    private Widget bottoni(ArrayList<String> tas, int time) {//ArrayList<String> conflictingUsers) {
        // if conflicting users != null it means that we want to show in this
        // position the users whose schedule is conflicting with the selected task
        // in order to do so, we add a color coded label, in the same way when
        // we want to show user calendars
        ArrayList<String> conflictingUsers = TaskGroup.current().conflictingSchedules(time);
        ArrayList<UiUser> thisBox = new ArrayList();
        HorizontalPanel oriz = new HorizontalPanel();
        for (String ta : tas) {
            final Button bu = new Button(ta);
            Task t = TaskGroup.get(ta);
            bu.addStyleName("gwt-ButtonSmall");
            if (t.isOverlap()) {
                bu.addStyleName("overlapping");
            }


            // bu.addMouseListener(
            //          new TooltipListener(
            //          t.userString(), 5000 /* timeout in milliseconds*/, "yourcssclass"));
            TooltipListener tip = new TooltipListener(t.userString(), 5000, "gwt-root");
            bu.addMouseOverHandler(tip);
            bu.addMouseOutHandler(tip);
            bu.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    String vv = bu.getText();
                    updateText(vv);
                }
            });
            // create a list of users that are involved in this task
            ArrayList<UiUser> uu = TaskGroup.ContainsUser(usersToShow, ta);
            for (UiUser us : uu) {
                if (!thisBox.contains(us)) {
                    thisBox.add(us);
                }
            }
            oriz.add(bu);
        }
// now add a color tag to this box for each user that is involved
        // in this tasks OR whose schedule is in conflict
        if (!conflictingUsers.isEmpty()) {
            ArrayList<UiUser> uuu = new ArrayList();
            for (String ss : conflictingUsers) {
                uuu.add(UiUser.find(ss));
            }
            for (UiUser ui : uuu) {
                Label l = new Label("");
                l.setSize("10px", "22px");
                l.setStyleName(ui.getStyle());
                oriz.add(l);
            }
        } else {
            // add colors for all users busy in this interval
            for (UiUser ui : thisBox) {
                Label l = new Label("");
                l.setSize("10px", "22px");
                l.setStyleName(ui.getStyle());
                oriz.add(l);
            }
        }
        return oriz;
    }
    // Create an asynchronous callback to handle the result.
    final AsyncCallback<ViaVai> callbackTask = new AsyncCallback<ViaVai>() {

        public void onSuccess(ViaVai result) {
            lblServerReply.setText("successo");
            if (result == null) {
                Window.alert("no solutions");
            } else {
                DialogBox dlg = new MyDialog("New Schedule", new TaskGroup(result), "proposal");
                dlg.center();
                /*
                TaskGroup.updateSchedule(new TaskGroup(result));
                riempi(taskTable, false);
                updateTasks();
                updateText(tName.getText());

                 */
            }
        }

        public void onFailure(Throwable caught) {
            //  lblServerReply.setText("Communication failed");
            Window.alert("Communication failed");
        }
    };
    final AsyncCallback<ViaVai> callbackTaskSuggest = new AsyncCallback<ViaVai>() {

        public void onSuccess(ViaVai result) {
            lblServerReply.setText("successo");
            if (result == null) {
                Window.alert("no solutions");
            } else {
                // clone current and add just new schedule
                TaskGroup tempr = new TaskGroup(TaskGroup.current());
                tempr.updateWith(result);
                DialogBox dlg = new MyDialog("Possible Schedules for: " + tempr.getSelectedTask(), tempr, "intervals");
                dlg.center();


                // per ORA settiamo nuovo schedule a corrente e mostriamo all'utente i conflitti
                // dandolo gia' per buono in seguito decidiamo come fare
             /* PER ORA NOP
                TaskGroup.current().updateWith(result); 
                checkUserConflicts(TaskGroup.current());
                riempi(taskTable, true, TaskGroup.current());
                updateTasks(taskDefTable);
                updateText(tName.getText());  */
            }
        }

        public void onFailure(Throwable caught) {
            //  lblServerReply.setText("Communication failed");
            Window.alert("Communication failed");
        }
    };
    final AsyncCallback<String> prova = new AsyncCallback<String>() {

        public void onSuccess(String s) {
            lblServerReply.setText("successo");
            if (s == null) {
                Window.alert("task not removed in scheduler");
            } else {
                Window.alert("task removed in scheduler");
            }
        }

        public void onFailure(Throwable caught) {
            //  lblServerReply.setText("Communication failed");
            Window.alert("Communication failed");
        }
    };

    public static TaskAllocationAsync getService() {
        // Create the client proxy. Note that although you are creating the
        // service interface proper, you cast the result to the asynchronous
        // version of the interface. The cast is always safe because the
        // generated proxy implements the asynchronous interface automatically.

        TaskAllocationAsync ta = GWT.create(TaskAllocation.class);
        // Window.alert("dopo create intf");
        return ta;
    }

    private String stringVal(ListBox day, ListBox time, int subtract) {

        return "" + (day.getSelectedIndex() * 12 + time.getSelectedIndex() - subtract);
    }

    class MyDialog extends DialogBox implements ClickHandler {

        private String what = "alltasks";
        private TaskGroup tg;
        int time = -1;
        TextBox info = new TextBox();

        ;

        public MyDialog(String title, TaskGroup tag, String what) {

            this.what = what;
            this.tg = tag;
            setText(title);
            FlexTable f = null;
            if (tg != null) {
                //  show a new task schedule proposal
                f = new FlexTable();
                iniziaTable(f, tg, what.equals("intervals"));
                if (what.equals("intervals")) {
                    f.addClickHandler(new ClickHandler() {

                        @Override
                        public void onClick(ClickEvent event) {
                            FlexTable ff = (FlexTable) event.getSource();
                            HTMLTable.Cell clickedCell = ff.getCellForEvent(event);
                            int rowIndex = clickedCell.getRowIndex();
                            int colIndex = clickedCell.getCellIndex();
                            String s = ff.getText(rowIndex, colIndex);
                            //  Window.alert("row col " + rowIndex + " " + colIndex);
                            if (s.equals("")) {
                                time = --rowIndex + --colIndex * 12;
                                tg.setSchedule(tg.getSelectedTask(), time);
                                riempi(ff, true, tg);
                                info.setText("");
                                //  Window.alert("in click: " + s);
                                // siamo in nuovo schedule, ,ostraimaolo!!
                                /*
                                ff.setWidget(rowIndex, colIndex, new Label("X"));
                                time = --rowIndex + --colIndex * 12;
                                info.setText("");
                                 *
                                 */
                            } else {
                                //  info.setText("conflicts for time: " + (--rowIndex + --colIndex * 12) + " "+ tg);
                                ArrayList<String> conflictingUsers = tg.conflictingSchedules(--rowIndex + --colIndex * 12);
                                if (!conflictingUsers.isEmpty()) {
                                    String us = "";
                                    for (String u : conflictingUsers) {
                                        us += u + " ";
                                    }
                                    info.setText("Change task schedule for: " + us);
                                }
                            }
                        }
                    });
                }
            } else {
                // just show the task list of the current proposal
                f = taskDefTable;
                initDefTable(f);

            }
            Button okButton = new Button("OK", this);
            Button cancelButton = new Button("Cancel", this);
            VerticalPanel vert = new VerticalPanel();
            HorizontalPanel oriz = new HorizontalPanel();
            // HTML msg = new HTML("<center>A standard dialog box component.</center>",true);

            DockPanel dock = new DockPanel();
            dock.setSpacing(4);
            dock.add(f, DockPanel.CENTER);
            //  dock.add(oriz, DockPanel.SOUTH);
            oriz.add(okButton);
            if (what.equals("proposal") || what.equals("intervals")) {
                oriz.add(cancelButton);
            }
            vert.add(oriz);
            info.setText("conflicts");
            vert.add(info);
            dock.add(vert, DockPanel.SOUTH);
            /*
            dock.add(okButton, DockPanel.SOUTH);
            if (proposal) {
            dock.add(cancelButton, DockPanel.SOUTH);
            }
            //  dock.add(msg, DockPanel.NORTH);
            dock.add(f, DockPanel.NORTH);


            dock.setCellHorizontalAlignment(okButton, DockPanel.ALIGN_CENTER);
            dock.setCellHorizontalAlignment(cancelButton, DockPanel.ALIGN_RIGHT);

             */
            dock.setWidth("100%");
            setWidget(dock);
        }

        public void onClick(ClickEvent evt) {
            Button b = (Button) evt.getSource();
            String which = b.getText();
            // Window.alert("Button= " + b.getText());
            if (what.equals("proposal") || what.equals("intervals")) {
                if (which.equals("OK")) {
                    if (what.equals("intervals") && time >= 0) {
                        // cambiamo ora di un task
                        tg.setSchedule(tg.getSelectedTask(), time);
                    }
                    TaskGroup.updateSchedule(tg);
                    riempi(taskTable, false, TaskGroup.current());
                    updateTasks(taskDefTable);
                    updateText(tName.getText());
                }
            }
            hide();
        }
    }

    private void iniziaLegenda(final FlexTable t) {

        FlexTable.CellFormatter form = t.getCellFormatter();
        t.setBorderWidth(1);
        t.setText(0, 0, "Legenda");

        t.setText(1, 0, "Conflict");
        form.addStyleName(1, 0, "styleConflict");
        t.setText(2, 0, "Available");
        form.addStyleName(2, 0, "styleAvailable");
        t.setText(3, 0, "Holiday");
        form.addStyleName(3, 0, "styleHoliday");
        t.setText(4, 0, "Free Slot");
        form.addStyleName(4, 0, "styleUnused");
        t.setText(5, 0, "Serious Conflict");
        form.addStyleName(5, 0, "styleSeriousConflict");

    }
    /* */

    private void iniziaUtenti(final FlexTable t) {

        FlexTable.CellFormatter form = t.getCellFormatter();
        t.setBorderWidth(1);
        t.setText(0, 0, "User name");
        t.setText(0, 1, "Show Tasks");

        ArrayList<UiUser> ids = UiUser.getUsers();
        int index = 1;
        for (UiUser u : ids) {
            t.setText(index, 0, u.getId());
            t.setWidget(index, 1, new CheckBox());
            form.addStyleName(index, 0, u.getStyle());
            index++;
        }


        // Let's put a button in the middle...
        //  t.setWidget(1, 0, new Button("Wide Button"));

        // ...and set it's column span so that it takes up the whole row.
        //  t.getFlexCellFormatter().setColSpan(1, 0, 3);

        t.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent event) {
                HTMLTable.Cell clickedCell = t.getCellForEvent(event);
                int rowIndex = clickedCell.getRowIndex();
                int colIndex = clickedCell.getCellIndex();
                if (colIndex == 1 && rowIndex > 0) { // click su un checkbox
                    usersToShow.clear();
                    for (int i = 1; i < t.getRowCount(); i++) {
                        CheckBox ch = (CheckBox) t.getWidget(i, 1);
                        if (ch.getValue()) {
                            usersToShow.add(t.getText(i, 0));
                        }
                    }
                    riempi(taskTable, false, TaskGroup.current());
                }
            }
        });

    }

    private ListBox iniziaUtentiOld() {
        final ListBox ret = new ListBox(true);
        ArrayList<String> ids = UiUser.getUserIds();
        for (String s : ids) {
            ret.addItem(s);
        }
        ret.addItem("*");
        ret.setVisibleItemCount(ids.size());
        ret.addChangeHandler(new ChangeHandler() {

            public void onChange(ChangeEvent event) {
                usersToShow.clear();
                for (int i = 0; i < ret.getItemCount(); i++) {
                    if (ret.isItemSelected(i)) {
                        usersToShow.add(ret.getItemText(i));
                    }
                }
                riempi(taskTable, false, TaskGroup.current());
            }
        });
        return ret;
    }

    private String selectedUser() {
        if (usersToShow.isEmpty()) {
            Window.alert("no user selected");
            return null;
        } else {
            return usersToShow.get(0);
        }
    }

    class DatePicker extends ListBox {

        int selectedValue = -1;

        public DatePicker(String what, int index) {
            if (what.equals("giorni")) {
                addItem("Monday");
                addItem("Tuesday");
                addItem("Wednesday");
                addItem("Thursday");
                addItem("Friday");
                setSelectedIndex(index);
            } else if (what.equals("ore")) {
                for (int i = 8; i <= 20; i++) {
                    addItem("" + i);
                }
                setSelectedIndex(index);
            } else {
                Window.alert("DatePicker, unknown option: " + what);
                setVisibleItemCount(1);
                addChangeHandler(new ChangeHandler() {

                    public void onChange(ChangeEvent event) {
                        selectedValue =
                                getSelectedIndex();
                    }
                });
            }
        }
    }

    //   class TooltipListener extends MouseListenerAdapter {
    class TooltipListener implements MouseOverHandler, MouseOutHandler {

        private static final String DEFAULT_TOOLTIP_STYLE = "TooltipPopup";
        private static final int DEFAULT_OFFSET_X = 40;
        private static final int DEFAULT_OFFSET_Y = 0;

        private class Tooltip extends PopupPanel {

            private int delay;

            public Tooltip(Widget sender, int offsetX, int offsetY,
                    final String text, final int delay, final String styleName) {
                super(true);

                this.delay = delay;

                HTML contents = new HTML(text);
                add(contents);

                int left = sender.getAbsoluteLeft() + offsetX;
                int top = sender.getAbsoluteTop() + offsetY;

                setPopupPosition(left, top);
                setStyleName(styleName);
            }

            public void show() {
                super.show();

                Timer t = new Timer() {

                    public void run() {
                        Tooltip.this.hide();
                    }
                };
                t.schedule(delay);
            }
        }
        private Tooltip tooltip;
        private String text;
        private String styleName;
        private int delay;
        private int offsetX = DEFAULT_OFFSET_X;
        private int offsetY = DEFAULT_OFFSET_Y;

        public TooltipListener(String text, int delay) {
            this(text, delay, DEFAULT_TOOLTIP_STYLE);
        }

        public TooltipListener(String text, int delay, String styleName) {
            this.text = text;
            this.delay = delay;
            this.styleName = styleName;
        }

        public void onMouseOver(MouseOverEvent e) {

            if (tooltip != null) {
                tooltip.hide();
            }
            Widget sender = (Widget) e.getSource();
            tooltip = new Tooltip(sender, offsetX, offsetY, text, delay, styleName);
            tooltip.show();
        }

        public void onMouseOut(MouseOutEvent e) {

            if (tooltip != null) {
                tooltip.hide();
            }

        }
        /*   public void onMouseEnter(Widget sender) {
        if (tooltip != null) {
        tooltip.hide();
        }
        tooltip = new Tooltip(sender, offsetX, offsetY, text, delay, styleName);
        tooltip.show();
        }

        public void onMouseLeave(Widget sender) {
        if (tooltip != null) {
        tooltip.hide();
        }
        }*/

        public String getStyleName() {
            return styleName;
        }

        public void setStyleName(String styleName) {
            this.styleName = styleName;
        }

        public int getOffsetX() {
            return offsetX;
        }

        public void setOffsetX(int offsetX) {
            this.offsetX = offsetX;
        }

        public int getOffsetY() {
            return offsetY;
        }

        public void setOffsetY(int offsetY) {
            this.offsetY = offsetY;
        }
    }
}
