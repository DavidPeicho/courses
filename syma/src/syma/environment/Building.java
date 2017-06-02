package syma.environment;

import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

import repast.simphony.space.grid.Grid;
import syma.agent.GodAgent;
import syma.agent.HumanAgent;
import syma.main.GridElement;
import syma.utils.Const;

public class Building extends AFixedGeography {
	
	private static Logger LOGGER = Logger.getLogger(Building.class.getName());
	
	public static int TILE_X = 2;
	public static int TILE_Y = 2;
	
	/**
	 * Contains every buildings that are on the grid.
	 * It allows to access every building quickly without
	 * making a search on the whole grid.
	 */
	public static ArrayList<Building> globalList = new ArrayList<Building>();
	
	private int foodLevel_;
	private boolean emptyCheck_;
	
	private Random rand_;

	public Building(Grid<GridElement> grid) {
		super(grid);
		rand_ = new Random();
		foodLevel_ = Const.randBetween(Const.MIN_HOUSE_FOOD_LVL, Const.MAX_HOUSE_FOOD_LVL, rand_);
		foodLevel_ = Const.MAX_HOUSE_FOOD_LVL;
		emptyCheck_ = false;
	}
	
	public void consumeFood() {
		if (foodLevel_ <= 0) {
			if (!emptyCheck_) {
				
				if (this.agents_.size() == 0) return;
				
				String agents = "";
				for (int i = 0; i < this.agents_.size() - 1; ++i) {
					agents += this.agents_.get(i).getID() + ", ";
				}
				agents += this.agents_.get(this.agents_.size() - 1).getID();
				
				String logMsg = Const.HOUSE_TAG + "\n";
				logMsg += "Building " + id_ + " has no more food. Agent(s) " + agents + " should fill it.";
				LOGGER.log(Level.WARNING, logMsg);
				emptyCheck_ = true;
			}
			
			return;
		}
		--foodLevel_;
	}
	
	public boolean isFoodEmpty() {
		return foodLevel_ <= 0;
	}
	
	public int getFoodLvl() {
		return foodLevel_;
	}
	
	public void upFoodLevel() {
		foodLevel_ = Const.randBetween(Const.MIN_HOUSE_FOOD_LVL, Const.MAX_HOUSE_FOOD_LVL, rand_);
		emptyCheck_ = false;
	}

	@Override
	public String toString() {
		return "Building [foodLevel_=" + foodLevel_ + ", emptyCheck_=" + emptyCheck_ + ", id_=" + id_ + "]";
	}
	
}
