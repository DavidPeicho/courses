package syma.styles;

import java.awt.Color;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;
import syma.agent.GodAgent;
import syma.utils.Const;

public class GodStyle extends DefaultStyleOGL2D {

	public Color getColor(Object o) {
		GodAgent a = (GodAgent)o;
		if (a.getHour() < Const.MORNING_HOUR) {
			return Const.COLOR_BEFORE_MORNING;
		} else if (a.getHour() < Const.NOON_HOUR) {
			return Const.COLOR_MORNING;
		} else if (a.getHour() < Const.END_AFTERNOON) {
			return Const.COLOR_NOON;
		} else if (a.getHour() < Const.NIGHT_BEGIN_HOUR) {
			return Const.COLOR_END_DAY;
		}
		
		return Const.COLOR_NIGHT;
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createRectangle(16, 16);
		}
		return spatial;
	}
	
}
