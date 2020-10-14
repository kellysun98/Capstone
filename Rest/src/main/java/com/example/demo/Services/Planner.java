package com.example.demo.Services;

import org.jxmapviewer.JXMapViewer;

import org.jxmapviewer.OSMTileFactoryInfo;
import org.jxmapviewer.input.CenterMapListener;
import org.jxmapviewer.input.PanKeyListener;
import org.jxmapviewer.input.PanMouseInputListener;
import org.jxmapviewer.input.ZoomMouseWheelListenerCursor;
import org.jxmapviewer.viewer.DefaultTileFactory;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.TileFactoryInfo;

import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.util.*;
import java.util.List;

import static com.example.demo.Services.Graph.normalize;

public class Planner {
    public JXMapViewer mapViewer;
    public JFrame frame;
    public Graph graph;

    public Planner(){}

    public Path plan(Graph graph, MapNode startNode, MapNode goalNode, String costFunction){
        if (costFunction.equals("distance")){
            this.graph = graph;
            HashMap<MapNode, MapNode> parents = new HashMap<>();
            HashMap<MapNode, Double> costs = new HashMap<>();
            PriorityQueue<MapNode> priorityQueue = new PriorityQueue<MapNode>();

            startNode.estimatedCost = heuristics(startNode,goalNode,costFunction);
            parents.put(startNode,null);
            costs.put(startNode,0.0);
            priorityQueue.add(startNode);

            while (!priorityQueue.isEmpty()){
                MapNode node = priorityQueue.remove();
                if(node.id == goalNode.id){
                    //                double total_cost = 0;
                    //                for(double c:costs.values()){total_cost+=c;};
                    //                Path fastestRoute = new Path(getGeoList(parents,goalNode),total_cost);
                    Path fastestRoute = new Path(getGeoList(parents,goalNode));
                    return fastestRoute;
                }
                for (MapEdge edge:node.edges){
                    MapNode nextNode = edge.destinationNode;
                    double newCost = costs.get(node) + edge.getLength("distance"); //newCost = g(n)
                    if (!parents.containsKey(nextNode) || newCost < costs.get(nextNode)) {
                        parents.put(nextNode,node);
                        costs.put(nextNode,newCost);
                        nextNode.estimatedCost = heuristics(nextNode,goalNode,costFunction) + newCost; // estimatedCost=f(n)=h(n)+g(n)
                        priorityQueue.add(nextNode);
                    }
                }
            }
            return null;
        }else if (costFunction.equals("covid")){
            this.graph = graph;
            HashMap<MapNode, MapNode> parents = new HashMap<>();
            HashMap<MapNode, Double> costs = new HashMap<>();
            PriorityQueue<MapNode> priorityQueue = new PriorityQueue<MapNode>();

            startNode.estimatedCost = heuristics(startNode,goalNode,costFunction);
            parents.put(startNode,null);
            costs.put(startNode,0.0);
            priorityQueue.add(startNode);

            while (!priorityQueue.isEmpty()){
                MapNode node = priorityQueue.remove();
                if(node.id == goalNode.id){
//                double total_cost = 0;
//                for(double c:costs.values()){total_cost+=c;};
//                Path fastestRoute = new Path(getGeoList(parents,goalNode),total_cost);
                    Path fastestRoute = new Path(getGeoList(parents,goalNode));
                    return fastestRoute;
                }
                for (MapEdge edge:node.edges){
                    edge.normalized_length = normalize(edge.length, graph.min_length, graph.max_length);

                    MapNode nextNode = edge.destinationNode;
                    double newCost = costs.get(node) + edge.getNormalized_length(); //newCost = g(n)
                    System.out.println("newCost: "+newCost);
                    if (!parents.containsKey(nextNode) || newCost < costs.get(nextNode)) {
                        parents.put(nextNode,node);
                        costs.put(nextNode,newCost);
                        nextNode.estimatedCost = heuristics(nextNode,goalNode,costFunction) + newCost; // estimatedCost=f(n)=h(n)+g(n)
                        priorityQueue.add(nextNode);
                    }
                }
            }return null;
        }
        return null;
    }

    public List<MapNode> planBestFirst(MapNode startNode, MapNode goalNode,String costFunction){

        HashMap<MapNode, MapNode> parents = new HashMap<>();
        HashMap<MapNode, Double> costs = new HashMap<>();
        PriorityQueue<MapNode> priorityQueue = new PriorityQueue<MapNode>(); // Create a priority queue for best first search

        startNode.estimatedCost = heuristics(startNode,goalNode,costFunction);
        parents.put(startNode,null);
        costs.put(startNode,0.0);
        priorityQueue.add(startNode);

        while (!priorityQueue.isEmpty()){
            MapNode node = priorityQueue.remove();
            if(node.id == goalNode.id){
                return getGeoList(parents,goalNode);
            }
            for (MapEdge edge:node.edges){
                MapNode nextNode = edge.destinationNode;
                double newCost = costs.get(node);
                if (!parents.containsKey(nextNode) || newCost < costs.get(nextNode)) {
                    parents.put(nextNode,node);
                    costs.put(nextNode,newCost);
                    nextNode.estimatedCost = heuristics(nextNode,goalNode,costFunction) + newCost;
                    priorityQueue.add(nextNode);
                }
            }
        }
        return null;
    }

    List<MapNode> planBreadthFirst(MapNode startNode, MapNode goalNode) {

        HashMap<MapNode, MapNode> parents = new HashMap<>();
        LinkedList<MapNode> queue = new LinkedList<>(); // Create a queue for BFS

        parents.put(startNode,null);
        queue.add(startNode);

        while (!queue.isEmpty()) {
            // Dequeue a vertex from
            MapNode node = queue.poll();
            if(node.id == goalNode.id){
                return getGeoList(parents,goalNode);
            }
            for (MapEdge edge:node.edges){
                MapNode nextNode = edge.destinationNode;

                if (!parents.containsKey(nextNode)) {
                    parents.put(nextNode,node);
                    queue.add(nextNode);
                }
            }
        }
        return null;
    }

    /* Heuristic function that calculates cost of next node.
     *  "distance" sets cost of next node = length from curr node to next node
     *  "covidrisk" sets cost of next node = number of pedestrian at next node */
    public double heuristics(MapNode node, MapNode goalNode, String objective){
        double res = 0;
        if (objective.equals("distance"))
            res = graph.getDistance(node,goalNode);
        else if (objective.equals("covid"))
            res = 0.5*goalNode.normalized_pedCount + 0.5*goalNode.normalized_euclid;
        return res;
    }

    public ArrayList<MapNode> getGeoList(HashMap<MapNode, MapNode> parents, MapNode goalNode){
        ArrayList<MapNode> geoList = new ArrayList<>();
        geoList.add(goalNode);
        MapNode thisNode = goalNode;
        while(thisNode != null){
            geoList.add(thisNode);
            thisNode = parents.get(thisNode);
        }
        Collections.reverse(geoList);

        return geoList;
    }
    /**
     public void initializeMapViewer(){
     mapViewer = new JXMapViewer();
     // Create a TileFactoryInfo for OpenStreetMap
     TileFactoryInfo info = new OSMTileFactoryInfo();
     DefaultTileFactory tileFactory = new DefaultTileFactory(info);
     mapViewer.setTileFactory(tileFactory);
     // Use 8 threads in parallel to load the tiles
     tileFactory.setThreadPoolSize(8);
     // Set the focus
     MapNode toronto = new MapNode(graph.focus[1], graph.focus[0]);
     mapViewer.setZoom(5);
     mapViewer.setAddressLocation(toronto);
     // Add mouse listeners for panning and zooming
     MouseInputListener mia = new PanMouseInputListener(mapViewer);
     mapViewer.addMouseListener(mia);
     mapViewer.addMouseMotionListener(mia);
     mapViewer.addMouseListener(new CenterMapListener(mapViewer));
     mapViewer.addMouseWheelListener(new ZoomMouseWheelListenerCursor(mapViewer));
     mapViewer.addKeyListener(new PanKeyListener(mapViewer));
     // Add mouse listener for placing waypoints
     frame = new JFrame("OpenStreetAStar");
     WayPointAdapter wayPointAdapter = new WayPointAdapter(mapViewer, graph.routeNodes, this, frame);
     // Display the viewer in a JFrame
     frame.setLayout(new BorderLayout());
     String text = "Use left mouse button to pan, mouse wheel to zoom and right mouse button to set waypoints";
     frame.add(new JLabel(text), BorderLayout.NORTH);
     frame.getContentPane().add(mapViewer);
     frame.setSize(800, 800);
     frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
     frame.setVisible(true);
     }
     */

    public List<List<Double>> getCoordinates(List<MapNode> plan_result){
        List<List<Double>> coordinates = new ArrayList<>();
        for(MapNode mapnode:plan_result){
            List<Double> one_coor = new ArrayList<>();
            one_coor.add(mapnode.longitude);
            one_coor.add(mapnode.latitude);
            coordinates.add(one_coor);
        }
        return coordinates;
    }

    public List<List<List<Double>>> runSearches(Graph graph, MapNode startNode, MapNode endNode){
        List<List<List<Double>>> solutions = new ArrayList<>();
//        solutions.add(getCoordinates(plan(graph, startNode, endNode, "distance").getNodes()));
        //solutions.add(plan(startNode, endNode,"distance"));

//        solutions.add(plan(startNode, endNode,"bikeLane"));
//        solutions.add(plan(startNode, endNode,"accidents"));
//        solutions.add(planBreadthFirst(startNode, endNode));
//        solutions.add(planBestFirst(startNode, endNode));
//        solutions.add(plan(startNode, endNode,"allFeatures"));
        return solutions;
    }

//    public HashMap<Integer, String> toHashMap(List<List<List<Double>>> solutions){
//        Integer count = 1;
//        HashMap<Integer, String> string_result = new HashMap<>();
//        for(List<List<Double>> route: solutions){
//            String route_to_string = new String();
//            for(List<Double> coord: route){
//                route_to_string += ("["+coord.get(0).toString()+", "+coord.get(1).toString()+"]" + ",");
//            }
//            string_result.put(count,route_to_string.substring(0,route_to_string.length()-1));
//            count += 1;
//        }
//        return string_result;
//    }
//    public static HashMap<Integer, Path> toHashMap(List<Path> solutions){
//        Integer count = 1;
//        HashMap<Integer, Path> Path_result = new HashMap<>();
//        for(Path route: solutions){
//            Path_result.put(count,route);
//            count += 1;
//        }
//        return Path_result;
//    }

//    public static HashMap<Integer, Path> toHashMap(Path solution){
//        Integer count = 1;
//        HashMap<Integer, Path> Path_result = new HashMap<>();
//        Path_result.put(count,solution);
//
//        return Path_result;
//    }


/**
 public static void main(String[] args){
 String osmFile = "./data/toronto.osm";
 Graph torontoGraph = new Graph(osmFile);
 Planner planner = new Planner(torontoGraph);
 planner.initializeMapViewer();
 }*/
}
