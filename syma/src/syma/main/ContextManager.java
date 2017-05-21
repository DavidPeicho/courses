package syma.main;

import syma.agent.*;
import syma.environment.Building;
import syma.environment.Road;
import syma.parsing.BaseMap;
import syma.parsing.GridParser;
import syma.utils.Const;

import java.io.IOException;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.engine.environment.RunEnvironment;

public class ContextManager implements ContextBuilder<GridElement> {

	@Override
	public Context build(Context<GridElement> context) {
		
		context.setId("moutouCity");
		
		int width = 10;
		int height = 10;
		BaseMap map = null;
		
		String pathToMap = RunEnvironment.getInstance().getParameters().getString("mapPath");
		try {
			map = GridParser.instance().parse(pathToMap);
			width = map.getWidth();
			height = map.getHeight();
		} catch (IOException e) {
			System.err.println("Map loading fail: impossible to find '" + pathToMap + "'");
		}
		
		GridFactory gfac = GridFactoryFinder.createGridFactory(null);
		GridBuilderParameters<GridElement> gbp = new GridBuilderParameters<GridElement>(new WrapAroundBorders(), new SimpleGridAdder<GridElement>(), true, width, height);
		Grid<GridElement> grid = gfac.createGrid("grid", context, gbp);
		
		if (map == null) return context;
		
		String mapStr = map.getRawMap();
		for (int i = 0; i < mapStr.length(); ++i) {
			char val = mapStr.charAt(i);
			String type = map.getType(val);
			
			if (type == null) continue;
			
			int x = i % width;
			int y = i / width;
			
			GridElement elt = null;
			if (type.equals("ROAD")) {
				elt = new Road(x, y, grid);
			} else if (type.equals("BUILDING")) {
				elt = new Building(x, y, grid);
			}
			
			if (elt == null) continue;
			
			context.add(elt);
			grid.moveTo(elt, x, height - y - 1);
		}
		
		return context;
		
	}

}
