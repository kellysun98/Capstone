package com.example.demo.Services;

import java.text.DecimalFormat;

public class MapEdge {
    public MapRoute mapRoute;
    public MapNode destinationNode;
    public MapNode sourceNode;
    public Integer accidentsCount;
    public Double weight;
    public static Graph graph;
    public static DecimalFormat df = new DecimalFormat("#.###");

    // Get source node id of MapEdge
    public double getSourceNodeID(){return this.sourceNode.id;}
    // Get destination node id of MapEdge
    public double getDestinationNodeID(){return this.destinationNode.id;}



    public MapEdge(MapRoute mapRoute, MapNode sourceNode, MapNode destinationNode) {
        this.mapRoute = mapRoute;
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
        this.weight = graph.getDistance(this.sourceNode, this.destinationNode); // Set weight of the edge as distance from src to dest MapNode
//        try {
        double lon = Double.parseDouble(df.format(destinationNode.longitude));
        double lat = Double.parseDouble(df.format(destinationNode.latitude));
//            accidentsCount = graph.accidents.get(lon).get(lat);
//        } catch (NullPointerException e){
//            accidentsCount = null;
//        }
    }
    public double getCost(String costFunction){
        double euclideanDistance = graph.getDistance(sourceNode,destinationNode);
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
