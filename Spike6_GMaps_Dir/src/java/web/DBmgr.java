/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;

/**
 *
 * @author goy
 */
public class DBmgr {

    public static ArrayList<Evento> getEvents() {
        ArrayList<Evento> result = new ArrayList();
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/eventi", "root", "");
            stmt = con.createStatement();
            rs = stmt.executeQuery("SELECT * FROM eventi_torino");
            // displaying records
            while (rs.next()) {
                //result = rs.toString();
                Evento ev = new Evento();
                ev.setName(rs.getString("nome"));
                ev.setAddress(rs.getString("indirizzo"));
                result.add(ev);
            }
            rs.close();
            stmt.close();
            con.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
