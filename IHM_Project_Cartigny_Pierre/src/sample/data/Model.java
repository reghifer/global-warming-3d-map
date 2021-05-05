package sample.data;

import java.util.Collection;
import java.util.HashMap;

public class Model {
	private CurrentState currentState;
	private int CurrentYear;
	private float animationSpeed;
	private double every;
	public boolean AnimationisOn = false,ChartIsOpen = false;
	protected HashMap<Integer,YearData> YearsData = new HashMap<Integer,YearData>();
	
	public Model(int firstYear,int lastYear) {
		this.currentState = CurrentState.position;
		if(lastYear < firstYear) {
			System.err.println("the first year value is bigger than the last Year");
			return;
		}
		for(int i = firstYear; i <= lastYear; i++) {
			YearsData.put(i, new YearData(i));
		}
		this.CurrentYear = 1880;
		this.animationSpeed = 1.0f;
		this.every = 1.0f;
	}
	
	public CurrentState getCurrentState() {
		return currentState;
	}

	public void setCurrentState(CurrentState currentState) {
		this.currentState = currentState;
	}
	
	
	public int getCurrentYear() {
		return CurrentYear;
	}

	public void setCurrentYear(int currentYear) {
		CurrentYear = currentYear;
	}
	
	
	public float getAnimationSpeed() {
		return animationSpeed;
	}

	public void setAnimationSpeed(float animationSpeed) {
		this.animationSpeed = animationSpeed;
	}
	
	public boolean iterateEvery(double value, double reset) {
		every -= value;
		if(every <= 0) {
			every = reset;
			return true;
		}else {
		return false;
		}
	}

	public HashMap<Integer, YearData> getYearsData() {
		return YearsData;
	}
	
	public YearData getYearData(int year) {
		return(YearsData.get(year));
	}
	
	public float getModelMinTemperature() {
		Collection<YearData> values = YearsData.values();
		float minValue = Float.MAX_VALUE;
		for(YearData year : values) {
			float value = year.findMinTemperature();
			if(value < minValue) {
				minValue = value;
			}
		}
		return minValue;
	}
	public float getModelMaxTemperature() {
		Collection<YearData> values = YearsData.values();
		float maxValue = Float.MIN_VALUE;
		for(YearData year : values) {
			float value = year.findMaxTemperature();
			if(value > maxValue) {
				maxValue = value;
			}
		}
		return maxValue;
	}
	public String stringYear(int year) {
		return YearsData.get(year).toString();
	}
}