package com.example.demo;


import com.example.demo.Services.Graph;
import com.example.demo.Services.MapNode;
import com.example.demo.Services.Planner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
		public HashMap<Integer, String> getList(@RequestParam(required = false) String longitude, @RequestParam(required = false) String latitude,
								   @RequestParam(required = false) String end_long, @RequestParam(required = false) String end_lat) {
//		public List<Double> getList(@RequestParam(required = false) String longitude, @RequestParam(required = false) String latitude,
//												@RequestParam(required = false) String end_long, @RequestParam(required = false) String end_lat) {
			torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
			torontoGraph.loadFiles("./data/toronto.osm", "./data/Cyclists.csv");
			HashMap<Double, MapNode> nodeMap = torontoGraph.routeNodes;

			Planner planner = new Planner(torontoGraph);
			HashMap<Integer, String> resultList = planner.toHashMap(planner.runSearches(getElement(nodeMap, longitude,latitude), getElement(nodeMap, end_long, end_lat)));

			if (! resultList.isEmpty()){
				System.out.println("11111111");
			}
			for (Integer key: resultList.keySet()){
				System.out.println(resultList.get(key));
			}

//			return resultList.get(0).get(0);
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
