package com.example.demo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
public class PSQLConnect {
    public static void main(String[] args) {

        String url = "jdbc:REMOVEDql://localhost:5432/osm";
        String user = "REMOVED";
        String password = "smile01981124";

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement("SELECT * FROM planet_osm_nodes");
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {

                System.out.print(rs.getInt(2));
                System.out.print(", ");
                System.out.println(rs.getInt(3));
            }

        } catch (SQLException ex) {
            System.out.println("Connection failure.");
            ex.printStackTrace();
        }
    }
}
