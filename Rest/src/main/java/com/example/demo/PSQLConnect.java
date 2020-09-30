package com.example.demo;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;
public class PSQLConnect {
    public static String url = "jdbc:REMOVEDql://localhost:5432/toronto";
    public static String user = "REMOVED";
    public static String password = "REMOVED";

    //get node id from longitude and latitude
    public static Double getNodeID(String longitude, String latitude){
        Double id = null;
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(
                     "SELECT id FROM planet_osm_nodes" +
                             " WHERE (lat = '" + latitude.replace(".","") +
                             "' AND lon = '" + longitude.replace(".","") + "');"
             );
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()){
                id = new Double (rs.getInt("id"));
            }

        } catch (SQLException ex) {
            System.out.println("Connection failure.");
            ex.printStackTrace();
        }
        return id;
    }

    //get neighbourhood coordinates
    public static HashMap<Integer, String> getNeighbourhoodCoordinate(){
        HashMap<Integer, String> res = new HashMap<>();
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(
                     "SELECT _id, coordinates FROM toronto_neighbourhood;"
             );
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()){
                res.put(rs.getInt("_id"), rs.getString("coordinates"));
            }

        } catch (SQLException ex) {
            System.out.println("Connection failure.");
            ex.printStackTrace();
        }
        return res;
    }

    //get pedestrian count data
    public static ArrayList<ArrayList<Double>> getPedestrianCount(String start_time, String end_time){
        ArrayList<ArrayList<Double>> res = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(
                     "SELECT \n" +
                             "latitude, longitude,\n" +
                             "AVG (ped_count) AS avg_ped_count\n" +
                             "FROM pedestrian_count_data \n" +
                             "WHERE time_stamp::time between '" + start_time + "' and '" + end_time + "'\n" +
                             "GROUP BY time_stamp, latitude, longitude;"
             );
             ResultSet rs = pst.executeQuery()) {
            ArrayList<Double> temp = new ArrayList<>();
            while (rs.next()){
                temp = new ArrayList<>();
                temp.add(rs.getDouble("latitude"));
                temp.add(rs.getDouble("longitude"));
                temp.add(rs.getDouble("avg_ped_count"));
                res.add(temp);
            }

        } catch (SQLException ex) {
            System.out.println("Connection failure.");
            ex.printStackTrace();
        }
        return res;
    }


    public static void main(String[] args) {


        HashMap<Integer, String> test = getNeighbourhoodCoordinate();
        test.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + ":   " + entry.getValue());
        });

        /*
        ArrayList<ArrayList<Double>> test2 = getPedestrianCount("08:00:00", "10:00:00");
        for (ArrayList list : test2) {
            System.out.println(list.get(0) + ", " + list.get(1) + ": " + list.get(2));
        }
        */

    }
}
