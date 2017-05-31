package syma.styles;

import java.awt.Color;
import java.io.IOException;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;
import syma.environment.AFixedGeography;
import syma.utils.Const;

public class GeographyStyle extends DefaultStyleOGL2D {

	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			String path = Const.getIconFromType((AFixedGeography)agent);
			try {
				spatial = shapeFactory.createImage(path);
				spatial.translate(-100.0f, 0.0f, 0.0f);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return spatial;
	}
	
}