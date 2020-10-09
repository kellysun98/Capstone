package com.example.demo;


import com.example.demo.Services.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import static com.example.demo.PSQLConnect.getNeighbourhoodCoordinate;
import static com.example.demo.PSQLConnect.getPedCountHeatmap;


@SpringBootApplication
public class DemoApplication {
	public Graph torontoGraph;
	public MapNode mapNode;
	public Planner planner;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

	}

	public MapNode getElement(HashMap<Double, MapNode> nodeMap, String bound) {
		MapNode res = new MapNode();

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
		return res;
	}

	@RestController
	@CrossOrigin(origins = "http://localhost:4200")
	class nodeController{

		@GetMapping("/api")
		public String getList(@RequestParam(required = false) String bound_start, @RequestParam(required = false) String bound_end) {

			torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
			//torontoGraph.loadFiles("./data/toronto.osm", "./data/Cyclists.csv");
			HashMap<Double, MapNode> nodeMap = torontoGraph.routeNodes;

			for (MapNode n : nodeMap.values()) {
				System.out.println("lon: " + n.longitude + " lat: " + n.latitude);
			}


			Planner planner = new Planner();
			//List<List<List<Double>>> resultList = planner.runSearches(getElement(nodeMap, longitude,latitude), getElement(nodeMap, end_long, end_lat));
//			HashMap<Integer, Path> resultList = planner.toHashMap(planner.plan(torontoGraph, getElement(nodeMap, longitude,latitude), getElement(nodeMap, end_long, end_lat),"distance"));

			ArrayList<Path> kspresultList = KSP.ksp(torontoGraph, getElement(nodeMap, bound_start), getElement(nodeMap, bound_end),"distance", 9);
			String resultList = KSP.KSPtoJson(kspresultList);
			return resultList;
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
		
	}
}
