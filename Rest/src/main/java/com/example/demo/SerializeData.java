package com.example.demo;

import com.example.demo.Services.Graph;
import com.example.demo.Services.MapNode;

import java.io.*;

import java.util.HashMap;

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
    public static HashMap<Double, MapNode> deserialize(){
        HashMap<Double, MapNode> nodeMap = new HashMap<>();
        ObjectInputStream objectinputstream = null;
        try {
            FileInputStream streamIn = new FileInputStream("G:\\address.ser");
            objectinputstream = new ObjectInputStream(streamIn);
            nodeMap = (HashMap<Double, MapNode>) objectinputstream.readObject();
        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            if(objectinputstream != null){
                try {
                    objectinputstream .close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return nodeMap;
    }

    public static void main(String[] args) {
        Graph torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
        torontoGraph.loadFiles("./data/toronto.osm", "./data/Cyclists.csv");
        HashMap<Double, MapNode> nodeMap_orig = torontoGraph.routeNodes;

        serrialize(nodeMap_orig);

        HashMap<Double, MapNode> nodeMap_deser = deserialize();



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
