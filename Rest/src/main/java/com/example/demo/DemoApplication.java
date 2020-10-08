package com.example.demo;


import com.example.demo.Services.*;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;

import static com.example.demo.PSQLConnect.getNeighbourhoodCoordinate;


@SpringBootApplication
public class DemoApplication {
	public Graph torontoGraph;
	public MapNode mapNode;
	public Planner planner;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);

	}

	public MapNode getElement(HashMap<Double, MapNode> nodeMap, String lon, String lat) {


		MapNode res = new MapNode();
		for (Double key : nodeMap.keySet()) {
			if(Double.toString(nodeMap.get(key).latitude).equals(lat) & Double.toString(nodeMap.get(key).longitude).equals(lon)){
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
		public HashMap<Integer, Path> getList(@RequestParam(required = false) String longitude, @RequestParam(required = false) String latitude,
											  @RequestParam(required = false) String end_long, @RequestParam(required = false) String end_lat) {

			torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
			//torontoGraph.loadFiles("./data/toronto.osm", "./data/Cyclists.csv");
			HashMap<Double, MapNode> nodeMap = torontoGraph.routeNodes;

			Planner planner = new Planner();
			//List<List<List<Double>>> resultList = planner.runSearches(getElement(nodeMap, longitude,latitude), getElement(nodeMap, end_long, end_lat));
			HashMap<Integer, Path> resultList = planner.toHashMap(planner.plan(torontoGraph, getElement(nodeMap, longitude,latitude), getElement(nodeMap, end_long, end_lat),"distance"));
			ArrayList<Path> kspresultList = KSP.ksp(torontoGraph, getElement(nodeMap, longitude,latitude), getElement(nodeMap, end_long, end_lat),"distance", 4);
			if(! resultList.isEmpty()){
				System.out.println("11111111");
			}
			System.out.println("resultList: "+ resultList);
			return resultList;
		}

			@GetMapping("/heatmap")
			public HashMap<Integer, String> getNeighbourCoord(){
				HashMap results = new HashMap<Integer, String>();
				results = getNeighbourhoodCoordinate();
				return results;
			}
	}
}