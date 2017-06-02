package syma.styles;

import java.awt.Color;
import java.io.IOException;

import repast.simphony.visualizationOGL2D.DefaultStyleOGL2D;
import saf.v3d.scene.VSpatial;
import syma.environment.AFixedGeography;
import syma.environment.Building;
import syma.utils.Const;

public class GeographyStyle extends DefaultStyleOGL2D {

	@Override
	public VSpatial getVSpatial(Object agent, VSpatial spatial) {
		if (spatial == null) {
			String path = Const.getIconFromType((AFixedGeography)agent);
			try {
				spatial = shapeFactory.createImage(path);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		Building b = null;
		if (agent instanceof Building) {
			b = (Building)agent;
			if (b.isBurnt() && !b.isBurntImgSet()) {
				System.out.println("CALLED");
				b.setBurntImg();
				try {
					spatial = shapeFactory.createImage(Const.HOUSE_BURNT_ICON);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} 
		}
		
		return spatial;
	}
	
}