/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.unito.server;

/**
 *
 * @author marino
 */
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.unito.client.Interval;
import org.unito.client.StartInterval;
import org.unito.client.Task;
import org.unito.client.TaskGroup;
import org.w3c.dom.*;

public class MyDOMParserBean
        implements java.io.Serializable {

    public MyDOMParserBean() {
    }

    public static Document getDocument(String file) throws Exception {

        // Step 1: create a DocumentBuilderFactory
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        dbf.setValidating(false);
        // Step 2: create a DocumentBuilder
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Step 3: parse the input file to get
        //  a Document object
        Document doc = db.parse(new File(file));
        doc.normalizeDocument();


        return doc;
    }

    public static Document getDocumentFromStream(InputStream is) throws Exception {

        // Step 1: create a DocumentBuilderFactory
        DocumentBuilderFactory dbf =
                DocumentBuilderFactory.newInstance();
        dbf.setNamespaceAware(false);
        dbf.setValidating(false);
        // Step 2: create a DocumentBuilder
        DocumentBuilder db = dbf.newDocumentBuilder();

        // Step 3: parse the input file to get
        //  a Document object
        Document doc = db.parse(is);
        doc.normalizeDocument();


        return doc;
    }

    public static void main(String[] args) {
        TaskGroup ret = fillReply(null);
        Request r = new Request(ret, "", "", "", "", "");
        System.out.println(r.toServerString());
    }

    public static void mainOLD(String[] args) {
        Document doc = null;
        try {
            doc = MyDOMParserBean.getDocument("/home/marino/Scheduler/Resp2");
            //  doc = MyDOMParserBean.getDocument("/home/marino/ESEMPIO.gpx");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Element ele = doc.getDocumentElement();//.getElementById("ACTIVE LOG 004");
        ArrayList<Interval> inter = new ArrayList();
        parseIntervals(ele, inter, "");
        for (Interval inte : inter) {
            System.out.println("interval : " + inte.getName() + " " + inte.getMin() + " " + inte.getMax());
        }
        printUnNodo(ele, "");
        TaskGroup ret = new TaskGroup();
        ret.setTaskSchedule(inter);
        Request r = new Request(ret, "", "", "", "", "");
        System.out.println(r.toServerString());
    }
    static String taskName = "";


    public static void parseIntervals(Node n, ArrayList<Interval> inter, String indent) {
        // recupera sia gli intervalli che eventualmente
        // in quali casi  ci siano utenti con schedule da modificare
        
        String s = "" + n.getClass();

        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {

            //  if (stampa)  System.out.println(indent + l5.getLength() + " attributes ");
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                // System.out.print(" " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                if (n8.getNodeName().equals("task") && n.getNodeName().equals("action")) {
                    taskName = n8.getNodeValue();
                    System.out.print("carico " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                }
                if (n.getNodeName().equals("interval") || n.getNodeName().equals("intervals")) {
                    n8 = l5.getNamedItem("start");

                    String s1 = n8.getNodeValue();
                    s1 = s1.replaceAll("[(),]", " ");
                    String[] pars = s1.split(" ");
                    int min = Integer.parseInt(pars[1]);
                    int max = Integer.parseInt(pars[2]);
                    Interval inte = new StartInterval(taskName, min, max);
                    inter.add(inte);
                    System.out.print("intervallo " + s1);
                    n8 = l5.getNamedItem("users");
                    if (n8 != null) {  // recupera utenti
                        s1 = n8.getNodeValue();                    
                        pars = s1.split(",");
                        for (int i = 0; i < pars.length; i++) {
                            inte.addUser(pars[i]);
                        }
                    }
                }
            }
        }
        System.out.println();
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ l33.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            parseIntervals(n67, inter, indent + "    ");
        }
    }

    public static TaskGroup fillReply(InputStream is) {
        Document doc = null;
        try {
            if (is == null) { //per prova leggi da file
                doc = MyDOMParserBean.getDocument("/home/marino/Scheduler/Resp2");
            } else {
                doc = MyDOMParserBean.getDocumentFromStream(is);
            }
            //  doc = MyDOMParserBean.getDocument("/home/marino/ESEMPIO.gpx");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Element ele = doc.getDocumentElement();//.getElementById("ACTIVE LOG 004");
        ArrayList<Interval> inte = new ArrayList();
        parseIntervals(ele, inte, "");
        TaskGroup ret = new TaskGroup();
        ret.setTaskSchedule(inte);
        parseNet(ele, ret, "", "");
        parsePrecs(ele, ret, "", "");
        return ret;
    }

    static void printUnNodo(Node n, String indent) {
        String s = "" + n.getClass();
        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {

            //  if (stampa)  System.out.println(indent + l5.getLength() + " attributes ");
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                System.out.print(" ATTR " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
            }
        }
        System.out.println();
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ l33.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            printUnNodo(n67, indent + "    ");
        }
        //   Punto p = new Punto(lat, lon, ele, ora);
        //  Punto.add(p);
    }

    public static void parsePrecs(Node n, TaskGroup iTasks, String prima, String indent) {
        String s = "" + n.getClass();

        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {

            //  if (stampa)  System.out.println(indent + l5.getLength() + " attributes ");
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                // System.out.print(" " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                if (n.getNodeName().equals("prec")) {
                    prima = n8.getNodeValue();
                    System.out.print("caricoprec " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                }
                if (n.getNodeName().equals("succ")) {
                    String dopo = n8.getNodeValue();
                    Task who = iTasks.getI(dopo);
                    if (who == null) {
                        System.out.print("tasknet, not found: " + dopo);
                    } else {
                        //    iTasks.getI(prima).addAfter(dopo); noppure:
                        iTasks.getI(dopo).addBefore(prima);
                        System.out.print("caricosucc " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                    }
                }
            }
        }
        System.out.println();
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ l33.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            parsePrecs(n67, iTasks, prima, indent + "    ");
        }
    }

    public static void parseNet(Node n, TaskGroup iTasks, String prima, String indent) {
        String s = "" + n.getClass();

        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {
            String tn = null;
            String dur = null;
            String sta = null;
            String end = null;
            //  if (stampa)  System.out.println(indent + l5.getLength() + " attributes ");
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                // System.out.print(" " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                if (n8.getNodeName().equals("name") && n.getNodeName().equals("task")) {
                    tn = n8.getNodeValue();
                    System.out.print("tasco " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                }
                if (n8.getNodeName().equals("dur") && n.getNodeName().equals("task")) {
                    dur = n8.getNodeValue();
                    System.out.print("dur " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                }
                if (n8.getNodeName().equals("start") && n.getNodeName().equals("task")) {
                    sta = n8.getNodeValue();
                    System.out.print("start " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                }
                if (n8.getNodeName().equals("end") && n.getNodeName().equals("task")) {
                    end = n8.getNodeValue();
                    System.out.print("end " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                }
            }
            if (tn != null) {
                sta = sta.replaceAll("[(),]", " ");
                String[] pars = sta.split(" ");
                int par = Integer.parseInt(pars[1]);
                end = end.replaceAll("[(),]", " ");
                pars = end.split(" ");
                int fin = Integer.parseInt(pars[2]);
                int du = Integer.parseInt(dur);
                Task tasn = new Task(tn, par, fin, du);
                iTasks.addI(tasn);
            }
        }
        System.out.println();
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ l33.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            parseNet(n67, iTasks, "", indent + "    ");
        }
    }

    public static void parseIntervalsOLD(Node n, ArrayList<Interval> inter, String indent) {
        String s = "" + n.getClass();

        boolean stampa = !s.equals("class com.sun.org.apache.xerces.internal.dom.DeferredTextImpl");

        //   if (stampa) System.out.println(indent+ "Nodo: " + n.getClass() + " name:" + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        if (stampa) {
            System.out.print(indent + "elem: " + n.getNodeName());// + " value:'" + n.getNodeValue()+"'");
        }
        NamedNodeMap l5 = n.getAttributes();
        if (l5 == null) {
            //   if (stampa) System.out.println(indent+"0 attributes ");
        } else {

            //  if (stampa)  System.out.println(indent + l5.getLength() + " attributes ");
            for (int k = 0; k < l5.getLength(); k++) {
                Node n8 = l5.item(k);
                // System.out.print(" " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                if (n8.getNodeName().equals("task") && n.getNodeName().equals("action")) {
                    taskName = n8.getNodeValue();
                    System.out.print("carico " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
                }
                if (n.getNodeName().equals("interval") || n.getNodeName().equals("intervals")) {
                    if (n8.getNodeName().equals("start")) {
                        String s1 = n8.getNodeValue();
                        s1 = s1.replaceAll("[(),]", " ");
                        String[] pars = s1.split(" ");
                        int min = Integer.parseInt(pars[1]);
                        int max = Integer.parseInt(pars[2]);
                        Interval inte = new StartInterval(taskName, min, max);
                        inter.add(inte);
                        System.out.print("intervallo " + s1);
                    }
                }
            }
        }
        System.out.println();
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ l33.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            parseIntervalsOLD(n67, inter, indent + "    ");
        }
    }

}
