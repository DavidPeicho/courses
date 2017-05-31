package syma.utils;

import syma.environment.AFixedGeography;
import syma.environment.Bar;
import syma.environment.Building;
import syma.environment.Ground;
import syma.environment.Road;
import syma.environment.School;
import syma.environment.WorkPlace;

public class Const {
	
	/* TIME */
	public static int INITIAL_MIN_TIME_FACTOR = 1;
	public static int MINUTE_TIME_FACTOR = INITIAL_MIN_TIME_FACTOR;
	
	public static long YEAR_IN_MIN = 525600l;
	
	/* GOD AGENT */
	public static final int MORNING_HOUR = 7;
	
	/* HUMAN CONST */
	public static final int MAX_AGE = 90;
	public static final int MAX_SEARCH_PARTNER_AGE = 45;
	
	public static final double MARRIEDTO = 1;
	public static final double PARENTOF = 2;
	
	/* GRID TYPE */
	public static final String NONE_TYPE = "NONE";
	public static final String BAR_TYPE = "BAR";
	public static final String GROUND_TYPE = "GROUND";
	public static final String HOUSE_TYPE = "HOUSE";
	public static final String ROAD_TYPE = "ROAD";
	public static final String SCHOOL_TYPE = "SCHOOL";
	public static final String WORKPLACE_TYPE = "WORKPLACE";
	
	/* STYLES */
	public static final String ICON_FOLDER = "icons/";
	public static final String BAR_ICON = ICON_FOLDER + "bar.png";
	public static final String GROUND_ICON = ICON_FOLDER + "dirt.png";
	public static final String HOUSE_ICON = ICON_FOLDER + "house.png";
	public static final String ROAD_ICON = ICON_FOLDER + "road.png";
	public static final String SCHOOL_ICON = ICON_FOLDER + "school.png";
	public static final String WORK_ICON = ICON_FOLDER + "workplace.png";
	
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
		}
		return res;
	}
	
	public static long yearToMin(int y) {
		return y * 365 * 24 * 60;
	}
	
	public static long dayToMin(long d) {
		return d * 24 * 60;
	}
	
	public static int timeToTick(int day, int h, int min) {
		int total = min + h * 60 + day * 24 * 60;
		return total / MINUTE_TIME_FACTOR;
	}
	
}
