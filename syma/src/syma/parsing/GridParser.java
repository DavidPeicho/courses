package syma.parsing;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;


public class GridParser {

	private static GridParser instance_ = null;
	
	public static GridParser instance() {
		
		if (instance_ == null) instance_ = new GridParser();
		
		return instance_;
		
	}
	
	public BaseMap parse(String filePath) throws IOException  {
		
		BufferedReader br = new BufferedReader(new FileReader(filePath));
		
		StringBuilder sb = new StringBuilder();
	    String line = br.readLine();

	    while (line != null) {
	        sb.append(line);
	        line = br.readLine();
	    }
		
		Gson gson = new Gson();
		MapObject parsedMap = gson.fromJson(sb.toString().replaceAll("\\s+",""), MapObject.class);
		
		BaseMap result = new BaseMap(parsedMap);
		
		return result;
		
	}
	
}
