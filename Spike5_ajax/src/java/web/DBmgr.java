/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package web;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 *
 * @author goy
 */
public class DBmgr {

    public static String getCap(String city) {
        String result = "";
        Connection con = null;
        Statement stmt = null;
        ResultSet rs = null;
        try {
            Class.forName("com.mysql.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/caps", "root", "");
            stmt = con.createStatement();
            //rs = stmt.executeQuery("SELECT * FROM codici WHERE citta='" + city + "'");
            rs = stmt.executeQuery("SELECT * FROM codici");
            // displaying records
            while (rs.next()) {
                result = rs.getString("cap");
                //result = rs.getObject(2).toString();
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (rs != null) {
                    rs.close();
                    rs = null;
                }
                if (stmt != null) {
                    stmt.close();
                    stmt = null;
                }
                if (con != null) {
                    con.close();
                    con = null;
                }
            } catch (SQLException e) {
            }
        }
        return city;
    }
}
