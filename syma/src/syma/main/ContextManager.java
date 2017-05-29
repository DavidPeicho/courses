package syma.main;

import syma.agent.*;
import syma.behaviors.MoveTo;
import syma.environment.AFixedGeography;
import syma.environment.Building;
import syma.environment.Road;
import syma.environment.WorkPlace;
import syma.exceptions.EnvironmentException;
import syma.exceptions.SymaException;
import syma.parsing.BaseMap;
import syma.parsing.GridParser;
import syma.utils.Const;
import syma.utils.PathSearch;

import java.io.IOException;
import java.util.ArrayList;

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
		
		context.setId("moutouCity");
		
		int width = 10;
		int height = 10;
		BaseMap map = null;
		
		String pathToMap = RunEnvironment.getInstance().getParameters().getString("mapPath");
		int nbAgents = RunEnvironment.getInstance().getParameters().getInteger("maxNbAgents");
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
	
	private void buildGrid(BaseMap map, int w, int h, Context<GridElement> context, Grid<GridElement> grid) {
		
		String mapStr = map.getRawMap();
		for (int i = 0; i < mapStr.length(); ++i) {
			char val = mapStr.charAt(i);
			String type = map.getType(val);
			
			if (type == null) continue;
			
			int x = i % w;
			int y = i / w;
			
			int relativeX = x;
			int relativeY = h - 1 - y;
			
			GridElement elt = null;
			if (type.equals("ROAD")) {
				elt = new Road(relativeX, relativeY, grid);
			} else if (type.equals("HOUSE")) {
				elt = new Building(relativeX, relativeY, grid);
				Building.globalList.add((Building)elt);
			} else if (type.equals("WORKPLACE")) {
				elt = new WorkPlace(relativeX, relativeY, grid);
				WorkPlace.globalList.add((WorkPlace)elt);
			}
			
			if (elt == null) continue;
			
			context.add(elt);
			grid.moveTo(elt, relativeX, relativeY);
		}
		
	}
	
	private void spawnDefaultAgents(int nbAgents, Context<GridElement> context, Grid<GridElement> grid) {
		int w = grid.getDimensions().getWidth();
		int h = grid.getDimensions().getHeight();
		GodAgent env = GodAgent.instance();
		
		for (int i = 0; i < nbAgents; ++i) {
			// Finds a house for the newly created agent
			Building home = getEmptyGeography(Building.globalList);
			int x = home.getX();
			int y = home.getY();
			// Finds a workplace for the newly created agent
			WorkPlace workplace = getEmptyGeography(WorkPlace.globalList);
			
			boolean gender = Math.random() >= 0.5f;
			int age = (int)(Math.random() * 50.0d + 18.0d);
			
			HumanAgent agent = env.createAgent(x, y, grid, age, gender, home, workplace);
			// DEBUG
			PathSearch p = new PathSearch(grid);
			p.search(agent.getPos(), workplace.getPos());
			p.convertToBehavior(agent);
			// END DEBUG
			
			home.addAgent(agent);

			context.add(agent);
			grid.moveTo(agent, x, y);

			if (i == 0) {
				HumanAgent child = env.createAgent(x, y, grid, 10, gender, home, null);
				home.addAgent(child);
				context.add(child);
				grid.moveTo(child, x, y);
				Network n = (Network)context.getProjection("genealogy");
				n.addEdge(agent, child, Const.PARENTOF);
			}
		}
		
	}
	
	/**
	 * Loops through every registered building to find an empty one,
	 * after applying a random selection.
	 * @return The first building being empty after random search.
	 */
	private <T extends AFixedGeography> T getEmptyGeography(ArrayList<T> list) {
		
		int nbGeography = list.size();
		
		int initialIdx = (int)(Math.random() * (nbGeography - 1)); 
		int idx = initialIdx;
		
		T elt = list.get(idx);
		while (!elt.isEmpty()) {
			idx = (idx + 1) % nbGeography;
			if (idx == initialIdx) {
				return null;
			}
			elt = list.get(idx);
		}
		
		return elt;
		
	}

}
