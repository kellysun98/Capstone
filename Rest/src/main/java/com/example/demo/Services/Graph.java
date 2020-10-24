package com.example.demo.Services;

import com.example.demo.PSQLConnect;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.lang.reflect.Array;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.*;


public class Graph {
    public Document osmDoc;
    public double[] focus;
    public HashMap<String, MapNode> nodes;
    public HashMap<Double, MapNode> routeNodes;
    public HashMap<Double, MapRoute> routes;
    public HashMap<Double,HashMap<Double,Integer>> accidents; //longitude, latitude
//    public static List<MapPolygon> polygons;

    public static double MPERLAT = 111320;
    public static double MPERLON;

    // members needed for normalization
    public ArrayList<Double> edgeLength_list = new ArrayList<Double>(); // list of length of all edges
    public ArrayList<Double> euclid_list = new ArrayList<Double>(); // list of euclid distance for each node to end node
    public ArrayList<Double> pedCount_list = new ArrayList<Double>(); // list of ped count for each node
    public double max_length = -1;
    public double min_length = -1;
    public double max_euclid = -1;
    public double min_euclid = -1;
    public double max_pedCont = -1;
    public double min_pedCount = -1;


    // prepare for normalization
    public void prepareNormalization( MapNode endNode){
        for (MapNode n : this.routeNodes.values()) {
            pedCount_list.add(n.getPedCount());
            euclid_list.add(getDistance(n, endNode));
            for (MapEdge e : n.getEdges()){
                edgeLength_list.add(e.getLength("distance"));
            }
        }
        max_length = Collections.max(edgeLength_list);
        min_length = Collections.min(edgeLength_list);
        max_euclid = Collections.max(euclid_list);
        min_euclid = Collections.min(euclid_list);
        max_pedCont = Collections.max(pedCount_list);
        min_pedCount = Collections.min(pedCount_list);

    }

    // normalize datapoint "me" based on min and max of dataset
    public static double normalize(double me, double min, double max){
        return (me-min)/(max-min);
    }


    //build graph from osm
    public Graph(String osmFilePath, String accidentsFilePath) {

        accidents = new HashMap<>();
        nodes = new HashMap<>();
        routeNodes = new HashMap<>();
        routes = new HashMap<>();
//        polygons = new ArrayList<>();

        loadFiles(osmFilePath, accidentsFilePath);
        getFocus();
        MPERLON = Math.cos(focus[1] * 3.1415 / 180) * MPERLAT;
        MapEdge.graph = this;
        buildGraph();
    }
    //build graph from db
    public Graph() {

        accidents = new HashMap<>();
        nodes = new HashMap<>();
        routeNodes = new HashMap<>();
        routes = new HashMap<>();
//        polygons = new ArrayList<>();

        getFocus();
        MPERLON = Math.cos(focus[1] * 3.1415 / 180) * MPERLAT;
        MapEdge.graph = this;
        buildGraph_db();
    }

    public void loadFiles(String osmFilePath, String accidentsFilePath){
        // load osm file
        try {
            File file = new File(osmFilePath);
            //an instance of factory that gives a document builder
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            //an instance of builder to parse the specified xml file
            DocumentBuilder db = dbf.newDocumentBuilder();
            osmDoc = db.parse(file);
            osmDoc.getDocumentElement().normalize();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // load toronto police csv file
        BufferedReader br = null;
        String line = "";
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.FLOOR);
        try {
            br = new BufferedReader(new FileReader(accidentsFilePath));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] entry = line.split(",");
                double lat = Double.parseDouble(df.format(Double.parseDouble(entry[15])));
                double lon = Double.parseDouble(df.format(Double.parseDouble(entry[16])));
                if (accidents.get(lon) == null){
                    accidents.put(lon, new HashMap<>());
                } else {
                    HashMap<Double,Integer> latMap = accidents.get(lon);
                    if (latMap.get(lat) == null){
                        latMap.put(lat,1);
                    } else {
                        latMap.put(lat,latMap.get(lat)+1);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

//        // load uber json file
//        JSONParser parser = new JSONParser();
//
//        try {
//            Object obj = parser.parse(new FileReader("./data/torontoBoundaries.json"));
//
//            JSONObject boundaryFile =  (JSONObject) obj;
//            JSONArray rawPolygons = (JSONArray) boundaryFile.get("features");
//
//            for (Object o : rawPolygons) {
//                JSONObject polygon = (JSONObject) o;
//                polygons.add(new MapPolygon(polygon));
//            }
//
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
    }
    public void buildGraph_db() {
        //nodes = PSQLConnect.getNodeList();
        ArrayList<ArrayList<String>> routeList = PSQLConnect.getRouteList();
/*        for (int i = 0; i < nodeList.getLength(); i++) {
            Element node = (Element) nodeList.item(i);
            MapNode newNode = new MapNode(node);
            nodes.put(newNode.id, newNode);
        }*/

        for (int i = 0; i < routeList.size(); i++) {
            //NodeList nodesInRoute = route.getElementsByTagName("nd");

            String thisNode = routeList.get(i).get(0); //nodeid
            String nextNode;
            MapNode thisnode = new MapNode();
            MapNode nextnode = new MapNode();
            for (int j = 1; j < routeList.get(i).size(); j++) {
                nextNode = routeList.get(i).get(j);
                thisnode = PSQLConnect.getNodebyID(thisNode);
                nextnode = PSQLConnect.getNodebyID(nextNode);
                if(nodes.containsKey(thisNode)){
                    nodes.get(thisNode).edges.add(new MapEdge(null, thisnode, nextnode));
                }
                else{
                    thisnode.edges.add(new MapEdge(null, thisnode, nextnode));
                    nodes.put(thisNode, thisnode);
                }
                //nodes.get(thisNode).edges.add(new MapEdge(null, nodes.get(thisNode), nodes.get(nextNode)));
                thisNode = nextNode;
            }
            for (String nodeId : routeList.get(i)) {
                routeNodes.put(Double.parseDouble(nodeId), nodes.get(nodeId));
            }
        }
        System.out.println(String.format("number of highway nodes: %d", routeNodes.size()));
    }
    public void buildGraph() {
        NodeList nodeList = osmDoc.getElementsByTagName("node");
        NodeList routeList = osmDoc.getElementsByTagName("way");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element node = (Element) nodeList.item(i);
            MapNode newNode = new MapNode(node);
            nodes.put(Double.toString(newNode.id), newNode);
        }
        for (int i = 0; i < routeList.getLength(); i++) {
            Element route = (Element) routeList.item(i);
            boolean useMe = false;
            boolean oneWay = false;
            boolean bikeLane = false;
            String routeName = "unnamed route";
            String routeType = "";
            int maxSpeed = -1;
            int lanes = -1;
            List<Double> nodeIdList = new ArrayList<>();

            // this for loop is not inside the MapRoute init function because not every way is a route
            NodeList tagsForRoute = route.getElementsByTagName("tag");
            for (int j = 0; j < tagsForRoute.getLength(); j++) {
                Element tag = (Element) tagsForRoute.item(j);
                if (tag.getAttribute("k").equals("highway")) {
                    useMe = true;
                    routeType = tag.getAttribute("v");
                } else if (tag.getAttribute("k").equals("name")) {
                    routeName = tag.getAttribute("v");
                } else if (tag.getAttribute("k").equals("oneway") && tag.getAttribute("v").equals("yes")) {
                    oneWay = true;
                } else if (tag.getAttribute("k").equals("cycleway") || (tag.getAttribute("k").equals("bicycle") && (tag.getAttribute("v").equals("yes") || tag.getAttribute("v").equals("designated")))) {
                    bikeLane = true;
                } else if (tag.getAttribute("k").equals("maxspeed")){
                    maxSpeed = Integer.parseInt(tag.getAttribute("v"));
                } else if (tag.getAttribute("k").equals("lanes")){
                    lanes = Character.getNumericValue(tag.getAttribute("v").charAt(0));
                }
            }
            if (useMe) {
                MapRoute newRoute = new MapRoute(route, routeName, routeType, bikeLane, maxSpeed, lanes);
                NodeList nodesInRoute = route.getElementsByTagName("nd");
                for (int j = 0; j < nodesInRoute.getLength(); j++) {
                    Element nd = (Element) nodesInRoute.item(j);
                    nodeIdList.add(Double.parseDouble(nd.getAttribute("ref")));
                }
                double thisNode = nodeIdList.get(0);
                double nextNode;
                for (int j = 1; j < nodeIdList.size(); j++) {
                    nextNode = nodeIdList.get(j);
                    nodes.get(thisNode).edges.add(new MapEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
                    thisNode = nextNode;
                }
                if (!oneWay) {
                    thisNode = nodeIdList.get(nodeIdList.size() - 1);
                    for (int j = nodeIdList.size() - 2; j > -1; j--) {
                        nextNode = nodeIdList.get(j);
                        nodes.get(thisNode).edges.add(new MapEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
                        thisNode = nextNode;
                    }
                }
                newRoute.nodeIds = nodeIdList;
                routes.put(newRoute.routeId, newRoute);
                for (double nodeId : nodeIdList) {
                    routeNodes.put(nodeId, nodes.get(nodeId));
                }
            }
        }
        System.out.println(String.format("number of highway nodes: %d", routeNodes.size()));
    }

    private void getFocus() {
        double minLat = 43.48;
        double maxLat = 43.92;
        double minLon = -79.7899999;
        double maxLon = -78.9999999;
        focus = new double[]{(minLon + maxLon) / 2, (minLat + maxLat) / 2};
    }

    public static double getDistance(MapNode sourceNode, MapNode destinationNode) {
        double dx = (destinationNode.longitude - sourceNode.longitude) * MPERLON;
        double dy = (destinationNode.latitude - sourceNode.latitude) * MPERLAT;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

}