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
        Document doc = null;
        try {
            doc = MyDOMParserBean.getDocument("/home/marino/Scheduler/Resp1");
            //  doc = MyDOMParserBean.getDocument("/home/marino/ESEMPIO.gpx");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Element ele = doc.getDocumentElement();//.getElementById("ACTIVE LOG 004");
        ArrayList<Interval> inter = new ArrayList();
        parseIntervals(ele, inter,  "");
        for (Interval inte : inter){
            System.out.println("interval : "+inte.getName()+ " "+ inte.getMin()+ " "+inte.getMax());
        }
        //printUnNodo(ele, "");
        Request r = new Request(null, "","","");
        System.out.println(r.toServerString());
    }

    static String taskName = "";
    
    public static void parseIntervals(Node n, ArrayList<Interval> inter, String indent) {
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
                if (n8.getNodeName().equals("start") && (n.getNodeName().equals("interval") || n.getNodeName().equals("intervals"))) {
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
        System.out.println();
        NodeList l33 = n.getChildNodes();
        //   if (stampa)    System.out.println(indent+ l33.getLength() + " children ");
        for (int w = 0; w < l33.getLength(); w++) {
            Node n67 = l33.item(w);
            parseIntervals(n67, inter,  indent + "    ");
        }
    }

    public static ArrayList<Interval> getIntervals(InputStream is) {
        Document doc = null;
        try {
            if (is == null){ //per prova leggi da file
                 doc = MyDOMParserBean.getDocument("/home/marino/Scheduler/Resp1");
            } else
            {
            doc = MyDOMParserBean.getDocumentFromStream(is);
            }
            //  doc = MyDOMParserBean.getDocument("/home/marino/ESEMPIO.gpx");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Element ele = doc.getDocumentElement();//.getElementById("ACTIVE LOG 004");
        ArrayList<Interval> ret = new ArrayList();
        parseIntervals(ele, ret,  "");
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
                System.out.print(" " + n8.getNodeName() + " ='" + n8.getNodeValue() + "'");
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
}
