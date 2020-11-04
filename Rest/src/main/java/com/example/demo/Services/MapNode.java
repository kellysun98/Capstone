package com.example.demo.Services;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.example.demo.Services.Graph.getDistance;
import static com.example.demo.Services.Graph.normalize;

public class MapNode implements Comparable<MapNode>{
    public Graph graph;
    public Element element;
    public double id;
    public double longitude;
    public double latitude;
    public List<MapEdge> edges;
    public double estimatedCost;// f(n);
    public double pedCount; // number of people at the node; h(n)
    public double normalized_pedCount; // normalized pedCount
    public double normalized_euclid; // normalized euclidean distance from this node to destination node
    public boolean isIndoor;

    public double getPedCount(){
        return this.pedCount;
    }

    public List<MapEdge> getEdges(){
        return edges;
    }

    public boolean equals(MapNode outsider){
        return outsider.id == this.id;
    }

    public MapNode(MapNode newMapNode){
        id = newMapNode.id;
        longitude = newMapNode.longitude;
        latitude = newMapNode.latitude;
        edges = new ArrayList<>(newMapNode.edges);
        pedCount = newMapNode.pedCount;
        isIndoor = false;
    }
    public MapNode (){
        id = -1;
        longitude = -1;
        latitude = -1;
        edges = new ArrayList<>();
        //pedCount = getRandomNumber(0,20);
        pedCount = -1;
        isIndoor = false;
    }

    public void setisIndoor(boolean state){this.isIndoor=state;}
    /**/
    public int getRandomNumber(int min, int max){
        return (int) ((Math.random()*(max-min))+min);
    }

    /** Used for osm initialization
     * */
    public MapNode (Element e){
        id = Double.parseDouble(e.getAttribute("id"));
        longitude = Double.parseDouble(e.getAttribute("lon"));
        latitude = Double.parseDouble(e.getAttribute("lat"));
        edges = new ArrayList<>();
        pedCount = getRandomNumber(0,20);
        isIndoor = false;
//        euclid = getDistance()
//        normalized_pedCount = normalize(this.pedCount, graph.min_pedCount, graph.max_pedCont);
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
