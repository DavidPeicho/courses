package syma.main;

import syma.agent.*;
import syma.environment.AFixedGeography;
import syma.environment.Building;
import syma.environment.School;
import syma.environment.BusStop;
import syma.environment.WorkPlace;
import syma.parsing.BaseMap;
import syma.parsing.GridParser;
import syma.parsing.Point;
import syma.utils.Const;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import repast.simphony.context.Context;
import repast.simphony.context.space.graph.NetworkBuilder;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.space.graph.Network;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.GridPoint;
import repast.simphony.space.grid.SimpleGridAdder;
import repast.simphony.engine.environment.RunEnvironment;

public class ContextManager implements ContextBuilder<GridElement> {

	@Override
	public Context build(Context<GridElement> context) {
		context.clear();
		context.setId("moutouCity");
		
		int width = 10;
		int height = 10;
		BaseMap map = null;
		
		String pathToMap = RunEnvironment.getInstance().getParameters().getString("mapPath");
		int nbAgents = RunEnvironment.getInstance().getParameters().getInteger("maxNbAgents");
		Const.YEAR_FACTOR = RunEnvironment.getInstance().getParameters().getInteger("yearFactor");
		Const.INIT_CHILD_PROBA = RunEnvironment.getInstance().getParameters().getFloat("childProbability");

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
		
		init();
		
		GodAgent.init(grid);
		context.add(GodAgent.instance());
		grid.moveTo(GodAgent.instance(), 0, 0);
		
		buildGrid(map, width, height, context, grid);
		NetworkBuilder<Object> netBuilder = new NetworkBuilder<Object>("genealogy", (Context)context, true);
		netBuilder.buildNetwork();
		
		// Checks whether the given map matches the parameters
		if (nbAgents > Building.globalList.size()) {
			System.err.println("Number of default agents is greater than number of houses.");
			return context;
		}
		
		spawnDefaultAgents(nbAgents, context, grid);
		return context;
	}
	
	private void init() {
		Building.globalList.clear();
		WorkPlace.globalList.clear();
		School.globalList.clear();
		AAgent.resetID();
	}
	
	private void parseBus(BaseMap map, Context<GridElement> context, Grid<GridElement> grid) {
		ArrayList<BusStop> stops = parseBusStops(map, context, grid);
		Tram t = new Tram(grid, stops);
		context.add(t);
		grid.moveTo(t, t.getStart().getX(), t.getStart().getY());
	}
	
	private ArrayList<BusStop> parseBusStops(BaseMap map, Context<GridElement> context, Grid<GridElement> grid) {
		ArrayList<BusStop> busStops = new ArrayList<>();
		for (List<Point> l : map.getBusPaths()) {
			for (Point e : l) {
				BusStop bs = new BusStop(grid);
				context.add(bs);
				busStops.add(bs);
				GridPoint pos = getRelativePos(e, map.getHeight());
				grid.moveTo(bs, pos.getX(), pos.getY());
			}
		}
		return busStops;
	}
	
	private void buildGrid(BaseMap map, int w, int h, Context<GridElement> context, Grid<GridElement> grid) {
		
		String mapStr = map.getRawMap();
		
		// Add grounds element in the background
		for (int i = 0; i < mapStr.length(); ++i) {
			char val = mapStr.charAt(i);
			String type = map.getType(val);
			if (type != null && type.equals(Const.ROAD_TYPE)) {
				continue;
			}
			
			int relativeX = i % w;
			int relativeY = h - 1 - (i / w);
			AFixedGeography elt = GridParser.instance().typeToFixedGeography(Const.GROUND_TYPE, relativeX, relativeY, grid);
			context.add(elt);
			grid.moveTo(elt, relativeX, relativeY);
		}
		
		for (int i = 0; i < mapStr.length(); ++i) {
			char val = mapStr.charAt(i);
			String type = map.getType(val);
			
			if (type == null) continue;
			
			int x = i % w;
			int y = i / w;
			
			int relativeX = x;
			int relativeY = h - 1 - y;

			AFixedGeography elt = GridParser.instance().typeToFixedGeography(type, relativeX, relativeY, grid);
			
			if (elt == null) continue;
			
			context.add(elt);
			grid.moveTo(elt, relativeX, relativeY);
		}
		
		parseBus(map, context, grid);
		
	}
	
	private void spawnDefaultAgents(int nbAgents, Context<GridElement> context, Grid<GridElement> grid) {
		int w = grid.getDimensions().getWidth();
		int h = grid.getDimensions().getHeight();
		GodAgent env = GodAgent.instance();
		env.setAgentsNb(nbAgents);

		for (int i = 0; i < nbAgents; ++i) {
			// Finds a house for the newly created agent
			Building home = env.getEmptyGeography(Building.globalList);
			int x = home.getX();
			int y = home.getY();
			// Finds a workplace for the newly created agent
			WorkPlace workplace = env.getEmptyGeography(WorkPlace.globalList);
			
			boolean gender = Math.random() >= 0.5f;
			int age = (int)(Math.random() * 50.0d + 18.0d);
			
			HumanAgent agent = env.createAgent(grid, age, gender, home, workplace);
			
			// Spawns a child agent according
			// to a given probability
			if (Math.random() <= Const.INIT_CHILD_PROBA) {
				HumanAgent child = env.createChildAgent(context, agent, grid, home);
				grid.moveTo(child, home.getX(), home.getY());
			}

			context.add(agent);
			grid.moveTo(agent, x, y);
		}
		
	}
	
	private GridPoint getRelativePos(GridPoint p, int height) {
		int relativeX = p.getX();
		int relativeY = height - 1 - p.getY();
		return new GridPoint(relativeX, relativeY);
	}
	
	private GridPoint getRelativePos(Point p, int height) {
		int relativeX = p.x;
		int relativeY = height - 1 - p.y;
		return new GridPoint(relativeX, relativeY);
	}
}
