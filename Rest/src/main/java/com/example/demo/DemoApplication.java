package com.example.demo;


import com.example.demo.Services.*;
import com.fasterxml.jackson.databind.util.BeanUtil;
import com.google.gson.Gson;
import org.springframework.beans.BeanUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static com.example.demo.PSQLConnect.getNeighbourhoodCoordinate;
import static com.example.demo.PSQLConnect.getPedCountHeatmap;
import static com.example.demo.Services.Graph.getDistance;


@SpringBootApplication
public class DemoApplication { //hi
	public Graph torontoGraph;
	public HashMap<Double, MapNode> nodeMap;
	public HashMap<Double, MapNode> ttcnodeMap;
	public MapNode mapNode;
	public Planner planner;
	public userPreference userPref;
	public Address add = null;
//	public String walk_result = new String();
//	public String ttc_result = new String();
	public String result = new String();
	public String startCheck = new String();
	public String endCheck = new String();
	public userPreference old_userPref;
	public boolean streamshutdown = false;
	public ArrayList<Path> ttc_resultList = new ArrayList<Path>();
	public ArrayList<Path> resultList = new ArrayList<Path>();


	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}
	/** getElement for walking
	 * */
//	public MapNode getElement(HashMap<Double, MapNode> ttcnodeMap, String bound) {
//		MapNode res = new MapNode();
//		double[] focus = new double[]{(-79.4054900 + -79.3886400) / 2, (43.6613600 + 43.6687500) / 2};
//		double MPERLAT = 111320;
//		double MPERLON = Math.cos(focus[1] * 3.1415 / 180) * MPERLAT;
//		double dist = 100000;
//		// 43.668459,43.6698816,-79.3891804,-79.3876308
//		ArrayList<String> l = new ArrayList<>(Arrays.asList(bound.split(",")));
//
//		for (Double key : ttcnodeMap.keySet()) {
//			if((ttcnodeMap.get(key).latitude >= Double.parseDouble(l.get(0))) &
//					(ttcnodeMap.get(key).latitude <= Double.parseDouble(l.get(1))) &
//					(ttcnodeMap.get(key).longitude >= Double.parseDouble(l.get(2))) &
//					(ttcnodeMap.get(key).longitude <= Double.parseDouble(l.get(3)))) {
//				res = ttcnodeMap.get(key);
//				break;
//			}
//		}
//		if(res.id == -1){
//			for (Double key : ttcnodeMap.keySet()) {
//				double dx = (ttcnodeMap.get(key).longitude - (Double.parseDouble(l.get(2)) + Double.parseDouble(l.get(3)))/2) * MPERLON;
//				double dy = (ttcnodeMap.get(key).latitude - (Double.parseDouble(l.get(0)) + Double.parseDouble(l.get(1)))/2) * MPERLAT;
//				double tempdist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
//				if (tempdist < dist) {
//					dist = tempdist;
//					res = ttcnodeMap.get(key);
//				}
//			}
//		}
//		return res;
//	}
	public MapNode getElement(HashMap<Double, MapNode> input_hashmap, String bound) {
		MapNode res = new MapNode();
		double[] focus = new double[]{(-79.4054900 + -79.3886400) / 2, (43.6613600 + 43.6687500) / 2};
		double MPERLAT = 111320;
		double MPERLON = Math.cos(focus[1] * 3.1415 / 180) * MPERLAT;
		double dist = 100000;
		// 43.668459,43.6698816,-79.3891804,-79.3876308
		ArrayList<String> l = new ArrayList<>(Arrays.asList(bound.split(",")));

		for (Double key : input_hashmap.keySet()) {
			if((input_hashmap.get(key).latitude >= Double.parseDouble(l.get(0))) &
					(input_hashmap.get(key).latitude <= Double.parseDouble(l.get(1))) &
					(input_hashmap.get(key).longitude >= Double.parseDouble(l.get(2))) &
					(input_hashmap.get(key).longitude <= Double.parseDouble(l.get(3)))) {
				res = input_hashmap.get(key);
				break;
			}
		}
		if(res.id == -1){
			for (Double key : input_hashmap.keySet()) {
				double dx = (input_hashmap.get(key).longitude - (Double.parseDouble(l.get(2)) + Double.parseDouble(l.get(3)))/2) * MPERLON;
				double dy = (input_hashmap.get(key).latitude - (Double.parseDouble(l.get(0)) + Double.parseDouble(l.get(1)))/2) * MPERLAT;
				double tempdist = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
				if (tempdist < dist) {
					dist = tempdist;
					res = input_hashmap.get(key);
				}
			}
		}
		return res;
	}


	@RestController
	@CrossOrigin(origins = "http://localhost:4200")
	class nodeController{

		//get tweets
		@GetMapping("/tweets1")
		public SseEmitter getTweets(){
			twitter twitter = new twitter();
			HashMap<String, String> tweets = new HashMap<String, String>();
			SseEmitter emitter = new SseEmitter();
			ExecutorService executor = Executors.newSingleThreadExecutor();
			try {
				tweets = twitter.streamFeed();
				//Thread.sleep(5000);
				emitter.send(new Gson().toJson(tweets));
			} catch (InterruptedException | IOException e) {
				e.printStackTrace();
			}
			executor.shutdown();
			streamshutdown = true;

			return emitter;
		}

//		public String getTweets() {
//			twitter twitter = new twitter();
//			HashMap<String, String> tweets = new HashMap<String, String>();
//			try {
//				tweets = twitter.streamFeed();
//			} catch (InterruptedException e) {
//				e.printStackTrace();
//			}
//			System.out.println("Tweets complete");
//			return new Gson().toJson(tweets);
//		}

		@GetMapping("/api")
		public String getList() {

			if (userPref == null){
				torontoGraph.avoidHospital=false;
				// Walking mode start&end
				MapNode startNode = getElement(nodeMap, add.getStart_bound());
				MapNode endNode = getElement(nodeMap, add.getEnd_bound());
				// Public transit mode start & end
				MapNode ttcstartNode = getElement(nodeMap, add.getStart_bound());
				MapNode ttcendNode = getElement(nodeMap, add.getEnd_bound());

				// Prepare for normalization for "covid" heuristic
				torontoGraph.prepareNormalization(endNode);

				Planner planner = new Planner();
				// Walking mode find route
//				ArrayList<Path> resultList = new ArrayList<Path>();
				resultList = KSP.Diverse_K(torontoGraph, startNode, endNode, "distance", 10);
				// Public transit mode find route
//				ArrayList<Path> ttc_resultList = new ArrayList<Path>();
				ttc_resultList = KSP.Diverse_K_TTC(torontoGraph, ttcstartNode, ttcendNode, "distance", 10);

//				walk_result = KSP.KSPtoJson(resultList);
//				ttc_result = KSP.KSPtoJsonTTC(ttc_resultList);
				result = KSP.Merge2ResultLists(resultList,ttc_resultList);

				startCheck = add.getStart_bound();
				endCheck = add.getEnd_bound();

			}else if ((userPref != null)|| result.isEmpty() || (!add.getStart_bound().equals(startCheck) || !add.getEnd_bound().equals(endCheck))||(!(old_userPref.equals(userPref)))) {
				old_userPref = new userPreference(userPref);

				// set questionnaire answer(avoid hospital or not)
				if (userPref.getQ3().get(0).contains("hospital")){
					torontoGraph.avoidHospital=true;
				}else{
					torontoGraph.avoidHospital=false;
				}
				// Walking mode start&end
				MapNode startNode = getElement(nodeMap, add.getStart_bound());
				MapNode endNode = getElement(nodeMap, add.getEnd_bound());
				// Public transit mode start & end
				MapNode ttcstartNode = getElement(nodeMap, add.getStart_bound());
				MapNode ttcendNode = getElement(nodeMap, add.getEnd_bound());

				Planner planner = new Planner();
				// Walking mode find route
//				ArrayList<Path> resultList = new ArrayList<Path>();
				resultList = KSP.Diverse_K(torontoGraph, startNode, endNode, "distance", 10);
				// Public transit mode find route
//				ArrayList<Path> ttc_resultList = new ArrayList<Path>();
				ttc_resultList = KSP.Diverse_K_TTC(torontoGraph, ttcstartNode, ttcendNode, "distance", 10);

//				walk_result = KSP.KSPtoJson(resultList);
//				ttc_result = KSP.KSPtoJsonTTC(ttc_resultList);
				result = KSP.Merge2ResultLists(resultList,ttc_resultList);

				startCheck = add.getStart_bound();
				endCheck = add.getEnd_bound();
			}
			return result;
		}

		@GetMapping("/publictransit")
		public String getTransitList() {
			try{
				Thread.sleep(3500);
//				System.out.print("finish sleep");
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return KSP.KSPtoJsonTTC(ttc_resultList);
		}

		@GetMapping("/walking")
		public String getWalkingList() {
			try{
				Thread.sleep(3500);
//				System.out.print("finish sleep");

			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			return KSP.KSPtoJson(resultList);
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
			//results = getNeighbourhoodCoordinate();
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

		@GetMapping("/getTrans")
		public List<String> outputTrans(){
			return userPref.getQ1();
		}

		@PostMapping("/address")
		public Address postAdd(@RequestBody Address address){
//			System.out.println("Initializing...");
//			System.out.println(address.getStart_bound());
//			System.out.println(address.getEnd_bound());

			add = new Address(address.getStart_bound(), address.getEnd_bound());

//			System.out.println("Finishing...");
//			System.out.println(add.getStart_bound());
//			System.out.println(add.getEnd_bound());
			return add;
		}

		@GetMapping("/init")
		public HashMap<String, Double> initTorontoGraph(@RequestParam String init_num){

			System.out.println("initializing graph");
			torontoGraph = new Graph("./data/DT4.osm", "./data/Cyclists.csv");
			torontoGraph.getPedestrianCountDistribution("2020-09-11 00:00:00","2020-09-25 00:00:00", 3);

			// set questionnaire answer(avoid hospital or not)
			if ((userPref != null)&&(userPref.getQ3().get(0).contains("hospital"))){
				torontoGraph.avoidHospital=true;
			}

			nodeMap = torontoGraph.routeNodes;
			ttcnodeMap = torontoGraph.TTCrouteNodes;

			HashMap temp = new HashMap<String, Double>();
			temp = MapNode.MapNodetoHash(nodeMap.values());

			System.out.println("complete");

			return temp;
		}

//		@GetMapping("/subway")
//		public String GetSubwayStops(@RequestParam String ver){
//			return new Gson().toJson(torontoSubwayGraph.visual_routes);
//		}




	}
}
