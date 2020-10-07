package com.example.demo.Services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class Graph implements Serializable{
    public Document osmDoc;
    public double[] focus;
    public HashMap<Double, MapNode> nodes;
    public HashMap<Double, MapNode> routeNodes;
    public HashMap<Double, MapRoute> routes;
    public HashMap<Double,HashMap<Double,Integer>> accidents; //longitude, latitude
//    public static List<MapPolygon> polygons;

    public static double MPERLAT = 111320;
    public static double MPERLON;

    public Graph() {
        this("./data/toronto.osm","./data/Cyclists.csv");
    }

    public Graph(String osmFilePath, String accidentsFilePath) {

        accidents = new HashMap<>();
        nodes = new HashMap<>();
        routeNodes = new HashMap<>();
        routes = new HashMap<>();
//        polygons = new ArrayList<>();

        loadFiles(osmFilePath, accidentsFilePath);
        getFocus();
        MPERLON = Math.cos(focus[1] * 3.1415 / 180) * MPERLAT;
        //MapEdge.graph = this;
        buildGraph();
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

    public void buildGraph() {
        NodeList nodeList = osmDoc.getElementsByTagName("node");
        NodeList routeList = osmDoc.getElementsByTagName("way");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Element node = (Element) nodeList.item(i);
            MapNode newNode = new MapNode(node);
            nodes.put(newNode.id, newNode);
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
        NodeList boundsList = osmDoc.getElementsByTagName("bounds");
        Element bounds = (Element) boundsList.item(0);
        double minLat = Double.parseDouble(bounds.getAttribute("minlat"));
        double maxLat = Double.parseDouble(bounds.getAttribute("maxlat"));
        double minLon = Double.parseDouble(bounds.getAttribute("minlon"));
        double maxLon = Double.parseDouble(bounds.getAttribute("maxlon"));
        focus = new double[]{(minLon + maxLon) / 2, (minLat + maxLat) / 2};
    }

    public static double getDistance(MapNode sourceNode, MapNode destinationNode) {
        double dx = (destinationNode.longitude - sourceNode.longitude) * MPERLON;
        double dy = (destinationNode.latitude - sourceNode.latitude) * MPERLAT;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public static void main(String[] args){
        String osmFile = "C://Users//Helen Wang//Documents//4th_yr//capstone//Capstone//Rest//data//toronto.osm";
        String cyclistFile = "C://Users//Helen Wang//Documents//4th_yr//capstone//Capstone//Rest//data//Cyclists.csv";
        Graph torontoGraph = new Graph(osmFile, cyclistFile);
        System.out.println(torontoGraph);
//        Planner planner = new Planner(torontoGraph);
//        planner.initializeMapViewer();
    }

}