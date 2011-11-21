/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vt.client;

import com.google.gdata.client.docs.DocsService;
import com.google.gdata.data.DateTime;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.acl.AclEntry;
import com.google.gdata.data.acl.AclFeed;
import com.google.gdata.data.docs.DocumentListEntry;
import com.google.gdata.data.docs.DocumentListFeed;
import com.google.gdata.data.docs.FolderEntry;
import com.google.gdata.data.extensions.LastModifiedBy;
import com.google.gdata.util.ServiceException;

import java.io.IOException;
import java.net.URL;

/**
 *
 * @author marino
 */
public class DocumentoGoogle {

    static String email = "icemgr09@gmail.com";
    static String pwd = "sync09fr";
    URL documentListFeedUrl = null;
    DocsService service = null;

    public DocumentoGoogle(DocsService service) {
        try {
            documentListFeedUrl = new URL("https://docs.google.com/feeds/documents/private/full");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        this.service = service;
    }

    public static void main(String[] args) {
        DocsService service = new DocsService("Document List Demo");

        try {
            service.setUserCredentials(email, pwd);
            //   new DocumentListDemoOld(service).doStuff();
            new DocumentoGoogle(service).showAllDocs();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println("CIAO");
    }

    public void showAllDocs() throws IOException, ServiceException {
        URL feedUri = new URL("https://docs.google.com/feeds/default/private/full");
        DocumentListFeed feed = service.getFeed(feedUri, DocumentListFeed.class);
createFolder ("pippo");
        for (DocumentListEntry entry : feed.getEntries()) {
            stampaAcl(entry );
            System.out.println("");
            printEntry(entry);
        }
    }

    public void printEntry(DocumentListEntry entry) {
        String resourceId = entry.getResourceId();
        String docType = entry.getType();

        System.out.println("'" + entry.getTitle().getPlainText() + "' (" + docType + ")");
        System.out.println("  link to Google Docs: " + entry.getDocumentLink().getHref());
        System.out.println("  resource id: " + resourceId);
        System.out.println("  doc id: " + entry.getDocId());

        // print the parent folder the document is in
        if (!entry.getParentLinks().isEmpty()) {
            System.out.println("  Parent folders: ");
            for (Link link : entry.getParentLinks()) {
                System.out.println("    --" + link.getTitle() + " - " + link.getHref());
            }
        }

        // print the timestamp the document was last viewed
        DateTime lastViewed = entry.getLastViewed();
        if (lastViewed != null) {
            System.out.println("  last viewed: " + lastViewed.toUiString());
        }

        // print who made the last modification
        LastModifiedBy lastModifiedBy = entry.getLastModifiedBy();
        if (lastModifiedBy != null) {
            System.out.println("  updated by: "
                    + lastModifiedBy.getName() + " - " + lastModifiedBy.getEmail());
        }

        // Files such as PDFs take up quota
        if (entry.getQuotaBytesUsed() > 0) {
            System.out.println("Quota used: " + entry.getQuotaBytesUsed() + " bytes");
        }

        // print other useful metadata
        System.out.println("  last updated: " + entry.getUpdated().toUiString());
        System.out.println("  viewed by user? " + entry.isViewed());
        System.out.println("  writersCanInvite? " + entry.isWritersCanInvite().toString());
        System.out.println("  hidden? " + entry.isHidden());
        System.out.println("  starred? " + entry.isStarred());
        System.out.println("  trashed? " + entry.isTrashed());
        System.out.println();
    }

   private void stampaAcl(DocumentListEntry entry1 )  {
       try {
  ////  DocumentQuery query = new DocumentQuery(new URL("https://docs.google.com/feeds/default/private/full/-/mine"));
//DocumentListFeed resultFeed = client.getFeed(query, DocumentListFeed.class);
//DocumentListEntry entry = resultFeed.getEntries().get(0);

AclFeed aclFeed = service.getFeed(new URL(entry1.getAclFeedLink().getHref()), AclFeed.class);
for (AclEntry entry : aclFeed.getEntries()) {
  System.out.println(
      entry.getScope().getValue() + " (" + entry.getScope().getType() + ") : " + entry.getRole().getValue());
}} catch (Exception ex){ex.printStackTrace();}
    }

   private  DocumentListEntry createFolder(String title) throws IOException, ServiceException {
  DocumentListEntry newEntry = new FolderEntry();
  newEntry.setTitle(new PlainTextConstruct(title));
  URL feedUrl = new URL("https://docs.google.com/feeds/default/private/full/");
  return service.insert(feedUrl, newEntry);
}
}
