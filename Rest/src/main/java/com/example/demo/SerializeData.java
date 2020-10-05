package com.example.demo;

import com.example.demo.Services.Graph;
import com.example.demo.Services.MapNode;

import java.util.HashMap;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
public class SerializeData {

    public static void serrialize(HashMap<Double, MapNode> nodeMap){
        ObjectOutputStream oos = null;
        FileOutputStream fout = null;
        try{
            fout = new FileOutputStream("./data/nodemap.ser", true);
            oos = new ObjectOutputStream(fout);
            oos.writeObject(nodeMap);
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        } finally {
            if(oos != null){
                try {
                    oos.close();
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
//    public static HashMap<Double, MapNode> deserialize(){
//        HashMap<Double, MapNode> nodeMap = new HashMap<>();
//        ObjectInputStream objectinputstream = null;
//        try {
//            FileInputStream streamIn = new FileInputStream("G:\\address.ser");
//            objectinputstream = new ObjectInputStream(streamIn);
//            nodeMap = (HashMap<Double, MapNode>) objectinputstream.readObject();
//        } catch (Exception e) {
//            e.printStackTrace();
//            System.out.println(e.getMessage());
//        } finally {
//            if(objectinputstream != null){
//                try {
//                    objectinputstream .close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    System.out.println(e.getMessage());
//                }
//            }
//        }
//        return nodeMap;
//    }

    public static void main(String[] args) {
//        Graph torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
//        torontoGraph.loadFiles("./data/toronto.osm", "./data/Cyclists.csv");
//        HashMap<Double, MapNode> nodeMap_orig = torontoGraph.routeNodes;
//
//        serrialize(nodeMap_orig);
        HashMap<Double, MapNode> nodeMap = new HashMap<>();
        try {
            FileInputStream fileIn = new FileInputStream("./data/nodemap.ser");
            ObjectInputStream in = new ObjectInputStream(fileIn);
            nodeMap = (HashMap<Double, MapNode>) in.readObject();
            in.close();
            fileIn.close();
        } catch (IOException i) {
            i.printStackTrace();
            return;
        } catch (ClassNotFoundException c) {
            System.out.println("class not found");
            c.printStackTrace();
            return;
        }

        System.out.println("Deserialized map...");
        System.out.println(nodeMap.keySet());
//        for (Double d: nodeMap.keySet()){
//            System.out.println("Lon: " + nodeMap.get(d).longitude);
//            System.out.println("Lat: " + nodeMap.get(d).latitude);
//        }

//        HashMap<Double, MapNode> nodeMap_deser = deserialize();



        /*
        HashMap<Integer, String> test = getNeighbourhoodCoordinate();
        test.entrySet().forEach(entry->{
            System.out.println(entry.getKey() + ":   " + entry.getValue());
        });
        */
        /*
        ArrayList<ArrayList<Double>> test2 = getPedestrianCount("08:00:00", "10:00:00");
        for (ArrayList list : test2) {
            System.out.println(list.get(0) + ", " + list.get(1) + ": " + list.get(2));
        }
        */

    }

}
