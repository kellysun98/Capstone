package com.example.demo.Services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class MapEdge implements Serializable {
    public MapRoute mapRoute;
    public MapNode destinationNode;
    public MapNode sourceNode;
    public double pedestrianCount;
    public double weight;

    // Get source node
    public MapNode getSourceNode(){return sourceNode;}

    // Get destination node
    public MapNode getDestinationNode(){return destinationNode;}

    //public static Graph graph;
    public static DecimalFormat df = new DecimalFormat("#.###");

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        mapRoute = (MapRoute) aInputStream.readObject();
        destinationNode = (MapNode) aInputStream.readObject();
        sourceNode = (MapNode) aInputStream.readObject();
        df = new DecimalFormat("#.###");
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        aOutputStream.writeObject(mapRoute);
        aOutputStream.writeObject(destinationNode);
        aOutputStream.writeObject(sourceNode);
        aOutputStream.writeObject(df);

    }

    public MapEdge(MapRoute mapRoute, MapNode sourceNode, MapNode destinationNode) {
        this.mapRoute = mapRoute;
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
//        try {
        double lon = Double.parseDouble(df.format(destinationNode.longitude));
        double lat = Double.parseDouble(df.format(destinationNode.latitude));

//        } catch (NullPointerException e){
//            accidentsCount = null;
//        }
    }

    /* Return the euclidean distance from source to destination node*/
    public double getEdgeDistance(){
        double euclideanDistance = Graph.getDistance(sourceNode,destinationNode);
        return euclideanDistance;
    }
}
