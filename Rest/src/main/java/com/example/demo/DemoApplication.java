package com.example.demo;


import com.example.demo.Services.*;
import com.fasterxml.jackson.databind.util.BeanUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Collections;

import static com.example.demo.PSQLConnect.getNeighbourhoodCoordinate;
import static com.example.demo.PSQLConnect.getPedCountHeatmap;
import static com.example.demo.Services.Graph.getDistance;


@SpringBootApplication
public class DemoApplication {
	public Graph torontoGraph;
	public HashMap<Double, MapNode> nodeMap;
	public MapNode mapNode;
	public Planner planner;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

	}

	public MapNode getElement(HashMap<Double, MapNode> nodeMap, String bound) {
		MapNode res = new MapNode();
		double[] focus = new double[]{(-79.4054900 + -79.3886400) / 2, (43.6613600 + 43.6687500) / 2};
		double MPERLAT = 111320;
		double MPERLON = Math.cos(focus[1] * 3.1415 / 180) * MPERLAT;
		double dist = 100000;
		// 43.668459,43.6698816,-79.3891804,-79.3876308
		ArrayList<String> l = new ArrayList<>(Arrays.asList(bound.split(",")));

		for (Double key : nodeMap.keySet()) {
			if((nodeMap.get(key).latitude >= Double.parseDouble(l.get(0))) &
					(nodeMap.get(key).latitude <= Double.parseDouble(l.get(1))) &
					(nodeMap.get(key).longitude >= Double.parseDouble(l.get(2))) &
					(nodeMap.get(key).longitude <= Double.parseDouble(l.get(3)))) {
				res = nodeMap.get(key);
				break;
			}
		}
		if(res.id == -1){
			for (Double key : nodeMap.keySet()) {
				double dx = (nodeMap.get(key).longitude - Double.parseDouble(l.get(2))) * MPERLON;
				double dy = (nodeMap.get(key).latitude - Double.parseDouble(l.get(0))) * MPERLAT;
				double tempdist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
				if (tempdist < dist) {
					dist = tempdist;
					res = nodeMap.get(key);
				}
			}
		}
		return res;
	}

	@RestController
	@CrossOrigin(origins = "http://localhost:4200")
	class nodeController{

		@GetMapping("/api")
		public String getList(@RequestParam(required = false) String bound_start, @RequestParam(required = false) String bound_end) {

			// Get start and end node of this tour (Address)
			MapNode startNode = getElement(nodeMap, bound_start);
			MapNode endNode = getElement(nodeMap, bound_end);
			// Using lat long
//			MapNode startNode = getElement(nodeMap, bound_start);
//			MapNode endNode = getElement(nodeMap, bound_end);

			torontoGraph.prepareNormalization(endNode);
//			ArrayList<Double> edgeLength_list = new ArrayList<Double>(); // list of length of all edges
//			ArrayList<Double> euclid_list = new ArrayList<Double>(); // list of euclid distance for each node to end node
//			ArrayList<Double> pedCount_list = new ArrayList<Double>(); // list of ped count for each node
//
//			for (MapNode n : nodeMap.values()) {
//				pedCount_list.add(n.getPedCount());
//				euclid_list.add(getDistance(n, endNode));
//				for (MapEdge e : n.getEdges()){
//					edgeLength_list.add(e.getLength("distance"));
//				}
////				System.out.println("lon: " + n.longitude + " lat: " + n.latitude);
//			}
//			// Find max and min vals in 3 lists above for normalization purpose
//			double max_length = Collections.max(edgeLength_list);
//			double min_length = Collections.min(edgeLength_list);
//			double max_euclid = Collections.max(euclid_list);
//			double min_euclid = Collections.min(euclid_list);
//			double max_pedCont = Collections.max(pedCount_list);
//			double min_pedCount = Collections.min(pedCount_list);

			Planner planner = new Planner();
			//List<List<List<Double>>> resultList = planner.runSearches(getElement(nodeMap, longitude,latitude), getElement(nodeMap, end_long, end_lat));
//			HashMap<Integer, Path> resultList = planner.toHashMap(planner.plan(torontoGraph, getElement(nodeMap, longitude,latitude), getElement(nodeMap, end_long, end_lat),"distance"));
//			ArrayList<Path> distancekspresultList = KSP.ksp(torontoGraph, startNode, endNode,"distance", 3);
//			System.out.println("distance ksp completed!");
//			ArrayList<Path> covidkspresultList = KSP.ksp(torontoGraph, startNode, endNode,"covid", 3);
//			System.out.println("covid ksp completed!");

			// Test detour_ksp function
			Path pure_distance_shortestpath = planner.plan(torontoGraph, startNode, endNode,"distance");
			Path covid_shortestpath = planner.plan(torontoGraph, startNode, endNode,"covid");
			ArrayList<Path> detourcovidresultList_001 = KSP.detour_ksp(torontoGraph, startNode, endNode,"covid", 3,0.01);
			ArrayList<Path> detourcovidresultList_5 = KSP.detour_ksp(torontoGraph, startNode, endNode,"covid", 3,5);
			ArrayList<Path> detourcovidresultList_10 = KSP.detour_ksp(torontoGraph, startNode, endNode,"covid", 3,10);
			System.out.println("covid detour ksp completed!");
			// Example usage of detour_ksp with 5 mins detour time
			ArrayList<Path> detourcovidresultList = KSP.detour_ksp(torontoGraph, startNode, endNode,"covid", 3,5);


//			for (Path p:covidkspresultList){
//				System.out.println("Time: " + p.getTotalTime());
//			}
			String result = KSP.KSPtoJson(detourcovidresultList);
			return result;
		}

		@GetMapping("/heatmap")
		public HashMap<Integer, String> getNeighbourCoord(){
			HashMap results = new HashMap<Integer, String>();
			results = getNeighbourhoodCoordinate();
			return results;
		}

		@GetMapping("/heatmap2")
		public HashMap<String, Double> getPedCount(@RequestParam(required = false) String start_time,@RequestParam(required = false) String end_time){
			HashMap results = new HashMap<String, Double>();
			results = getPedCountHeatmap(start_time,end_time);
			return results;
		}

		@PostMapping("/questionnaire")
		public userPreference postPref(@RequestBody userPreference pref){
			userPreference returnValue = new userPreference();
			returnValue.setQ1(pref.getQ1());
			returnValue.setQ2(pref.getQ2());
			returnValue.setQ3(pref.getQ3());
			return returnValue;
		}

		@GetMapping("/init")
		public String initTorontoGraph(@RequestParam String init_num){
			System.out.println("initializing graph");
			torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
			nodeMap = torontoGraph.routeNodes;
			return ("TorontoGraph Loaded");
		}

	}
}
