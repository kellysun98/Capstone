package com.example.demo.Services;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapNode implements Comparable<MapNode>{
    public Graph graph;
    public Element element;
    public double id;
    public double longitude;
    public double latitude;
    public List<MapEdge> edges;
    public double estimatedCost;

    public List<MapEdge> getEdges(){
        return edges;
    }

    public boolean equals(MapNode outsider){
        return outsider.id == this.id;
    }

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

    public MapNode clone(){
        MapNode copy = new MapNode();
        copy.id = this.id;
        copy.edges = this.edges;
        copy.estimatedCost = this.estimatedCost;
        copy.latitude = this.latitude;
        copy.longitude = this.longitude;

        return copy;

    }
    public void removeEdges(MapNode Node2Remove){
        for (int i=0; i<this.edges.size(); i++){
            if (edges.get(i).getDestinationNodeID() == Node2Remove.id){
                edges.remove(i);
            }
        }
    }

//    @Override
//    public boolean equals(MapNode outsider){
//        boolean flag = false;
//        if (this.id == outsider.id)
//            flag = true;
//        return flag;
//    }



    @Override
    public int compareTo(MapNode o) {
        if(this.estimatedCost == o.estimatedCost)
            return 0;
        return this.estimatedCost < o.estimatedCost ? -1 : 1;
    }
}
