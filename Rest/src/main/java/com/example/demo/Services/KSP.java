package com.example.demo.Services;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.*;

import org.apache.commons.math3.util.Precision;


public class KSP {


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
        ArrayList<Integer> weight = new ArrayList<>(Arrays.asList(0, 1, 10, 50, 100, 500, 1000, 2000,4000));
        boolean add = true;
        for (int i : weight){
            //double riskWeight = i/(double)K;
            double riskWeight = i;
            double distWeight = 1;
            //System.out.println("distWeight:"+String.valueOf(distWeight));
            //System.out.println("riskWeight:"+String.valueOf(riskWeight));
            Path temp = new Path();
            if (graph.avoidHospital==true) { // Case 1: 躲避医院
                temp = planner.AStar_avoidHospital_walking(graph, src, dest, costFunction, riskWeight, distWeight);
                temp.weight = i;
            }else if (graph.avoidHospital==false){ // Case 2: 不躲避医院
                temp = planner.AStar_walking(graph, src, dest, costFunction, riskWeight, distWeight);
                temp.weight = i;
            }
            if(result.isEmpty()){
                result.add(temp);
                result_dist.add(temp.getTotalLength());
            }else if(!result_dist.contains(temp.getTotalLength())){
                for( Path prev_path: result){
                    if(Math.abs(prev_path.totalTime - temp.totalTime) < 1.5){
                        add = false;
                    }
                }
                if(add){
                    result.add(temp);
                    result_dist.add(temp.getTotalLength());
                }
                add = true;
            }
        }
//        Path safestp = planner.AStar_avoidHospital(graph, src, dest, costFunction, 1, 0);
//        if(!result_dist.contains(safestp.getTotalLength())){
//            result.add(safestp);
//        }
        return result;
    }

    /** Search K diverse routes --- Public Transit Mode*/
    public static ArrayList<Path> Diverse_K_TTC(Graph graph, MapNode src, MapNode dest, String costFunction, int K){
        ArrayList<Path> result = new ArrayList<>();
        ArrayList<Double> result_dist = new ArrayList<>();
        Planner planner = new Planner();
//        double distWeight = 1;
//        double riskWeight = 0;
        ArrayList<Integer> weight = new ArrayList<>(Arrays.asList(0, 1, 10, 50, 100, 500, 1000, 2000,4000));
        //ArrayList<Integer> weight = new ArrayList<>(Arrays.asList(0));

        //for (int i=0;i<K;i++){
        for (int i : weight){
            //double riskWeight = i/(double)K;
            double riskWeight = i;
            double distWeight = 1;
            //System.out.println("distWeight:"+String.valueOf(distWeight));
            //System.out.println("riskWeight:"+String.valueOf(riskWeight));
            boolean add = true;
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
                for( Path prev_path: result){
                    if(Math.abs(prev_path.totalTime - temp.totalTime) < 1.5){
                        add = false;
                    }
                }
                if(add){
                    result.add(temp);
                    result_dist.add(temp.getTotalLength());
                }
                add = true;
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
            List<MapNode> node_list = p.getNodes();
            ArrayList<ArrayList<Double>> mn = new ArrayList<>();
            ArrayList<Double> risk = new ArrayList<>();
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
                Double risk1 = 0.0;
                String ttcname = "";
//                if(node_list.get(i).nodetype == 5){
//                    risk1 += node_list.get(i).pedCount;
//                }
//                else{
//                    risk1 += (node_list.get(i).passengerCount * 5.52);
//                    ttcname += node_list.get(i).ttcName;
//                }
                Double walk_risk1 = first.pedCount; // pedCount of node 0
                Double walk_risk1andhalf = (first.pedCount+second.pedCount)/2; // pedCount of node 0.5 -> avg of node 0 & 1

                al1.add(longitude);
                al1.add(latitude);
                al2.add(middle_lon);
                al2.add(middle_lat);
                mn.add(al1);
                mn.add(al2);
                risk.add(walk_risk1);
                risk.add(walk_risk1andhalf);
                ttcnames.add(ttcname);
                //risk.add(risk2);
            }
            Double cost = p.getTotalLength();
            Double time = Precision.round(p.getTotalTime(),0);
            Double distance = Precision.round(p.getTotalLength()/1000,2);
            path_map.put("ttcname", new Gson().toJson(ttcnames));
            path_map.put("cost", new Gson().toJson(cost));
            path_map.put("routeNode", new Gson().toJson(mn));
            path_map.put("risk", new Gson().toJson(risk));
            path_map.put("time", new Gson().toJson(time));
            path_map.put("description", p.getDescription());
            path_map.put("distance", new Gson().toJson(distance));
            path_map.put("walkingtime", new Gson().toJson(time));
            path_map.put("ttctime", new Gson().toJson(0));
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
            if(p.pathtype != 5){
//                System.out.println(p.pathtype);
                HashMap<String, String> path_map = new HashMap<>();
                List<MapNode> node_list = p.getNodes();
                ArrayList<ArrayList<Double>> mn = new ArrayList<>();
                ArrayList<Double> risks = new ArrayList<>();
                ArrayList<Integer> nodetypes = new ArrayList<>(); // node type of each MapNode
                ArrayList<String> ttclineNumbers = new ArrayList<>(); // list contains bus/streetcar/subway line number of each ttc segments in each Path
                ArrayList<Integer> numberStops = new ArrayList<>(); // list contains # of stops in each ttc segment in each Path
                ArrayList<String> startStops = new ArrayList<>(); // list contains startStop name of each ttc segments in each Path
                ArrayList<String> endStops = new ArrayList<>(); // list contains endStop name of each ttc segments in each Path
                boolean isTTC = false;
                int number_of_stops = 0;

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
                    Double risk1 = first.pedCount;
                    Double risk2 = (first.pedCount+second.pedCount)/2;

                    if(node_list.get(i).nodetype == 5){
                        risk1 += node_list.get(i).pedCount;
                    }
                    else{
                        risk1 += (node_list.get(i).passengerCount * 5.52);
                    }

                    if (first.nodetype !=5 & ! ttclineNumbers.contains(first.ttcName)){
                        ttclineNumbers.add(first.ttcName);
                    }
                    if (first.nodetype==5 & second.nodetype!=5){
                        startStops.add(second.stopName);
                        isTTC = true;
                        risk2 = first.pedCount;
                    }
                    if (first.nodetype != 5 & second.nodetype == 5){
                        endStops.add(first.stopName);
                        isTTC = false;
                        numberStops.add(number_of_stops);
                        number_of_stops = 0;
                        risk2 = second.pedCount;
                    }
                    if (isTTC){
                        number_of_stops ++;
                    }
                    if (first.nodetype !=5 & second.nodetype!=5){
                        risk1 = first.occupancyPercent;
                        risk2 = second.occupancyPercent;
                    }

                    al1.add(longitude);
                    al1.add(latitude);
                    al2.add(middle_lon);
                    al2.add(middle_lat);
                    mn.add(al1);
                    mn.add(al2);
                    nodetypes.add(nt1);
                    nodetypes.add(nt2);
                    risks.add(risk1);
                    risks.add(risk2);

                }
                Double cost = p.getTotalLength();
                Double time = Precision.round(p.getTotalTime(),0);
                Double distance = Precision.round(p.getTotalLength()/1000,2);
                Double walkingtime = Precision.round(p.walkingTime,0); //Total walking time of route under public transit mode
                Double ttctime = Precision.round(p.ttcTime,0); //Total time on public transit
                //Set<String> set = new HashSet<String>(ttcnames);
                path_map.put("cost", new Gson().toJson(cost));
                path_map.put("routeNode", new Gson().toJson(mn));
                path_map.put("nodetype", new Gson().toJson(nodetypes));
//                path_map.put("ttcname",new Gson().toJson(ttclinenumber));
//                path_map.put("nstop", new Gson().toJson(p.numberStop));
                path_map.put("ttcname",new Gson().toJson(ttclineNumbers));
                path_map.put("nstop", new Gson().toJson(numberStops));

                path_map.put("time", new Gson().toJson(time));
                path_map.put("description", p.getDescription());
                path_map.put("distance", new Gson().toJson(distance));
                path_map.put("risk", new Gson().toJson(risks));
                path_map.put("walkingtime", new Gson().toJson(walkingtime));
                path_map.put("ttctime", new Gson().toJson(ttctime));
//                path_map.put("startstop", new Gson().toJson(p.startStop));
//                path_map.put("endstop", new Gson().toJson(p.endStop));
                path_map.put("startstop", new Gson().toJson(startStops));
                path_map.put("endstop", new Gson().toJson(endStops));

                count++;
                solution.add(path_map); //[cost, routeNode, nodetype, ttcname, time, description, distance]
            }

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
            List<MapNode> node_list = p.getNodes();
            ArrayList<ArrayList<Double>> mn = new ArrayList<>();
            ArrayList<Double> risk = new ArrayList<>();
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
                Double risk1 = 0.0;
                String ttcname = "";
                Double walk_risk1 = first.pedCount; // pedCount of node 0
                Double walk_risk1andhalf = (first.pedCount+second.pedCount)/2; // pedCount of node 0.5 -> avg of node 0 & 1

                al1.add(longitude);
                al1.add(latitude);
                al2.add(middle_lon);
                al2.add(middle_lat);
                mn.add(al1);
                mn.add(al2);
                risk.add(walk_risk1);
                risk.add(walk_risk1andhalf);
                ttcnames.add(ttcname);
            }
            Double cost = p.getTotalLength();
            Double time = Precision.round(p.getTotalTime(),0);
            Double distance = Precision.round(p.getTotalLength()/1000,2);
            Double sumwalkpedcount = p.totalPedCount;
            Double totalwalkpedcount = p.totalPedCount/ distance;
            Double avgpedcountpernode = p.totalPedCount/p.nodes.size();
            Double avgnodepedcountKM = p.totalPedCount/p.nodes.size()*distance;
            Double avgnodepedcountM = p.totalPedCount/p.nodes.size()*p.totalLength;

            System.out.println("\nsumWPC:"+sumwalkpedcount);
            System.out.println("\ntotalWPC/distKM:"+totalwalkpedcount);
            System.out.println("totalnodes:"+p.nodes.size());
            System.out.println("avgWPC:"+avgpedcountpernode);
            System.out.println("avg*KM:"+avgnodepedcountKM);
            System.out.println("avg*M"+avgnodepedcountM);

            path_map.put("ttcname", new Gson().toJson(ttcnames));
            path_map.put("cost", new Gson().toJson(cost));
            path_map.put("routeNode", new Gson().toJson(mn));
            path_map.put("risk", new Gson().toJson(risk));
            path_map.put("time", new Gson().toJson(time)); // totaltime of route
            path_map.put("description", p.getDescription());
            path_map.put("distance", new Gson().toJson(distance));
            path_map.put("walkingtime", new Gson().toJson(time)); // total walking time = total time for walking mode
            path_map.put("ttctime", new Gson().toJson(0));
//            path_map.put("totalwalkpedcount",new Gson().toJson(totalwalkpedcount));
//            path_map.put("avgpedcountpernode", new Gson().toJson(avgpedcountpernode));
//            path_map.put("avgnodepedcountKM", new Gson().toJson(avgnodepedcountKM));
//            path_map.put("avgnodepedcountM", new Gson().toJson(avgnodepedcountM));

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
            if(p.pathtype != 5){
//                System.out.println(p.pathtype);
                HashMap<String, String> path_map = new HashMap<>();
                List<MapNode> node_list = p.getNodes();
                ArrayList<ArrayList<Double>> mn = new ArrayList<>();
                ArrayList<Double> risks = new ArrayList<>();
                ArrayList<Integer> nodetypes = new ArrayList<>(); // node type of each MapNode
                ArrayList<String> ttclineNumbers = new ArrayList<>(); // list contains bus/streetcar/subway line number of each ttc segments in each Path
                ArrayList<Integer> numberStops = new ArrayList<>(); // list contains # of stops in each ttc segment in each Path
                ArrayList<String> startStops = new ArrayList<>(); // list contains startStop name of each ttc segments in each Path
                ArrayList<String> endStops = new ArrayList<>(); // list contains endStop name of each ttc segments in each Path
                boolean isTTC = false;
                int number_of_stops = 0;
                for (int i = 1; i<node_list.size(); i++) {
                    ArrayList<Double> al1 = new ArrayList<>();
                    ArrayList<Double> al2 = new ArrayList<>();
                    MapNode first = node_list.get(i-1);
                    MapNode second = node_list.get(i);
                    Double middle_lon = (first.longitude+second.longitude)/2+count/80000;
                    Double middle_lat = (first.latitude+second.latitude)/2+count/80000;
                    Double longitude = node_list.get(i-1).longitude+count/80000;
                    Double latitude = node_list.get(i-1).latitude+count/80000;
                    int nt1 = first.nodetype;
                    int nt2 = second.nodetype;

                    Double risk1 = first.pedCount;
                    Double risk2 = (first.pedCount+second.pedCount)/2;

                    if (first.nodetype !=5 & !ttclineNumbers.contains(first.ttcName)){
                        ttclineNumbers.add(first.ttcName);
                    }
                    if (first.nodetype==5 & second.nodetype!=5){
                        startStops.add(second.stopName);
                        isTTC = true;
                        nt2 = first.nodetype;
                        risk2 = first.pedCount;
                    }
                    if (first.nodetype != 5 & second.nodetype == 5){
                        endStops.add(first.stopName);
                        isTTC = false;
                        numberStops.add(number_of_stops);
                        number_of_stops = 0;
                        nt2 = second.nodetype;
                        risk2 = first.occupancyPercent;
                    }
                    if (isTTC){
                        number_of_stops ++;
                    }
                    if (first.nodetype !=5 & nt2!=5){
                        risk1 = first.occupancyPercent;
                        risk2 = second.occupancyPercent;
                    }


                    al1.add(longitude);
                    al1.add(latitude);
                    al2.add(middle_lon);
                    al2.add(middle_lat);
                    mn.add(al1);
                    mn.add(al2);
                    nodetypes.add(nt1);
                    nodetypes.add(nt2);
                    risks.add(risk1);
                    risks.add(risk2);

                }
                Double cost = p.getTotalLength();
                Double time = Precision.round(p.getTotalTime(),0);
                Double distance = Precision.round(p.getTotalLength()/1000,2);
                Double walkingtime = Precision.round(p.walkingTime,0); //Total walking time of route under public transit mode
                Double ttctime = Precision.round(p.ttcTime,0); //Total time on public transit

                Double sumwalkpedcount = p.totalPedCount;
                Double totalwalkpedcount = p.totalPedCount/ distance;
                Double avgoccupancypercent = p.avgOccupancyPercent;

                System.out.println("\nTTC begin");
                System.out.println("sumWPC: "+sumwalkpedcount);
                System.out.println("totalWPC/dist: "+totalwalkpedcount);
                System.out.println("avgOP: "+avgoccupancypercent);
                System.out.println("ttctime: "+ttctime);
                System.out.println("\nTTC ends");

                path_map.put("cost", new Gson().toJson(cost));
                path_map.put("routeNode", new Gson().toJson(mn));
                path_map.put("nodetype", new Gson().toJson(nodetypes));
                path_map.put("ttcname",new Gson().toJson(ttclineNumbers));
                path_map.put("nstop", new Gson().toJson(numberStops));
                path_map.put("time", new Gson().toJson(time));
                path_map.put("description", p.getDescription());
                path_map.put("distance", new Gson().toJson(distance));
                path_map.put("risk", new Gson().toJson(risks));
                path_map.put("walkingtime", new Gson().toJson(walkingtime));
                path_map.put("ttctime", new Gson().toJson(ttctime));
                path_map.put("startstop", new Gson().toJson(startStops));
                path_map.put("endstop", new Gson().toJson(endStops));
//                path_map.put("totalwalkpedcount",new Gson().toJson(totalwalkpedcount));
//                path_map.put("avgoccupancypercent",new Gson().toJson(avgoccupancypercent));


                count++;
                solution.add(path_map); //[cost, routeNode, nodetype, ttcname, time, description, distance]
            }

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
