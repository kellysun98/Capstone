import javax.swing.*;
import javax.swing.event.MouseInputListener;
import javax.swing.text.StyledEditorKit;
import java.awt.*;
import java.util.*;
import java.util.List;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


public class Planner {
    public JXMapViewer mapViewer;
    public JFrame frame;
    public Graph graph;

    public Planner(Graph graph){
        this.graph = graph;
    }

    public List<MapNode> plan(MapNode startNode, MapNode goalNode, String costFunction){

        HashMap<MapNode, MapNode> parents = new HashMap<>();
        HashMap<MapNode, Double> costs = new HashMap<>();
        PriorityQueue<MapNode> priorityQueue = new PriorityQueue<MapNode>();

        startNode.estimatedCost = heuristics(startNode,goalNode);
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
                double newCost = costs.get(node) + edge.getCost(costFunction);
                if (!parents.containsKey(nextNode) || newCost < costs.get(nextNode)) {
                    parents.put(nextNode,node);
                    costs.put(nextNode,newCost);
                    nextNode.estimatedCost = heuristics(nextNode,goalNode) + newCost;
                    priorityQueue.add(nextNode);
                }
            }
        }
        return null;
    }

    public List<MapNode> planBestFirst(MapNode startNode, MapNode goalNode){

        HashMap<MapNode, MapNode> parents = new HashMap<>();
        HashMap<MapNode, Double> costs = new HashMap<>();
        PriorityQueue<MapNode> priorityQueue = new PriorityQueue<MapNode>(); // Create a priority queue for best first search

        startNode.estimatedCost = heuristics(startNode,goalNode);
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
                    nextNode.estimatedCost = heuristics(nextNode,goalNode) + newCost;
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

    public double heuristics(MapNode node, MapNode goalNode){
        return graph.getDistance(node,goalNode);
    }

    public List<MapNode> getGeoList(HashMap<MapNode, MapNode> parents, MapNode goalNode){
        List<MapNode> geoList = new ArrayList<>();
        geoList.add(new MapNode(goalNode.latitude,goalNode.longitude));
        MapNode thisNode = goalNode;
        while(thisNode != null){
            geoList.add(new MapNode(thisNode.latitude,thisNode.longitude));
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
    public List<List<MapNode>> runSearches(MapNode startNode, MapNode endNode){
        List<List<MapNode>> solutions = new ArrayList<>();
        solutions.add(plan(startNode, endNode,"distance"));
//        solutions.add(plan(startNode, endNode,"bikeLane"));
//        solutions.add(plan(startNode, endNode,"accidents"));
//        solutions.add(planBreadthFirst(startNode, endNode));
//        solutions.add(planBestFirst(startNode, endNode));
//        solutions.add(plan(startNode, endNode,"allFeatures"));
        return solutions;
    }

/** 
    public static void main(String[] args){
        String osmFile = "./data/toronto.osm";
        Graph torontoGraph = new Graph(osmFile);
        Planner planner = new Planner(torontoGraph);
        planner.initializeMapViewer();
    }*/
}
