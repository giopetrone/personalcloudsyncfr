/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package jav;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.media.MediaSource;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;

/**
 *
 * @author fabrizio
 */
public class GoDoc
{

    URL documentListFeedUrl = null;
    DocsService service = null;
    static HashMap<String, DateTime> documentVersions = new HashMap();
    private boolean printed = false;
    boolean one = false;
    static String docMakerLogin = "icemgr09@gmail.com";  // fino a che  ???  auth funziona
    static String docMakerPasswd = "sync09fr";

     public GoDoc(DocsService service) {
        try {
            documentListFeedUrl = new URL("https://docs.google.com/feeds/default/private/full");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.service = service;
    }


     public DocumentListEntry findEntry(String title) throws Exception {
        DocumentListEntry ret = null;
        DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries())
        {
            
            if (entry.getTitle().getPlainText().equals(title))
            {
                return entry;
            }
        }
        return ret;
    }


    private String findFiles(String s)
    {

        int ind1 = s.lastIndexOf(" ");
        int ind2 = s.indexOf("<br></body>");

        s = s.substring(ind1,ind2);
        if(s.contains("\n")) s = s.replaceAll("\n","");
        if(s.contains("<br>")) s = s.replaceAll("<br>","");
        return s;
    }

    public static String checkPermission(String nomeFile,String flowName,String notification)
    {
        try
        {
            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            return new GoDoc(service).checkPermissionOnFile(nomeFile,flowName,notification);


        }
        catch(Exception ex)
        {
            System.out.println("Errore checkPermission: "+ex.getMessage());
            return "error";
        }
    }




    public String checkPermissionOnFile(String nomeFile,String flowName,String notification)
    {
        try
        {

            DocumentListEntry doc =  findEntry(nomeFile);
            if(doc != null)
            {

                String resourceId = doc.getResourceId();
                String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));
                String docId = resourceId.substring(resourceId.lastIndexOf(':') + 1);
                URL exportUrl = new URL("http://docs.google.com/feeds/download/" + docType
                                + "s/Export?docID=" + docId + "&exportFormat=" + "html");
                MediaContent mc = new MediaContent();
                mc.setUri(exportUrl.toString());
                MediaSource ms = service.getMedia(mc);
                String s = "";
                byte[] b = new byte[1024];
                InputStream inStream = ms.getInputStream();
                int c;
                while ((c = inStream.read(b)) != -1)
                {
                     s += new String(b, 0, c);
                }
                s = new GoDoc(service).findFiles(s);
            
                boolean subscribe = false;
                if(s.contains(flowName+"/"+"All")) subscribe = true;
                else if(s.contains(flowName+"/"+notification)) subscribe = true;
                else if(s.contains(flowName+"/"))
                {
                    // DA FARE
                }

                if(subscribe == true) return "subscribe";
                else return "none";


            }
            else
            {
                return "no permission file";
            }

        }
        catch(Exception ex)
        {
            System.out.println("Errore in check on file: "+ex.getMessage());
            return "errore";
        }
    }



}
