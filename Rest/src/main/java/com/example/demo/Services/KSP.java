package com.example.demo.Services;

import com.google.gson.Gson;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;
import java.util.*;
import com.google.gson.Gson;


public class KSP {
//    public void changeA0(ArrayList<Path> inList){
//        for (int i=0;i<inList.size();i++ ){
//            if (i==0)
//        }
//    }


    public static ArrayList<Path> ksp(Graph graph, MapNode src, MapNode dest, String costFunction, int K) {


        ArrayList<Path> A = new ArrayList<>();

        // Initialize a set to store potential kth shortest path
        ArrayList<Path> B = new ArrayList<>();

        // Find shortest path from src to sink
        Planner planner = new Planner();
        Path shortestpath = planner.plan(graph, src, dest, costFunction);
        A.add(shortestpath);

        for (int k = 1; k < K; k++) {
            // The spur node ranges from the first node to the next to last node in the previous k-shortest path.
            Path previousPath = A.get(k - 1);
            for (int i = 0; i < previousPath.size() - 2; i++) {
                System.out.println("Hi "+i);
                // Spur node is retrieved from the previous k-shortest path, k − 1.
                MapNode spurNode = previousPath.get(i);
                // The sequence of nodes from the source to the spur node of the previous k-shortest path.
                Path rootPath = previousPath.subPath(0, i);
                // Clone spurNode for restore purpose
                //MapNode spurNode_copy = spurNode.clone();
                MapNode spurNode_copy = new MapNode(spurNode);


                // Remove edges
                for (int n = 0; n < previousPath.size(); n++) {
                    if (previousPath.getNodes().get(n).equals(spurNode)) {
                        spurNode_copy.removeEdges(previousPath.get(n + 1));
                    }
                }

                // Calculate the spur path from spurNode to destinationNode
                Path spurPath = planner.plan(graph, spurNode_copy, dest, costFunction);


                // If a new spur path is found
                if (spurPath != null) {
                    // Entire path is made up of root path and spur path
                    Path totalPath = Path.concatenate(rootPath, spurPath);

                    // Add potential k-shortest path to the heap
                    if (!B.contains(totalPath))
                        B.add(totalPath);
                }
            }
            if (B.isEmpty())
                break;
            Collections.sort(B);
            // Add the lowest cost path becomes the k-shortest path
            A.add(B.get(0));
            B.remove(0);
//            System.out.println("A size = " + A.size());
//            System.out.println("A: " + A);
//            System.out.println("B size = " + B.size());
//            System.out.println("B[0]: " + B.get(0));
        }
        // Print cost of each path in A for sanity check
//        for (int j = 0; j < A.size(); j++) {
//            System.out.println("Cost of A[" + j + "]= " + A.get(j).getTotalCost());
//        }
//        A.get(0)
        return A;
    }

    public static String KSPtoJson(ArrayList<Path> ksp_sol) {
        ArrayList solution = new ArrayList<>();

        for (Path p : ksp_sol) {
//            HashMap<Integer, ArrayList<String>> path_map = new HashMap<>();
            ArrayList<String> return_value = new ArrayList<>();
            List<MapNode> node_list = p.getNodes();
            String mn_toString = new String();
            for (MapNode mn : node_list) {
                Double longitude = mn.longitude;
                Double latitude = mn.latitude;
                mn_toString += ('[' + longitude.toString() + ',' + latitude.toString() + ']' + ',');
            }
            Double cost = p.getTotalCost();
            Double time = p.getTotalTime();
            return_value.add(cost.toString());
            return_value.add(mn_toString.substring(0, mn_toString.length() - 1));
            return_value.add(time.toString());
            return_value.add(p.getDescription());
//            path_map.put(count, return_value);

            solution.add(return_value); //[cost, nodelist, totaltime, description]
        }
        String solution_to_string = new Gson().toJson(solution);
        return solution_to_string;
    }
}