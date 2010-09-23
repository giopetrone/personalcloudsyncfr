/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

//import com.google.appengine.repackaged.com.google.protobuf.ServiceException;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.FolderEntry;

import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.ServiceException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import maillib.SendMailCl;
import pubsublib.event.AtomEvent;
import pubsublib.pubsubhubbub.Discovery;
import pubsublib.test.TestPub;

/**
 *
 * @author marino
 */
public class GoDoc {

    private static void uploadFile() {
        throw new UnsupportedOperationException("Not yet implemented");
    }

   
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    URL documentListFeedUrl = null;
    DocsService service = null;
    static HashMap<String, DateTime> documentVersions = new HashMap();
    private boolean printed = false;
    boolean one = false;

    static String docMakerLogin = "icemgr09@gmail.com";  // fino a che  ???  auth funziona
    static String docMakerPasswd = "sync09fr";


    //fine Anna gio

    public GoDoc(DocsService service) {
        try {
            documentListFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full/");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.service = service;
    }

    public  static void main(String[] args) throws MalformedURLException, IOException, ServiceException {
  
        try{
            
            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            URL listFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full/");
            DocumentListFeed feed = service.getFeed(listFeedUrl, DocumentListFeed.class);
          //  System.out.println("FEED :"+feed.toString());
            DocumentListEntry doc =  new GoDoc(service).findEntry("fabrizio_torretta.txt");
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
                        int d;
                        int l = 0;
                        String r = "";
                        while ((c = inStream.read(b)) != -1) {
                            s += new String(b, 0, c);
                        }
                        System.out.println("PRIMA "+s);
                        s = new GoDoc(service).findFiles(s);
                        System.out.println("DOPO: "+s);
                        
                        if(s.contains("ciao.txt")) s = s.replaceAll("ciao.txt","blabla");
                        else s += "\nciao.txt/All";
                       
                         service.getRequestFactory().setHeader("If-Match", "*");
                         doc.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));
                         doc.updateMedia(false);
            }
            else
            {
                String s = "ciao.txt/All";
                doc = new GoDoc(service).uploadFile(s, "fabrizio_torretta.txt");
            }

        }
            catch(Exception e){System.out.println("ERRORE: "+e.getMessage());}

    }

public void printDocuments(DocumentListFeed feed) throws IOException, ServiceException
{
    DocsService service2 = new DocsService("Document List Demo");
    service2.setUserCredentials(docMakerLogin, docMakerPasswd);
    String documentName = "ilarialbano.txt";
        try {
            DocumentListEntry documentEntry = findEntry(documentName);
        
        } catch (Exception ex) {
            Logger.getLogger(GoDoc.class.getName()).log(Level.SEVERE, null, ex);
        }

    

    
  
}



public static DocumentListEntry createFolder(String title) throws IOException, ServiceException {

    DocsService service = new DocsService("Document List Demo");
    //service.setUserCredentials(docMakerLogin, docMakerPasswd);
    DocumentListEntry newEntry = new FolderEntry();
    newEntry.setTitle(new PlainTextConstruct(title));
    URL Url = new URL("http://docs.google.com/feeds/documents/private/full");
    return service.insert(Url, newEntry);

}


    public static String prendi(String login, String pwd) {
        
        DocsService service = new DocsService("Document List Demo");
        try {
             
            service.setUserCredentials(login, pwd);
            return new GoDoc(service).showAllDocs();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }


    public void saveNewDiagram(String login, String documentName,String s, String users, String writers,String pwd,String assignees)
    {
                try{
                  
                    DocumentListEntry documentEntry = findEntry(documentName);
                    System.out.println("DOCUMENTO NUOVO");
                    List<AtomEvent> listaeventi = new ArrayList();
                    List<AtomEvent> listasave = new ArrayList();
                    documentEntry = uploadFile(s, documentName);
                    AtomEvent eventSave = new AtomEvent(login, "TaskManager", "Save New Diagram");
                    String link = documentEntry.getDocumentLink().getHref();
                    eventSave.setParameter("File", documentName);
                    eventSave.setParameter("Link", link);
                    listasave.add(eventSave);
                    FeedUtil.addEntries("", documentName, listasave);
                    String[] tempwriter;
                    String delimiter = ",";
                    List<String> destinatari = new LinkedList();
                    //if(writers == null) writers = "";
                    if (users == null && writers == null && assignees ==null)  {
                        System.out.println("NESSEUN UTENTE IN SHARING IN SAVE NEW DIAGRAM");

                    }
                    else
                    {
                        String[] tempusers;
                        if(writers == null) writers = "";
                        if(users == null) users ="";
                        if(assignees == null) assignees = "";
                        tempusers = users.split(delimiter);

                        for (int i = 0; i < tempusers.length; i++)
                        {
                            if (!tempusers[i].equals("") && !assignees.contains(tempusers[i]))
                            {

                                System.out.println("%%% ASSEGNO Reader a %%%%%%%%% " + tempusers[i]);
                                addReaders(documentEntry, tempusers[i]);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Permission", "Read");
                                event.setParameter("Who", tempusers[i]);
                                listaeventi.add(event);
                              //  new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                destinatari.add(tempusers[i]);

                            }

                        }
                        tempwriter = writers.split(delimiter);
                        for (int j = 0; j < tempwriter.length; j++) {
                            boolean check = false;

                            for (int i = 0; i < tempusers.length; i++) {
                                if (tempusers[i].equals(tempwriter[j])) {
                                    check = true;
                                   // System.out.println("true");
                                }

                            }
                            if (check == false && !tempwriter[j].equals(""))
                            {
                                System.out.println("#####ASSEGNO Writer a #### " + tempwriter[j]);
                                addWriting(documentEntry, tempwriter[j]);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Permission", "Write");
                                event.setParameter("Who", tempwriter[j]);
                                listaeventi.add(event);
                               // new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                destinatari.add(tempwriter[j]);
                            }
                        }
                       
                        String [] tempassignees;
                        tempassignees = assignees.split(delimiter);
                        for (int j = 0; j < tempassignees.length; j++)
                        {
                            String thisassigner = tempassignees[j];
                            int count0 = thisassigner.indexOf("@");
                            int count1 = thisassigner.lastIndexOf("@");
                            boolean checkchiocciola = false;
                            if(count0 > 0 && count0 == count1) checkchiocciola = true;
                            if(!tempassignees[j].equalsIgnoreCase("") &&  tempassignees[j] !=null && !tempassignees[j].equals(" ") && checkchiocciola == true)
                            {

                                try
                                {
                                    System.out.println("Assignees " + tempassignees[j]);
                                    addWriting(documentEntry, tempassignees[j]);
                                    AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                    event.setParameter("File", documentName);
                                    event.setParameter("Permission", "Write");
                                    event.setParameter("Who", tempassignees[j]);
                                    listaeventi.add(event);
                                    //new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                    destinatari.add(tempassignees[j]);
                                }catch(Exception ex)
                                {
                                    if(ex.getMessage().equalsIgnoreCase("This user already has access to the document."))
                                    {
                                    System.out.println("DENTRO CATCH X  UTENTE: "+thisassigner);

                                    }
                                    else System.out.println("Dentro CATCH X utente "+thisassigner+" "+ex.getMessage());
                                }
                            }
                    
                        }

                        destinatari.add(login);
                      
                        
                        String dest = destinatari.toString();
                        int length = dest.length();
                        String destfinal = dest.substring(1, length - 1);
                        if(destinatari.size() != 1) sendMail(destfinal,login,pwd,documentName);
                       
                     //   FeedUtil.addEntries("",documentName,listaeventi);
                        

                        

                    } 

                    
                    }catch(Exception ex)
                    {

                         System.out.println("IN SAVE NEW DIAGRAM "+ex.getMessage());
                        
                    }



    }

    public String uploadDiagram(String login, String documentName, String users, String writers,String s,String assignees) throws Exception
    {
        try{
              //  DocsService service = new DocsService("Document List Demo");
                System.out.println("DocumentEntry NOT null");
                DocumentListEntry documentEntry = findEntry(documentName);
                AtomEvent eventUpdate = new AtomEvent(login, "TaskManager", "Update Diagram");
                eventUpdate.setParameter("File", documentName);
                Discovery discovery = new Discovery();
                String hub = discovery.getHub("http://taskmanagerunito.xoom.it/Flow/"+documentName+".xml");
                String typeNotif = "";
                if(hub.equalsIgnoreCase("http://localhost:8080")) typeNotif = "local";
                else if(hub.equals("http://pubsubhubbub.appspot.com")) typeNotif = "remote";
                SaveServlet.setTypeNotification(typeNotif);
                List<AtomEvent> listaeventi = new ArrayList();
                listaeventi.add(eventUpdate);
               // FeedUtil.addEntries("", documentName, listaeventi);
                
                    List<String> readers = new ArrayList();
                    List<String> collaborators = new ArrayList();
                    AclFeed aclFeed = service.getFeed(new URL(documentEntry.getAclFeedLink().getHref()), AclFeed.class);

                    for (AclEntry entry : aclFeed.getEntries()) {
                        if (entry.getRole().getValue().equals("reader")) {
                            readers.add(entry.getScope().getValue());
                        } else if (entry.getRole().getValue().equals("writer")) {
                            collaborators.add(entry.getScope().getValue());
                        }


                    }




                    String[] tempwriter;
                    String delimiter = ",";

                    if (users != null)
                    {
                        String[] tempusers;
                        if (writers == null)
                        {
                            writers = "";
                        }
                        if(assignees == null)
                        {
                            assignees ="";
                        }
                        tempusers = users.split(delimiter);
                      //  System.out.println("READERS SIZE: " + readers.size());
                      //  System.out.println("TEMPUSERS LENGTH: " + tempusers.length);
                        try
                        {
                            for (int i = 0; i < tempusers.length; i++)
                            {
                                boolean check = false;

                             //   System.out.println("Il reder e'' assignees? "+assignees.contains(tempusers[i]));
                                if (!tempusers[i].equals("") && !assignees.contains(tempusers[i]))
                                {
                                 //   System.out.println("TEMPUSER: " + tempusers[i] + " finisce qui");
                                    for (int z = 0; z < readers.size(); z++) {
                                        System.out.println(readers.get(z));
                                        if (tempusers[i].contains(readers.get(z))) {
                                            check = true;
                                        }

                                   //     System.out.println("Check Reader: " + check);
                                    }

                                    for (int z = 0; z < collaborators.size(); z++)
                                    {

                                     //   System.out.println("collaborators: " + collaborators.get(z));
                                        if (tempusers[i].contains(collaborators.get(z))) 
                                        {
                                            System.out.println("%%%%% CAMBIO PERMESSI DI %%%% "+collaborators.get(z));
                                            update(documentEntry, aclFeed, collaborators.get(z));
                                            AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                            event.setParameter("File", documentName);
                                            event.setParameter("Change Permission", "From Write To Read");
                                            event.setParameter("Who", collaborators.get(z));
                                            listaeventi.add(event);
                                            //new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                       //     System.out.println("DOPO UPDATE");
                                            check = true;

                                        }
                                    }

                                    if (check == false)
                                    {
                                        System.out.println("%%%%%%%%% ASSEGNO READER a %%%%% "+tempusers[i]);
                                        addReaders(documentEntry, tempusers[i]);
                                        AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                        event.setParameter("File", documentName);
                                        event.setParameter("Permission", "Read");
                                        event.setParameter("Who", tempusers[i]);
                                        listaeventi.add(event);
                                      //  new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                    }

                                }
                            }
                        }catch(Exception ex){System.out.println("Dentro for readers: "+ex.getMessage());}
                        tempwriter = writers.split(delimiter);
                        // System.out.println("tempwriter: "+tempwriter.length);
                        // System.out.println("Collaborators SIZE: "+collaborators.size());
                        try
                        {
                            for (int j = 0; j < tempwriter.length; j++)
                            {
                                boolean check = false;
                           //     System.out.println("-------------------------");
                           //     System.out.println("TEMPWRITER: " + tempwriter[j]);

                                for (int i = 0; i < tempusers.length; i++)
                                {
                                    // System.out.println("TEMPUSER: " + tempusers[i]);
                                    if (tempusers[i].equals(tempwriter[j])) {
                                        check = true;
                                    }
                                }
                                for (int z = 0; z < collaborators.size(); z++) {
                                    System.out.println("collaborators: " + collaborators.get(z));
                                    if (collaborators.get(z).contains(tempwriter[j]) || tempwriter[j].contains(collaborators.get(z))) {
                                   //     System.out.println("DENTRO IF");
                                        check = true;
                                    }
                                }
                               // System.out.println("Check Writer: " + check);
                                if (check == false)
                                {
                                    System.out.println("%%%%%% Aggiungo a Writer%%%%% " + tempwriter[j]);
                                    addWriting(documentEntry, tempwriter[j]);
                                    AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                    event.setParameter("File", documentName);
                                    event.setParameter("Permission", "Write");
                                    event.setParameter("Who", tempwriter[j]);
                                    listaeventi.add(event);
                                   // new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                                }
                            }
                        }catch(Exception ex)
                        {
                            System.out.println("Dentro for Writers: "+ex.getMessage());
                        }

                    }
                    else
                    {
                        tempwriter = writers.split(delimiter);
                        for (int j = 0; j < tempwriter.length; j++)
                        {
                            boolean check = false;
                            for (int z = 0; z < collaborators.size(); z++) {
                             //   System.out.println("collaborators: " + collaborators.get(z));
                                if (collaborators.get(z).equals(tempwriter[j])) {
                                    check = true;
                                }
                            }
                           // System.out.println("Check Writer: " + check);
                            if (check == false)
                            {
                              //  System.out.println("%%%%%%% Aggiungo a Writer%%%%%% " + tempwriter[j]);
                                addWriting(documentEntry, tempwriter[j]);
                                AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                event.setParameter("File", documentName);
                                event.setParameter("Permission", "Write");
                                event.setParameter("Who", tempwriter[j]);
                                listaeventi.add(event);
                              //  new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));
                            }
                        }
                    }
                    if(assignees != null)
                    {
                       String[] tempassignes;
                       String thisassigner;
                       tempassignes = assignees.split(",");
                       for(int z =0;z<tempassignes.length;z++)
                       {
                           thisassigner = tempassignes[z];
                           int count0 = thisassigner.indexOf("@");
                           int count1 = thisassigner.lastIndexOf("@");
                           boolean checkchiocciola = false;
                           if(count0 > 0 && count0 == count1) checkchiocciola = true;
                           if(!tempassignes[z].equals("") && checkchiocciola == true)
                           {
                               
                               try{
                                   
                                   addWriting(documentEntry, tempassignes[z]);
                              //     System.out.println("%%%% Diventa writer Assign%%%%%%% "+tempassignes[z]);
                                   AtomEvent event = new AtomEvent(login, "TaskManager", "DocumentAccess");
                                   event.setParameter("File", documentName);
                                   event.setParameter("Permission", "Write");
                                   event.setParameter("Who", tempassignes[z]);
                                   listaeventi.add(event);
                                //   new TestPub().testPublisher("", FeedUtil.SubFeedName(documentName));

                               }catch(Exception ex){System.out.println("DENTRO FOR ASSIGNEES in Update DIagram X UTENTE "+tempassignes[z]+" "+ex.getMessage());}
                           } 
                       }
                      
                       
                    }
                  //  FeedUtil.addEntries("", documentName, listaeventi);

                    service.getRequestFactory().setHeader("If-Match", "*");
                    // documentEntry.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));
                   documentEntry.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));

                    //         documentEntry.setContent(new PlainTextConstruct(s));
                    documentEntry.updateMedia(false);
                    return "notnew";

                } catch (Exception ex) {
                    
                    if(ex.getMessage().equalsIgnoreCase("This user already has access to the document."))
                    {

                      //  System.out.println("DENTRO If update diagram "+ex.getMessage());
                        return "notnew";
                    }
                    else 
                    {
                        System.err.println("DEntro Update diagram" +ex.getMessage());
                        return ex.getMessage();
                    }
                }
            }

    public String saveDoc(String login, String documentName, String s, String users, String writers, String pwd,String assignees) {

        try {

            DocumentListEntry documentEntry = findEntry(documentName);

            if (documentEntry == null) {
               
               saveNewDiagram(login,documentName,s,users,writers,pwd,assignees);

                return "new";

            } else 
            {
             //   System.out.println("%%%%%%%%%%%%%%%%%%%%%%%%%%");
                String ret = uploadDiagram(login,documentName,users,writers,s,assignees);
                System.out.println("RET: "+ret);
                service.getRequestFactory().setHeader("If-Match", "*");
                documentEntry.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));
                documentEntry.updateMedia(false);
                return ret;

            }

        } catch (Exception ex)
        {
            System.out.println("Dentro SAVEDOC "+ex.getMessage());
            if(ex.getMessage().equalsIgnoreCase("Could not convert document.")) return "notnew";
            else return ex.toString();
        }
    }

   

    public DocumentListEntry uploadFile(String content, String title)
            throws IOException, ServiceException {
        File file = new File("/tmp/" + title);
        FileOutputStream of = new FileOutputStream(file);
        of.write(content.getBytes(), 0, content.length());
        of.close();
        DocumentListEntry newDocument = new DocumentListEntry();
        String mimeType = DocumentListEntry.MediaType.fromFileName(file.getName()).getMimeType();
        newDocument.setFile(file, mimeType);
        newDocument.setTitle(new PlainTextConstruct(title));
        DocumentListEntry ret = service.insert(new URL("http://docs.google.com/feeds/documents/private/full/"), newDocument);
        file.delete();
        return ret;
    }

    public void uploadPermissions(String documentName,String flowName ,String notification) throws Exception
    {
        System.out.println("Doc: "+documentName);
        System.out.println("FlowName: "+flowName);
        System.out.println("Notification "+notification);
        DocumentListEntry documentEntry = findEntry(documentName);
        if(documentEntry == null)
        {
             String s = flowName+"/"+notification;
             documentEntry = uploadFile(s, documentName);
        }
        else
        {
            String resourceId = documentEntry.getResourceId();
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
            String oldsetting = "";
            String oldtext ="";
            String newtext = "";
            while ((c = inStream.read(b)) != -1)
            {
                //System.out.println("LINE: "+s);
                s += new String(b, 0, c);
                if(s.contains(flowName)) oldsetting =s;
                oldtext += s + "\r\n";
            }
            if(!oldsetting.equals(""))
            {
               newtext = oldtext.replaceAll(oldsetting, flowName+"/"+notification);

            }
            documentEntry = uploadFile(newtext, documentName);

         //   s = findBody(s);

        }
    }

    private AclEntry addWriting(DocumentListEntry documentEntry, String who) throws IOException, MalformedURLException, ServiceException {
        AclRole role = new AclRole("writer");

        AclScope scope = new AclScope(AclScope.Type.USER, who);
        AclEntry entry = new AclEntry();
        entry.setRole(role);
        entry.setScope(scope);
        URL url = new URL("http://docs.google.com/feeds/acl/private/full/" + documentEntry.getResourceId());
        return service.insert(url, entry);
    }

    private void update(DocumentListEntry documentEntry, AclFeed aclFeed, String who) throws IOException, MalformedURLException, ServiceException {


        for (AclEntry entry : aclFeed.getEntries()) {

            //  System.err.println(entry.getRole().getValue());
            if (entry.getScope().getValue().equals(who)) {
                entry.setRole(new AclRole("reader"));
                entry.update();
            }



        }

    }

    private AclEntry addReaders(DocumentListEntry documentEntry, String who) throws IOException, MalformedURLException, ServiceException {

        AclRole role = new AclRole("reader");

        AclScope scope = new AclScope(AclScope.Type.USER, who);
        AclEntry entry = new AclEntry();
        entry.setRole(role);
        entry.setScope(scope);
        URL url = new URL("http://docs.google.com/feeds/acl/private/full/" + documentEntry.getResourceId());
        return service.insert(url, entry);
    }

    public List<String> loadDoc(String valorefile, boolean refresh, String owner) {
        try {
            
            DocumentListEntry documentEntry = findEntry(valorefile);
            //   System.out.println(documentEntry.toString());
            List<String> list = new LinkedList();

            if (documentEntry == null) {

                return list; //document not found
            }


            DateTime last = documentEntry.getUpdated();
            DateTime curr = documentVersions.get(valorefile);
            if (refresh) {
                if (curr == null || curr.getValue() == last.getValue()) {
                    return list;  // no version or no new version
                }
            }
            documentVersions.put(valorefile, last); // update current version
            String resourceId = documentEntry.getResourceId();
           
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
            while ((c = inStream.read(b)) != -1) {
                s += new String(b, 0, c);
            }
            
            s = findBody(s);
           
            String error = "Non hai i permessi";
            List<String> people = new ArrayList();
            List<String> collaborators = new ArrayList();
            List<String> readers = new ArrayList();
            System.out.println("Prima ciclo reader");
            String docowner = "";

            AclFeed aclFeed = service.getFeed(new URL(documentEntry.getAclFeedLink().getHref()), AclFeed.class);
            int size = aclFeed.getEntries().size();
            System.out.println("SIZE " +aclFeed.getEntries().size());
            for (AclEntry entry : aclFeed.getEntries()) {
                
                System.out.println("RUOLO "+entry.getRole().getValue());
                if (entry.getRole().getValue().equals("reader")) {
                    System.out.println("Trovato reader");
                    readers.add(entry.getScope().getValue());
                }
                
                else if (entry.getRole().getValue().equals("writer")) {
                    System.out.println("Trovato Writer");
                    collaborators.add(entry.getScope().getValue());
                }
                else if (entry.getRole().getValue().equals("owner"))
                {
                    docowner = entry.getScope().getValue();
                }
                people.add(entry.getScope().getValue());


            }
            
            String users = readers.toString();


            String nousers = "";
            if (users.equals("[]")) {
                users = "";
            } else {
                users = users.substring(1, users.length() - 1);
            }
            System.out.println("USERS: "+users);
            String writers = collaborators.toString();
          




            if (writers.equals("[]")) {
                writers = "";
            } else {
                writers = writers.substring(1, writers.length() - 1);
            }
            System.out.println("Writers "+writers);
            System.out.println("People: "+people.toString());
            System.out.println("Owner: "+owner);
            if(size == 1 && !owner.equals(docowner))
            {
                System.out.println("SIZE 1 e docowner"); readers.add(owner);people.add(owner);
                list.add(s);
                list.add(users);
                list.add(writers);
                list.add("nosave");
                return list;
            }
            else if (people.toString().contains(owner) == true) {
                list.add(s);
                list.add(users);
                list.add(writers);
                return list;
            } else {
                list.add(error);
                list.add(nousers);
                list.add(nousers);
                return list;
            }

        } catch (Exception ex) {
            System.out.println("In Load Doc "+ex.toString());
            List<String> list = new LinkedList();
            list.add("errore");
            list.add("errore");
            list.add("errore");
            return list;
        }
    }

    private String findBody(String r) {
        int ind1 = r.indexOf("connections");
        int ind2 = r.indexOf("<br></body>");
        r = r.substring(ind1 - 2, ind2);
        r = r.replaceAll("&gt;", ">");
        r = r.replaceAll("&lt;", "<");
        return r;
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

    public String showAllDocs() throws Exception {
        String ret = "";
        // for (int i = 0; i < 10; i++) {
        DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries()) {
            ret += printDocumentEntry(entry) + ",";
        }
        try {
            //  Thread.currentThread().sleep(1000 * 121);
        } catch (Exception e) {
            System.out.println("Dentro show all: "+e.getMessage());
            // }
        }
        return ret;
    }

    public String printDocumentEntry(DocumentListEntry doc) throws MalformedURLException, IOException, ServiceException {
        String shortId = doc.getId().substring(doc.getId().lastIndexOf('/') + 1);
        DateTime date = doc.getPublished();
        DateTime date1 = doc.getUpdated();
        String versionId = doc.getVersionId();
        String resourceId = doc.getResourceId();
        String modified = doc.getLastModifiedBy().getName();
        /*String resourceFolderId = "";
        URL feedUri = new URL("http://docs.google.com/feeds/documents/private/full/-/folder?showfolders=true");
        DocumentListFeed feed = service.getFeed(feedUri, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries())
        {        String name =entry.getTitle().getPlainText();
                 if(name.equalsIgnoreCase("Flow")){

                            resourceFolderId = entry.getResourceId();

                     }
        }*/
        String title = "";
        if (doc.getTitle().getPlainText().contains(".txt")) {
            title = doc.getTitle().getPlainText();
           /* URL url = new URL("http://docs.google.com/feeds/folders/private/full/"+resourceFolderId);
            DocumentEntry newEntry = new DocumentEntry();
            newEntry.setId(doc.getId());
            service.insert(url, newEntry);*/

        }
        String docType = resourceId.substring(0, resourceId.lastIndexOf(':'));

        //   System.out.println(" -- Document(" + shortId + "/" + doc.getTitle().getPlainText() + ")" + date.toString() +" ver. "+ versionId);
        String s = " " + doc.getTitle().getPlainText() + "C: " + date.toString() + " U: " + date1.toString();
     //   System.out.println("S "+s);

        return title;
    }

    public static String saveDiagram(String login, String name, boolean publish, String s, String users, String writers,String pwd,String assignees) {
        try {
            if (publish) {
                DocsService service = new DocsService("Document List Demo");
                service.setUserCredentials(login, pwd);
                if (users != null) {
                    return new GoDoc(service).saveDoc(login, name, s, users, writers,pwd,assignees);
                } else {
                    return new GoDoc(service).saveDoc(login, name, s);
                }
            } else {
                File tmp = File.createTempFile(name, null);
                FileWriter w = new FileWriter(tmp);
                w.write(s, 0, s.length());
                w.close();
                return "saved temp copy";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public static String savePermissions(String nomeFile,String flowName, String notification)
    {
        try {
            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            new GoDoc(service).savePermissionsOnFile(nomeFile,flowName,notification);
            return "save";
        } catch (Exception ex)
        {
            Logger.getLogger(GoDoc.class.getName()).log(Level.SEVERE, null, ex);
            System.out.println("In savePermission: "+ex.getMessage());
            return "error";
        }
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

            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            URL listFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full/");
            DocumentListFeed feed = service.getFeed(listFeedUrl, DocumentListFeed.class);
            DocumentListEntry doc =  new GoDoc(service).findEntry(nomeFile);
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
                File f;
                f=new File("/var/www/Permissions/prova.txt");
                if(!f.exists()) f.createNewFile();
                FileWriter writer = new FileWriter(f);
                writer.write(s);
                writer.close();
                FileReader fr = new FileReader(f);
                BufferedReader br = new BufferedReader(fr);
             
                String t ="";
                
                boolean subscribe = false;
                while((t = br.readLine()) != null)
                {

                    if(t.contains(flowName+"/"+"All")) subscribe = true;
                    else if(t.contains(flowName) && t.contains(notification)) subscribe = true;

                }
                fr.close();
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

     public void savePermissionsOnFile(String nomeFile, String flowName, String notification) {
         try
         {
             DocsService service = new DocsService("Document List Demo");
             service.setUserCredentials(docMakerLogin, docMakerPasswd);
             URL listFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full/");
             DocumentListFeed feed = service.getFeed(listFeedUrl, DocumentListFeed.class);
             DocumentListEntry doc =  new GoDoc(service).findEntry(nomeFile);
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
                 String add = flowName+"/"+notification;
                 File f;
                 f=new File("/var/www/Permissions/prova.txt");
                
                 if(!f.exists()) f.createNewFile();
                 FileWriter writer = new FileWriter(f);
                 writer.write(s);
                 writer.close();
                 FileReader fr = new FileReader(f);
                 BufferedReader br = new BufferedReader(fr);
                 String oldsetting = "";
                 String t ="";
                 String oldtext = "";
                 while((t = br.readLine()) != null)
                 {
                     System.out.println("LINE: "+t);
                     if(t.contains(flowName)) oldsetting =t;
                     oldtext += t + "\r\n";
                 }
                 fr.close();
                 System.out.println("OLD: "+oldsetting);
                 if(!oldsetting.equals(""))  s = oldtext.replaceAll(oldsetting, add);
                 else s += "\r\n"+add;
                 service.getRequestFactory().setHeader("If-Match", "*");
                 doc.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));
                 doc.updateMedia(false);
            }
            else
            {
                String s = flowName+"/"+notification+"\r\n";
                doc = new GoDoc(service).uploadFile(s, nomeFile);
            }

        }
            catch(Exception e){System.out.println("ERRORE: "+e.getMessage());}

    }


     public String saveDoc(String login, String name, String s) {
        try {
            DocumentListEntry documentEntry = findEntry(name);
            if (documentEntry == null) {
                documentEntry = uploadFile(s, name);

                return "salvato";
            }

            service.getRequestFactory().setHeader("If-Match", "*");
            documentEntry.setMediaSource(new MediaByteArraySource(s.getBytes(), "text/plain"));

            //  documentEntry.setContent(new PlainTextConstruct(s));
            documentEntry.updateMedia(false);
            return "salvato";
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }






    public static String saveTemplate(String login, String name, String s) {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(docMakerLogin, docMakerPasswd);


            return new GoDoc(service).saveDoc(login, name, s);

        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public static List<String> loadDiagram(String valorefile, boolean refresh, String owner,String pwd) {
        DocsService service = new DocsService("Document List Demo");
        try {
        
            service.setUserCredentials(owner, pwd);

            return new GoDoc(service).loadDoc(valorefile, refresh, owner);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println("In loadDiagram "+ex.toString());
            return null;
        }
    }

    public DocumentListEntry findEntry(String title) throws Exception {
        DocumentListEntry ret = null;
        DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries()) {
            if (entry.getTitle().getPlainText().equals(title)) {
                return entry;
            }
        }
        return ret;
    }


    public static String checkid(String user, String pwd)
    {
        try
        {
            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(user, pwd);
            return "checked";
        }
        catch(Exception e)
        {
            System.out.println("Checkid method: "+e.getMessage());
            return "error";
        }
    }



    public DocumentListEntry uploadFile(String content, String title, URL uri)
        throws IOException, ServiceException  {
      File file = new File("/tmp/" + title);
      FileOutputStream of = new FileOutputStream(file);
      of.write(content.getBytes(), 0, content.length());
      of.close();
      DocumentListEntry newDocument = new DocumentListEntry();
      String mimeType = DocumentListEntry.MediaType.fromFileName(file.getName()).getMimeType();
      newDocument.setFile(file, mimeType);;
      newDocument.setTitle(new PlainTextConstruct(title));
      DocumentListEntry ret = service.insert(uri, newDocument);
      return ret;






        



    }



    public void sendMail(String dest,String login,String pwd,String name)
    {
        try
        {
            System.out.println("%%%%LOGIN: "+login);
            String SMTP_HOST_NAME = "smtp.gmail.com";
            String SMTP_PORT = "465";
            String url = "http://localhost:8081/index.jsp?Flow="+name;
            String url2 = "http://localhost:8081/NotifMgrG/settings.jsp?Flow="+name;
            String text = "Hi, a new diagram is interested in you.\nGo and check this workflow "+url+"\n";
            String text2 ="Do not forget to subscribe your Notification Manager to this application: "+url2+"\n";
            String emailMsgTxt = text+text2;
            
            String emailSubjectTxt = "New Collaborative Workflow: "+name;
            String emailFromAddress = login;
            String SSL_FACTORY = "javax.net.ssl.SSLSocketFactory";
            String[] sendTo = dest.split(",");
            new SendMailCl().sendSSLMessage(sendTo, emailSubjectTxt, emailMsgTxt, emailFromAddress,pwd);
        }
        catch(Exception ex)
        { 
            System.out.println("In SendMail Method: "+ex.getMessage());
        }


    }

    public static String checkNew(String nomeFile,String user,String pwd)
    {
        try
        {
            DocsService service = new DocsService("Document List Demo");
            service.setUserCredentials(user, pwd);
            return new GoDoc(service).findNewVersion(nomeFile);
            

        }
        catch(Exception ex)
        {
            System.out.println("ERRORE IN CHECKNEW: "+ex.getMessage());
            return "error";
        }


    }

    public String findNewVersion(String nomeFile) 
    {
        try
        {
            DocumentListEntry documentEntry = findEntry(nomeFile);
            DateTime last = documentEntry.getUpdated();
            DateTime curr = documentVersions.get(nomeFile);
           // System.out.println("LAST: "+last.getValue());
           // System.out.println("CURR: "+curr.getValue());
            if (curr == null || curr.getValue() == last.getValue())
            {

                    return "old";
            }
            else
            {
                documentVersions.put(nomeFile, last); // update current version
                return "new";
            }
       }catch(Exception ex)
       {
           System.out.println("ERRORE IN FIND NEW VERSION "+ex.getMessage());
           return "error";
       }
        
    }



}
