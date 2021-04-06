package com.example.demo;
import com.example.demo.Services.MapNode;
import org.w3c.dom.NodeList;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.sql.*;

//joyce database connection
public class PSQLConnect {
//kelly's db conn
//    public static String url = "jdbc:postgresql:torontodata";
//    public static String user = "yixinsun";
//    public static String password = "torontodata";

    public static String url = "jdbc:postgresql://localhost:5432/toronto";
    public static String user = "postgres";
    public static String password = "postgres";

//    public static String url = "jdbc:postgresql://localhost:5432/Toronto";
//    public static String user = "postgres";
//    public static String password = "capstone";

    public static MapNode getNodebyID(String id){
        MapNode node = new MapNode();

        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(
                     "SELECT * FROM planet_osm_nodes" +
                             " WHERE (id = '" + id + "');"
             );
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()){
                node = new MapNode();
                node.id = new Double (rs.getLong("id"));
                node.latitude = new Double (rs.getInt("lat"));
                node.longitude = new Double (rs.getInt("lon"));
            }

        } catch (SQLException ex) {
            System.out.println("Connection failure.");
            ex.printStackTrace();
        }
        return node;
    }

    public static HashMap<Double,MapNode> getNodeList(){
        HashMap<Double,MapNode> nodelist = new HashMap<Double,MapNode>();
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(
                     "SELECT * FROM planet_osm_nodes;"
             );
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()){
                MapNode node = new MapNode();
                node.id = new Double (rs.getInt("id"));
                node.latitude = new Double (rs.getInt("lat"));
                node.longitude = new Double (rs.getInt("lon"));
                nodelist.put(node.id,node);
            }

        } catch (SQLException ex) {
            System.out.println("Connection failure.");
            ex.printStackTrace();
        }
        return nodelist;
    }
    public static ArrayList<ArrayList<String>> getRouteList(){
        ArrayList<ArrayList<String>> routelist = new ArrayList<>();
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(
                     "SELECT nodes FROM planet_osm_ways;"
             );
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()){
                ArrayList<String> newnodelist = new ArrayList<String>(Arrays.asList(rs.getString("nodes").substring(1, rs.getString("nodes").length() -1).split(",",0)));

                routelist.add(newnodelist);
            }

        } catch (SQLException ex) {
            System.out.println("Connection failure.");
            ex.printStackTrace();
        }
        return routelist;
    }
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
                             "WHERE time_stamp >= '" + start_time +"'AND  time_stamp <  '"+ end_time+ "'\n"+
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

    public static HashMap<String, Double> getPedCountHeatmap(String start_time, String end_time){
        HashMap<String, Double> res = new HashMap<>();
        try (Connection con = DriverManager.getConnection(url, user, password);
             PreparedStatement pst = con.prepareStatement(
                     "select distinct(concat_ws(',',longitude,latitude)) as coord, round(avg(ped_count)::numeric,3) as count from pedestrian_count_data \n" +
                             "WHERE time_stamp >= '" + start_time +"'AND  time_stamp <  '"+ end_time+ "'\n"+
                             "group by coord;\n"
             );
             ResultSet rs = pst.executeQuery()) {
            while (rs.next()){
                res.put(rs.getString("coord"), rs.getDouble("count"));
            }

        } catch (SQLException ex) {
            System.out.println("Connection failure.");
            ex.printStackTrace();
        }
        return res;
    }


    public static void main(String[] args) {

        /*
        HashMap<Integer, String> test = getNeighbourhoodCoordinate();
        test.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + ":   " + entry.getValue());
        });
        /*
        /*
        ArrayList<ArrayList<Double>> test2 = getPedestrianCount("08:00:00", "10:00:00");
        for (ArrayList list : test2) {
            System.out.println(list.get(0) + ", " + list.get(1) + ": " + list.get(2));
        }
        */

    }
}
