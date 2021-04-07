package com.example.demo.Services;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.Services.Graph.getDistance;

public class Path implements Comparable<Path>{ //hi
    protected ArrayList<MapNode> nodes;
    protected double totalLength;
    protected double totalTime; // in minutes
    protected double totalPedCount;
    protected double avgOccupancyPercent; // avg occupancy percent of ttc path
    protected double ttcTime;
    protected double walkingTime;
    protected double pathtype;



    public int weight = -1;


    public Path(){
        nodes = new ArrayList<MapNode>();
        totalLength =0;
        totalPedCount = 0;
        avgOccupancyPercent = 0;

        ttcTime = 0;
        walkingTime = 0;
    }

    /** Path constructor
     * @param input_nodes
     * set totalLength and totalTime of the path
    * */

    public Path(ArrayList<MapNode> input_nodes){
        nodes = input_nodes;
        totalLength = 0;
        totalPedCount = 0;
        avgOccupancyPercent = 0;
        pathtype = 5;
        ttcTime = 0;
        walkingTime = 0;
        double occupancypercent_sum = 0.0;
        int occupancypercent_counter = 0;
        for (int i=0; i< input_nodes.size()-1;i++) {
            totalLength += getDistance(input_nodes.get(i), input_nodes.get(i + 1));

            if (input_nodes.get(i).nodetype == 5) {
                walkingTime += getDistance(input_nodes.get(i), input_nodes.get(i + 1)) / 5000.0 * 60;
                totalPedCount += input_nodes.get(i).pedCount;
//                System.out.println("nodepedCount:"+input_nodes.get(i).pedCount);
            } else {
                pathtype = -1;
                occupancypercent_sum += input_nodes.get(i).occupancyPercent;
                occupancypercent_counter ++;
                for (MapEdge edge : input_nodes.get(i).edges) {
                    if (edge.destinationNode.id == input_nodes.get(i + 1).id) {
//                        ttcTime += edge.length; // using schedule
                        ttcTime += 1.5;
                        break;
                    }
                }
            }
        }avgOccupancyPercent = occupancypercent_sum/Double.valueOf(occupancypercent_counter);

        setTime(); // set total time of the path in mins
    }

//    public Path(ArrayList<MapNode> input_nodes, double total_cost){
//        nodes = input_nodes;
//        totalLength = total_cost;
//    }

    public void setTime(){
        totalTime = ttcTime + walkingTime;
    }

    public List<MapNode> getNodes(){return nodes;}
    public double getTotalLength(){return totalLength;}

    public double getTotalTime() {
        return this.totalTime;
//        return Math.floor(this.totalTime * 1e2)/ 1e2;  //rounded time
    }

    public String getDescription(){
        String str = "Suggestive route ";//changed from 要速度还是要命
        return str;
    }

    public int size(){return this.nodes.size(); }
    public MapNode get(int idx){return this.nodes.get(idx);}

    public Path subPath(int start, int end) {
        if ((start==0)&&(end==0)){
//            ArrayList myArrayList = new ArrayList();
//            ArrayList part1 = new ArrayList(myArrayList.subList(0, 25));
//            ArrayList part2 = new ArrayList(myArrayList.subList(26, 51));
            ArrayList myArrayList = new ArrayList(this.nodes.subList(start,start+1));
            Path res = new Path(myArrayList);
            return res;
        }else{
            ArrayList myArrayList = new ArrayList(this.nodes.subList(start,end));
            Path res = new Path(myArrayList);
            return res;
        }
    }
    public List<MapNode> subList(int start, int end){
        if ((start==0)&&(end==0)){
            return this.nodes.subList(start, start+1);
    }else{return this.nodes.subList(start, end);}}

    public static Path concatenate(Path head, Path butt){
        ArrayList<MapNode> concat_list = new ArrayList();
        concat_list.addAll(head.getNodes());
        concat_list.addAll(butt.getNodes());
        Path res = new Path(concat_list);
        return res;
    }
    @Override
    public boolean equals(Object v) {
        boolean retVal = false;
        if (v instanceof Path){
            Path ptr = (Path) v;
            for (int i=0;i<ptr.nodes.size();i++) {
                if(this.nodes.get(i).id == ptr.nodes.get(i).id){
                    retVal = true;
                }else{
                    retVal = false;
                    break;
                }
            }
        }
        return retVal;
    }


    @Override
    public int compareTo(Path o) {
        if(this.totalLength == o.totalLength)
            return 0;
        return this.totalLength < o.totalLength ? -1 : 1;
    }
}
