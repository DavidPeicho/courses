package syma.parsing;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.environment.AFixedGeography;
import syma.environment.Bar;
import syma.environment.Building;
import syma.environment.BusStop;
import syma.environment.Ground;
import syma.environment.Road;
import syma.environment.School;
import syma.environment.ShoppingCentre;
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
			case Const.GROUND_TYPE:
				result = new Ground(grid);
				break;
			case Const.BAR_TYPE:
				result = new Bar(grid);
				Bar.globalList.add((Bar)result);
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
				School.globalList.add((School)result);
				break;
			case Const.WORKPLACE_TYPE:
				result = new WorkPlace(grid);
				WorkPlace.globalList.add((WorkPlace)result);
				break;
			case Const.SHOPPING_TYPE:
				result = new ShoppingCentre(grid);
				ShoppingCentre.globalList.add((ShoppingCentre)result);
				break;
		}
		return result;
	}
	
}
