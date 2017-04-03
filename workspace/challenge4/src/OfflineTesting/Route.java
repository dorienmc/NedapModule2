package OfflineTesting;

/**
 * Created by dorien.meijercluwen on 31/03/2017.
 */
public class Route implements Comparable<Route>{
  int ip;
  byte prefixLength;
  byte port;

  public Route(int ip, byte prefixLength, byte portNumber) {
    this.ip = ip;
    this.prefixLength = prefixLength;
    this.port = portNumber;
  }

  public int getPort() {
    return port;
  }

  public int getPrefixLength() {
    return prefixLength;
  }

  /*
  Get first n characters of the ipstring
  requires: n <= 32
   */
  public String getIPString(int n) {
    return Utils.toBinary32String(ip).substring(0,n);
  }

  public String getNetworkHeader(){
    return Utils.toBinary32String(ip).substring(0,prefixLength);
  }

  public int getIP(){
    return ip;
  }

  public long getLongIP() {
    return Utils.getUnsignedInt(ip);
  }

  /*
    Return the number of bits in which this ip and the given ip match (from left to right).
    Assumes both are 32 bits long.
   */
  public int matchingBits(int ip){
    return Utils.matchingBits(this.ip, ip);
  }

  /*
  Compare given route to this route.
  Returns 0 when they are equal, -1 if this route is smaller and if this route is larger.
   */
  @Override
  public int compareTo(Route route) {
    if(this == null){
      return -1;
    }

    if(route == null) {
      return 1;
    }

    return this.getNetworkHeader().compareTo(route.getNetworkHeader());
//			byte minPrefixLength = (byte) Math.min(this.getPrefixLength(), route.getPrefixLength());
//			int a = (this.ip >>> (minPrefixLength - prefixLength));
//			int b = ((route.getIP() >>> (minPrefixLength - route.getPrefixLength())));
//			return a-b;
  }

  public int compareTo(int ip) {
    if(this == null){
      return -1;
    }

    int matchCount = matchingBits(ip);
    return (matchCount < prefixLength ? 0 : prefixLength);
  }

  public int compareTo(String ipString) {
    if(this == null){
      return -1;
    }

    if(ipString == null) {
      return 1;
    }

    return this.getNetworkHeader().compareTo(ipString);
  }



  @Override
  public String toString() {
    return Utils.ipToHuman(ip) + "/" + prefixLength + ":" + port;
    //return getNetworkHeader() + ":" + port;
  }
}
