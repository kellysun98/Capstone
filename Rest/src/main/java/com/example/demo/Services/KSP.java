package com.example.demo.Services;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.*;

import org.apache.commons.math3.util.Precision;


public class KSP { //hi
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
        }
        return A;
    }
    /** Search K diverse routes --- Walking Mode */
    public static ArrayList<Path> Diverse_K(Graph graph, MapNode src, MapNode dest, String costFunction, int K){
        ArrayList<Path> result = new ArrayList<>();
        ArrayList<Double> result_dist = new ArrayList<>();
        Planner planner = new Planner();
//        double distWeight = 1;
//        double riskWeight = 0;
        ArrayList<Integer> weight = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 100, 200, 300, 400, 500,1000,2000,3000,4000));
        //for (int i=0;i<K;i++){
        for (int i : weight){
            //double riskWeight = i/(double)K;
            double riskWeight = i;
            double distWeight = 1;
            //System.out.println("distWeight:"+String.valueOf(distWeight));
            //System.out.println("riskWeight:"+String.valueOf(riskWeight));
            Path temp = new Path();
            if (graph.avoidHospital==true) { // Case 1: 躲避医院
                temp = planner.AStar_avoidHospital(graph, src, dest, costFunction, riskWeight, distWeight);
                temp.weight = i;
            }else if (graph.avoidHospital==false){ // Case 2: 不躲避医院
                temp = planner.AStar(graph, src, dest, costFunction, riskWeight, distWeight);
                temp.weight = i;
            }
            if(result.isEmpty()){
                result.add(temp);
                result_dist.add(temp.getTotalLength());
            }else if(!result_dist.contains(temp.getTotalLength())){
                result.add(temp);
                result_dist.add(temp.getTotalLength());
            }
        }
        Path safestp = planner.AStar_avoidHospital(graph, src, dest, costFunction, 1, 0);
        if(!result_dist.contains(safestp.getTotalLength())){
            result.add(safestp);
        }
        return result;
    }

    /** Search K diverse routes --- Public Transit Mode*/
    public static ArrayList<Path> Diverse_K_TTC(Graph graph, MapNode src, MapNode dest, String costFunction, int K){
        ArrayList<Path> result = new ArrayList<>();
        ArrayList<Double> result_dist = new ArrayList<>();
        Planner planner = new Planner();
//        double distWeight = 1;
//        double riskWeight = 0;
        //ArrayList<Integer> weight = new ArrayList<>(Arrays.asList(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 20, 30, 40, 50, 60, 70, 100, 200, 300, 400, 500,1000,2000,3000,4000));
        ArrayList<Integer> weight = new ArrayList<>(Arrays.asList(0));

        //for (int i=0;i<K;i++){
        for (int i : weight){
            //double riskWeight = i/(double)K;
            double riskWeight = i;
            double distWeight = 1;
            //System.out.println("distWeight:"+String.valueOf(distWeight));
            //System.out.println("riskWeight:"+String.valueOf(riskWeight));
            Path temp = new Path();
            if (graph.avoidHospital==true) { // Case 1: 躲避医院
                temp = planner.AStar_avoidHospital(graph, src, dest, costFunction, riskWeight, distWeight);
                temp.weight = i;
            }else if (graph.avoidHospital==false){ // Case 2: 不躲避医院
                temp = planner.AStar(graph, src, dest, costFunction, riskWeight, distWeight);
                temp.weight = i;
            }
            if(result.isEmpty()){
                result.add(temp);
                result_dist.add(temp.getTotalLength());
            }else if(!result_dist.contains(temp.getTotalLength())){
                result.add(temp);
                result_dist.add(temp.getTotalLength());
                }
            }
        //Path safestp = planner.AStar_avoidHospital(graph, src, dest, costFunction, 1, 0);
        //if(!result_dist.contains(safestp.getTotalLength())){
        //    result.add(safestp);
        //}
        return result;
        }


    /** Walking */
    public static String KSPtoJson(ArrayList<Path> ksp_sol) {
        ArrayList solution = new ArrayList<>();
        double count = 0;
        for (Path p : ksp_sol) {
            HashMap<String, String> path_map = new HashMap<>();
            ArrayList<String> return_value = new ArrayList<>();
            List<MapNode> node_list = p.getNodes();
            String mn_toString = new String();
            ArrayList<ArrayList<Double>> mn = new ArrayList<>();
            ArrayList<Double> risk = new ArrayList<>();
            String risk_toString = new String();
            for (int i = 1; i<node_list.size(); i++) {
                ArrayList<Double> al1 = new ArrayList<>();
                ArrayList<Double> al2 = new ArrayList<>();
                MapNode first = node_list.get(i-1);
                MapNode second = node_list.get(i);
                Double middle_lon = (first.longitude+second.longitude)/2+count/80000;
                Double middle_lat = (first.latitude+second.latitude)/2+count/80000;
                Double longitude = node_list.get(i-1).longitude+count/80000;
                Double latitude = node_list.get(i-1).latitude+count/80000;
                Double risk1 = node_list.get(i-1).pedCount;
                Double risk2 = node_list.get(i).pedCount;

                al1.add(longitude);
                al1.add(latitude);
                al2.add(middle_lon);
                al2.add(middle_lat);
                mn.add(al1);
                mn.add(al2);
                risk.add(risk1);
                risk.add(risk2);
            }
            Double cost = p.getTotalLength();
            Double time = Precision.round(p.getTotalTime(),0);
            Double distance = Precision.round(p.getTotalLength()/1000,2);
            path_map.put("cost", new Gson().toJson(cost));
            path_map.put("routeNode", new Gson().toJson(mn));
            path_map.put("risk", new Gson().toJson(risk));
            path_map.put("time", new Gson().toJson(time));
            path_map.put("description", p.getDescription());
            path_map.put("distance", new Gson().toJson(distance));
            count++;
            solution.add(path_map); //[cost, routeNode, risk, time, description, distance]
        }
        String solution_to_string = new Gson().toJson(solution);
        return solution_to_string;
    }

    /** Public Transit */
    public static String KSPtoJsonTTC(ArrayList<Path> ksp_sol) {
        ArrayList solution = new ArrayList<>();
        double count = 0;
        for (Path p : ksp_sol) {
            HashMap<String, String> path_map = new HashMap<>();
            ArrayList<String> return_value = new ArrayList<>();
            List<MapNode> node_list = p.getNodes();
            String mn_toString = new String();
            ArrayList<ArrayList<Double>> mn = new ArrayList<>();
            ArrayList<Integer> nodetypes = new ArrayList<>(); // node type of each MapNode
            String risk_toString = new String();
            ArrayList<String> ttcnames = new ArrayList<>(); // stop name of each MapNode
            for (int i = 1; i<node_list.size(); i++) {
                ArrayList<Double> al1 = new ArrayList<>();
                ArrayList<Double> al2 = new ArrayList<>();
                MapNode first = node_list.get(i-1);
                MapNode second = node_list.get(i);
                Double middle_lon = (first.longitude+second.longitude)/2+count/80000;
                Double middle_lat = (first.latitude+second.latitude)/2+count/80000;
                Double longitude = node_list.get(i-1).longitude+count/80000;
                Double latitude = node_list.get(i-1).latitude+count/80000;
                int nt1 = node_list.get(i-1).nodetype;
                int nt2 = node_list.get(i).nodetype;
                String sn1 = node_list.get(i-1).ttcName;
                String sn2 = node_list.get(i).ttcName;

                al1.add(longitude);
                al1.add(latitude);
                al2.add(middle_lon);
                al2.add(middle_lat);
                mn.add(al1);
                mn.add(al2);
                nodetypes.add(nt1);
                nodetypes.add(nt2);
                ttcnames.add(sn1);
                ttcnames.add(sn2);
            }
            Double cost = p.getTotalLength();
            Double time = Precision.round(p.getTotalTime(),0);
            Double distance = Precision.round(p.getTotalLength()/1000,2);
            path_map.put("cost", new Gson().toJson(cost));
            path_map.put("routeNode", new Gson().toJson(mn));
            path_map.put("nodetype", new Gson().toJson(nodetypes));
            path_map.put("ttcname",new Gson().toJson(ttcnames));
            path_map.put("time", new Gson().toJson(time));
            path_map.put("description", p.getDescription());
            path_map.put("distance", new Gson().toJson(distance));
            count++;
            solution.add(path_map); //[cost, routeNode, nodetype, ttcname, time, description, distance]
        }
        String solution_to_string = new Gson().toJson(solution);
        return solution_to_string;
    }
    /** Walking */
    public static ArrayList KSPtoJson_AL(ArrayList<Path> ksp_sol) {
        ArrayList solution = new ArrayList<>();
        double count = 0;
        for (Path p : ksp_sol) {
            HashMap<String, String> path_map = new HashMap<>();
            ArrayList<String> return_value = new ArrayList<>();
            List<MapNode> node_list = p.getNodes();
            String mn_toString = new String();
            ArrayList<ArrayList<Double>> mn = new ArrayList<>();
            ArrayList<Double> risk = new ArrayList<>();
            String risk_toString = new String();
            for (int i = 1; i<node_list.size(); i++) {
                ArrayList<Double> al1 = new ArrayList<>();
                ArrayList<Double> al2 = new ArrayList<>();
                MapNode first = node_list.get(i-1);
                MapNode second = node_list.get(i);
                Double middle_lon = (first.longitude+second.longitude)/2+count/80000;
                Double middle_lat = (first.latitude+second.latitude)/2+count/80000;
                Double longitude = node_list.get(i-1).longitude+count/80000;
                Double latitude = node_list.get(i-1).latitude+count/80000;
                Double risk1 = node_list.get(i-1).pedCount;
                Double risk2 = node_list.get(i).pedCount;

                al1.add(longitude);
                al1.add(latitude);
                al2.add(middle_lon);
                al2.add(middle_lat);
                mn.add(al1);
                mn.add(al2);
                risk.add(risk1);
                risk.add(risk2);
            }
            Double cost = p.getTotalLength();
            Double time = Precision.round(p.getTotalTime(),0);
            Double distance = Precision.round(p.getTotalLength()/1000,2);
            path_map.put("cost", new Gson().toJson(cost));
            path_map.put("routeNode", new Gson().toJson(mn));
            path_map.put("risk", new Gson().toJson(risk));
            path_map.put("time", new Gson().toJson(time));
            path_map.put("description", p.getDescription());
            path_map.put("distance", new Gson().toJson(distance));
            count++;
            solution.add(path_map); //[cost, routeNode, risk, time, description, distance]
        }
        return solution;
    }

    /** Public Transit */
    public static ArrayList KSPtoJsonTTC_AL(ArrayList<Path> ksp_sol) {
        ArrayList solution = new ArrayList<>();
        double count = 0;
        for (Path p : ksp_sol) {
            HashMap<String, String> path_map = new HashMap<>();
            ArrayList<String> return_value = new ArrayList<>();
            List<MapNode> node_list = p.getNodes();
            String mn_toString = new String();
            ArrayList<ArrayList<Double>> mn = new ArrayList<>();
            ArrayList<Integer> nodetypes = new ArrayList<>(); // node type of each MapNode
            String risk_toString = new String();
            ArrayList<String> ttcnames = new ArrayList<>(); // stop name of each MapNode
            for (int i = 1; i<node_list.size(); i++) {
                ArrayList<Double> al1 = new ArrayList<>();
                ArrayList<Double> al2 = new ArrayList<>();
                MapNode first = node_list.get(i-1);
                MapNode second = node_list.get(i);
                Double middle_lon = (first.longitude+second.longitude)/2+count/80000;
                Double middle_lat = (first.latitude+second.latitude)/2+count/80000;
                Double longitude = node_list.get(i-1).longitude+count/80000;
                Double latitude = node_list.get(i-1).latitude+count/80000;
                int nt1 = node_list.get(i-1).nodetype;
                int nt2 = node_list.get(i).nodetype;
                String sn1 = node_list.get(i-1).ttcName;
                String sn2 = node_list.get(i).ttcName;

                al1.add(longitude);
                al1.add(latitude);
                al2.add(middle_lon);
                al2.add(middle_lat);
                mn.add(al1);
                mn.add(al2);
                nodetypes.add(nt1);
                nodetypes.add(nt2);
                ttcnames.add(sn1);
                ttcnames.add(sn2);
            }
            Double cost = p.getTotalLength();
            Double time = Precision.round(p.getTotalTime(),0);
            Double distance = Precision.round(p.getTotalLength()/1000,2);
            path_map.put("cost", new Gson().toJson(cost));
            path_map.put("routeNode", new Gson().toJson(mn));
            path_map.put("nodetype", new Gson().toJson(nodetypes));
            path_map.put("ttcname",new Gson().toJson(ttcnames));
            path_map.put("time", new Gson().toJson(time));
            path_map.put("description", p.getDescription());
            path_map.put("distance", new Gson().toJson(distance));
            count++;
            solution.add(path_map); //[cost, routeNode, nodetype, ttcname, time, description, distance]
        }
        return solution;
    }

    /** Merge walking and public transit result lists
     * @return
     * */
    public static String Merge2ResultLists(ArrayList<Path> walking_list, ArrayList<Path> ttc_list){
        ArrayList<ArrayList<HashMap>> solution = new ArrayList<>();
        solution.add(KSPtoJson_AL(walking_list));
        solution.add(KSPtoJsonTTC_AL(ttc_list));
        String solution_to_string = new Gson().toJson(solution);
        return solution_to_string;
    }

    public static ArrayList KSPToStrings(ArrayList <Path> temp,String cost_function){
        ArrayList solution = new ArrayList<>();
        int i = 0;
        for (Path p : temp) {
//            HashMap<Integer, ArrayList<String>> path_map = new HashMap<>();

//            if (i == 2){
//                break;
//            }  //only 2 routes per cost funcion
            ArrayList<String> return_value = new ArrayList<>();
            List<MapNode> node_list = p.getNodes();
            String mn_toString = new String();
            for (MapNode mn : node_list) {
                Double longitude = mn.longitude;
                Double latitude = mn.latitude;
                mn_toString += ('[' + longitude.toString() + ',' + latitude.toString() + ']' + ',');
            }
            Double cost = p.getTotalLength();
            Double time = p.getTotalTime();
            return_value.add(cost.toString());
            return_value.add(mn_toString.substring(0, mn_toString.length() - 1));
            return_value.add(time.toString());
            return_value.add(p.getDescription());
            return_value.add(cost_function);
//            path_map.put(count, return_value);
            solution.add(return_value); //[cost, nodelist, totaltime, description,cost_func]
            i = i++;
        }
        return solution;
    }

    public static String KSPSToJson(ArrayList <Path> distance, ArrayList<Path> covid){
            ArrayList sol1 = KSPToStrings(distance, "distance");
            ArrayList sol2 = KSPToStrings(covid, "covid");
            sol1.addAll(sol2);
            String sol = new Gson().toJson(sol1);

            return sol;

    }


    /** Find k shortest routes that satisfy user's input for "max time spent detouring route"*/
    public static ArrayList<Path> detour_ksp(Graph graph, MapNode src, MapNode dest, String costFunction, int K, double detour_time) {
        double detour_distance = detour_time/60*5000;
        ArrayList<Path> A = new ArrayList<>();

        // Initialize a set to store potential kth shortest path
        ArrayList<Path> B = new ArrayList<>();

        // Find shortest path from src to sink
        Planner planner = new Planner();
        Path shortestpath = planner.plan(graph, src, dest, costFunction);
        A.add(shortestpath);
        if (detour_time == -1.0){ // Case 1: user choose "No Time Limit"
//            System.out.println("I am in the case of -1.0");
            for (int k = 1; k < K; k++) {
                // The spur node ranges from the first node to the next to last node in the previous k-shortest path.
                Path previousPath = A.get(k - 1);
                for (int i = 0; i < previousPath.size() - 2; i++) {
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
            }
        }else{
        Path pure_distance_shortestpath = planner.plan(graph, src, dest, "distance");
        if (shortestpath.getTotalLength()>=(pure_distance_shortestpath.getTotalLength()+detour_distance))
            System.out.println("Can't find any lower risk routes within detour time limit, here are lower risk routes with possible minimal detour time:");

        for (int k = 1; k < K; k++) {
            // The spur node ranges from the first node to the next to last node in the previous k-shortest path.
            Path previousPath = A.get(k - 1);
            for (int i = 0; i < previousPath.size() - 2; i++) {
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
                    if ((!B.contains(totalPath))&&(totalPath.getTotalLength()<=pure_distance_shortestpath.getTotalLength()+detour_distance))
                        B.add(totalPath);
                }
            }
            if (B.isEmpty())
                break;
            Collections.sort(B);
            // Add the lowest cost path becomes the k-shortest path
            A.add(B.get(0));
            B.remove(0);
        }
        }
        return A;
    }

    }
