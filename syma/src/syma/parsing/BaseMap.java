package syma.parsing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import repast.simphony.space.grid.GridPoint;

public class BaseMap {
	
	private MapObject mapObj_;
	private HashMap<Character, String> keyToType_;
		
	public BaseMap(MapObject m) {
		mapObj_ = m;
		keyToType_ = new HashMap<Character, String>();		
		buildKeyToTypeMap();
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
	
	public List<List<Point>> getBusPaths() {
		return mapObj_.busPaths;
	}
	
	private void buildKeyToTypeMap() {
		for (int i = 0; i < mapObj_.props.size(); ++i) {
			char key = mapObj_.props.get(i).key;
			String type = mapObj_.props.get(i).type;
			keyToType_.put(key, type);
		}
	}
}
