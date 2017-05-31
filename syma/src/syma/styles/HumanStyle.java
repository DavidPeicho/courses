package syma.styles;

import java.awt.Color;

import syma.agent.HumanAgent;
import syma.utils.Const;
import repast.simphony.engine.environment.RunEnvironment;
import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;
import java.awt.Shape;

public class HumanStyle extends DefaultStyleOGL2D {
	
	public Color getColor(Object o) {
		if (o instanceof HumanAgent) {
			HumanAgent a = (HumanAgent)o;
			float rate = (float)a.getAge() / (float)Const.MAX_AGE;
			int value = 255 - Math.min((int)(rate * 255.0f), 255);

			return a.getID() == 11 ? new Color(0, 0, 255) : new Color(255, value, value);
		}
		return null;
	}
	
	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			spatial = shapeFactory.createCircle(6, 10);
			//shapeFactory.createRectangle(8, 8);
		}
		return spatial;
	}

}
