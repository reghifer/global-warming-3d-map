package sample.data;

public class Position {
	private float latitude,longitude;
	public Position(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public void setPosition(float latitude, float longitude) {
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Position)) return false;
        Position position = (Position) o;
        return latitude == position.latitude && longitude == position.longitude;
    }

    @Override
    public int hashCode() {
        int result = (int) latitude;
        result = (int) (31 * result + longitude);
        return result;
    }
    
    @Override
    public String toString() {
    	return "Position :\nlat : " + latitude + ", longitude :" + longitude;
    }
}
