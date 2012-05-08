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

    final FlexTable taskTable = new FlexTable();//(13,8);
    final FlexTable taskDefTable = new FlexTable();//(13,8);
    private Label lblServerReply = new Label();
    DatePicker dayStart = new DatePicker("giorni", 0);
    DatePicker timeStart = new DatePicker("ore", 0);
    DatePicker dayEnd = new DatePicker("giorni", 4);
    DatePicker timeEnd = new DatePicker("ore", 11);
    DatePicker daySchedule = new DatePicker("giorni", 0);
    DatePicker timeSchedule = new DatePicker("ore", 0);
    int partenza = 8;
    int fine = 20;
    int orario = fine - partenza;
    Label label1 = new Label("Task name:");
    TextBox te1 = new TextBox();
    Label label2 = new Label("Duration:");
    TextBox te2 = new TextBox();
    Label label3 = new Label("Start:");
    //   TextBox te3 = new TextBox();
    Label label4 = new Label("End:");
    //   TextBox te4 = new TextBox();
    Label label5 = new Label("Before:");
    TextBox te5 = new TextBox();
    Label label6 = new Label("After:");
    TextBox te6 = new TextBox();
    Label label7 = new Label("Schedule:");
    //  TextBox te7 = new TextBox();
    Label label8 = new Label("Users:");
    TextBox te8 = new TextBox();
    Label label9 = new Label("Select User:");
    // Label label7 = new Label("Overlap:");
    CheckBox ch7 = new CheckBox("Can overlap other tasks ");
    ArrayList<String> currentUsers = new ArrayList();

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
        TaskGroup.addTaskGroup();
        TaskGroup.esempio();

        iniziaTable(taskTable, TaskGroup.current());
        HorizontalPanel h11 = new HorizontalPanel();
        h11.add(taskTable);

        //   RootPanel.get().add(taskTable);
        VerticalPanel vert = new VerticalPanel();
        //   vert.setBorderWidth(1);
        vert.setSpacing(10);
        HorizontalPanel h = new HorizontalPanel();
        label1.setWidth("100px");
        h.add(label1);
        h.add(te1);
        vert.add(h);
        h = new HorizontalPanel();
        label2.setWidth("100px");
        h.add(label2);
        h.add(te2);
        vert.add(h);
        h = new HorizontalPanel();
        label3.setWidth("100px");
        h.add(label3);
        h.add(dayStart);
        h.add(timeStart); // h.add(te3);
        vert.add(h);
        h = new HorizontalPanel();
        label4.setWidth("100px");
        h.add(label4);
        h.add(dayEnd);
        h.add(timeEnd); //h.add(te4);
        vert.add(h);
        h = new HorizontalPanel();
        label5.setWidth("100px");
        h.add(label5);
        h.add(te5);
        vert.add(h);
        h = new HorizontalPanel();
        label6.setWidth("100px");
        h.add(label6);
        h.add(te6);
        vert.add(h);
        h = new HorizontalPanel();
        label7.setWidth("100px");
        h.add(label7);
        h.add(daySchedule);
        h.add(timeSchedule); // h.add(te7);
        vert.add(h);
        h = new HorizontalPanel();
        label8.setWidth("100px");
        h.add(label8);
        h.add(te8);
        vert.add(h);
        h = new HorizontalPanel();
        ch7.setWidth("100px");
        h.add(ch7);
        vert.add(h);
        h = new HorizontalPanel();
        final Button addButton = new Button("Add");
        h.add(addButton);
        addButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                if (TaskGroup.current().get(te1.getText()) != null) {
                    Window.alert("task already existent: " + te1.getText());
                    return;
                }
                //  Window.alert("giornoSelezionato: " + dayStart.getSelectedIndex());
                String vv = stringVal(dayStart, timeStart);
                String vvv = stringVal(dayEnd, timeEnd);
                String vvvv = stringVal(daySchedule, timeSchedule);
                String msg = TaskGroup.checkTask(te1.getText(), vv, vvv, te2.getText(), te5.getText(), te6.getText(), vvvv, te8.getText(), ch7.getValue());
                if (!msg.equals("")) {
                    Window.alert(msg);
                    return;
                }
                int sch = TaskGroup.addScheduleTask(te1.getText(), vv, vvv, te2.getText(), te5.getText(), te6.getText(), vvvv, te8.getText(), ch7.getValue());
                if (sch == -1) {
                    boolean callScheduler = Window.confirm("task has not been scheduled, do you want to change something?");
                    if (callScheduler) {
                        Window.alert("still to be done");
                    }
                    return;
                }
                riempi(taskTable, false, TaskGroup.current());
                updateText(te1.getText());
                updateTasks(taskDefTable);
            }
        });
        final Button changeButton = new Button("Change");
        h.add(changeButton);
        changeButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                String vv = stringVal(dayStart, timeStart);
                String vvv = stringVal(dayEnd, timeEnd);
                String vvvv = stringVal(daySchedule, timeSchedule);
                String msg = TaskGroup.checkTask(te1.getText(), vv, vvv, te2.getText(), te5.getText(), te6.getText(), vvv, te8.getText(), ch7.getValue());
                if (!msg.equals("")) {
                    Window.alert(msg);
                }
                Task tat = new Task(te1.getText(), vv, vvv, te2.getText(), te5.getText(), te6.getText(), vvv, te8.getText(), ch7.getValue());
                msg = TaskGroup.change(tat);
                if (!msg.equals("")) {
                    Window.alert(msg);
                }
                //      Window.alert("add task "+ Task.get(te1.getText()).toString());
                // INUTILE???     TaskGroup.current().setSchedule(tat);
                riempi(taskTable, false, TaskGroup.current());
                updateText(te1.getText());
                updateTasks(taskDefTable);
            }
        });
        final Button removeButton = new Button("Remove");
        h.add(removeButton);
        removeButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                getService().removeTask(te1.getText(), prova);
                TaskGroup.remove(te1.getText());
                riempi(taskTable, false, TaskGroup.current());
                updateText("");
                updateTasks(taskDefTable);
            }
        });
        final Button moveButton = new Button("Where can I place the task? ");
        h.add(moveButton);
        moveButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final String tName = te1.getText();
                if (TaskGroup.exists(tName)) {
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "startintervals", "", "move", "pippo", callbackTaskSuggest);
                } else {
                    Window.alert("task=NOT found:" + tName);
                }
            }
        });
        final Button insertButton = new Button("Insert(startIntervals)");
        h.add(insertButton);
        insertButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final String tName = te1.getText();
                if (TaskGroup.exists(tName)) {
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "startintervals", "", "insert", "pippo", callbackTaskSuggest);
                } else {
                    Window.alert("task=NOT found:" + tName);
                }
            }
        });
        final Button moveNetButton = new Button("move(taskNet) ");
        h.add(moveNetButton);
        moveNetButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final String tName = te1.getText();
                if (TaskGroup.exists(tName)) {
                    TaskGroup.current().setChoiceForTask(tName);
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "tasknet", "", "move", "pippo", callbackTaskSuggest);
                } else {
                    Window.alert("task=NOT found:" + tName);
                }
            }
        });
        final Button insertNetButton = new Button("Insert(taskNet)");
        h.add(insertNetButton);
        insertNetButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final String tName = te1.getText();
                if (TaskGroup.exists(tName)) {
                    TaskGroup.current().setChoiceForTask(tName);
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "tasknet", "", "insert", "pippo", callbackTaskSuggest);
                } else {
                    Window.alert("task=NOT found:" + tName);
                }
            }
        });
        final Button lunchButton = new Button("Lunch");
        h.add(lunchButton);
        lunchButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {

                TaskGroup.lunch();
                riempi(taskTable, false, TaskGroup.current());
                updateTasks(taskDefTable);
            }
        });
        final Button liliButton = new Button("Esempio Liliana ");
        h.add(liliButton);
        liliButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                TaskGroup.reset();
                TaskGroup.addTaskGroup();
                TaskGroup.esempioLili();
                riempi(taskTable, false, TaskGroup.current());
                updateTasks(taskDefTable);
            }
        });

        VerticalPanel schedVertPanel = new VerticalPanel();
        schedVertPanel.setSpacing(70);
        schedVertPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
        final Button schedule1Button = new Button("Schedule early tasks first ");
        schedVertPanel.add(schedule1Button);
        schedule1Button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                // Window.alert("bottonr cliccato size " + TaskGroup.current().getTasks().size());

                getService().schedule(new ViaVai(TaskGroup.current()), "start", "new", callbackTask);
                // getService().schedule(new TaskGroup(), "start", callbackTask);
                //   getService().myMethod("mar", prova);

            }
        });
        final Button schedule2Button = new Button("Schedule urgent tasks first");
        schedVertPanel.add(schedule2Button);
        schedule2Button.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                getService().schedule(new ViaVai(TaskGroup.current()), "end", "new", callbackTask);

            }
        });
        final Button showListButton = new Button("Show full task list");
        schedVertPanel.add(showListButton);
        showListButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {


                DialogBox dlg = new MyDialog("Specification of all tasks", null, false);
                dlg.center();
            }
        });
        h11.add(schedVertPanel);
        FlexTable li9 = new FlexTable();
        iniziaUtenti(li9);
        h11.add(label9);
        h11.add(li9);
        RootPanel.get().add(h11);
        vert.add(h);
        h = new HorizontalPanel();
        h.setSpacing(30);
        //   initDefTable(taskDefTable);
        //    RootPanel.get().add(taskDefTable);
        h.add(vert);
        //    h.add(taskDefTable);
        RootPanel.get().add(h);
        RootPanel.get().add(lblServerReply);

    }

    private void iniziaTable(final FlexTable t, TaskGroup tg) {
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
        riempi(t, false, tg);

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
            te1.setText(ta.getName());
            te2.setText("" + ta.getDuration());
            dayStart.setSelectedIndex(Task.dayOf(ta.getMinStartHour()));
            timeStart.setSelectedIndex(Task.timeOf(ta.getMinStartHour()));
            // te4.setText("" + ta.getMaxEndHour());
            dayEnd.setSelectedIndex(Task.dayOf(ta.getMaxEndHour()));
            timeEnd.setSelectedIndex(Task.timeOf(ta.getMaxEndHour()));
            te5.setText(ta.beforeString());
            te6.setText(ta.afterString());

            daySchedule.setSelectedIndex(Task.dayOf(ta.getOfficialSchedule()));
            timeSchedule.setSelectedIndex(Task.timeOf(ta.getOfficialSchedule()));

            //  te7.setText("" + ta.getOfficialScheduleAsString());
            te8.setText(ta.userString());
            ch7.setValue(ta.getOverlap());
        } else {
            te1.setText("");
            te2.setText("");
            dayStart.setSelectedIndex(0);
            timeStart.setSelectedIndex(0);
            // te4.setText("");
            dayEnd.setSelectedIndex(0);
            timeEnd.setSelectedIndex(0);
            te5.setText("");
            te6.setText("");
            daySchedule.setSelectedIndex(0);
            timeSchedule.setSelectedIndex(0);
            //  te7.setText("");
            te8.setText("");
            ch7.setValue(false);
        }
    }

    private void checkUserConflicts(TaskGroup te) {

        ArrayList<Interval> inters = te.getCurrSchedule();
        String msg = "";
        for (Interval inte : inters) {
            ArrayList<String> use = inte.getUsers();
            if (!use.isEmpty()) {
                msg += "schedule: " + inte.getMin() + " conflicts with users: ";
                for (String s : use) {
                    msg += s + " ";
                }
                msg += "\n";
            }
        }
        if (!msg.equals("")) {
            Window.alert(msg);
        }
    }

    private void riempi(FlexTable t, boolean showAlt, TaskGroup tg) {
        FlexTable.CellFormatter form = t.getCellFormatter();

        ArrayList<String>[] vals = TaskGroup.retr(showAlt, tg);

        int k = 0;
        for (int j = 0; j < 7 + 1; j++) {  // migliorare i colori
            for (int i = 0; i < orario && k < vals.length; i++) {
                ArrayList<String> curr = vals[k];
                if (curr.isEmpty()) {
                    t.setText(i + 1, j + 1, "");
                    form.setStyleName(i + 1, j + 1, "styleUnused");
                } else if (curr.get(0).equals("***")) {
                    t.setText(i + 1, j + 1, "");
                    form.setStyleName(i + 1, j + 1, "styleAvailable");
                } else {
                    t.setWidget(i + 1, j + 1, bottoni(curr));
                    /* VECCHIO
                    t.setText(i + 1, j + 1, curr.get(0));
                    UiUser uu = TaskGroup.ContainsUser(currentUsers, curr.get(0));
                    if (uu != null) {
                    form.setStyleName(i + 1, j + 1, uu.getStyle());
                    } else {
                    form.setStyleName(i + 1, j + 1, "styleBusy");
                    }                   
                     */
                }
                k++;
            }
        }
        for (int j = 5; j < 7 + 1; j++) {
            for (int i = 0; i < orario; i++) {
                form.setStyleName(i + 1, j + 1, "styleHoliday");
            }
        }
    }

    private Widget bottoni(ArrayList<String> tas) {
        ArrayList<UiUser> thisBox = new ArrayList();
        HorizontalPanel oriz = new HorizontalPanel();
        for (String ta : tas) {
            final Button bu = new Button(ta);
            Task t = TaskGroup.get(ta);

            // bu.addMouseListener(
            //          new TooltipListener(
            //          t.userString(), 5000 /* timeout in milliseconds*/, "yourcssclass"));
            TooltipListener tip = new TooltipListener(
                    t.userString(), 5000, "yourcssclass");
            bu.addMouseOverHandler(tip);
            bu.addMouseOutHandler(tip);
            bu.addClickHandler(new ClickHandler() {

                public void onClick(ClickEvent event) {
                    String vv = bu.getText();
                    updateText(vv);
                }
            });
            ArrayList<UiUser> uu = TaskGroup.ContainsUser(currentUsers, ta);
            for (UiUser us : uu) {
                if (!thisBox.contains(us)) {
                    thisBox.add(us);
                }
            }
            oriz.add(bu);
        }
        // add colors for all users busy in this interval
        for (UiUser ui : thisBox) {
            Label l = new Label("");
            l.setSize("10px", "22px");
            l.setStyleName(ui.getStyle());
            oriz.add(l);
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

                DialogBox dlg = new MyDialog("New Schedule", new TaskGroup(result), true);
                dlg.center();
                /*
                TaskGroup.updateSchedule(new TaskGroup(result));
                riempi(taskTable, false);
                updateTasks();
                updateText(te1.getText());

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
                /*   DialogBox dlg = new MyDialog("New Schedule", new TaskGroup(result), true);
                dlg.center();*/
                //   era, proviamo, magari mancano i tasks originali
                // cerchiamo i conflitti

                // per ORA settiamo nuovo schedule a corrente e mostriamo all'utente i conflitti
                // dandolo gia' per buono in seguito decidiamo come fare
                TaskGroup.current().updateTaskSlots(new TaskGroup(result));

                checkUserConflicts(TaskGroup.current());

                riempi(taskTable, true, TaskGroup.current());
                updateTasks(taskDefTable);
                updateText(te1.getText());
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

    private String stringVal(ListBox day, ListBox time) {

        return "" + (day.getSelectedIndex() * 12 + time.getSelectedIndex());
    }

    class MyDialog extends DialogBox implements ClickHandler {

        private boolean proposal = false;
        private TaskGroup tg;

        public MyDialog(String title, TaskGroup tg, boolean proposal) {

            this.proposal = proposal;
            this.tg = tg;

            setText(title);
            FlexTable f = null;
            if (tg != null) {

                // it's a new task schedule proposal, show it
                f = new FlexTable();
                iniziaTable(f, tg);
            } else {

                // just show the task list of the current proposal
                f = taskDefTable;
                initDefTable(f);

            }
            Button okButton = new Button("OK", this);
            Button cancelButton = new Button("Cancel", this);
            HorizontalPanel oriz = new HorizontalPanel();
            // HTML msg = new HTML("<center>A standard dialog box component.</center>",true);

            DockPanel dock = new DockPanel();
            dock.setSpacing(4);
            dock.add(f, DockPanel.CENTER);
            dock.add(oriz, DockPanel.SOUTH);
            oriz.add(okButton);
            if (proposal) {
                oriz.add(cancelButton);
            }

            ;            /*
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
            if (proposal) {
                if (which.equals("OK")) {
                    TaskGroup.updateSchedule(tg);
                    riempi(taskTable, false, TaskGroup.current());
                    updateTasks(taskDefTable);
                    updateText(te1.getText());
                }
            }
            hide();
        }
    }


    /* */
    private void iniziaUtenti(final FlexTable t) {

        FlexTable.CellFormatter form = t.getCellFormatter();
        t.setBorderWidth(1);
        t.setText(0, 0, "User name");
        t.setText(0, 1, "Show");

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
                    currentUsers.clear();
                    for (int i = 1; i < t.getRowCount(); i++) {
                        CheckBox ch = (CheckBox) t.getWidget(i, 1);
                        if (ch.isChecked()) {
                            currentUsers.add(t.getText(i, 0));
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
                currentUsers.clear();
                for (int i = 0; i < ret.getItemCount(); i++) {
                    if (ret.isItemSelected(i)) {
                        currentUsers.add(ret.getItemText(i));
                    }
                }
                riempi(taskTable, false, TaskGroup.current());
            }
        });
        return ret;
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
                for (int i = 8; i < 20; i++) {
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
        private static final int DEFAULT_OFFSET_X = 10;
        private static final int DEFAULT_OFFSET_Y = 35;

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
