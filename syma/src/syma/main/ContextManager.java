package syma.main;

import syma.agent.*;

import repast.simphony.context.Context;
import repast.simphony.context.space.grid.GridFactory;
import repast.simphony.context.space.grid.GridFactoryFinder;
import repast.simphony.dataLoader.ContextBuilder;
import repast.simphony.space.grid.WrapAroundBorders;
import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridBuilderParameters;
import repast.simphony.space.grid.SimpleGridAdder;

public class ContextManager implements ContextBuilder<Agent> {

	@Override
	public Context build(Context<Agent> context) {
		
		context.setId("EcoSys");
		int width = 10;
		int height = 10;
		GridFactory gfac = GridFactoryFinder.createGridFactory(null);
		GridBuilderParameters<Agent> gbp = new GridBuilderParameters<Agent>(new WrapAroundBorders(), new SimpleGridAdder<Agent>(), true, width, height);
		Grid<Agent> grid = gfac.createGrid("grid", context, gbp);
		
		return context;
		
	}

}
