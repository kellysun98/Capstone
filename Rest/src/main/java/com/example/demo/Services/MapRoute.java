package com.example.demo.Services;

import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.List;

public class MapRoute implements Serializable {
    public static double routeId;
    public static String routeName;
    public static String routeType;
    public static List<Double> nodeIds;
    public static boolean bikeLane;
    public static boolean steps = false;
    public static int maxSpeed;
    public static int lanes;

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
