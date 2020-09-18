import org.w3c.dom.Element;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MapNode implements Comparable<MapNode>{
    public double id;
    public double longitude;
    public double latitude;
    public List<MapEdge> edges;
    public double estimatedCost;

    public MapNode (Element e){
        id = Double.parseDouble(e.getAttribute("id"));
        longitude = Double.parseDouble(e.getAttribute("lon"));
        latitude = Double.parseDouble(e.getAttribute("lat"));
        edges = new ArrayList<>();
    }

    @Override
    public int compareTo(MapNode o) {
        if(this.estimatedCost == o.estimatedCost)
            return 0;
        return this.estimatedCost < o.estimatedCost ? -1 : 1;
    }
}
