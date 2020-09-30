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

import static com.example.demo.PSQLConnect.getNeighbourhoodCoordinate;


@SpringBootApplication
public class DemoApplication {
	public Graph torontoGraph;
	public MapNode mapNode;
	public Planner planner;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	public Element getElement(Double lon, Double lat) {
		torontoGraph = new Graph("./data/toronto.osm", "./data/Cyclists.csv");
		torontoGraph.loadFiles("./data/toronto.osm", "./data/Cyclists.csv");
		NodeList nList = torontoGraph.osmDoc.getElementsByTagName("node");
		Element final_e = null;

		for (int temp = 0; temp < 5; temp++) {
			Node node = nList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				Double lon_osm = Double.parseDouble(eElement.getAttribute("lon"));
				Double lat_osm = Double.parseDouble(eElement.getAttribute("lat"));

//				System.out.println("osm longtiude found is: " + lon_osm);
//				System.out.println("input longitude is: " + lon.substring(0,8));
				//System.out.println("is equal: " + lon_osm == lon);

				if (lon_osm == lon || lat_osm == lat) {
					System.out.println("im in!");
					final_e = (Element) eElement;
					break;
				}
				/**else{
					System.out.println("im not in!");
				}**/
			}
		}
		return final_e;
	}

	@RestController
	@CrossOrigin(origins = "http://localhost:4200")
	class nodeController{

		@GetMapping("/api")
		public List<List<List<Double>>> getList(@RequestParam(required = false) String longitude, @RequestParam(required = false) String latitude,
								   @RequestParam(required = false) String end_long, @RequestParam(required = false) String end_lat) {

			Element e_startNode = getElement(Double.parseDouble(longitude),Double.parseDouble(latitude));
			Element e_endNode = getElement(Double.parseDouble(end_long), Double.parseDouble(end_lat));

			MapNode m_startNode = new MapNode(e_startNode);
			MapNode m_endNode = new MapNode(e_endNode);

			Planner planner = new Planner(torontoGraph);
			List<List<List<Double>>> resultList = planner.runSearches(m_startNode, m_endNode);

			if(! resultList.isEmpty()){
				System.out.println("11111111");
			}
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
