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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

	public static HashMap<Double, MapNode> deserialize() {
		HashMap<Double, MapNode> nodeMap = new HashMap<>();
		ObjectInputStream objectinputstream = null;
        try
        {
            FileInputStream fis = new FileInputStream("./data/hashmap.ser");
            ObjectInputStream ois = new ObjectInputStream(fis);
            nodeMap = (HashMap) ois.readObject();
            ois.close();
            fis.close();
        }catch(IOException ioe)
        {
            ioe.printStackTrace();
        }catch(ClassNotFoundException c)
        {
            System.out.println("Class not found");
            c.printStackTrace();
        }
		return nodeMap;
	}

	public MapNode getElement(String lon, String lat) {

		HashMap<Double, MapNode> nodeMap = deserialize();
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
//			HashMap<Double, MapNode> nodeMap = torontoGraph.routeNodes;

			Planner planner = new Planner(torontoGraph);
			HashMap<Integer, String> resultList = planner.toHashMap(planner.runSearches(getElement(longitude,latitude), getElement(end_long, end_lat)));

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

		@GetMapping("/heatmap2")
		public HashMap<String, Double> getPedCount(String start_time,String end_time){
			HashMap results = new HashMap<String, Double>();
			results = getPedCountHeatmap(start_time,end_time);
			return results;
		}
	}


}
