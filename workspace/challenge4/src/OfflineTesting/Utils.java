package OfflineTesting;

import java.util.List;

/**
 * Created by dorien.meijercluwen on 31/03/2017.
 */
public class Utils {

  /**
   * Converts an integer representation IP to the human readable form
   *
   * @param ip The IP address to convert
   * @return The String representation for the IP (as xxx.xxx.xxx.xxx)
   */
  public static String ipToHuman(int ip) {
    return Integer.toString(ip >> 24 & 0xff) + "." +
        Integer.toString(ip >> 16 & 0xff) + "." +
        Integer.toString(ip >> 8 & 0xff) + "." +
        Integer.toString(ip & 0xff);
  }

  /* Return 'len' leftmost bits. */
  public static int cutOff(int ip, int len){
    while (ip >= Math.pow(2,len)){
      ip = ip >>> 1;
    }
    return ip;
  }

  /**
   * Return binary representation in 32bits,
   * adds paddings zeros if needed.
   * @param ip
   */
  public static String toBinary32String(int ip){
    return String.format("%32s", Integer.toBinaryString(ip)).replace(' ','0');
  }

  public static long getUnsignedInt(int x) {
    return x & 0x00000000ffffffffL;
  }

  /* Get binary search lowerbound for whole array*/
  public static int bs_lower_bound(List<Route> arr, int ip) {
    return bs_lower_bound(arr,ip,0,arr.size());
  }

  /* Get binary search lowerbound */
  public static int bs_lower_bound(List<Route> arr, int ip, int l, int h) {
    int low = l;
    int high = h;
    if(low >= high) {
      return low;
    }

    while(low < high) {
      int mid = (low + high) / 2;
      if(arr.get(mid).compareTo(ip) >= 0) {
        high = mid;
      } else {
        low = mid + 1;
      }
    }
    return low;
  }

  /* Get binary search upperbound */
  public static int bs_upper_bound(List<Route> arr, int ip, int len, int l, int h) {
    int low = l;
    int high = h;
    if(low >= high) {
      return low;
    }

    while(low < high) {
      int mid = (low + high) / 2;
      if(arr.get(mid).compareTo(ip) <= 0) {
        low = mid + 1;
      } else {
        high = mid;
      }
    }
    return low;
  }

  /**
   * Parses an IP
   *
   * @param ipString The IP address to convert
   * @return The integer representation for the IP
   */
  public static int parseIP(String ipString) {
    String[] ipParts = ipString.split("\\.");

    int ip = 0;
    for (int i = 0; i < 4; i++) {
      ip |= Integer.parseInt(ipParts[i]) << (24 - (8 * i));
    }

    return ip;
  }

  /*
  Return the number of bits in which the routeIp and given ip match (from left to right).
  Assumes both are 32 bits long.
   */
  static int matchingBits(int routeIp,
      int ip) {
//    String diff = Integer.toBinaryString(routeIp ^ ip);
//    int diffLength = (diff.equals("0") ? 0 : diff.length());
//    //System.out.println("difference between routeIp and ip " + diff + " so they match in the first " + (32 - diff.length()) + " bits.");
//    return 32 - diffLength;
    int diff = routeIp ^ ip;
    int count = 0;
    while(Utils.getUnsignedInt(diff) > 0) {
      diff = diff >>> 1;
      count++;
    }
    return 32 - count;
  }

  /* //An ip matches a given routeIp if the first 'prefixLength' bytes are equal. */
  static boolean isIPinPrefixRange(int routeIp, int prefixLength, int ip) {
    return (matchingBits(routeIp, ip) >= prefixLength);
  }

  /*
	Get nth block of bytes in the given bitarray/number,
	from right to left (eg for left most set n=3).
	 */
  static int getNthByteBlock(int number, int n) {
    return (number >> (8*n)) & 0xff;
  }

  static int roundIpDown(int ip, int prefixLength) {
    int mask = (1 << (32-prefixLength)) - 1; //2^(32-prefixLength) - 1
    ip = ip | mask;
    return ip - mask;
  }

  static int roundIpUp(int ip, int prefixLength) {
    int mask = (1 << (32-prefixLength)) - 1; //2^(32-prefixLength) - 1
    return ip | mask;
  }
}
