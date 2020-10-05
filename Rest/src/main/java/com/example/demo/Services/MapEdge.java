package com.example.demo.Services;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.List;

public class MapEdge implements Serializable {
    public static MapRoute mapRoute;
    public static MapNode destinationNode;
    public static MapNode sourceNode;
    public static Integer accidentsCount;
    //public static Graph graph;
    public static DecimalFormat df = new DecimalFormat("#.###");

    private static void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        mapRoute = (MapRoute) aInputStream.readObject();
        destinationNode = (MapNode) aInputStream.readObject();
        sourceNode = (MapNode) aInputStream.readObject();
        df = new DecimalFormat("#.###");
    }

    private static void writeObject(ObjectOutputStream aOutputStream) throws IOException
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
//            accidentsCount = graph.accidents.get(lon).get(lat);
//        } catch (NullPointerException e){
//            accidentsCount = null;
//        }
    }
    public static double getCost(String costFunction){
        double euclideanDistance = Graph.getDistance(sourceNode,destinationNode);
        if (costFunction.equals("distance")){
            return euclideanDistance;
        } else if (costFunction.equals("bikeLane")){
            double cost = euclideanDistance;
            if (mapRoute.steps){
                cost = cost * 5;
            } else if (!mapRoute.bikeLane){
                cost = cost + euclideanDistance;
            }
            return cost;
        } else if (costFunction.equals("accidents")){
            double cost = euclideanDistance;
            if (mapRoute.steps){
                cost = cost * 5;
            }
            if (!mapRoute.bikeLane){
                cost = cost + euclideanDistance;
            }
            if (mapRoute.lanes < 3){
                cost = cost + euclideanDistance;
            }
            if (mapRoute.maxSpeed > 80){
                cost = cost + euclideanDistance * 0.5;
            }
            if (accidentsCount != null) {
                cost = cost + euclideanDistance * 100;
            }
            return cost;
        } else if (costFunction.equals("allFeatures")){
            double cost = euclideanDistance;
            if (mapRoute.steps){
                cost = cost * 5;
            }
            if (!mapRoute.bikeLane){
                cost = cost + euclideanDistance;
            }
            if (mapRoute.lanes < 3){
                cost = cost + euclideanDistance;
            }
            if (mapRoute.maxSpeed > 80){
                cost = cost + euclideanDistance * 0.5;
            }
            if (accidentsCount != null) {
                cost = cost + euclideanDistance * 0.1 * accidentsCount;
            }
            return cost;
        }
        return euclideanDistance;
    }
}
