package com.example.demo.Services;

import java.text.DecimalFormat;

import static com.example.demo.Services.Graph.normalize;

public class MapEdge {
    public MapRoute mapRoute;
    public MapNode destinationNode;
    public MapNode sourceNode;
    public Integer accidentsCount;
    public Double length; // length of the edge
    public static Graph graph;
    public static DecimalFormat df = new DecimalFormat("#.###");
    public double normalized_length; //normalized length of each edge
    public boolean isIndoor; // whether node is indoor
    public boolean isHospital; // whether node is hospital
    public boolean isShoppers; // whether node is shoppers

    // Get source node id of MapEdge
    public double getSourceNodeID(){return this.sourceNode.id;}
    // Get destination node id of MapEdge
    public double getDestinationNodeID(){return this.destinationNode.id;}

    public MapEdge(MapEdge newMapEdge) {
        this.mapRoute = new MapRoute(newMapEdge.mapRoute);
        this.sourceNode = new MapNode(newMapEdge.sourceNode);
        this.destinationNode = new MapNode(newMapEdge.sourceNode);
        this.length = newMapEdge.length;
        double lon = Double.parseDouble(df.format(destinationNode.longitude));
        double lat = Double.parseDouble(df.format(destinationNode.latitude));
        this.isIndoor = (sourceNode.isIndoor) && (destinationNode.isIndoor);
        this.isHospital = (sourceNode.isHospital) && (destinationNode.isHospital);
        this.isShoppers = (sourceNode.isShoppers) && (destinationNode.isShoppers);
    }

    public MapEdge(MapRoute mapRoute, MapNode sourceNode, MapNode destinationNode) {
        this.mapRoute = mapRoute;
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
        this.length = graph.getDistance(this.sourceNode, this.destinationNode); // Set weight of the edge as distance from src to dest MapNode
//        this.normalized_length = normalize(this.length, graph.min_length, graph.max_length);
//        try {
        double lon = Double.parseDouble(df.format(destinationNode.longitude));
        double lat = Double.parseDouble(df.format(destinationNode.latitude));
        this.isIndoor = (sourceNode.isIndoor) && (destinationNode.isIndoor);
        this.isHospital = (sourceNode.isHospital) && (destinationNode.isHospital);
        this.isShoppers = (sourceNode.isShoppers) && (destinationNode.isShoppers);
//            accidentsCount = graph.accidents.get(lon).get(lat);
//        } catch (NullPointerException e){
//            accidentsCount = null;
//        }
    }
    public double getNormalized_length(){
        return this.normalized_length;
    }
    public double getLength(String costFunction){
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
//            if (mapRoute.maxSpeed > 80){
//                cost = cost + euclideanDistance * 0.5;
//            }
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
//            if (mapRoute.maxSpeed > 80){
//                cost = cost + euclideanDistance * 0.5;
//            }
            if (accidentsCount != null) {
                cost = cost + euclideanDistance * 0.1 * accidentsCount;
            }
            return cost;
        }
        return euclideanDistance;
    }
}
