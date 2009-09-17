/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package googlecontacts;

import com.google.gdata.client.contacts.ContactsService;


//import com.google.gdata.data.appsforyourdomain.Email;
import com.google.gdata.data.Link;
import com.google.gdata.data.PlainTextConstruct;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.contacts.ContactGroupEntry;
import com.google.gdata.data.contacts.ContactGroupFeed;
import com.google.gdata.data.contacts.GroupMembershipInfo;

import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.ExtendedProperty;
import com.google.gdata.data.extensions.Im;
import com.google.gdata.util.ServiceException;
import java.io.IOException;
import java.net.URL;
import java.util.List;

/**
 *
 * @author marino
 */
public class ContactCallSAV {

    ContactsService myService = null;
    String googleContactFeed = "http://www.google.com/m8/feeds/contacts/";
    String googleGroupFeed = "http://www.google.com/m8/feeds/groups/";
    String googleUserMail;
    URL contactsUrl;
    URL groupsUrl;

    public ContactCallSAV(String googleUserMail, String pwd) {
        System.out.println("mail,pwd " + googleUserMail + "," + pwd);
        this.googleUserMail = googleUserMail;
        myService = new ContactsService("exampleCo-exampleApp-1");

        try {
            myService.setUserCredentials(googleUserMail, pwd);
            contactsUrl = new URL(googleContactFeed + googleUserMail + "/full");
            groupsUrl = new URL(googleGroupFeed + googleUserMail + "/full");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        System.out.println(googleContactFeed + googleUserMail + "/full");
    }

    public ContactGroupEntry creaGruppo(String nomeGruppo, String noteGruppo, ExtendedProperty additionalInfo) {
        ContactGroupEntry newGroup = null;
        try {
            newGroup =
                    createContactGroup(myService, nomeGruppo, noteGruppo, additionalInfo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return newGroup;
    }

      public void  addGroupToContact(String nomeGruppo, String noteGruppo, ExtendedProperty additionalInfo) {

        try {
//            myService.update(contactsUrl, entry);
           // newGroup =                    createContactGroup(myService, nomeGruppo, noteGruppo, additionalInfo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }
 /**
   * Gets a contact by it's id.
   *
   * @param id the id of the contact.
   * @return the ContactEntry or null if not found.
   */
  private ContactEntry getContactInternal(String id)
      throws IOException, ServiceException {
    return myService.getEntry(contactsUrl, ContactEntry.class);
  }
  /**
   * Deletes a contact or a group
   *
   * @param parameters the parameters determining contact to delete.
   */
  /**
   * Deletes a contact or a group
   *
   * @param parameters the parameters determining contact to delete.
   */
  private void deleteEntry(ContactsExampleParameters parameters)
      throws IOException, ServiceException {
    if (parameters.isGroupFeed()) {
      // get the Group then delete it
      ContactGroupEntry group = getGroupInternal(parameters.getId());
      if (group == null) {
        System.err.println("No Group found with id: " + parameters.getId());
        return;
      }
      group.delete();
    } else {
      // get the contact then delete them
      ContactEntry contact = getContactInternal(parameters.getId());
      if (contact == null) {
        System.err.println("No contact found with id: " + parameters.getId());
        return;
      }
      contact.delete();
    }
  }

 /**
   * Gets a Group by it's id.
   *
   * @param id the id of the group.
   * @return the GroupEntry or null if not found.
   */
  private ContactGroupEntry getGroupInternal(String id)
      throws IOException, ServiceException {
    return myService.getEntry(contactsUrl,        ContactGroupEntry.class);
  }

    public List<ContactEntry> getUserContacts() {
        List<ContactEntry> lis = null;
        try {
            ContactFeed resultFeed = myService.getFeed(contactsUrl, ContactFeed.class);
            System.out.println(resultFeed.getTitle().getPlainText());
            lis = resultFeed.getEntries();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lis;
    }

    public List<ContactGroupEntry> getUserGroups() {
        List<ContactGroupEntry> lis = null;
        try {
            ContactGroupFeed resultFeed = myService.getFeed(groupsUrl, ContactGroupFeed.class);
            lis = resultFeed.getEntries();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return lis;
    }

    // usata per prova iniziale
    public void getContacts() {
        try {
            ContactFeed resultFeed = myService.getFeed(contactsUrl, ContactFeed.class);
            // Print the results
            System.out.println(resultFeed.getTitle().getPlainText());
            for (int i = 0; i < resultFeed.getEntries().size(); i++) {
                ContactEntry entry = resultFeed.getEntries().get(i);
                System.out.println("\t" + entry.getTitle().getPlainText());

                System.out.println("Email addresses:");
                for (Email email : entry.getEmailAddresses()) {
                    System.out.print(" " + email.getAddress());
                    if (email.getRel() != null) {
                        System.out.print(" rel:" + email.getRel());
                    }
                    if (email.getLabel() != null) {
                        System.out.print(" label:" + email.getLabel());
                    }
                    if (email.getPrimary()) {
                        System.out.print(" (primary) ");
                    }
                    System.out.print("\n");
                }

                System.out.println("IM addresses:");
                for (Im im : entry.getImAddresses()) {
                    System.out.print(" " + im.getAddress());
                    if (im.getLabel() != null) {
                        System.out.print(" label:" + im.getLabel());
                    }
                    if (im.getRel() != null) {
                        System.out.print(" rel:" + im.getRel());
                    }
                    if (im.getProtocol() != null) {
                        System.out.print(" protocol:" + im.getProtocol());
                    }
                    if (im.getPrimary()) {
                        System.out.print(" (primary) ");
                    }
                    System.out.print("\n");
                }

                System.out.println("Groups:");
                for (GroupMembershipInfo group : entry.getGroupMembershipInfos()) {
                    String groupHref = group.getHref();
                    System.out.println("  Id: " + groupHref);
                }

                System.out.println("Extended Properties:");
                for (ExtendedProperty property : entry.getExtendedProperties()) {
                    if (property.getValue() != null) {
                        System.out.println("  " + property.getName() + "(value) = " +
                                property.getValue());
                    } else if (property.getXmlBlob() != null) {
                        System.out.println("  " + property.getName() + "(xmlBlob)= " +
                                property.getXmlBlob().getBlob());
                    }
                }

                Link photoLink = entry.getContactPhotoLink();
                if (photoLink != null) {
                    String photo = entry.getContactPhotoLink().getHref();
                    System.out.println("Photo Link: " + photo);

                    if (photoLink.getEtag() != null) {
                        System.out.println("Contact Photo's ETag: " + photoLink.getEtag());
                    }

                    System.out.println("Contact's ETag: " + entry.getEtag());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public ContactGroupEntry createContactGroup(ContactsService service,
            String name, String notes, ExtendedProperty additionalInfo)
            throws ServiceException, IOException {
        // Create the entry to insert
        ContactGroupEntry group = new ContactGroupEntry();
        group.setTitle(new PlainTextConstruct(name));

        group.addExtendedProperty(additionalInfo);

        // Ask the service to insert the new entry
        //  URL postUrl = new URL("http://www.google.com/m8/feeds/groups/liz@gmail.com/full");
        return service.insert(groupsUrl, group);
    }

    public void printAllGroups() {
        try {
            // Request the feed
            //   URL feedUrl = new URL("http://www.google.com/m8/feeds/groups/liz@gmail.com/full");
            ContactGroupFeed resultFeed = myService.getFeed(groupsUrl, ContactGroupFeed.class);
            // Print the results
            System.out.println(resultFeed.getTitle().getPlainText());

            for (int i = 0; i < resultFeed.getEntries().size(); i++) {
                ContactGroupEntry groupEntry = resultFeed.getEntries().get(i);
                System.out.println("Id: " + groupEntry.getId());
                System.out.println("Group Name: " + groupEntry.getTitle().getPlainText());
                System.out.println("Last Updated: " + groupEntry.getUpdated());
                System.out.println("Extended Properties:");
                for (ExtendedProperty property : groupEntry.getExtendedProperties()) {
                    if (property.getValue() != null) {
                        System.out.println("  " + property.getName() + "(value) = " +
                                property.getValue());
                    } else if (property.getXmlBlob() != null) {
                        System.out.println("  " + property.getName() + "(xmlBlob) = " +
                                property.getXmlBlob().getBlob());
                    }
                }
                System.out.println("Self Link: " + groupEntry.getSelfLink().getHref());
                if (!groupEntry.hasSystemGroup()) {
                    // System groups do not have an edit link
                    System.out.println("Edit Link: " + groupEntry.getEditLink().getHref());
                    System.out.println("ETag: " + groupEntry.getEtag());
                }
                if (groupEntry.hasSystemGroup()) {
                    System.out.println("System Group Id: " +
                            groupEntry.getSystemGroup().getId());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ContactCallSAV c = new ContactCallSAV("gio.petrone@gmail.com", "mer20ia05");
        c.getContacts();
        c.printAllGroups();
    //  c.creaGruppo();
    }
}
