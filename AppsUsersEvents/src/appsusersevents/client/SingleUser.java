/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package appsusersevents.client;

/**
 *
 * @author marino
 */
public class SingleUser extends TreeElement {

    private String mailAddress;
    private String pwd;
    private String id;
    //gio&anna 1-10-09
    private String googleId;
    private String googleDisplayName;

    public String getGoogleDisplayName() {
        return googleDisplayName;
    }

    public void setGoogleDisplayName(String googleDisplayName) {
        this.googleDisplayName = googleDisplayName;
    }

    public String getGoogleId() {
        return googleId;
    }

    public void setGoogleId(String googleId) {
        this.googleId = googleId;
    }

    //  static HashMap AllUsers = new HashMap();
    public SingleUser(String name) {
        super(name);
        mailAddress = "NOID";
        this.pwd = "NOPWD";
        //  AllUsers.put(name, this);
    }

    public SingleUser(String name, String mail, String pwd, String id) {
        super(name);
        mailAddress = mail;
        this.pwd = pwd;
        this.id = id;
        //  AllUsers.put(name, this);
    }

    // costruttore GIO E ANNA : role e' vuoto, potrebbe servire in seguito, al momento solo x distingure costrut.
    public SingleUser(String googleDisplayName, String mail, String pwd, String googleId, String role) {

        this.googleDisplayName = googleDisplayName;
        this.googleId = googleId;
        this.mailAddress = mail;
        this.pwd = pwd;

    }

    public SingleUser() {
    }

    public boolean equals(SingleUser u) {
        return getName().equals(u.getName()) &&
                getMailAddress().equals(u.getMailAddress());
    }

    /**
     * @return the mailAddress
     */
    public String getMailAddress() {
        return mailAddress;
    }

    /**
     * @param mailAddress the mailAddress to set
     */
    public void setMailAddress(String mailAddress) {
        this.mailAddress = mailAddress;
    }

    /**
     * @return the pwd
     */
    public String getPwd() {
        return pwd;
    }

    /**
     * @param pwd the pwd to set
     */
    public void setPwd(String pwd) {
        this.pwd = pwd;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(String id) {
        this.id = id;
    }
}
