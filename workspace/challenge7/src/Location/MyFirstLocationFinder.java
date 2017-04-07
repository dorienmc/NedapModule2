package Location;

import java.util.HashMap;
import Utils.*;

/**
 * Simple Location finder that returns the APs location from the list of received MAC addresses with the highest RRSI.
 * Created by dorien.meijercluwen on 05/04/2017.
 */
public class MyFirstLocationFinder implements LocationFinder {

  private HashMap<String, Position> knownLocations; //Contains the known locations of APs. The long is a MAC address.

  public MyFirstLocationFinder(){
    knownLocations = Utils.getKnownLocations(); //Put the known locations in our hashMap
  }

  @Override
  public Position locate(MacRssiPair[] data) {
    printMacs(data); //print all the received data
    return getClosestFromList(data); //return the first known APs location
  }

  /**
   * Returns the position of the AP found in the list of MacRssi pairs with highest RSSI.
   * @param data
   * @return
   */
  private Position getClosestFromList(MacRssiPair[] data){
    Position ret = new Position(0,0);
    MacRssiPair bestAP = null;
    for(int i=0; i<data.length; i++){
      if(knownLocations.containsKey(data[i].getMacAsString())){
        if(bestAP == null || data[i].getRssi() > bestAP.getRssi()) {
          bestAP = data[i];
          ret = knownLocations.get(bestAP.getMacAsString());
        }
      }
    }
    return ret;
  }

  /**
   * Outputs all the received MAC RSSI pairs to the standard out
   * This method is provided so you can see the data you are getting
   * @param data
   */
  private void printMacs(MacRssiPair[] data) {
    for (MacRssiPair pair : data) {
      if(knownLocations.containsKey(pair.getMacAsString())){
        System.out.println(pair + " " + knownLocations.get(pair.getMacAsString()));
     }

    }
  }

}
