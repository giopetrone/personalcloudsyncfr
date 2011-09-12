/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vt.client;

import com.allen_sauer.gwt.dnd.client.PickupDragController;
import com.allen_sauer.gwt.dnd.client.drop.AbsolutePositionDropController;
import com.allen_sauer.gwt.dnd.client.drop.DropController;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.NodeList;
import com.google.gwt.dom.client.TableCellElement;
import com.google.gwt.dom.client.TableElement;
import com.google.gwt.dom.client.TableRowElement;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseUpEvent;
import com.google.gwt.event.dom.client.MouseUpHandler;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.Event.NativePreviewEvent;
import com.google.gwt.user.client.Event.NativePreviewHandler;
import com.google.gwt.user.client.ui.AbsolutePanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.PushButton;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Main entry point.
 *
 * @author marino
 */
public class MainEntryPoint implements EntryPoint {


     TextArea l ;
    /**
     * Creates a new instance of MainEntryPoint
     */
    public MainEntryPoint() {
    }
  private static TextArea messaggio = new TextArea();

    public static void debug(String s) {
        messaggio.setText(messaggio.getText() + "\n" + s);
    }
    /** 
     * The entry point method, called automatically by loading a module
     * that declares an implementing class as an entry-point
     */
    public void onModuleLoad() {

//        String text1 = "fshadhdsah";
//        String text2 = "Sed egestas, arcu nec accumsan...";
//        String text3 = "Proin tristique, elit at blandit...";
        
          RootPanel.get().add(messaggio);
        TabPanel panel = new TabPanel();
        AbsolutePanel absolutepanel = new AbsolutePanel();
        absolutepanel.setPixelSize(600, 800);
    //    PickupDragController dragController = new PickupDragController(absolutepanel, true);
           PickupDragController dragController = new PickupDragController( RootPanel.get(), true);
           com.google.gwt.user.client.Window.alert("obiettivo="+ RootPanel.get("obiettivoOrg"));
            DropController dropController = new AbsolutePositionDropController(RootPanel.get("obiettivoOrg"));

    // Don't forget to register each DropController with a DragController
    dragController.registerDropController(dropController);

        PushButton nut = new PushButton(new Image("TaskMgr.jpg"));
   //     nut.addDomHandler(new HH(),MouseMoveEvent.getType());
// nut.sinkEvents(Event.ONMOUSEMOVE);
        VerticalPanel pa = new VerticalPanel();
        Label lab = new Label("titolo documento");
        HTML header = new HTML("Title/Header (Drag Handle) with <a href='http://google.com/' target='_blank'>link</a>");
        pa.add(header);
        pa.add(nut);
        pa.add(lab);
        absolutepanel.add((pa));
        dragController.makeDraggable(pa, header);
        nut.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open(" http://www.piemonte.di.unito.it/TaskMgr/index.jsp?Flow=Flow_aug30-1.txt", "_blank", "");
                //  Window.alert("Hello, again");
                
            }
        });
        nut = new PushButton(new Image("GogDoc.jpg"));
        pa = new VerticalPanel();
        lab = new Label("titolo documento");
        header = new HTML("Title/Header (Drag Handle) with <a href='http://google.com/' target='_blank'>link</a>");
        pa.add(header);
        pa.add(nut);
        pa.add(lab);
        absolutepanel.add((pa));
        dragController.makeDraggable(pa, header);
        nut.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open("https://docs.google.com/document/d/11zOX13sBVGExwkFgmlUdGFgz3L0Uvp7l73rL2VR0sxk/edit?hl=en_US", "_blank", "");
            }
        });
        nut = new PushButton(new Image("GogDoc.jpg"));
        pa = new VerticalPanel();
        lab = new Label("titolo documento2");
        header = new HTML("Title/Header ALTRA ROBA with <a href='http://google.com/' target='_blank'>link</a>");
        pa.add(header);
        pa.add(nut);
        pa.add(lab);
        absolutepanel.add((pa));
        dragController.makeDraggable(pa, header);
        nut.addClickHandler(new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                com.google.gwt.user.client.Window.open(" http://www.piemonte.di.unito.it/TaskMgr/index.jsp?Flow=Flow_aug30-1.txt", "_blank", "");
            }
        });
        //   absolutepanel.add(new Label(text1));
        panel.add(absolutepanel, "STEPS");
        absolutepanel = new AbsolutePanel();
        //  absolutepanel.add(new Label(text2));
        panel.add(absolutepanel, "Virtual Collaboration");
        absolutepanel = new AbsolutePanel();
        // absolutepanel.add(new Label(text3));
        panel.add(absolutepanel, "Trip");
        panel.selectTab(0);
        panel.setSize("500px", "250px");
        panel.addStyleName("table-center");
   //     RootPanel.get("demo").add(panel);
        String content = " <div id=\"obiettivo\" ondragenter=\"return false;\" ondragover=\"return false;\" ondrop=\"return OnDropTarget (event);\" style=\"width:300px; height:100px; background-color:#f08080;\"> </div>";
        HTML widget = new HTML(content);
     //     widget.addDomHandler(new HH(),MouseUpEvent.getType());
/* widget*/ RootPanel.get("demo").sinkEvents(Event.MOUSEEVENTS);


   Event.addNativePreviewHandler(new NativePreviewHandler() {
    public void onPreviewNativeEvent(NativePreviewEvent preview) {
      NativeEvent event = preview.getNativeEvent();
       Element elt = event.getEventTarget().cast();
      // if (! event.getType().equals("miospeciale")){
      //     return;
    //   }
      if (!elt.getId().equals("obiettivo")) {
          return;
       }
      debug ("Evento: "+ event.getString() + " " + event.getType());
      TableElement tabo = TableElement.as(DOM.getElementById("info"));
      NodeList<TableRowElement> righe = tabo.getRows();
   // PROSEGUIRE!!!!!   NodeList<TableCellElement>
      debug("dopo righe");
      String cont = "";
      for (int i =0; i < righe.getLength(); i++){
          TableRowElement riga = righe.getItem(i);
          NodeList<TableCellElement> colonne = riga.getCells();
          for (int j =0; j < colonne.getLength(); j++){
              TableCellElement cella = colonne.getItem(j);
              cont += cella.getInnerText() + " ";
          }
          cont += "\n";
         
      }
    debug(cont);
      int keycode = event.getKeyCode();
      boolean ctrl = event.getCtrlKey();
      boolean shift = event.getShiftKey();
      boolean alt = event.getAltKey();
      boolean meta = event.getMetaKey();
//      if (event.getType().equalsIgnoreCase("keypress") || ctrl || shift
//          || alt || meta || keyboardEventReceivers.contains(elt)
//          || !isInterestingKeycode(keycode)) {
//        // Tell the event handler to continue processing this event.
//        return;
 //     }

     l.setText(l.getText()+"\n"+ event.getType());
     // handleKeycode(keycode);

      // Tell the event handler that this event has been consumed
    //  preview.consume();
    }
  });


         RootPanel.get("demo").add(widget);
    }

    class HH implements MouseUpHandler{
        @Override
public void onMouseUp(MouseUpEvent event) {

event.preventDefault();
            com.google.gwt.user.client.Window.alert("onMouseUp "+ event.toDebugString());
}

    }
}
