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


   String mailAddress;
   String pwd;
   private String id;

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


    public SingleUser() {}

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
