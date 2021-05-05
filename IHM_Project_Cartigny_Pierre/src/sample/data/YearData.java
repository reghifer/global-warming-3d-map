package sample.data;


import java.util.Collection;
import java.util.HashMap;

public class YearData {
	public final int year;
	private HashMap<Position,Float> dataPosition = new HashMap<Position,Float>();
	
	public YearData(int year) {
		this.year = year;
	}

	public boolean addData(float lat, float lon, float data) {
		Position pos = new Position(lat,lon);
		if(dataPosition.containsKey(pos)){
			return dataPosition.replace(pos, dataPosition.get(pos), data);
		}
		dataPosition.put(pos, data);
		return true;
	}
	
	public float getData(float lat, float lon) {
		Position pos = new Position(lat,lon);
		return dataPosition.get(pos);
	}
	
	public boolean contain(float lat, float lon) {
		return dataPosition.containsKey(new Position(lat,lon));
	}
	protected float findMinTemperature() {
		Collection<Float> values = dataPosition.values();
		float minValue = Float.MAX_VALUE;
		for(float value : values) {
			if(value < minValue) {
				minValue = value;
			}
		}
		return minValue;
	}
	protected float findMaxTemperature() {
		Collection<Float> values = dataPosition.values();
		float maxValue = Float.MIN_VALUE;
		for(float value : values) {
			if(value > maxValue) {
				maxValue = value;
			}
		}
		return maxValue;
	}
	
	@Override
	public String toString() {
		String s = " year : " + this.year + "\n";
		float lat = -88.0f, lon = -178.0f;
		do {
			do {
				 s += "lat : " + lat + ", lon : " + lon + ", Data:" + this.getData(lat, lon) + "\n";
				lon += 4.0f;
			}while(lon < 178.0f);
			lon = -178.0f;
			lat += 4.0f;
		}while(lat < 88.0f);
		return s;
	}
}
