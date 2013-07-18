/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.client;

import java.util.ArrayList;
import java.util.Date;

import com.bradrydzewski.gwt.calendar.client.Appointment;
import com.bradrydzewski.gwt.calendar.client.AppointmentStyle;
import com.bradrydzewski.gwt.calendar.client.Calendar;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings;
import com.bradrydzewski.gwt.calendar.client.CalendarViews;
import com.bradrydzewski.gwt.calendar.client.CalendarSettings.Click;
import com.bradrydzewski.gwt.calendar.client.event.CreateEvent;
import com.bradrydzewski.gwt.calendar.client.event.CreateHandler;
import com.bradrydzewski.gwt.calendar.client.event.DateRequestEvent;
import com.bradrydzewski.gwt.calendar.client.event.DateRequestHandler;
import com.bradrydzewski.gwt.calendar.client.event.DeleteEvent;
import com.bradrydzewski.gwt.calendar.client.event.DeleteHandler;
import com.bradrydzewski.gwt.calendar.client.event.MouseOverEvent;
import com.bradrydzewski.gwt.calendar.client.event.MouseOverHandler;
import com.bradrydzewski.gwt.calendar.client.event.TimeBlockClickEvent;
import com.bradrydzewski.gwt.calendar.client.event.UpdateEvent;
import com.bradrydzewski.gwt.calendar.client.event.UpdateHandler;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

import com.google.gwt.event.logical.shared.OpenEvent;
import com.google.gwt.event.logical.shared.OpenHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DecoratedPopupPanel;
import com.google.gwt.user.client.ui.DecoratedTabBar;
import com.google.gwt.user.client.ui.DecoratorPanel;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;
import java.util.List;

public class GoogleCalendarPanel extends FlowPanel {

    private Calendar calendar = null;
    private DatePicker datePicker = new DatePicker();
    private FlexTable layoutTable = new FlexTable();
    private AbsolutePanel leftPanel = new AbsolutePanel();
    private AbsolutePanel topPanel = new AbsolutePanel();
    private DecoratorPanel dayViewDecorator = new DecoratorPanel();
    private DecoratorPanel datePickerDecorator = new DecoratorPanel();
    private DecoratedTabBar calendarViewsTabBar = new DecoratedTabBar();
    private CalendarSettings settings = new CalendarSettings();
    static int newAppIndex = 1;

    public GoogleCalendarPanel(boolean picker) {

        // style this element as absolute position
        DOM.setStyleAttribute(this.getElement(), "position", "absolute");
        // DOM.setStyleAttribute(this.getElement(), "top", "20px");
        if (picker) {
            DOM.setStyleAttribute(this.getElement(), "top", "50px");
        }
        DOM.setStyleAttribute(this.getElement(), "left", "0px");

        configureCalendar();
        configureViewsTabBar();

        datePicker.setValue(new Date());
        datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {
            public void onValueChange(ValueChangeEvent<Date> event) {
                calendar.setDate(event.getValue());
            }
        });

        final Button leftWeekButton = new Button("<b>&lt;</b>");
        final Button rightWeekButton = new Button("<b>&gt;</b>");
        final Button todayButton = new Button("<b>Today</b>");

        leftWeekButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                clickChangeDateButton(-7);
            }
        });
        rightWeekButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                clickChangeDateButton(7);
            }
        });
        todayButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                clickChangeDateButton(0);
            }
        });

        topPanel.add(calendarViewsTabBar);
        topPanel.setStyleName("daysTabBar");
        leftPanel.setStyleName("leftPanel");

        leftPanel.add(datePickerDecorator);


        datePickerDecorator.add(datePicker);
        dayViewDecorator.add(calendar);

        layoutTable.setWidth("99%");
        layoutTable.setCellPadding(0);
        layoutTable.setCellSpacing(0);

        if (picker) {
            layoutTable.setText(0, 0, "");
            layoutTable.setWidget(0, 1, topPanel);

            layoutTable.setWidget(1, 1, dayViewDecorator);

            layoutTable.setWidget(1, 0, leftPanel);
        } else {
            layoutTable.setWidget(0, 0, dayViewDecorator);
        }
        layoutTable.getCellFormatter().setVerticalAlignment(1, 0,
                HasVerticalAlignment.ALIGN_TOP);
        layoutTable.getCellFormatter().setVerticalAlignment(1, 1,
                HasVerticalAlignment.ALIGN_TOP);
        if (picker) {
            layoutTable.getCellFormatter().setWidth(1, 0, "50px");
        }
        add(layoutTable);

        // window events to handle resizing
        Window.enableScrolling(false);
        Window.addResizeHandler(new ResizeHandler() {
            public void onResize(ResizeEvent event) {
                resizeTimer.schedule(500);
            }
        });
        Scheduler.get().scheduleDeferred(new ScheduledCommand() {
            public void execute() {
                calendar.setHeight(Window.getClientHeight() - 85 + "px");
            }
        });
        DOM.setStyleAttribute(getElement(), "padding", "10px");
    }

    /**
     * Configures the calendar widget in the calendar panel. Configuration
     * includes handlers for the
     * <code>delete</code> and
     * <code>open
     * appointment</code> operations, a set of random appointments and two multi
     * day appointments.
     *
     * @see AppointmentBuilder#build()
     */
    @SuppressWarnings("deprecation")
    private void configureCalendar() {

        // change hour offset to false to facilitate google style
        settings.setOffsetHourLabels(false);
        settings.setEnableDragDrop(true);
        settings.setEnableDragDropCreation(true);
        settings.setTimeBlockClickNumber(Click.Drag);

        calendar = new Calendar();
        calendar.setSettings(settings);
        //calendar.setView(Calendar.DAY_VIEW);
        calendar.setWidth("100%");

        calendar.addSelectionHandler(new SelectionHandler<Appointment>() {
            @Override
            public void onSelection(SelectionEvent<Appointment> event) {
                Appointment aa = event.getSelectedItem();
                //  Window.alert("selezionato: " + event.getSelectedItem().getTitle());
                aa.setStyle(AppointmentStyle.BLUE);
                Element el = DOM.getElementById(aa.getId());
                //  Window.alert("el1="+el);
                el = DOM.getElementById(aa.getTitle());
                //   Window.alert("el2="+el);
                // DOM.setStyleAttribute(aa.getElement(), "position", "absolute");

            }
        });
        calendar.addDeleteHandler(new DeleteHandler<Appointment>() {
            public void onDelete(DeleteEvent<Appointment> event) {
//            	boolean commit = true;
                boolean commit = Window
                        .confirm(
                        "Are you sure you want to delete appointment \""
                        + event.getTarget().getTitle() + "\"");
                if (!commit) {
                    event.setCancelled(true);
                    System.out.println("Cancelled Appointment deletion");
                } else {
                    MainEntryPoint.current.removeTask(event.getTarget().getTitle());
                }
            }
        });
        calendar.addUpdateHandler(new UpdateHandler<Appointment>() {
            public void onUpdate(UpdateEvent<Appointment> event) {
                boolean commit = Window
                        .confirm(
                        "Are you sure you want to update the appointment \""
                        + event.getTarget().getTitle() + "\"");
                if (!commit) {
                    event.setCancelled(true);
                    System.out.println("Cancelled Appointment update");
                } else {
                    Appointment ap = event.getTarget();
                    MainEntryPoint.current.updateTask(ap);
                }
            }
        });
        calendar.addOpenHandler(new OpenHandler<Appointment>() {
            public void onOpen(OpenEvent<Appointment> event) {
                if (true) {
                    MainEntryPoint.current.updateText(event.getTarget().getTitle());
                  //   Window.alert("OPEN");
                    MainEntryPoint.current.dialogoTask(null,-1);
                } else {
                    final DialogBox dialogBox =
                            createCalendaryEventDialogBox(event);
                    dialogBox.center();
                    dialogBox.show();
                    System.out.println("You double-clicked appointment \""
                            + event.getTarget().getTitle() + "\"");
                }
            }
        });
        calendar.addCreateHandler(new CreateHandler<Appointment>() {
            public void onCreate(CreateEvent<Appointment> event) {

                /*   boolean commit = Window
                 .confirm("Are you sure you want to create a new appointment");
                 if (!commit) {
                 event.setCancelled(true);
                 System.out.println("Cancelled Appointment creation");
                 } else {*/
                //  MainEntryPoint.current.updateText("New Appointment");
                Appointment ap = event.getTarget();
                Date ad = ap.getStart();
                Date now = new Date();
                int or = ad.getHours() - 8;
                int schedule = (ad.getDate() - now.getDate()) * 12 + or;
              //  Window.alert("sched: "+schedule);
                MainEntryPoint.current.dialogoTask("New Appointment"+ newAppIndex++, schedule);
                /*   Window.alert("sciopa");
                 Appointment app = event.getTarget();
                 app.setTitle("New Appointment");
                 calendar.addAppointment(app);*/
                //   }
            }
        });
        final DecoratedPopupPanel appointmentPopup = new DecoratedPopupPanel(true);
        appointmentPopup.setAutoHideEnabled(true);

        calendar.addMouseOverHandler(new MouseOverHandler<Appointment>() {
            @Override
            public void onMouseOver(MouseOverEvent<Appointment> event) {
                Element element = (Element) event.getElement();
                String ute = TaskGroup.get(event.getTarget().getTitle()).getUsersAsString();
                appointmentPopup.setWidget(new HTML(ute));

                int left = element.getAbsoluteLeft() + 10;
                int top = element.getAbsoluteTop() + 10;
                appointmentPopup.setPopupPosition(left, top);
                appointmentPopup.show();
            }
        });

        ArrayList<Appointment> appts = AppointmentBuilder.build();
        calendar.suspendLayout();
        if (false) {
            // usiamo i nostri
            calendar.addAppointments(appts);
        }

        // Create a couple of multi day appointments for demoing purposes...
        Date today = new Date();

        Appointment multiDayAppt = new Appointment();
        multiDayAppt.setStyle(AppointmentStyle.BLUE);
        multiDayAppt.setStart(
                new Date(today.getYear(), today.getMonth(), today.getDate(),
                0, 0, 0));
        multiDayAppt.setEnd(new Date(today.getYear(), today.getMonth(),
                today.getDate() + 14));
        multiDayAppt.setTitle("All day 1");
//        multiDayAppt.setMultiDay(true);
        calendar.addAppointment(multiDayAppt);

        Appointment multiDayApptA = new Appointment();
        multiDayApptA.setStart(
                new Date(today.getYear(), today.getMonth(), today.getDate(),
                0, 0, 0));
        multiDayApptA.setEnd(new Date(today.getYear(), today.getMonth(),
                today.getDate() + 14));
        multiDayApptA.setTitle("All day A");
        multiDayApptA.setStyle(AppointmentStyle.RED);
//        multiDayApptA.setMultiDay(true);
        calendar.addAppointment(multiDayApptA);

        Appointment multiDayAppt2 = new Appointment();
        multiDayAppt2.setStart(
                new Date(today.getYear(), today.getMonth(), today.getDate() + 3,
                0, 0, 0));
        multiDayAppt2.setEnd(new Date(today.getYear(), today.getMonth(),
                today.getDate() + 6));
        multiDayAppt2.setTitle("All day 2");
        multiDayAppt2.setStyle(AppointmentStyle.RED);
//        multiDayAppt2.setMultiDay(true);
        calendar.addAppointment(multiDayAppt2);

        calendar.resumeLayout();



        calendar.addDateRequestHandler(new DateRequestHandler<Date>() {
            public void onDateRequested(DateRequestEvent<Date> event) {
                Window.alert("requested: " + event.getTarget() + " " + ((Element) event.getClicked()).getInnerText());
            }
        });
    }

    /**
     * Configures the tab bar that allows users to switch views in the calendar.
     */
    private void configureViewsTabBar() {
        calendarViewsTabBar.addTab("1 Day");
        calendarViewsTabBar.addTab("3 Day");
        calendarViewsTabBar.addTab("Work Week");
        calendarViewsTabBar.addTab("Week");
        calendarViewsTabBar.addTab("Agenda");
        calendarViewsTabBar.addTab("Month");
        calendarViewsTabBar.selectTab(1);
        calendarViewsTabBar
                .addSelectionHandler(new SelectionHandler<Integer>() {
            public void onSelection(SelectionEvent<Integer> event) {
                int tabIndex = event.getSelectedItem();
                if (tabIndex == 0) {
                    calendar.setView(CalendarViews.DAY, 1);
                } else if (tabIndex == 1) {
                    calendar.setView(CalendarViews.DAY, 3);
                } else if (tabIndex == 2) {
                    calendar.setView(CalendarViews.DAY, 5);
                } else if (tabIndex == 3) {
                    calendar.setView(CalendarViews.DAY, 7);
                } else if (tabIndex == 4) {
                    Window.alert("Agenda View will not be included in version 0.9.0");
                } //calendar.setView(CalendarViews.AGENDA);
                else if (tabIndex == 5) {
                    calendar.setView(CalendarViews.MONTH);
                }
            }
        });
    }

    private DialogBox createCalendaryEventDialogBox(Object event) {
        // Create a dialog box and set the caption text

        final DialogBox dialogBox = new DialogBox();
        dialogBox.ensureDebugId("cwDialogBox");
        dialogBox.setText("Calendar event");

        // Create a table to layout the content
        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        dialogBox.setWidget(dialogContents);

        // Add some text to the top of the dialog
        HTML eventName = new HTML("Event Name");
        dialogContents.add(eventName);
        final TextBox eventNameText = new TextBox();
        dialogContents.add(eventNameText);
        eventNameText.ensureDebugId("cwBasicText-textbox");
        eventNameText.setFocus(true);
        eventNameText.selectAll();
        HTML when = new HTML("When");
        dialogContents.add(when);
        final TextBox eventWhenText = new TextBox();
        dialogContents.add(eventWhenText);
        HTML until = new HTML("To");
        dialogContents.add(until);
        final TextBox eventUntilText = new TextBox();
        dialogContents.add(eventUntilText);
        // Description
        HTML description = new HTML("Description");
        dialogContents.add(description);
        // Add a text area
        final TextArea descriptionText = new TextArea();
        descriptionText.ensureDebugId("cwBasicText-textarea");
        descriptionText.setVisibleLines(5);

        dialogContents.add(descriptionText);

        OpenEvent<Appointment> targetAppointment = null;
        Appointment ap = null;
        if (event instanceof OpenEvent) {
            targetAppointment = (OpenEvent<Appointment>) event;
            Appointment appt = targetAppointment.getTarget();
            eventNameText.setText(appt.getTitle());
            eventWhenText
                    .setText(appt.getStart().toString());

            eventUntilText.setText(appt.getEnd().toString());
            descriptionText.setText(appt.getDescription());
            ap = appt;
        } else if (event instanceof TimeBlockClickEvent) {
            TimeBlockClickEvent<Date> clickedDate =
                    (TimeBlockClickEvent<Date>) event;
            Date startDate = clickedDate.getTarget();
            eventWhenText.setText(startDate.toString());
            ap = new Appointment();
            ap.setTitle("(No title)");
            ap.setStart(startDate);
            Date endDate = (Date) startDate.clone();
            // default time is 1 hour
            endDate.setHours(startDate.getHours() + 1);
            ap.setEnd(endDate);
            calendar.addAppointment(ap);
        }
        final Appointment appointment = ap;

        // Add a close button at the bottom of the dialog
        Button closeButton = new Button("Close", new ClickHandler() {
            public void onClick(ClickEvent event) {
                appointment.setTitle(eventNameText.getText());
                appointment.setDescription(
                        descriptionText.getText());
                dialogBox.hide();
                //Window.alert("titolo="+appointment.getTitle());
                calendar.fireUpdateEvent(appointment);
            }
        });

        // NUOVO
        MainEntryPoint.current.updateText(appointment.getTitle());
        // FINE nuovo
        dialogContents.add(closeButton);
        // Add a close button at the bottom of the dialog
        Button deleteButton = new Button("Delete", new ClickHandler() {
            public void onClick(ClickEvent event) {
                calendar.removeAppointment(appointment);
                dialogBox.hide();
            }
        });
        dialogContents.add(deleteButton);

        return dialogBox;

    }

    private void clickChangeDateButton(int numOfDays) {
        if (numOfDays == 0) {
            calendar.setDate(new Date());
        } else {
            calendar.addDaysToDate(numOfDays);
        }
    }
    private int height = -1;
    private Timer resizeTimer = new Timer() {
        @Override
        public void run() {
            int newHeight = Window.getClientHeight();
            if (newHeight != height) {
                height = newHeight;
                calendar.setHeight(height - 85 + "px");
                calendar.doSizing();
                calendar.doLayout();
            }
        }
    };

    public void setAppt(List<Task> appts, boolean clear, Task selectedTask) {
        if (clear) {
            calendar.clearAppointments();
        }
        for (Task a : appts) {
            a.updateAppo();
            // do not add task with same schedule
            if (selectedTask != null && selectedTask.getSchedule() == a.getFirstStartHour()) {
                continue;
            }
            calendar.addAppointment(a.getAppo());
        }
        calendar.doLayout();
    }

    public void addAppt(Task app) {
        // calendar.clearAppointments();

        calendar.addAppointment(app.getAppo());

    }

    public void removeAppt(Task app) {
        // calendar.clearAppointments();

        calendar.removeAppointment(app.getAppo());

    }

    public void setOra() {
        calendar.setDays(7);
        calendar.scrollToHour(7);
        calendar.doLayout();
    }

    public Appointment getSelectedApp() {
        return calendar.getSelectedAppointment();
    }

    public void selectTask(Task t) {
        t.getAppo().setStyle(AppointmentStyle.BLUE);
    }
}
