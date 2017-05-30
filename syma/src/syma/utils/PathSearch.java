package syma.utils;

import java.util.LinkedList;
import java.util.Queue;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import repast.simphony.space.grid.Grid;
import repast.simphony.space.grid.GridPoint;
import syma.environment.AFixedGeography;
import syma.environment.Road;
import syma.main.GridElement;

public class PathSearch {

	private static Logger LOGGER = Logger.getLogger(PathSearch.class.getName());
	
	private static final int EMPTY = 0;
	private static final int ROAD = 1;
	private static final int VISITED = 2;
	
	Grid<GridElement> grid_;
	Queue<GridPoint> queue_;
	
	int[] weights_;
	GridPoint[] previous_;
	Stack<GridPoint> path_;
	
	GridPoint start_;
	GridPoint dest_;
	
	boolean found_;
	
	public PathSearch(Grid<GridElement> grid) {
		grid_ = grid;
		
		int w = grid_.getDimensions().getWidth();
		int h = grid_.getDimensions().getHeight();
		
		weights_ = new int[w * h];
		previous_ = new GridPoint[w * h];
		path_ = new Stack<GridPoint>();
		
		queue_ = new LinkedList<GridPoint>();
	}
	
	public GridPoint[] search(GridPoint start, GridPoint dest) {
		
		init(start, dest);
		
		do {
			GridPoint p = queue_.poll();
			
			// Left point
			if (isValid(p.getX() - 1, p.getY())) {
				GridPoint target = new GridPoint(p.getX() - 1, p.getY());
				setPrevious(target.getX(), target.getY(), p);
				if (target.getX() == dest.getX() && target.getY() == dest.getY()) {
					found_ = true;
					return previous_;
				}
				if (getWeight(target.getX(), target.getY()) == ROAD) {
					queue_.add(target);
					setWeight(target.getX(), target.getY(), VISITED);	
				}
			}
			// Right point
			if (isValid(p.getX() + 1, p.getY())) {
				GridPoint target = new GridPoint(p.getX() + 1, p.getY());
				setPrevious(target.getX(), target.getY(), p);
				if (target.getX() == dest.getX() && target.getY() == dest.getY()) {
					found_ = true;
					return previous_;
				}
				if (getWeight(target.getX(), target.getY()) == ROAD) {
					queue_.add(target);
					setWeight(target.getX(), target.getY(), VISITED);	
				}
			}
			// Up point
			if (isValid(p.getX(), p.getY() - 1)) {
				GridPoint target = new GridPoint(p.getX(), p.getY() - 1);
				setPrevious(target.getX(), target.getY(), p);
				if (target.getX() == dest.getX() && target.getY() == dest.getY()) {
					found_ = true;
					return previous_;
				}
				if (getWeight(target.getX(), target.getY()) == ROAD) {
					queue_.add(target);
					setWeight(target.getX(), target.getY(), VISITED);	
				}
			}
			// Down point
			if (isValid(p.getX(), p.getY() + 1)) {
				GridPoint target = new GridPoint(p.getX(), p.getY() + 1);
				setPrevious(target.getX(), target.getY(), p);
				if (target.getX() == dest.getX() && target.getY() == dest.getY()) {
					found_ = true;
					return previous_;
				}
				if (getWeight(target.getX(), target.getY()) == ROAD) {
					queue_.add(target);
					setWeight(target.getX(), target.getY(), VISITED);	
				}
			}
			
		} while (!queue_.isEmpty());
		
		return null;
		
	}
	
	public Stack<GridPoint> computePath() {
		if (!found_) {
			LOGGER.log(Level.WARNING, "Path: no path found");
			return null;
		}
		int w = grid_.getDimensions().getWidth();
		int h = grid_.getDimensions().getHeight();
		
		GridPoint curr = dest_;
		while (curr.getX() != -1 && curr.getY() != -1) {
			path_.push(curr);
			curr = previous_[curr.getX() + curr.getY() * w];
		}
		
		return path_;
	}
	
	public Stack<GridPoint> getPath() {
		return path_;
	}
	
	private void init(GridPoint start, GridPoint dest) {
		int width = grid_.getDimensions().getWidth();
		int height = grid_.getDimensions().getHeight();
		
		start_ = start;
		dest_ = dest;
		
		found_ = false;
		
		queue_.clear();
		path_.clear();
		for (int i = 0; i < width * height; ++i) {
			previous_[i] = null;
		}
		
		for (int h = 0; h < height; ++h) {
			for (int w = 0; w < width; ++w) {
				Iterable<GridElement> e = grid_.getObjectsAt(w, h);
				if (!e.iterator().hasNext()) {
					setWeight(w, h, EMPTY);
					continue;
				}
				Object x = e.iterator().next();
				if (x instanceof AFixedGeography) {
					AFixedGeography a = (AFixedGeography)x;
					if (a instanceof Road) {
						setWeight(w, h, ROAD);
					}
				}
			}	
		}
		
		setWeight(start.getX(), start.getY(), VISITED);
		setPrevious(start.getX(), start.getY(), new GridPoint(-1, -1));
		queue_.add(start);
	}
	
	private int relativePos(int x, int y) {
		int w = grid_.getDimensions().getWidth();
		int h = grid_.getDimensions().getHeight();
		return x + y * w;
	}
	
	private int getWeight(int x, int y) {
		return weights_[relativePos(x, y)];
	}
	
	private void setWeight(int x, int y, int val) {
		weights_[relativePos(x, y)] = val;
	}
	
	private void setPrevious(int x, int y, GridPoint val) {
		previous_[relativePos(x, y)] = val;
	}
	
	private boolean isValid(int x, int y) {
		int w = grid_.getDimensions().getWidth();
		int h = grid_.getDimensions().getHeight();
		return (x >= 0 && x < w && y >= 0 && y < h) && getWeight(x, y) != VISITED;
	}
	
}
