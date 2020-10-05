package com.example.demo.Services;

import org.w3c.dom.Element;

import java.io.Serializable;
import java.util.List;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class MapRoute implements Serializable {
    public double routeId;
    public String routeName;
    public String routeType;
    public List<Double> nodeIds;
    public boolean bikeLane;
    public boolean steps = false;
    public int maxSpeed;
    public int lanes;

    private void readObject(ObjectInputStream aInputStream) throws ClassNotFoundException, IOException
    {
        routeId = aInputStream.readDouble();
        routeName = aInputStream.readUTF();
        routeType = aInputStream.readUTF();
        nodeIds = (List<Double>) aInputStream.readObject();
        bikeLane = aInputStream.readBoolean();
        steps = aInputStream.readBoolean();
        maxSpeed = aInputStream.read();
        lanes = aInputStream.read();
    }

    private void writeObject(ObjectOutputStream aOutputStream) throws IOException
    {
        aOutputStream.writeDouble(routeId);
        aOutputStream.writeUTF(routeName);
        aOutputStream.writeUTF(routeType);
        aOutputStream.writeObject(nodeIds);
        aOutputStream.writeBoolean(bikeLane);
        aOutputStream.writeBoolean(steps);
        aOutputStream.write(maxSpeed);
        aOutputStream.write(lanes);
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
