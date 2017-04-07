package Utils;
/**
 * Represents an position with x and y coordinates
 * The timestamp gets automatically set during creation of the object
 * @author Bernd
 *
 */
public class Position {
	
	private double x;
	private double y;
	private long timestamp;
	
	public Position(double x, double y){
		this.x = x;
		this.y = y;
		timestamp = System.currentTimeMillis();
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	/* Get distance between 'other' position and this one.*/
	public double getDistance(Position other) {
		return Math.sqrt((this.x - other.getX())*(this.x - other.getX()) + (this.y - other.getY())*(this.y - other.getY()));
	}

	public long getTimestamp() {
		return timestamp;
	}
	
	@Override
	public String toString(){
		return "("+x+","+y+")";
	}
	
}
