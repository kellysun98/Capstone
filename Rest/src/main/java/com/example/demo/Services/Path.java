package com.example.demo.Services;

import java.util.ArrayList;
import java.util.List;

import static com.example.demo.Services.Graph.getDistance;

public class Path implements Comparable<Path>{
    private ArrayList<MapNode> nodes;
    private double totalCost;

    public Path(){
        nodes = new ArrayList<MapNode>();
        totalCost =0;
    }

    public Path(ArrayList<MapNode> input_nodes){
        nodes = input_nodes;
        totalCost = 0;
        for (int i=0; i< input_nodes.size()-1;i++){
            totalCost+= getDistance(input_nodes.get(i), input_nodes.get(i+1));
        }
    }
    public Path(ArrayList<MapNode> input_nodes, double total_cost){
        nodes = input_nodes;
        totalCost = total_cost;
    }

    public List<MapNode> getNodes(){return nodes;}
    public double getTotalCost(){return totalCost;}
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
        if(this.totalCost == o.totalCost)
            return 0;
        return this.totalCost < o.totalCost ? -1 : 1;
    }
}
