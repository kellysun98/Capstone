package com.example.demo.Services;

import java.text.DecimalFormat;

public class SubwayEdge extends MapEdge{
    public MapRoute mapRoute;
    public SubwayNode destinationNode;
    public SubwayNode sourceNode;
    public Double length; // length of the edge
    public static SubwayGraph graph;
    public static DecimalFormat df = new DecimalFormat("#.###");


    public SubwayEdge(MapRoute mapRoute, SubwayNode sourceNode, SubwayNode destinationNode) {
        this.mapRoute = mapRoute;
        this.sourceNode = sourceNode;
        this.destinationNode = destinationNode;
        this.length = graph.getDistance(this.sourceNode, this.destinationNode); // Set weight of the edge as distance from src to dest MapNode
//        this.normalized_length = normalize(this.length, graph.min_length, graph.max_length);
//        try {
        double lon = Double.parseDouble(df.format(destinationNode.longitude));
        double lat = Double.parseDouble(df.format(destinationNode.latitude));

//            accidentsCount = graph.accidents.get(lon).get(lat);
//        } catch (NullPointerException e){
//            accidentsCount = null;
//        }
    }

    public SubwayEdge(MapEdge newMapEdge) {
        super(newMapEdge);
    }

}
