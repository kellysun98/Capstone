package com.example.demo.Services;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.Serializable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MapNode implements Comparable<MapNode>, Serializable{
    //public Graph graph;
    public static Element element;
    public static double id;
    public static double longitude;
    public static double latitude;
    public static List<MapEdge> edges;
    public static double estimatedCost;

    public MapNode (){
        id = -1;
        longitude = -1;
        latitude = -1;
        edges = new ArrayList<>();
    }


    public MapNode (Element e){
        id = Double.parseDouble(e.getAttribute("id"));
        longitude = Double.parseDouble(e.getAttribute("lon"));
        latitude = Double.parseDouble(e.getAttribute("lat"));
        edges = new ArrayList<>();
    }


    private static void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        id = aInputStream.readDouble();
        longitude = aInputStream.readDouble();
        latitude = aInputStream.readDouble();
        edges = (List<MapEdge>) aInputStream.readObject();
        estimatedCost = aInputStream.readDouble();
    }

    private static void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        aOutputStream.writeDouble(id);
        aOutputStream.writeDouble(longitude);
        aOutputStream.writeDouble(latitude);
        aOutputStream.writeObject(edges);
        aOutputStream.writeDouble(estimatedCost);
    }


    @Override
    public int compareTo(MapNode o) {
        if(this.estimatedCost == o.estimatedCost)
            return 0;
        return this.estimatedCost < o.estimatedCost ? -1 : 1;
    }
}
