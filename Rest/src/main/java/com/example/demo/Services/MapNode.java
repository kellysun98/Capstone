package com.example.demo.Services;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.*;

import static com.example.demo.Services.Graph.getDistance;
import static com.example.demo.Services.Graph.normalize;

public class MapNode implements Comparable<MapNode>{
    public Graph graph;
    public Element element;
    public double id;
    public double longitude;
    public double latitude;
    public List<MapEdge> edges;
    public double estimatedCost;// f(n);
    public double pedCount; // number of people at the node; h(n)
    public double euclid; // euclidean distance from this MapNode to destination node
    public double normalized_pedCount; // normalized pedCount
    public double normalized_euclid; // normalized euclidean distance from this node to destination node
    public boolean isIndoor; // whether node is indoor
    public boolean isHospital; // whether node is hospital
    public boolean isShoppers; // whether node is shoppers
    public boolean isMall; //whether node is mall

    public double getPedCount(){
        return this.pedCount;
    }

    public List<MapEdge> getEdges(){
        return edges;
    }

    public boolean equals(MapNode outsider){
        return outsider.id == this.id;
    }

    public MapNode(MapNode newMapNode){
        id = newMapNode.id;
        longitude = newMapNode.longitude;
        latitude = newMapNode.latitude;
        edges = new ArrayList<>(newMapNode.edges);
        pedCount = newMapNode.pedCount;
        isIndoor = false;
        isHospital = false;
        isShoppers = false;
        isMall = false;
    }
    public MapNode (){
        id = -1;
        longitude = -1;
        latitude = -1;
        edges = new ArrayList<>();
        //pedCount = getRandomNumber(0,20);
        pedCount = -1;
        isIndoor = false;
        isHospital = false;
        isShoppers = false;
        isMall = false;
    }
    /** Set Function for isIndoor, isHospital, isShoppers
     * */
    public void setisIndoor(boolean state){this.isIndoor=state;}
    public void setisHospital(boolean state){this.isHospital=state;}
    public void setisShoppers(boolean state){this.isShoppers=state;}
    /**/
    public int getRandomNumber(int min, int max){
        return (int) ((Math.random()*(max-min))+min);
    }

    /** Used for osm initialization
     * */
    public MapNode (Element e){
        id = Double.parseDouble(e.getAttribute("id"));
        longitude = Double.parseDouble(e.getAttribute("lon"));
        latitude = Double.parseDouble(e.getAttribute("lat"));
        edges = new ArrayList<>();
        pedCount = -1;
        isIndoor = false;
        isHospital = false;
        isShoppers = false;
        isMall = false;
//        euclid = getDistance()
//        normalized_pedCount = normalize(this.pedCount, graph.min_pedCount, graph.max_pedCont);
    }

    public MapNode clone(){
        MapNode copy = new MapNode();
        copy.id = this.id;
        copy.edges = this.edges;
        copy.estimatedCost = this.estimatedCost;
        copy.latitude = this.latitude;
        copy.longitude = this.longitude;
        copy.pedCount = this.pedCount;
        copy.isIndoor = this.isIndoor;
        copy.isHospital = this.isHospital;
        copy.isShoppers = this.isShoppers;
        copy.isMall = this.isMall;
        return copy;

    }

    public void removeEdges(MapNode Node2Remove){
        for (int i=0; i<this.edges.size(); i++){
            if (edges.get(i).getDestinationNodeID() == Node2Remove.id){
                edges.remove(i);
            }
        }
    }

    public static HashMap<String, Double> MapNodetoHash (Collection<MapNode> k){
        HashMap resultmap = new HashMap<String, Double>();  //DT3.0SM
        double minlat=43.6467000;
        double minlon=-79.3938000;
        double maxlat=43.6629000;
        double maxlon=-79.3731000;
        double dlon = (maxlon- minlon)/20;
        double dlat = (maxlat -minlat)/20;
        double lon, lat= 0.0;
        double cutoff = 0.5*(Math.sqrt(Math.pow(dlon, 2) + Math.pow(dlat, 2)));

        ArrayList<Double> pedCount = new ArrayList<>(Arrays.asList(0.0,0.0));
        HashMap<ArrayList<Double>, ArrayList<Double>> tempNodeMap = new HashMap<ArrayList<Double>, ArrayList<Double>>();
        for (lon = minlon; lon <= maxlon; lon += dlon){
             for (lat = minlat; lat <= maxlat; lat += dlat){
                 ArrayList<Double> tempCoord = new ArrayList<>(Arrays.asList(lon,lat));
                 tempNodeMap.put(tempCoord,pedCount);
             }
        }

        for (MapNode x: k) {
            //<bounds minlat="43.6467000" minlon="-79.3938000" maxlat="43.6629000" maxlon="-79.3731000"/> osm3

            if (x.pedCount < 10) {
                for (ArrayList<Double> y : tempNodeMap.keySet()) {
                    double dx = (x.longitude - y.get(0));
                    double dy = (x.latitude - y.get(1));
//                    System.out.print(y.get(0));
//                    System.out.print(y.get(1));
                    if (Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2)) <= cutoff){
                        ArrayList<Double> tempCount = new ArrayList<>(Arrays.asList(tempNodeMap.get(y).get(0) + x.pedCount, tempNodeMap.get(y).get(1) + 1));
                        //System.out.print(tempCount);
                        tempNodeMap.replace(y, tempCount);
                        break;
                    }
                }
            }
        }
        for (ArrayList<Double> y: tempNodeMap.keySet()){
            String coord= "[" + y.get(0) + "," + y.get(1) + "]";
            resultmap.put(coord, tempNodeMap.get(y).get(0)/tempNodeMap.get(y).get(1));
        }
        //System.out.print(resultmap);
        return resultmap;
    }

//    @Override
//    public boolean equals(MapNode outsider){
//        boolean flag = false;
//        if (this.id == outsider.id)
//            flag = true;
//        return flag;
//    }



    @Override
    public int compareTo(MapNode o) {
        if(this.estimatedCost == o.estimatedCost)
            return 0;
        return this.estimatedCost < o.estimatedCost ? -1 : 1;
    }
}
