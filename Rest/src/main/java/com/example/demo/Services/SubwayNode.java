package com.example.demo.Services;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;

public class SubwayNode extends MapNode{ //hi
    public SubwayGraph graph;
    public Element element;
    public double id;
    public double longitude;
    public double latitude;
    public List<SubwayEdge> edges;
    public ArrayList arrivalTime;
    public int lineNumber;

    public SubwayNode(SubwayNode newMapNode){
        id = newMapNode.id;
        longitude = newMapNode.longitude;
        latitude = newMapNode.latitude;
        edges = new ArrayList<>(newMapNode.edges);
    }
    public SubwayNode (Element e) {
        id = Double.parseDouble(e.getAttribute("id"));
        longitude = Double.parseDouble(e.getAttribute("lon"));
        latitude = Double.parseDouble(e.getAttribute("lat"));
        edges = new ArrayList<>();
    }
    public SubwayNode(){

    }

    /** take 2 user inputs: 1) start location; 2) start time
     * */
    public void takeInput(){

    }


}
