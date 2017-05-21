package syma.parsing;

import java.util.ArrayList;

public class Map {
	
	private class Size {
		public int width;
		public int height;
	}
	
	public Size size;
	public String map;
	public ArrayList<Prop> props;
	
	public int getWidth() {
		return size.width;
	}
	
	public int getHeight() {
		return size.height;
	}
	
}
