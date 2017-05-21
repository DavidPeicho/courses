package syma.main;

import syma.agent.*;
import syma.parsing.GridParser;
import syma.parsing.Map;

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

public class ContextManager implements ContextBuilder<IAgent> {

	@Override
	public Context build(Context<IAgent> context) {
		
		context.setId("moutouCity");
		
		int width = 10;
		int height = 10;
		Map map = null;
		
		String pathToMap = RunEnvironment.getInstance().getParameters().getString("mapPath");
		try {
			map = GridParser.instance().parse(pathToMap);
			width = map.getWidth();
			height = map.getHeight();
		} catch (IOException e) {
			System.err.println("Map loading fail: impossible to find '" + pathToMap + "'");
		}
		
		GridFactory gfac = GridFactoryFinder.createGridFactory(null);
		GridBuilderParameters<IAgent> gbp = new GridBuilderParameters<IAgent>(new WrapAroundBorders(), new SimpleGridAdder<IAgent>(), true, width, height);
		Grid<IAgent> grid = gfac.createGrid("grid", context, gbp);
		
		if (map == null) return context;
		
		map.props.forEach((x) -> {
			
			
			
		});
		
		return context;
		
	}

}
