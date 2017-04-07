package Location;

import Utils.MacRssiPair;
import Utils.Position;
import Utils.Utils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * LocationFinder that uses a RSSI weighted average to estimate the location.
 * It assumes all APs have the same strength.
 * Created by dorien.meijercluwen on 05/04/2017.
 */
public class UseReferenceRSSILocationFinder implements LocationFinder {

  private HashMap<String, Position> knownLocations; //Contains the known locations of APs. The long is a MAC address.

  public UseReferenceRSSILocationFinder(){
    knownLocations = Utils.getKnownLocations5GHz();
  }

  @Override
  public Position locate(MacRssiPair[] data) {
    printMacs(data); //print all the received data
    return interpolatePosition(getFoundFromList(data));
  }

  /**
   * Returns known APs that are found.
   */
  private List<MacRssiPair> getFoundFromList(MacRssiPair[] data) {
    ArrayList<MacRssiPair> found = new ArrayList<MacRssiPair>();
    for(int i=0; i<data.length; i++) {
      if (knownLocations.containsKey(data[i].getMacAsString())) {
        found.add(data[i]);
      }
    }
    return found;
  }

  private Position interpolatePosition(List<MacRssiPair> found) {
    double weightedX = 0;
    double weightedY = 0;
    double weightSum = 0;
    for(MacRssiPair pair: found) {
      Position pos = knownLocations.get(pair.getMacAsString());
      weightedX += pos.getX() * pair.getWeight();
      weightedY += pos.getY() * pair.getWeight();
      weightSum += pair.getWeight();
    }
    return new Position(weightedX/weightSum, weightedY/weightSum);
  }


  /**
   * Outputs all the received MAC RSSI pairs to the standard out
   * This method is provided so you can see the data you are getting
   * @param data
   */
  private void printMacs(MacRssiPair[] data) {
    for (MacRssiPair pair : data) {
      Position pos = knownLocations.get(pair.getMacAsString());
      if(pos != null){
        System.out.println(pair + " " + pos + " " + pair.getWeight());
     }

    }
  }

}
