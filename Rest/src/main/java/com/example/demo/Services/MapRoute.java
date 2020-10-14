package com.example.demo.Services;

import org.w3c.dom.Element;
import java.util.List;

public class MapRoute {
    public double routeId;
    public String routeName;
    public String routeType;
    public List<Double> nodeIds;
    public boolean bikeLane;
    public boolean steps = false;
    public int maxSpeed;
    public int lanes;

    public MapRoute(MapRoute newMapRoute){
        routeId = newMapRoute.routeId;
        this.routeName = newMapRoute.routeName;
        this.routeType = newMapRoute.routeType;
        this.maxSpeed = newMapRoute.maxSpeed;
        this.lanes = newMapRoute.lanes;
        this.bikeLane = newMapRoute.bikeLane;
        steps = newMapRoute.steps;
    }
    public MapRoute(Element route, String routeName, String routeType, boolean bikeLane, int maxSpeed, int lanes){
        routeId = Double.parseDouble(route.getAttribute("id"));
        this.routeName = routeName;
        this.routeType = routeType;
        this.maxSpeed = maxSpeed;
        this.lanes = lanes;

        if (routeType.equals("cycleway")){
            this.bikeLane = true;
        } else {
            this.bikeLane = bikeLane;
        }

        if (routeType.equals("steps")){
            steps = true;
        }
    }
}
