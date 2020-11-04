package com.example.demo;


import com.example.demo.Services.*;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.google.gson.Gson;
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
	public userPreference userPref;
	public Address add = null;
	public String result = new String();
	public String startCheck = new String();
	public String endCheck = new String();

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
		public String getList() {
			if (result.isEmpty() || (!add.getStart_bound().equals(startCheck) || !add.getEnd_bound().equals(endCheck))) {
				// Get start and end node of this tour (Address)
//			System.out.println("start bound: "+ add.getStart_bound());
//			System.out.println("end bound: "+add.getEnd_bound());

				MapNode startNode = getElement(nodeMap, add.getStart_bound());
				MapNode endNode = getElement(nodeMap, add.getEnd_bound());
				// Prepare for normalization for "covid" heuristic
				torontoGraph.prepareNormalization(endNode);

				Planner planner = new Planner();
				ArrayList<Path> resultList = new ArrayList<Path>();
				resultList = KSP.Diverse_K(torontoGraph, startNode, endNode, "distance", 10);
//				int temp = 0;
				/**
				if (userPref != null) { // Case 1: user填写了questionnaire
					double timeLimit = userPref.getTimefromQ2();

					if (userPref.getQ3().contains("I don't have a specific concern")) { // Case 1.1: user不care covid risk, 直接叫ksp with distance
						long timeStart = System.currentTimeMillis();
						resultList = KSP.ksp(torontoGraph, startNode, endNode, "distance", 10);
						long timeFinish = System.currentTimeMillis();
						System.out.println("Search took " + (timeFinish - timeStart) / 1000.0 + " seconds.");

					} else { // Case 1.2: user在q3 check off了一些东西，证明他care about covid risk, 同时user在q2选择了: 1)具体detour time limit 或者 2)他没选detour time limit then we default set timeLimit = -1
						long timeStart = System.currentTimeMillis();
						resultList = KSP.detour_ksp(torontoGraph, startNode, endNode, "covid", 10, timeLimit);
						long timeFinish = System.currentTimeMillis();
						System.out.println("Search took " + (timeFinish - timeStart) / 1000.0 + " seconds.");
					}

				} else { // Case 2: user skip了questionnaire，默认为他only care about distance, 给他三条距离最短的路线
					resultList = KSP.ksp(torontoGraph, startNode, endNode, "distance", 10);
				} */
				result = KSP.KSPtoJson(resultList);
				startCheck = add.getStart_bound();
				endCheck = add.getEnd_bound();
				return result;
			} else{
				return result;
			}
		}

		@GetMapping("/api2")
		public String gettwoList(@RequestParam(required = false) String bound_start, @RequestParam(required = false) String bound_end) {

			// Get start and end node of this tour (Address)
			MapNode startNode = getElement(nodeMap, bound_start);
			MapNode endNode = getElement(nodeMap, bound_end);
			// Prepare for normalization for "covid" heuristic
			torontoGraph.prepareNormalization(endNode);

			Planner planner = new Planner();
			ArrayList<Path> resultList_distance = new ArrayList<Path>();
			ArrayList<Path> resultList_covid = new ArrayList<Path>();


			if (userPref!=null){ // Case 1: user填写了questionnaire
				double timeLimit = userPref.getTimefromQ2();
				resultList_distance = KSP.ksp(torontoGraph, startNode, endNode,"distance", 2);

				resultList_covid = KSP.ksp(torontoGraph, startNode, endNode, "covid", 2);

			}else{ // Case 2: user skip了questionnaire，默认为他only care about distance, 给他三条距离最短的路线
				resultList_distance = KSP.ksp(torontoGraph, startNode, endNode,"distance", 2);
				resultList_covid = KSP.ksp(torontoGraph, startNode, endNode, "covid", 2);

			}
			String result = KSP.KSPSToJson(resultList_distance,resultList_covid);
			return result;
		}


		@GetMapping("/demo1")
		public String getDemoList1(@RequestParam(required = false) String bound_start, @RequestParam(required = false) String bound_end){
			MapNode startNode = getElement(nodeMap, bound_start);
			MapNode endNode = getElement(nodeMap, bound_end);
			// Prepare for normalization for "covid" heuristic
			torontoGraph.prepareNormalization(endNode);

			Planner planner = new Planner();
			ArrayList<Path> resultList1 = new ArrayList<Path>();

			resultList1=KSP.ksp(torontoGraph, startNode, endNode,"distance", 2);

			return KSP.KSPtoJson(resultList1);


		}
		@GetMapping("/demo2")
		public String getDemoList2(@RequestParam(required = false) String bound_start, @RequestParam(required = false) String bound_end){
			MapNode startNode = getElement(nodeMap, bound_start);
			MapNode endNode = getElement(nodeMap, bound_end);
			// Prepare for normalization for "covid" heuristic
			torontoGraph.prepareNormalization(endNode);

			Planner planner = new Planner();
			ArrayList<Path> resultList2 = new ArrayList<Path>();

			resultList2=KSP.ksp(torontoGraph, startNode,endNode,"covid",2);

			return KSP.KSPtoJson(resultList2);


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
			userPref = new userPreference(pref.getQ1(), pref.getQ2(), pref.getQ3());
//			userPref.setQ1(pref.getQ1());
//			userPref.setQ2(pref.getQ2());
//			userPref.setQ3(pref.getQ3());
			System.out.println("finished fetching user questionnaire answers");
			return userPref;
		}

		@PostMapping("/address")
		public Address postAdd(@RequestBody Address address){
			System.out.println("Initializing...");
			System.out.println(address.getStart_bound());
			System.out.println(address.getEnd_bound());

			add = new Address(address.getStart_bound(), address.getEnd_bound());

			System.out.println("Finishing...");
			System.out.println(add.getStart_bound());
			System.out.println(add.getEnd_bound());
			return add;
		}

		@GetMapping("/init")
		public void initTorontoGraph(@RequestParam String init_num){
			System.out.println("initializing graph");
			//torontoGraph = new Graph();
			torontoGraph = new Graph("./data/DT.osm", "./data/Cyclists.csv");
			nodeMap = torontoGraph.routeNodes;
			System.out.println("complete");
//			return ("TorontoGraph Loaded");
		}

	}
}
