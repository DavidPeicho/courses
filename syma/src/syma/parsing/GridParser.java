package syma.parsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import com.google.gson.Gson;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.environment.AFixedGeography;
import syma.environment.Bar;
import syma.environment.Building;
import syma.environment.Road;
import syma.environment.School;
import syma.environment.WorkPlace;
import syma.main.GridElement;
import syma.utils.Const;


public class GridParser {

	private static GridParser instance_ = null;
	
	public static GridParser instance() {
		
		if (instance_ == null) instance_ = new GridParser();
		
		return instance_;
		
	}
	
	public BaseMap parse(String filePath) throws IOException  {
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		
		StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
	        sb.append(line);
	        line = br.readLine();
	    }
		
		Gson gson = new Gson();
		MapObject parsedMap = gson.fromJson(sb.toString().replaceAll("\\s+",""), MapObject.class);
		
		BaseMap result = new BaseMap(parsedMap);
		
		return result;
		
	}
	
	public AFixedGeography typeToFixedGeography(String type, int x, int y, Grid<GridElement> grid) {
		AFixedGeography result = null;
		switch (type) {
			case Const.BAR_TYPE:
				result = new Bar(grid);
				break;
			case Const.ROAD_TYPE:
				result = new Road(grid);
				break;
			case Const.HOUSE_TYPE:
				result = new Building(grid);
				Building.globalList.add((Building)result);
				break;
			case Const.SCHOOL_TYPE:
				result = new School(grid);
				break;
			case Const.WORKPLACE_TYPE:
				result = new WorkPlace(x, y, grid);
				WorkPlace.globalList.add((WorkPlace)result);
				break;
		}
		return result;
	} 
	
}
