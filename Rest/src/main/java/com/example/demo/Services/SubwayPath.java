package com.example.demo.Services;

import java.util.ArrayList;

import static com.example.demo.Services.Graph.getDistance;

public class SubwayPath extends Path{ //hi
    protected ArrayList<SubwayNode> nodes;

    public SubwayPath(ArrayList<SubwayNode> input_nodes){
        nodes = input_nodes;
        totalLength = 0;
        for (int i=0; i< input_nodes.size()-1;i++){
            totalLength+= getDistance(input_nodes.get(i), input_nodes.get(i+1));
            totalPedCount += input_nodes.get(i).pedCount;
        }
        setTime(); // set total time of the path in mins
    }
}
