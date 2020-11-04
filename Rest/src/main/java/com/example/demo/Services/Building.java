package com.example.demo.Services;

import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.Collections;

public class Building {
    public double buildingID;
    public String buildingName;
    public String buildingType;
    public ArrayList<MapNode> boundNodeList;
    private double min_lat;
    private double min_lon;
    private double max_lat;
    private double max_lon;

    public Building(Element route, String routeName, String routeType,ArrayList<MapNode> newNodeList) {
        this.buildingID = Double.parseDouble(route.getAttribute("id"));
        this.buildingName = routeName;
        this.buildingType = routeType;
        this.boundNodeList = newNodeList;
        setMinMaxLatLon();
    }
    public void setMinMaxLatLon(){
        ArrayList<Double> latList = new ArrayList<>();
        ArrayList<Double> lonList = new ArrayList<>();
        for(MapNode n:this.boundNodeList){
            latList.add(n.latitude);
            lonList.add(n.longitude);
        }
        this.min_lat = Collections.min(latList);
        this.max_lat = Collections.max(latList);
        this.min_lon = Collections.min(lonList);
        this.max_lon = Collections.max(lonList);
    }


    public void addToboundNodeList(MapNode newNode){boundNodeList.add(newNode);}
    public double getbuildingID(){return this.buildingID;}
}
