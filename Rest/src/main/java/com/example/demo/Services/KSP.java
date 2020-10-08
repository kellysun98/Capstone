package com.example.demo.Services;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

public class KSP {


    public static ArrayList<Path> ksp(Graph graph, MapNode src, MapNode dest, String costFunction, int K){


        ArrayList<Path> A = new ArrayList<>();

        // Initialize a set to store potential kth shortest path
        ArrayList<Path> B = new ArrayList<>();

            // Find shortest path from src to sink
            Planner planner = new Planner();
            Path shortestpath = planner.plan(graph, src, dest, costFunction);
            A.add(shortestpath);

            for (int k=1; k<K; k++){
                // The spur node ranges from the first node to the next to last node in the previous k-shortest path.
                Path previousPath = A.get(k-1);
                for (int i=0; i<previousPath.size()-2; i++ ) {
                    // Spur node is retrieved from the previous k-shortest path, k − 1.
                    MapNode spurNode = previousPath.get(i);
                    // The sequence of nodes from the source to the spur node of the previous k-shortest path.
                    Path rootPath = previousPath.subPath(0, i);
                    // Clone spurNode for restore purpose
                    MapNode spurNode_copy = spurNode.clone();


                    // Remove edges
                    for (int n = 0; n < previousPath.size(); n++){
                        if(previousPath.getNodes().get(n).equals(spurNode)){
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
                System.out.println("A size = " + A.size());
                System.out.println("A: "+ A);
                System.out.println("B size = "+B.size());
                System.out.println("B[0]: "+ B.get(0));
            }
        return A;
    }
}
