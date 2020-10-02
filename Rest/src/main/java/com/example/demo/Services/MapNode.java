package com.example.demo.Services;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapNode implements Comparable<MapNode>, Serializable {
    public static Graph graph;
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



    @Override
    public int compareTo(MapNode o) {
        if(this.estimatedCost == o.estimatedCost)
            return 0;
        return this.estimatedCost < o.estimatedCost ? -1 : 1;
    }
}
