package syma.utils;

import java.awt.Color;
import java.util.Random;

import syma.environment.AFixedGeography;
import syma.environment.Bar;
import syma.environment.Building;
import syma.environment.Ground;
import syma.environment.Road;
import syma.environment.School;
import syma.environment.ShoppingCentre;
import syma.environment.WorkPlace;

public class Const {
	
	/* GLOBAL */
	public static boolean IS_SIMULATION_OVER = false;
	
	/* INITIALIZATION */
	public static int INIT_NB_AGENTS = 1;
	public static float INIT_CHILD_PROBA = 0.25f;
	public static float SPARE_TIME_RATE = 0.5f;
	
	/* TIME */
	public static int INITIAL_YEAR_FACTOR = 1;
	public static int YEAR_FACTOR = INITIAL_YEAR_FACTOR;
	
	public static long YEAR_IN_MIN = 525600l;
	public static long DAY_IN_MIN = 1440l;
	
	public static int NIGHT_BEGIN_HOUR = 22; // In virtual hours
	public static int END_AFTERNOON = 19; // In virtual hours
	
	public static final int MORNING_HOUR = 7;
	public static final int NOON_HOUR = 12;
	
	public static int MIN_NB_MIN_HANG_OUT = 120;
	public static int MAX_NB_MIN_HANG_OUT = 200;
	
	public static int MIN_NB_MIN_SHOPPING = 80;
	public static int MAX_NB_MIN_SHOPPING = 130;
	
	/* HUMAN CONST */
	public static final int MIN_DEATH_AGE = 52;
	public static final int MAX_AGE = 90;
	public static final int MAX_SEARCH_PARTNER_AGE = 45;
	public static final int MAJOR_AGE = 18;
	
	public static int MEAN_NB_CHILD = 2;
	public static final int MAX_NB_CHILDREN = 3;
	
	public static final int MAX_DELAY_BEFORE_WORK = 80; // In virtual minutes
	
	public static final double MARRIEDTO = 1;
	public static final double PARENTOF = 2;
	
	/* HOUSES */
	public static final int MIN_HOUSE_FOOD_LVL = 5000;
	public static final int MAX_HOUSE_FOOD_LVL = 15000;
	
	/* BUS */
	public static final int BUS_WAITING_TIME = 10;
	
	/* GRID TYPE */
	public static final String NONE_TYPE = "NONE";
	public static final String CLOCK_TYPE = "CLOCK";
	public static final String BAR_TYPE = "BAR";
	public static final String GROUND_TYPE = "GROUND";
	public static final String HOUSE_TYPE = "HOUSE";
	public static final String ROAD_TYPE = "ROAD";
	public static final String SCHOOL_TYPE = "SCHOOL";
	public static final String SHOPPING_TYPE = "SHOPPING_CENTRE";
	public static final String WORKPLACE_TYPE = "WORKPLACE";
	
	/* STYLES */
	public static final String ICON_FOLDER = "icons/";
	public static final String BAR_ICON = ICON_FOLDER + "bar.png";
	public static final String GROUND_ICON = ICON_FOLDER + "dirt.png";
	public static final String HOUSE_ICON = ICON_FOLDER + "house.png";
	public static final String HOUSE_BURNT_ICON = ICON_FOLDER + "house-burnt.png";
	public static final String ROAD_ICON = ICON_FOLDER + "road.png";
	public static final String SCHOOL_ICON = ICON_FOLDER + "school.png";
	public static final String WORK_ICON = ICON_FOLDER + "workplace.png";
	public static final String SHOPPING_ICON = ICON_FOLDER + "shopping-centre.png";
	
	/* LOGGING */
	public static boolean LOGGER_INITIALIZED = false;
	
	public static final String AGENT_TAG = "[ AGENT ]";
	public static final String WORK_TAG = "[ WORK ]";
	public static final String HANG_OUT_TAG = "[ HANG_OUT ]";
	public static final String HOUSE_TAG = "[ HOUSE ]";
	public static final String SHOPPING_TAG = "[ SHOPPING ]";
	public static final String ENV_TAG = "[ ENVIRONMENT ]";
	
	/* GOD AGENT */
	public static int MAX_HOUSE_BURN_WEEK = 1; 
	
	public static final Color COLOR_BEFORE_MORNING = new Color(52, 73, 94);
	public static final Color COLOR_MORNING = new Color(241, 196, 15);
	public static final Color COLOR_NOON = new Color(41, 128, 185);
	public static final Color COLOR_END_DAY = new Color(211, 84, 0);
	public static final Color COLOR_NIGHT = new Color(94, 109, 110);

	public static final String getIconFromType(AFixedGeography a) {
		String res = null;
		if (a instanceof Bar) {
			res = Const.BAR_ICON;
		} else if (a instanceof Building) {
			res = Const.HOUSE_ICON;
		} else if (a instanceof Ground) {
			res = Const.GROUND_ICON;
		} else if (a instanceof Road) {
			res = Const.ROAD_ICON;
		} else if (a instanceof School) {
				res = Const.SCHOOL_ICON;
		} else if (a instanceof WorkPlace) {
			res = Const.WORK_ICON;
		} else if (a instanceof ShoppingCentre) {
			res = Const.SHOPPING_ICON;
		}
		return res;
	}
	
	public static long yearToMin(int y) {
		return y * 365 * 24 * 60;
	}
	
	public static long dayToMin(long d) {
		return d * 24 * 60;
	}
	
	public static long hourToMin(long h) {
		return h * 60;
	}
	
	public static int timeToTick(int day, int h, int min) {
		int total = min + h * 60 + day * 24 * 60;	
		return total;
	}
	
	public static int randBetween(int min, int max, Random rand) {
		return rand.nextInt(max - min + 1) + min;
	}
}
