package syma.utils;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import syma.environment.Building;
import syma.environment.Road;
import syma.main.GridElement;

public class Const {

	public static final HashMap<String, Class<? extends GridElement>> TYPE_MAP;
	static {
		TYPE_MAP = new HashMap<String, Class<? extends GridElement>>();
		TYPE_MAP.put("NONE", Road.class);
		TYPE_MAP.put("HOUSE", Building.class);
		TYPE_MAP.put("ROAD", Road.class);
	}
	
}
