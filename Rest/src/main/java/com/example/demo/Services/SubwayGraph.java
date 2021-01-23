package com.example.demo.Services;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SubwayGraph extends Graph { //hi

    public HashMap<Double, SubwayNode> nodes;
    public HashMap<Double, SubwayNode> routeNodes;

    // Test
    public ArrayList<ArrayList<Double>> visual_routes = new ArrayList<>();

    public SubwayGraph() {
        this("./data/DT2.osm");
    }

    public SubwayGraph(String osmFilePath) {
        nodes = new HashMap<>();
        routeNodes = new HashMap<>();
        loadFiles(osmFilePath);
        getFocus();
        MPERLON = Math.cos(focus[1] * 3.1415 / 180) * MPERLAT;
        buildSubwayGraph();
    }

    public void loadFiles(String osmFilePath) {
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
    }

    /**
     * 最新的buildGraph function; allow avoid hospital
     */
    public void buildSubwayGraph() {

        NodeList nodeList = osmDoc.getElementsByTagName("node");
        NodeList routeList = osmDoc.getElementsByTagName("way");

        for (int i = 0; i < nodeList.getLength(); i++) {
            Element node = (Element) nodeList.item(i);
            SubwayNode newNode = new SubwayNode(node);
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
                if (tag.getAttribute("k").equals("railway")) {
                    useMe = true;
                    routeType = tag.getAttribute("v");
                }
            }
            if (useMe) {
                MapRoute newRoute = new MapRoute(route, routeName, routeType, bikeLane, lanes);
                NodeList nodesInRoute = route.getElementsByTagName("nd");
                for (int j = 0; j < nodesInRoute.getLength(); j++) {
                    Element nd = (Element) nodesInRoute.item(j);
                    nodeIdList.add(Double.parseDouble(nd.getAttribute("ref")));
                }
                /***/
                for (int k = 0; k < nodeIdList.size(); k++){
                    ArrayList lonlat = new ArrayList();
                    SubwayNode sn = nodes.get(nodeIdList.get(k));

                    lonlat.add(sn.longitude);
                    lonlat.add(sn.latitude);
                    visual_routes.add(lonlat);
                }
                /***/
                double thisNode = nodeIdList.get(0);
                double nextNode;
                for (int j = 1; j < nodeIdList.size(); j++) {
                    nextNode = nodeIdList.get(j);
                    nodes.get(thisNode).edges.add(new SubwayEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
                    thisNode = nextNode;
                }
                if (!oneWay) {
                    thisNode = nodeIdList.get(nodeIdList.size() - 1);
                    for (int j = nodeIdList.size() - 2; j > -1; j--) {
                        nextNode = nodeIdList.get(j);
                        nodes.get(thisNode).edges.add(new SubwayEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
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
        System.out.println(String.format("number of subway nodes: %d", routeNodes.size()));
    }

}