/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import argo.jdom.*;
import argo.jdom.JsonNode;
import java.util.Map;
import javax.xml.parsers.*;
import org.xml.sax.InputSource;
import org.w3c.dom.*;
import java.io.*;
import java.util.Iterator;

import pubsublib.pubsubhubbub.Discovery;
import pubsublib.test.TestSub;
public class SubServlet extends HttpServlet {
   
    /** 
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code> methods.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        String valorefile = request.getHeader("filenamemio");
        PrintWriter out = response.getWriter();
        String val = "";
        try 
        {
            new TestSub().testSubscriber("", "", "");
      //      new TestSub().testSubscriber("http://localhost:8081/Roma1/marinofeed.xml", "", "");
            Discovery discovery = new Discovery();
            String xmlRecords = discovery.getContents("http://localhost/Atomi/marinofeed.xml");
            DocumentBuilderFactory dbf =
            DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xmlRecords));
            Document doc = db.parse(is);
            NodeList nodes = doc.getElementsByTagName("entry");
             // iterate the update
           for (int i = 0; i < nodes.getLength(); i++) {
           Element element = (Element) nodes.item(i);

           NodeList name = element.getElementsByTagName("updated");
           Element line = (Element) name.item(0);
           //System.out.println("Up: " + getCharacterDataFromElement(line));

           NodeList summary = element.getElementsByTagName("summary");
           line = (Element) summary.item(0);
           if(getCharacterDataFromElement(line).contains("Block"))
           {
               System.out.println("Summary: " + getCharacterDataFromElement(line));

               String jsonText = getCharacterDataFromElement(line);
               val = jsonText;
            //   JsonNode secondSingle = new JdomParser().parse(jsonText).getFields().get(0);
              // String conn = json.getStringValue("connections");
               Map<JsonStringNode, JsonNode> cc = new JdomParser().parse(jsonText).getFields();

               String aa = cc.toString();
               Iterator it = cc.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry)it.next();
                   // System.out.println(pairs.getKey() + " = " + pairs.getValue());
                   // System.out.println( pairs.getValue().getClass().toString());
                  //  System.out.println( pairs.getKey().getClass().toString());
                  // JsonNode node = (JsonNode) pairs.getValue();
                    JsonRootNode node = (JsonRootNode) pairs.getValue();
                    JsonStringNode node2 =(JsonStringNode) pairs.getKey();
                    int size = node.getElements().size();
            //        System.out.println(size);
                //    node2.getStringValue("type", 0);
                    node2.getText();
                    for(int j=0;j<size;j++)
                    {

                    //    System.out.println(node.getElements().get(j).toString());


                    }
                 //   System.out.println(node.hasElements());

                }


               //System.out.println(aa);
        //        String text =  json.getStringValue("blocks");


           }

            
        }
             out.print(val);
        }
            catch (Exception e) {
            e.printStackTrace();
        } finally {
            out.close();
        }
    }

        public static String getCharacterDataFromElement(Element e) {
    Node child = e.getFirstChild();
    if (child instanceof CharacterData) {
       CharacterData cd = (CharacterData) child;
       return cd.getData();
    }
    return "?";
  }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /** 
     * Handles the HTTP <code>GET</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    } 

    /** 
     * Handles the HTTP <code>POST</code> method.
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
    throws ServletException, IOException {
        processRequest(request, response);
    }

    /** 
     * Returns a short description of the servlet.
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
