package syma.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import repast.simphony.space.grid.GridPoint;

public class BaseMap {
	
	private MapObject mapObj_;
	private HashMap<Character, String> keyToType_;
	
	private ArrayList<ArrayList<GridPoint>> busPaths_;
	
	public BaseMap(MapObject m) {
		mapObj_ = m;
		keyToType_ = new HashMap<Character, String>();
		busPaths_ = new ArrayList<ArrayList<GridPoint>>();
		
		buildKeyToTypeMap();
		computeBusPaths();
	}
	
	public String getRawMap() {
		return mapObj_.map;
	}
	
	public String getType(char key) {
		return keyToType_.get(key);
	}
	
	public int getWidth() {
		return mapObj_.size.width;
	}
	
	public int getHeight() {
		return mapObj_.size.height;
	}
	
	public ArrayList<ArrayList<GridPoint>> getBusPaths() {
		return busPaths_;
	}
	
	private void buildKeyToTypeMap() {
		for (int i = 0; i < mapObj_.props.size(); ++i) {
			char key = mapObj_.props.get(i).key;
			String type = mapObj_.props.get(i).type;
			keyToType_.put(key, type);
		}
	}
	
	private void computeBusPaths() {
		for (List<Point> l : mapObj_.busPaths) {
			ArrayList<GridPoint> points = new ArrayList<GridPoint>();
			for (Point p : l) {
				points.add(new GridPoint(p.x, p.y));
			}
			busPaths_.add(points);
		}
	}

}
