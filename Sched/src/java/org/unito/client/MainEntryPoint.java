/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Main entry point.
 *
 * @author marino
 */
public class MainEntryPoint implements EntryPoint {

    final FlexTable taskTable = new FlexTable();
    final FlexTable taskDefTable = new FlexTable();
    private Label lblServerReply = new Label();
    int partenza = 8;
    int fine = 20;
    int orario = fine - partenza;
    Label label1 = new Label("Task name:");
    TextBox te1 = new TextBox();
    Label label2 = new Label("Duration:");
    TextBox te2 = new TextBox();
    Label label3 = new Label("Start:");
    TextBox te3 = new TextBox();
    Label label4 = new Label("End:");
    TextBox te4 = new TextBox();
    Label label5 = new Label("Before:");
    TextBox te5 = new TextBox();
    Label label6 = new Label("After:");
    TextBox te6 = new TextBox();
    Label label7 = new Label("Schedule:");
    TextBox te7 = new TextBox();
    // Label label7 = new Label("Overlap:");
    CheckBox ch7 = new CheckBox("Can overlap other tasks ");

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
        iniziaTable(taskTable);
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
        h.add(te3);
        vert.add(h);
        h = new HorizontalPanel();
        label4.setWidth("100px");
        h.add(label4);
        h.add(te4);
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
        h.add(te7);
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
                String msg = TaskGroup.checkAndAddTask(te1.getText(), te3.getText(), te4.getText(), te2.getText(), te5.getText(), te6.getText(), te7.getText(), ch7.getValue());
                if (!msg.equals("")) {
                    Window.alert(msg);
                    return;
                }
                riempi(taskTable, false);
                updateText(te1.getText());
                updateTasks();
            }
        });
        final Button changeButton = new Button("Change");
        h.add(changeButton);
        changeButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                Task tat = new Task(te1.getText(), te3.getText(), te4.getText(), te2.getText(), te5.getText(), te6.getText(), te7.getText(), ch7.getValue());
                TaskGroup.change(tat);
                //      Window.alert("add task "+ Task.get(te1.getText()).toString());
                // INUTILE???     TaskGroup.current().setSchedule(tat);
                riempi(taskTable, false);
                updateText(te1.getText());
                updateTasks();
            }
        });
        final Button removeButton = new Button("Remove");
        h.add(removeButton);
        removeButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                getService().removeTask(te1.getText(), prova);
                TaskGroup.remove(te1.getText());
                riempi(taskTable, false);
                updateText("");
                updateTasks();
            }
        });
        final Button moveButton = new Button("Where can I place the task? ");
        h.add(moveButton);
        moveButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                final String tName = te1.getText();
                if (TaskGroup.exists(tName)) {
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "startintervals", "move", callbackTaskSuggest);
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
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "startintervals", "insert", callbackTaskSuggest);
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
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "tasknet", "move", callbackTaskSuggest);
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
                    getService().scheduleRequest(new ViaVai(TaskGroup.current()), tName, "tasknet", "insert", callbackTaskSuggest);
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
                riempi(taskTable, false);
                updateTasks();
            }
        });
        final Button liliButton = new Button("Esempio Liliana ");
        h.add(liliButton);
        liliButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                TaskGroup.reset();
                TaskGroup.addTaskGroup();
                TaskGroup.esempioLili();
                riempi(taskTable, false);
                updateTasks();
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
        final Button showListButton = new Button("Show task list");
        schedVertPanel.add(showListButton);
        showListButton.addClickHandler(new ClickHandler() {

            public void onClick(ClickEvent event) {
                initDefTable(taskDefTable);
                DialogBox dlg = new MyDialog(taskDefTable);
                dlg.center();
            }
        });
        h11.add(schedVertPanel);
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

    private void iniziaTable(final FlexTable t) {
        FlexTable.FlexCellFormatter form = t.getFlexCellFormatter();
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
        riempi(t, false);

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
        FlexTable.FlexCellFormatter form = t.getFlexCellFormatter();
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
        updateTasks();

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

    void addRow(Task ta) {
        int row = taskDefTable.getRowCount();
        taskDefTable.setText(row, 0, ta == null ? "" : ta.getName());
        taskDefTable.setText(row, 1, ta == null ? "" : "" + ta.getDuration());
        taskDefTable.setText(row, 2, ta == null ? "" : "" + ta.getMinStartHour());
        taskDefTable.setText(row, 3, ta == null ? "" : "" + ta.getMaxEndHour());
        taskDefTable.setText(row, 4, ta == null ? "" : ta.beforeString());
        taskDefTable.setText(row, 5, ta == null ? "" : ta.afterString());
        taskDefTable.setText(row, 6, ta == null ? "" : "" + ta.getOverlap());
        taskDefTable.setText(row, 7, ta == null ? "" : "" + ta.getOfficialScheduleAsString());
    }

    private void updateTasks() {
        while (taskDefTable.getRowCount() > 1) {
            taskDefTable.removeRow(taskDefTable.getRowCount() - 1);
        }
        for (Task ta : TaskGroup.current().getTasks()) {
            addRow(ta);
        }
        addRow(null);
    }

    private void updateText(String s) {
        if (!s.equals("")) {
            Task ta = TaskGroup.get(s);
            te1.setText(ta.getName());
            te2.setText("" + ta.getDuration());
            te3.setText("" + ta.getMinStartHour());
            te4.setText("" + ta.getMaxEndHour());
            te5.setText(ta.beforeString());
            te6.setText(ta.afterString());
            te7.setText("" + ta.getOfficialScheduleAsString());
            ch7.setValue(ta.getOverlap());
        } else {
            te1.setText("");
            te2.setText("");
            te3.setText("");
            te4.setText("");
            te5.setText("");
            te6.setText("");
            te7.setText("");
            ch7.setValue(false);
        }
    }

    private void riempi(FlexTable t, boolean showAlt) {
        FlexTable.FlexCellFormatter form = t.getFlexCellFormatter();
        String[] vals = TaskGroup.retr(showAlt);
        int k = 0;
        for (int j = 0; j < 7 + 1; j++) {  // migliorare i colori
            for (int i = 0; i < orario; i++) {
                if (vals[k].equals("***")) {
                    t.setText(i + 1, j + 1, "");
                    form.setStyleName(i + 1, j + 1, "styleAvailable");
                } else if (vals[k].equals("")) {
                    t.setText(i + 1, j + 1, "");
                    form.setStyleName(i + 1, j + 1, "styleUnused");
                } else {
                    t.setText(i + 1, j + 1, vals[k]);
                    form.setStyleName(i + 1, j + 1, "styleBusy");
                }
                k++;
            }
        }
    }
    // Create an asynchronous callback to handle the result.
    final AsyncCallback<ViaVai> callbackTask = new AsyncCallback<ViaVai>() {

        public void onSuccess(ViaVai result) {
            lblServerReply.setText("successo");
            if (result == null) {
                Window.alert("no solutions");
            } else {
                TaskGroup.updateSchedule(new TaskGroup(result));
                riempi(taskTable, false);
                updateTasks();
                updateText(te1.getText());
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
                TaskGroup.current().updateTaskSlots(new TaskGroup(result));
                riempi(taskTable, true);
                updateTasks();
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

    class MyDialog extends DialogBox implements ClickListener {

        public MyDialog(FlexTable f) {
            setText("Specification of all tasks");

            Button closeButton = new Button("Close", this);
            // HTML msg = new HTML("<center>A standard dialog box component.</center>",true);

            DockPanel dock = new DockPanel();
            dock.setSpacing(4);

            dock.add(closeButton, DockPanel.SOUTH);
            //  dock.add(msg, DockPanel.NORTH);
            dock.add(f, DockPanel.NORTH);


            dock.setCellHorizontalAlignment(closeButton, DockPanel.ALIGN_RIGHT);
            dock.setWidth("100%");
            setWidget(dock);
        }

        public void onClick(Widget sender) {
            hide();
        }
    }
}
