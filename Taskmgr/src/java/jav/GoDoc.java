/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jav;

//import com.google.appengine.repackaged.com.google.protobuf.ServiceException;
import com.google.gdata.client.*;
import com.google.gdata.client.DocumentQuery;
import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.MediaContent;
import com.google.gdata.data.Person;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.acl.AclRole;
import com.google.gdata.data.acl.AclScope;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.media.MediaByteArraySource;
import com.google.gdata.data.media.MediaSource;
import com.google.gdata.util.ServiceException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * @author marino
 */
public class GoDoc {
    /*
     * To change this template, choose Tools | Templates
     * and open the template in the editor.
     */

    URL documentListFeedUrl = null;
    DocsService service = null;
    static HashMap<String, DateTime> documentVersions = new HashMap();
    private boolean printed = false;
    boolean one = false;
    //anna gio
    //   static String docMakerLogin = "annamaria.goy@gmail.com";  // fino a che  ???  auth funziona
    //   static String docMakerPasswd = "tex_willer";
   // static String docMakerLogin = "sgnmrn@gmail.com";  // fino a che  ???  auth funziona
    //static String docMakerPasswd = "micio11";
    static String docMakerLogin = "fabrizio.torretta@gmail.com";  // fino a che  ???  auth funziona
    static String docMakerPasswd = "gregorio";
    //fine Anna gio

    public GoDoc(DocsService service) {
        try {
            documentListFeedUrl = new URL("http://docs.google.com/feeds/documents/private/full");

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.service = service;
    }

    public static void main(String[] args) throws MalformedURLException, IOException, ServiceException {
         DocsService service = new DocsService("Document List Demo");
         service.setUserCredentials(docMakerLogin, docMakerPasswd);
        DocumentQuery query = new DocumentQuery(new URL("http://docs.google.com/feeds/documents/private/full/-/mine"));
//DocumentListEntry documentEntry = findEntry("prova6.txt");
DocumentListFeed resultFeed = service.getFeed(query, DocumentListFeed.class);






       System.out.println(prendi());
    }

    public static String prendi() {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
           return new GoDoc(service).loadDoc("prova17.txt",false,"giovanna@di.unito.it").toString();
       //     return new GoDoc(service).showAllDocs();
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }


    public String saveDoc(String name, String s,String users, String writers) {
        try {
            DocumentListEntry documentEntry = findEntry(name);
            if (documentEntry == null) {
                documentEntry = uploadFile(s, name);
                
                String[] tempreader;
                String delimiter = ",";

                if(writers != null){
                    String[] tempwriter;

                    tempwriter = writers.split(delimiter);
                    
                    for(int i =0; i < tempwriter.length ; i++) 
                    {
                        System.out.println("Writer "+tempwriter[i]);
                        addWriting(documentEntry, tempwriter[i]);
                        
                    }
                    tempreader = users.split(delimiter);
                    for(int j =0; j < tempreader.length ; j++)
                    {
                       boolean check = false;

                       for(int i = 0;i<tempwriter.length;i++)
                       {
                           if(tempwriter[i].equals(tempreader[j])) check = true;
                       }
                        if(check == false) { System.out.println("Reader " +tempreader[j]+check); addReaders(documentEntry,tempreader[j]);}
                    }
                    
                }
                tempreader = users.split(delimiter);
                    for(int j =0; j < tempreader.length ; j++)
                    {
                        addReaders(documentEntry,tempreader[j]);
                    }
                
               
                
                //addWriting(documentEntry, "gio.petrone@gmail.com");

                return "salvato";
            }
            String[] tempreader;
                String delimiter = ",";

                if(writers != null){
                    String[] tempwriter;
                    tempwriter = writers.split(delimiter);

                    for(int i =0; i < tempwriter.length ; i++)
                    {
                        System.out.println("Writer "+tempwriter[i]);
                        addWriting(documentEntry, tempwriter[i]);
                    }
                    tempreader = users.split(delimiter);
                    for(int j =0; j < tempreader.length ; j++)
                    {
                        System.out.println("Reader " +tempreader[j]);
                        if(!tempwriter.toString().contains(tempreader[j])) addReaders(documentEntry,tempreader[j]);
                    }

                }
                tempreader = users.split(delimiter);
                    for(int j =0; j < tempreader.length ; j++)
                    {
                        addReaders(documentEntry,tempreader[j]);
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



     public String saveDoc(String name, String s) {
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

    private AclEntry addWriting(DocumentListEntry documentEntry, String who) throws IOException, MalformedURLException, ServiceException {
        AclRole role = new AclRole("writer");

        AclScope scope = new AclScope(AclScope.Type.USER, who);
        AclEntry entry = new AclEntry();
        entry.setRole(role);
        entry.setScope(scope);
        URL url = new URL("http://docs.google.com/feeds/acl/private/full/" + documentEntry.getResourceId());
        return service.insert(url, entry);
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
            AclFeed aclFeed = service.getFeed(new URL(documentEntry.getAclFeedLink().getHref()), AclFeed.class);
          
            for (AclEntry entry : aclFeed.getEntries()) {
                people.add(entry.getScope().getValue());
              //  System.err.println(entry.getRole().getValue());
                if(entry.getRole().getValue().equals("writer")) collaborators.add(entry.getScope().getValue());

                        
            }
            String users = people.toString();
            if(users.equals("[]")) users = "";
            else users = users.substring(1, users.length() - 1);
            String writers = collaborators.toString();
            if(writers.equals("[]"))writers = "";
            else writers = writers.substring(1, writers.length() - 1);
            if(people.toString().contains(owner) == true)  {list.add(s);list.add(users);list.add(writers);return list;}
            else { list.add(error);list.add(null);list.add(null); return list;}
        } catch (Exception ex) {
            System.out.println( ex.toString());
            return null;
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

    public String showAllDocs() throws Exception {
        String ret = "";
        // for (int i = 0; i < 10; i++) {
        DocumentListFeed feed = service.getFeed(documentListFeedUrl, DocumentListFeed.class);
        for (DocumentListEntry entry : feed.getEntries()) {
            ret += printDocumentEntry(entry) + "\n";
        }
        try {
            //  Thread.currentThread().sleep(1000 * 121);
        } catch (Exception e) {
            // }
        }
        return ret;
    }

    public String printDocumentEntry(DocumentListEntry doc) {
        String shortId = doc.getId().substring(doc.getId().lastIndexOf('/') + 1);
        DateTime date = doc.getPublished();
        DateTime date1 = doc.getUpdated();
        String versionId = doc.getVersionId();
        //   System.out.println(" -- Document(" + shortId + "/" + doc.getTitle().getPlainText() + ")" + date.toString() +" ver. "+ versionId);
        String s = " " + doc.getTitle().getPlainText() + "C: " + date.toString() + " U: " + date1.toString();
        // System.out.println(s);
        return s;
    }

    public static String saveDiagram(String name, String s,String users,String writers) {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(docMakerLogin, docMakerPasswd);
            if(users != null) return new GoDoc(service).saveDoc(name, s, users,writers);

            else  return new GoDoc(service).saveDoc(name,s);
         
        } catch (Exception ex) {
            ex.printStackTrace();
            return ex.toString();
        }
    }

    public static List<String> loadDiagram(String valorefile, boolean refresh,String owner) {
        DocsService service = new DocsService("Document List Demo");
        try {
            service.setUserCredentials(docMakerLogin, docMakerPasswd);

            return new GoDoc(service).loadDoc(valorefile, refresh,owner);
        } catch (Exception ex) {
            ex.printStackTrace();
            System.out.println(ex.toString());
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
}
