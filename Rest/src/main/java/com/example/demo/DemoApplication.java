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
import java.util.List;


@SpringBootApplication
public class DemoApplication {
	public Graph torontoGraph;
	public MapNode mapNode;
	public Planner planner;

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	public Element getElement(String lon, String lat) {
		Graph torontoGraph = new Graph("./data/toronto.osm");
		NodeList nList = torontoGraph.osmDoc.getElementsByTagName("node");
		Element final_e = null;
		for (int temp = 0; temp < nList.getLength(); temp++) {
			Node node = nList.item(temp);
			if (node.getNodeType() == Node.ELEMENT_NODE) {
				Element eElement = (Element) node;
				String lon_osm = eElement.getAttribute("lon");
				String lat_osm = eElement.getAttribute("lat");
				if (lon_osm.substring(0, 7) == lon || lat_osm.substring(0, 7) == lat) {
					final_e = (Element) eElement;
					break;
				}
			}
		}
		return final_e;
	}

	@RestController
	@CrossOrigin(origins = "http://localhost:4200")
	class nodeController{

		@GetMapping("/api")
		public List<List<MapNode>> getList(@RequestParam(required = false) String longitude, @RequestParam(required = false) String latitude,
								   @RequestParam(required = false) String end_long, @RequestParam(required = false) String end_lat) {

			Element e_startNode = getElement(longitude,latitude);
			Element e_endNode = getElement(end_long, end_lat);

			MapNode m_startNode = new MapNode(e_startNode);
			MapNode m_endNode = new MapNode(e_endNode);

			Planner planner = new Planner(torontoGraph);
			List<List<MapNode>> resultList = planner.runSearches(m_startNode, m_endNode);

			System.out.println(resultList.get(0).size());
			return resultList;

		}
	}

}
