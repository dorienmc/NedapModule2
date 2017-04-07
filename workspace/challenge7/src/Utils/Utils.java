package Utils;

import com.sun.tools.hat.internal.parser.PositionInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.geometry.Pos;

public class Utils {

	/**
	 * Make a long from a byte[6], useful for storing the MAC addresses
	 * 
	 * @param bytes
	 * @return
	 */
	public static long macToLong(byte[] bytes) {
		long ret = 0;
		ret += (bytes[0] & 0xFF) << 40;
		ret += (bytes[1] & 0xFF) << 32;
		ret += (bytes[2] & 0xFF) << 24;
		ret += (bytes[3] & 0xFF) << 16;
		ret += (bytes[4] & 0xFF) << 8;
		ret += (bytes[5]) & 0xFF;
		return ret;
	}

	/**
	 * Returns a HashMap of the known AP locations as a <String, Postion> The string
	 * is used because this is easily searchable for the hashMap
	 * 
	 * @return
	 */
	public static HashMap<String, Position> getKnownLocations() {

		HashMap<String, Position> knownLocations = new HashMap<String, Position>();

		// APs Oost Horst 115
		knownLocations.put("64:D9:89:43:C7:D0", new Position(112,45));	//ap3600-0102  THIS ONE IS NOT BROADCASTING!
		knownLocations.put("64:D9:89:43:C1:50", new Position(190,45));	//ap3600-0099
		knownLocations.put("64:D9:89:46:01:30", new Position(190,6));	//ap3600-0100		
		knownLocations.put("64:D9:89:43:C4:B0", new Position(112,6));	//ap3600-0101
		
		// APs Oost Horst 116
		knownLocations.put("64:D9:89:43:CF:E0", new Position(58,6));	//ap3600-0104
		knownLocations.put("64:D9:89:43:D4:F0", new Position(10,6));	//ap3600-0105
		knownLocations.put("64:D9:89:43:CD:60", new Position(10,45));	//ap3600-0106
		knownLocations.put("64:D9:89:43:D0:00", new Position(58,45));	//ap3600-0103  THIS ONE IS NOT BROADCASTING!		

		return knownLocations;
	}

	public static HashMap<String, Position> getKnownLocations5GHz() {

		HashMap<String, Position> knownLocations = new HashMap<String, Position>();

		// APs Oost Horst 115
		knownLocations.put("64:D9:89:43:C7:DF", new Position(112,45));	//ap3600-0102	5GHz
		knownLocations.put("64:D9:89:43:C1:5F", new Position(190,45));	//ap3600-0099	5GHz
		knownLocations.put("64:D9:89:46:01:3F", new Position(190,6));	//ap3600-0100	5GHz
		knownLocations.put("64:D9:89:43:C4:BF", new Position(112,6));	//ap3600-0101	5GHz

		// APs Oost Horst 116
		knownLocations.put("64:D9:89:43:CF:EF", new Position(58,6));	//ap3600-0104	5GHz
		knownLocations.put("64:D9:89:43:D4:FF", new Position(10,6));	//ap3600-0105	5GHz
		knownLocations.put("64:D9:89:43:CD:6F", new Position(10,45));	//ap3600-0106	5GHz
		knownLocations.put("64:D9:89:43:D0:0F", new Position(58,45));	//ap3600-0103	5GHz

		return knownLocations;
	}

	/*
		Get intersections between circles around Positions p0,p1 (with radius r0, r1)
	 */
	public static List<Position> calculateCircleIntersections(Position p0, double r0,
			Position p1, double r1) {
		ArrayList<Position> intersectionPoints = new ArrayList<Position>();
		//System.out.println(p0 + " " + p1);

		//Determine distance between circle 0 and circle 1
		double dp0_p1 = p0.getDistance(p1);
		//System.out.println("d " + dp0_p1);

    /* Check for solvability. */
		if (dp0_p1 > (r0 + r1)) {
        /* no solution. circles do not intersect. */
			return intersectionPoints;
		} else if (dp0_p1 < Math.abs(r0 - r1)) {
        /* no solution. one circle is contained in the other */
			return intersectionPoints;
		}

    /* 'point 2' is the point where the line through the 'circle
    * intersection points' crosses the line between the circle
    * centers.
    */

    /* Determine the distance from point p0 to point 2. */
		double dp0_p2 = (r0*r0 - r1*r1 + dp0_p1*dp0_p1) / (2*dp0_p1);
		//System.out.println("d1 " + dp0_p2);

    /* Determine the coordinates of point 2. */
    double x2 = p0.getX() + (dp0_p2/dp0_p1) * (p1.getX() - p0.getX());
    double y2 = p0.getY() + (dp0_p2/dp0_p1) * (p1.getY() - p0.getY());
    Position p2 = new Position(x2,y2);
		//System.out.println("p2 " + p2);

    /* Determine the distance from point 2 to either of the
    * intersection points.
    */
		double h = Math.sqrt((r0*r0) - dp0_p2 * dp0_p2);
		//System.out.println("h " + h);

		/* Determine the absolute intersection points. */
    double rx = h * (p1.getX() - p0.getX())/dp0_p1;
    double ry = h * (p1.getY() - p0.getY())/dp0_p1;
		//System.out.println("rx " + rx + " ry " + ry);
		intersectionPoints.add(new Position(p2.getX() + ry, p2.getY() - rx));
		intersectionPoints.add(new Position(p2.getX() - ry, p2.getY() + rx));

		return intersectionPoints;
	}

	public static void main(String[] args) {
		Position p0 = new Position(-9,1);
		Position p1 = new Position(5,-5);
		System.out.println(calculateCircleIntersections(p0,7,p1,18));

		p0 = new Position(0,0);
		p1 = new Position(4,3);
		System.out.println(calculateCircleIntersections(p0,3,p1,2.5));
	}

}

