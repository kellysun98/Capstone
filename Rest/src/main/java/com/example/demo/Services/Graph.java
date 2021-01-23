package com.example.demo.Services;

import com.example.demo.PSQLConnect;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.lang.*;


public class Graph { //hi
    public Document osmDoc;
    public double[] focus;
    public HashMap<Double, MapNode> nodes;
    public HashMap<Double, MapNode> routeNodes; // HashMap of only walking network
    public HashMap<Double, MapNode> TTCrouteNodes; // HashMap of connected walking&public transit networks

    public HashMap<Double, SubwayNode> subwaynodes;
    public HashMap<Double, SubwayNode> subwayrouteNodes;
    public HashMap<Double, Building> buildings; // HashMap of building boundaries; key= way id; value =
    public HashMap<Double, MapRoute> routes;
    public HashMap<Double,HashMap<Double,Integer>> accidents; //longitude, latitude
//    public ArrayList<AmenityAdPairs> possibleHspNodes = new ArrayList<>();
    public ArrayList<MapNode> hospitalNodes;
    public boolean avoidHospital;

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

    // members for subway
//    public SubwayGraph subwayGraph;

    public void getPedestrianCountDistribution(String startTime, String endTime, int k){
        ArrayList<ArrayList<Double>> pedCountMap = PSQLConnect.getPedestrianCount(startTime, endTime);

        for (MapNode n : this.routeNodes.values()){
            ArrayList<ArrayList<Double>> countList = new ArrayList<>();
            for(int i = 0; i < k; i++){
                ArrayList<Double> tempList = new ArrayList<>();
                tempList.add(1000000.0);
                countList.add(tempList);
            }//for each node
            for (ArrayList<Double> l : pedCountMap){ // find k closest camera
                double dx = (n.latitude - l.get(0)) * MPERLAT;
                double dy = (n.longitude - l.get(1)) * MPERLON;
                double tempdist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                for(int j = 0; j < k; j++){
                    if(tempdist <= countList.get(j).get(0)){
                        ArrayList<Double> newList = new ArrayList<>();
                        newList.add(tempdist);
                        newList.add(l.get(2));
                        countList.set(j, newList);
                        break;
                    }
                }
            }
            double sum = 0;
            for(ArrayList<Double> list : countList){
                sum += Math.abs(list.get(0));
            }
            double numerator = 0;
            for(ArrayList<Double> list : countList){
                numerator += sum * list.get(1) / Math.abs(list.get(0));
            }
            double denominator = 0;
            for(ArrayList<Double> list : countList){
                denominator += sum / Math.abs(list.get(0));
            }
            this.routeNodes.get(n.id).pedCount = numerator / denominator;
//            System.out.println("________ped count: " + this.routeNodes.get(n.id).pedCount);
            if(this.routeNodes.get(n.id).isIndoor){
                this.routeNodes.get(n.id).pedCount = this.routeNodes.get(n.id).pedCount*10 + 50000;
//                System.out.println("_____________________indoor count: " + this.routeNodes.get(n.id).pedCount);

            }
        }
        System.out.println("pedestrian counts initialized");
    }

    // prepare for normalization
    public void prepareNormalization(MapNode endNode){
        for (MapNode n : this.routeNodes.values()) {
            n.euclid = getDistance(n, endNode);
            pedCount_list.add(n.getPedCount());
            euclid_list.add(n.euclid);
            for (MapEdge e : n.getEdges()){
                edgeLength_list.add(e.getLength("distance"));
            }
        }
        max_length = Collections.max(edgeLength_list);
        min_length = Collections.min(edgeLength_list);
        max_euclid = Collections.max(euclid_list);
        System.out.println("max euclid: "+ max_euclid);
        min_euclid = Collections.min(euclid_list);
        max_pedCont = Collections.max(pedCount_list);
        min_pedCount = Collections.min(pedCount_list);

        normalize_pedCount_and_euclid(endNode);
    }

    public void normalize_pedCount_and_euclid(MapNode endNode){
        for(MapNode n: this.routeNodes.values()){
            n.normalized_pedCount = normalize(n.pedCount, min_pedCount, max_pedCont);
            n.normalized_euclid = normalize(n.euclid, min_euclid, max_euclid);
        }
    }
    // normalize datapoint "me" based on min and max of dataset
    public static double normalize(double me, double min, double max){
        return (me-min)/(max-min);
    }

    public Graph() {
        this("./data/DT3.osm","./data/Cyclists.csv");
    }

    public Graph(String osmFilePath, String accidentsFilePath) {

        accidents = new HashMap<>();
        nodes = new HashMap<>();
        routeNodes = new HashMap<>();
        TTCrouteNodes = new HashMap<>();
        subwaynodes = new HashMap<>();
        subwayrouteNodes = new HashMap<>();
        routes = new HashMap<>();
        buildings = new HashMap<>();
        hospitalNodes = new ArrayList<>();
        avoidHospital = false;
//        subwayGraph = new SubwayGraph(osmFilePath); // 在Graph里面建subwayGraph

        loadFiles(osmFilePath, accidentsFilePath);
        getFocus();
        MPERLON = Math.cos(focus[1] * 3.1415 / 180) * MPERLAT;
        MapEdge.graph = this;
        buildGraph_avoidHospital();
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

        // load TTC and bus route csv file


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
//    public void buildEaton(){
//        NodeList nodeList = osmDoc.getElementsByTagName("node");
//        NodeList routeList = osmDoc.getElementsByTagName("way");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Element node = (Element) nodeList.item(i);
//            MapNode newNode = new MapNode(node);
//            nodes.put(newNode.id, newNode);
//        }
//        for (int i = 0; i < routeList.getLength(); i++) {
//            Element route = (Element) routeList.item(i);
//            boolean useMe = false;
//            boolean oneWay = false;
//            boolean bikeLane = false;
//            String routeName = "unnamed route";
//            String routeType = "";
//            int maxSpeed = -1;
//            int lanes = -1;
//            List<Double> nodeIdList = new ArrayList<>();
//
//            // this for loop is not inside the MapRoute init function because not every way is a route
//            NodeList tagsForRoute = route.getElementsByTagName("tag");
//            for (int j = 0; j < tagsForRoute.getLength(); j++) {
//                Element tag = (Element) tagsForRoute.item(j);
//                if (tag.getAttribute("k").equals("highway")) {
//                    useMe = false;
//                    routeType = tag.getAttribute("v");
//                } else if (tag.getAttribute("k").equals("name")) {
//                    routeName = tag.getAttribute("v");
//                    if (routeName.equals("CF Toronto Eaton Centre"))
//                            useMe= true;
//                } else if (tag.getAttribute("k").equals("oneway") && tag.getAttribute("v").equals("yes")) {
//                    oneWay = true;
//                } else if (tag.getAttribute("k").equals("cycleway") || (tag.getAttribute("k").equals("bicycle") && (tag.getAttribute("v").equals("yes") || tag.getAttribute("v").equals("designated")))) {
//                    bikeLane = true;
//                } else if (tag.getAttribute("k").equals("maxspeed")){
//                    maxSpeed = Integer.parseInt(tag.getAttribute("v"));
//                } else if (tag.getAttribute("k").equals("lanes")){
//                    lanes = Character.getNumericValue(tag.getAttribute("v").charAt(0));
//                }
//            }
//            if (useMe) {
//                MapRoute newRoute = new MapRoute(route, routeName, routeType, bikeLane, lanes);
//                NodeList nodesInRoute = route.getElementsByTagName("nd");
//                for (int j = 0; j < nodesInRoute.getLength(); j++) {
//                    Element nd = (Element) nodesInRoute.item(j);
//                    nodeIdList.add(Double.parseDouble(nd.getAttribute("ref")));
//                }
//                double thisNode = nodeIdList.get(0);
//                double nextNode;
//                for (int j = 1; j < nodeIdList.size(); j++) {
//                    nextNode = nodeIdList.get(j);
//                    nodes.get(thisNode).edges.add(new MapEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
//                    thisNode = nextNode;
//                }
//                if (!oneWay) {
//                    thisNode = nodeIdList.get(nodeIdList.size() - 1);
//                    for (int j = nodeIdList.size() - 2; j > -1; j--) {
//                        nextNode = nodeIdList.get(j);
//                        nodes.get(thisNode).edges.add(new MapEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
//                        thisNode = nextNode;
//                    }
//                }
//                newRoute.nodeIds = nodeIdList;
//                routes.put(newRoute.routeId, newRoute);
//                for (double nodeId : nodeIdList) {
//                    routeNodes.put(nodeId, nodes.get(nodeId));
//                }
//            }
//        }
//        System.out.println(String.format("number of highway nodes: %d", routeNodes.size()));
//    }

//    public void buildGraph() {
//        System.out.println("buildGraph");
//        NodeList nodeList = osmDoc.getElementsByTagName("node");
//        NodeList routeList = osmDoc.getElementsByTagName("way");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            boolean isShoppers = false; // set Shoppers node
//            Element node = (Element) nodeList.item(i);
//            MapNode newNode = new MapNode(node);
//            nodes.put(newNode.id, newNode);
//        }
//        for (int i = 0; i < routeList.getLength(); i++) {
//            Element route = (Element) routeList.item(i);
//            boolean isHighway = false;
//            boolean isIndoor = false;
//            boolean isHospital = false;
//            boolean isMall = false;
//            boolean oneWay = false;
//            boolean bikeLane = false;
//            boolean isBuilding = false;
//            String routeName = "unnamed route";
//            String routeType = "";
//            int lanes = -1;
//            List<Double> nodeIdList = new ArrayList<>();
//
//            // this for loop is not inside the MapRoute init function because not every way is a route
//            NodeList tagsForRoute = route.getElementsByTagName("tag");
//            for (int j = 0; j < tagsForRoute.getLength(); j++) {
//                Element tag = (Element) tagsForRoute.item(j);
//                if (tag.getAttribute("k").equals("highway")) {
//                    if (tag.getAttribute("v").equals(("footway"))) {
//                        isHighway = true;
//                        routeType = tag.getAttribute("v");
//                    }
//                } else if (tag.getAttribute("k").equals("level")) {
//                    isIndoor = true;
//                } else if (tag.getAttribute("v").contains("hospital") || tag.getAttribute("v").contains("Hospital")) {
//                    isHospital = true;
//                } else if (tag.getAttribute("v").contains("mall")){
//                    isMall = true;
//                } else if (tag.getAttribute("k").equals("building")){
//                    isBuilding = true;
//                } else if (tag.getAttribute("k").equals("name")) {
//                    routeName = tag.getAttribute("v");
//                } else if (tag.getAttribute("k").equals("oneway") && tag.getAttribute("v").equals("yes")) {
//                    oneWay = true;
//                } else if (tag.getAttribute("k").equals("cycleway") || (tag.getAttribute("k").equals("bicycle") && (tag.getAttribute("v").equals("yes") || tag.getAttribute("v").equals("designated")))) {
//                    bikeLane = true;
//                } else if (tag.getAttribute("k").equals("lanes")){
//                    lanes = Character.getNumericValue(tag.getAttribute("v").charAt(0));
//                }
//            }
//            if (isHighway) {
//                MapRoute newRoute = new MapRoute(route, routeName, routeType, bikeLane, lanes);
//                NodeList nodesInRoute = route.getElementsByTagName("nd");
//                for (int j = 0; j < nodesInRoute.getLength(); j++) {
//                    Element nd = (Element) nodesInRoute.item(j);
//                    nodeIdList.add(Double.parseDouble(nd.getAttribute("ref")));
//                }
//                // set indoor
//                if (isIndoor){
//                    for (int j = 0; j<nodeIdList.size();j++){
//                        nodes.get(nodeIdList.get(j)).setisIndoor(true);
//                    }
//                }
//
//                double thisNode = nodeIdList.get(0);
//                double nextNode;
//                for (int j = 1; j < nodeIdList.size(); j++) {
//                    nextNode = nodeIdList.get(j);
//                    nodes.get(thisNode).edges.add(new MapEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
//                    thisNode = nextNode;
//                }
//                if (!oneWay) {
//                    thisNode = nodeIdList.get(nodeIdList.size() - 1);
//                    for (int j = nodeIdList.size() - 2; j > -1; j--) {
//                        nextNode = nodeIdList.get(j);
//                        nodes.get(thisNode).edges.add(new MapEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
//                        thisNode = nextNode;
//                    }
//                }
//                newRoute.nodeIds = nodeIdList;
//                routes.put(newRoute.routeId, newRoute);
//                for (double nodeId : nodeIdList) {
//                    routeNodes.put(nodeId, nodes.get(nodeId));
//                }
//            }
//
//            if (isMall){
//                NodeList nodesInRoute = route.getElementsByTagName("nd");
//                for (int j = 0; j < nodesInRoute.getLength(); j++) {
//                    Element nd = (Element) nodesInRoute.item(j);
//                    nodes.get(Double.parseDouble(nd.getAttribute("ref"))).setisHospital(true);
//                }
//            }
//        }
//        System.out.println(String.format("number of highway nodes: %d", routeNodes.size()));
//    }

    /** 最新的buildGraph function; allow avoid hospital
     * */
    public void buildGraph_avoidHospital() {
        System.out.println("why call me?");
        NodeList nodeList = osmDoc.getElementsByTagName("node");
        NodeList routeList = osmDoc.getElementsByTagName("way");

        for (int i = 0; i < nodeList.getLength(); i++) {
            boolean isShoppers = false; // set Shoppers node
            Element node = (Element) nodeList.item(i);
            MapNode newNode = new MapNode(node);
            newNode.nodetype = 5;
            nodes.put(newNode.id, newNode);
        }
        // Find all hospitals
        for (int i = 0; i < routeList.getLength(); i++) {
            Element route = (Element) routeList.item(i);
            boolean isHospital = false;
            boolean oneWay = false;
            boolean bikeLane = false;
            String routeName = "unnamed route";
            String routeType = "";
            int lanes = -1;
            List<Double> nodeIdList = new ArrayList<>();

            // this for loop is not inside the MapRoute init function because not every way is a route
            NodeList tagsForRoute = route.getElementsByTagName("tag");
            for (int j = 0; j < tagsForRoute.getLength(); j++) {
                Element tag = (Element) tagsForRoute.item(j);
                if (tag.getAttribute("v").contains("hospital") || tag.getAttribute("v").contains("Hospital")) {
                    isHospital = true;
                } else if (tag.getAttribute("k").equals("name")) {
                    routeName = tag.getAttribute("v");
                } else if (tag.getAttribute("k").equals("oneway") && tag.getAttribute("v").equals("yes")) {
                    oneWay = true;
                } else if (tag.getAttribute("k").equals("cycleway") || (tag.getAttribute("k").equals("bicycle") && (tag.getAttribute("v").equals("yes") || tag.getAttribute("v").equals("designated")))) {
                    bikeLane = true;
                } else if (tag.getAttribute("k").equals("lanes")){
                    lanes = Character.getNumericValue(tag.getAttribute("v").charAt(0));
                }
            }
            if (isHospital){
                NodeList nodesInRoute = route.getElementsByTagName("nd");
                for (int j = 0; j < nodesInRoute.getLength(); j++) {
                    Element nd = (Element) nodesInRoute.item(j);
                    nodes.get(Double.parseDouble(nd.getAttribute("ref"))).setisHospital(true);
                    hospitalNodes.add(nodes.get(Double.parseDouble(nd.getAttribute("ref"))));
                }
            }
        }
        ArrayList<MapNode> debug_list = hospitalNodes;
        // Build ways (including subway route)
        for (int i = 0; i < routeList.getLength(); i++) {
            Element route = (Element) routeList.item(i);
            boolean isHighway = false;
            boolean isSubway = false;
            boolean isIndoor = false;
            boolean isMall = false;
            boolean oneWay = false;
            boolean bikeLane = false;
            boolean isBuilding = false;
            String routeName = "unnamed route";
            String routeType = "";
            int lanes = -1;
            List<Double> nodeIdList = new ArrayList<>();

            // this for loop is not inside the MapRoute init function because not every way is a route
            NodeList tagsForRoute = route.getElementsByTagName("tag");
            for (int j = 0; j < tagsForRoute.getLength(); j++) {
                Element tag = (Element) tagsForRoute.item(j);
                if (tag.getAttribute("k").equals("highway")) {
                    if (tag.getAttribute("v").equals(("footway")) || tag.getAttribute("v").equals(("pedestrian"))) {
                        isHighway = true;
                        routeType = tag.getAttribute("v");
                    }
                } else if (tag.getAttribute("k").equals("level")) {
                    isIndoor = true;
                } else if (tag.getAttribute("v").contains("mall")){
                    isMall = true;
                } else if (tag.getAttribute("k").equals("building")){
                    isBuilding = true;
                } else if (tag.getAttribute("k").equals("name")) {
                    routeName = tag.getAttribute("v");
                } else if (tag.getAttribute("k").equals("oneway") && tag.getAttribute("v").equals("yes")) {
                    oneWay = true;
                } else if (tag.getAttribute("k").equals("cycleway") || (tag.getAttribute("k").equals("bicycle") && (tag.getAttribute("v").equals("yes") || tag.getAttribute("v").equals("designated")))) {
                    bikeLane = true;
                } else if (tag.getAttribute("k").equals("lanes")){
                    lanes = Character.getNumericValue(tag.getAttribute("v").charAt(0));
                }
            }
            if (isHighway) {
                MapRoute newRoute = new MapRoute(route, routeName, routeType, bikeLane, lanes);
                NodeList nodesInRoute = route.getElementsByTagName("nd");
                for (int j = 0; j < nodesInRoute.getLength(); j++) {
                    Element nd = (Element) nodesInRoute.item(j);
                    nodeIdList.add(Double.parseDouble(nd.getAttribute("ref")));
                }
                // set indoor
                if (isIndoor){
                    for (int j = 0; j<nodeIdList.size();j++){
                        nodes.get(nodeIdList.get(j)).setisIndoor(true);
                    }
                }
                // set hospital
                for (int j = 0; j<nodeIdList.size();j++){
                    for(int k=0;k<hospitalNodes.size();k++){
                        if (getDistance(nodes.get(nodeIdList.get(j)),hospitalNodes.get(k))<=30.0){
                            nodes.get(nodeIdList.get(j)).setisHospital(true);
//                            System.out.println("isHospital id: "+ nodes.get(nodeIdList.get(j)).id);
                        }
                    }
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
                    TTCrouteNodes.put(nodeId,nodes.get(nodeId));
                }
            }
        }
        System.out.println(String.format("number of highway nodes: %d", routeNodes.size()));

        //excel file ttc data
        // load toronto police csv file
        BufferedReader br = null;
        String line = "";
        DecimalFormat df = new DecimalFormat("#.###");
        df.setRoundingMode(RoundingMode.FLOOR);
        int prev_route_id = -1;
        int prev_seq = -1;
        Date prev_time = new Date();
        Double prev_id = -1.0;
        try {
            br = new BufferedReader(new FileReader("./data/ttc data.csv"));
            br.readLine();
            while ((line = br.readLine()) != null) {
                String[] entry = line.split(",");
                String trip_id = entry[0];
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
                Date arrival_time = sdf.parse(entry[1]);
                String stop_id = entry[3];

                int stop_sequence = Integer.parseInt(entry[4]);
                int route_id = Integer.parseInt(entry[6]);
                String trip_name = entry[8];
                int route_type = Integer.parseInt(entry[10]);
                double lon = Double.parseDouble(entry[11]);
                double lat = Double.parseDouble(entry[12]);

                if(prev_route_id == route_id){
                    if(stop_sequence == prev_seq + 1){
                        if(nodes.containsKey(stop_id)){
                            nodes.get(Double.parseDouble(stop_id)).arrivalTime.add(arrival_time);
                        }
                        else{
                            MapNode newnode = new MapNode();
                            newnode.id = Double.parseDouble(stop_id);
                            newnode.longitude = lon;
                            newnode.latitude = lat;
                            newnode.isIndoor = true;
                            newnode.nodetype = route_type;
                            newnode.arrivalTime.add(arrival_time);
                            newnode.ttcName = trip_name;
                            nodes.get(prev_id).edges.add(new MapEdge(nodes.get(prev_id), newnode, (arrival_time.getTime() - prev_time.getTime())/(60 * 1000) % 60));
                            nodes.put(newnode.id, newnode);
                            TTCrouteNodes.put(prev_id, nodes.get(prev_id));

                            // connect ttc network w/ walking network
                            MapNode walknode;
                            for (Double i : routeNodes.keySet()){//(Double i : routeNodes.keySet()){
                                walknode = routeNodes.get(i);
                                double dist = getDistance(walknode,newnode);
                                if (dist <=10.0){
                                    TTCrouteNodes.get(walknode.id).edges.add(new MapEdge(nodes.get(walknode.id), newnode, (new Double((dist/5000.0)*60.0)).longValue()));
//                                    walknode.edges.add(new MapEdge(walknode,newnode,(new Double((dist/5000.0)*60.0)).longValue()));
//                                    TTCrouteNodes.put(walknode.id,walknode);
                                    nodes.get(newnode.id).edges.add(new MapEdge(nodes.get(newnode.id),nodes.get(walknode.id),(new Double((dist/5000.0)*60.0)).longValue()));
                                    TTCrouteNodes.put(newnode.id,newnode);
                                }

                            }

                        }

                    }
                    else{
                        if(nodes.containsKey(Double.parseDouble(stop_id))){
                            nodes.get(Double.parseDouble(stop_id)).arrivalTime.add(arrival_time);
                        }
                        else{
                            MapNode newnode = new MapNode();
                            newnode.id = Double.parseDouble(stop_id);
                            newnode.longitude = lon;
                            newnode.latitude = lat;
                            newnode.isIndoor = true;
                            newnode.nodetype = route_type;
                            newnode.arrivalTime.add(arrival_time);
                            newnode.ttcName = trip_name;

                            nodes.put(newnode.id, newnode);

                            // connect walk net to ttc net
                            MapNode walknode;
                            for (Double i : routeNodes.keySet()){//(Double i : routeNodes.keySet()){
                                walknode = routeNodes.get(i);
                                double dist = getDistance(walknode,newnode);
                                if (dist <=10.0){
                                    TTCrouteNodes.get(walknode.id).edges.add(new MapEdge(nodes.get(walknode.id), newnode, (new Double((dist/5000.0)*60.0)).longValue()));
//                                    walknode.edges.add(new MapEdge(walknode,newnode,(new Double((dist/5000.0)*60.0)).longValue()));
//                                    TTCrouteNodes.put(walknode.id,walknode);
                                    nodes.get(newnode.id).edges.add(new MapEdge(nodes.get(newnode.id),nodes.get(walknode.id),(new Double((dist/5000.0)*60.0)).longValue()));
                                    TTCrouteNodes.put(newnode.id,newnode);
                                }
                            }
                        }

                    }
                }
                else{
                    if(nodes.containsKey(stop_id)){
                        //nodes.get(prev_id).arrivalTime.add(arrival_time);
                        System.out.println("error 610");
                    }
                    else{
                        MapNode newnode = new MapNode();
                        newnode.id = Double.parseDouble(stop_id);
                        newnode.longitude = lon;
                        newnode.latitude = lat;
                        newnode.isIndoor = true;
                        newnode.nodetype = route_type;
                        newnode.arrivalTime.add(arrival_time);
                        newnode.ttcName = trip_name;

                        nodes.put(newnode.id, newnode);
                        // connect walk net to ttc net
                        MapNode walknode;
                        for (Double i : routeNodes.keySet()){//(Double i : routeNodes.keySet()){
                            walknode = routeNodes.get(i);
                            double dist = getDistance(walknode,newnode);
                            if (dist <=10.0){
                                TTCrouteNodes.get(walknode.id).edges.add(new MapEdge(nodes.get(walknode.id), newnode, (new Double((dist/5000.0)*60.0)).longValue()));
//                                walknode.edges.add(new MapEdge(walknode,newnode,-1));
//                                TTCrouteNodes.put(walknode.id,walknode);
                                nodes.get(newnode.id).edges.add(new MapEdge(nodes.get(newnode.id),nodes.get(walknode.id),(new Double((dist/5000.0)*60.0)).longValue()));
                                TTCrouteNodes.put(newnode.id,newnode);
                            }
                        }
                    }
                }

                prev_seq = stop_sequence;
                prev_route_id = route_id;
                prev_id = Double.parseDouble(stop_id);
                prev_time =arrival_time;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        System.out.println(String.format("number of connected temp route nodes: %d", TTCrouteNodes.size()));
    }



    /** Original buildGraph
     * */
//    public void buildGraph() {
//        NodeList nodeList = osmDoc.getElementsByTagName("node");
//        NodeList routeList = osmDoc.getElementsByTagName("way");
//        for (int i = 0; i < nodeList.getLength(); i++) {
//            Element node = (Element) nodeList.item(i);
//            MapNode newNode = new MapNode(node);
//            nodes.put(newNode.id, newNode);
//        }
//        for (int i = 0; i < routeList.getLength(); i++) {
//            Element route = (Element) routeList.item(i);
//            boolean useMe = false;
//            boolean oneWay = false;
//            boolean bikeLane = false;
//            String routeName = "unnamed route";
//            String routeType = "";
//            int maxSpeed = -1;
//            int lanes = -1;
//            List<Double> nodeIdList = new ArrayList<>();
//
//            // this for loop is not inside the MapRoute init function because not every way is a route
//            NodeList tagsForRoute = route.getElementsByTagName("tag");
//            for (int j = 0; j < tagsForRoute.getLength(); j++) {
//                Element tag = (Element) tagsForRoute.item(j);
//                if (tag.getAttribute("k").equals("highway")) {
//                    useMe = true;
//                    routeType = tag.getAttribute("v");
//                } else if (tag.getAttribute("k").equals("name")) {
//                    routeName = tag.getAttribute("v");
//                } else if (tag.getAttribute("k").equals("oneway") && tag.getAttribute("v").equals("yes")) {
//                    oneWay = true;
//                } else if (tag.getAttribute("k").equals("cycleway") || (tag.getAttribute("k").equals("bicycle") && (tag.getAttribute("v").equals("yes") || tag.getAttribute("v").equals("designated")))) {
//                    bikeLane = true;
//                } else if (tag.getAttribute("k").equals("maxspeed")){
//                    maxSpeed = Integer.parseInt(tag.getAttribute("v"));
//                } else if (tag.getAttribute("k").equals("lanes")){
//                    lanes = Character.getNumericValue(tag.getAttribute("v").charAt(0));
//                }
//            }
//            if (useMe) {
//                MapRoute newRoute = new MapRoute(route, routeName, routeType, bikeLane, maxSpeed, lanes);
//                NodeList nodesInRoute = route.getElementsByTagName("nd");
//                for (int j = 0; j < nodesInRoute.getLength(); j++) {
//                    Element nd = (Element) nodesInRoute.item(j);
//                    nodeIdList.add(Double.parseDouble(nd.getAttribute("ref")));
//                }
//                double thisNode = nodeIdList.get(0);
//                double nextNode;
//                for (int j = 1; j < nodeIdList.size(); j++) {
//                    nextNode = nodeIdList.get(j);
//                    nodes.get(thisNode).edges.add(new MapEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
//                    thisNode = nextNode;
//                }
//                if (!oneWay) {
//                    thisNode = nodeIdList.get(nodeIdList.size() - 1);
//                    for (int j = nodeIdList.size() - 2; j > -1; j--) {
//                        nextNode = nodeIdList.get(j);
//                        nodes.get(thisNode).edges.add(new MapEdge(newRoute, nodes.get(thisNode), nodes.get(nextNode)));
//                        thisNode = nextNode;
//                    }
//                }
//                newRoute.nodeIds = nodeIdList;
//                routes.put(newRoute.routeId, newRoute);
//                for (double nodeId : nodeIdList) {
//                    routeNodes.put(nodeId, nodes.get(nodeId));
//                }
//            }
//        }
//        System.out.println(String.format("number of highway nodes: %d", routeNodes.size()));
//    }
    protected void getFocus() {
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

}