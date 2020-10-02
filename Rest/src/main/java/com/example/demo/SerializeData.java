
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
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class SerializeData {

//    public static void serrialize(HashMap<Double, MapNode> nodeMap) {
//        ObjectOutputStream oos = null;
//        FileOutputStream fout = null;
//        try {
//            fout = new FileOutputStream("./data/nodemap.ser", true);
//            oos = new ObjectOutputStream(fout);
//            oos.writeObject(nodeMap);
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//            ex.printStackTrace();
//        } finally {
//            if (oos != null) {
//                try {
//                    oos.close();
//                } catch (IOException e) {
//                    System.out.println(e.getMessage());
//                    e.printStackTrace();
//                }
//            }
//        }
//    }
//
//    public static HashMap<Double, MapNode> deserialize() {
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
//            if (objectinputstream != null) {
//                try {
//                    objectinputstream.close();
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
//        try
//        {
//            FileOutputStream fos =
//                    new FileOutputStream("./data/hashmap.ser");
//            ObjectOutputStream oos = new ObjectOutputStream(fos);
//            oos.writeObject(nodeMap_orig);
//            oos.close();
//            fos.close();
//            System.out.printf("Serialized HashMap data is saved in hashmap.ser");
//        }catch(IOException ioe)
//        {
//            ioe.printStackTrace();
//        }
//    }
//
        HashMap<Double, MapNode> map = null;
        try
        {
            FileInputStream fis = new FileInputStream("./data/hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            map = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
            return;
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
            return;
        }
        System.out.println("Deserialized HashMap..");
        // Display content using Iterator
        for (double k: map.keySet()){
            System.out.println("Key: "+k);
            System.out.println("Lat: " +map.get(k).latitude);
            System.out.println("Lon: "+map.get(k).longitude);
    }
//        serrialize(nodeMap_orig);

//        HashMap<Double, MapNode> nodeMap_deser = deserialize();
    }
}
