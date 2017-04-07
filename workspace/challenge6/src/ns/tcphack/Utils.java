package ns.tcphack;

/**
 * Created by dorien.meijercluwen on 27/03/2017.
 */
public class Utils {

  /* Convert decimal 32bit integer to hexadecimal string, with the given length.
  * Pads the string with zeros at the left side. */
  public static String HexToString(int number, int length){
    return padString(Integer.toHexString(number),length);
  }

  /* Convert decimal 64bit integer to hexadecimal string, with the given length.
 * Pads the string with zeros at the left side. */
  public static String HexToString(long number, int length){
    return padString(Long.toHexString(number),length);
  }

  /* Convert text to hexadecimal string */
  public static String textToHexString(String txt) {
    String result = "";
    for(char c: txt.toCharArray()){
      result += Integer.toHexString((int)c);
    }
    return result;
  }

  /* Pad string with zeros to given length */
  public static String padString(String txt, int length) {
    return String.format("%" + length + "s", txt).replace(' ', '0');
  }

  /* Convert hex string to base16 integer */
  public static int hexStringToHex(String value) {
    return Integer.decode("0x" + value);
  }

  /* Convert hex string to array of hexadecimal integers. */
  public static int[] stringToHexArr(String hexString) {
    int length = hexString.length() / 2;
    int[] arr = new int[length];
    for (int j = 0; j < length; j++) {
      int start = j * 2;
      String substring = hexString.substring(start, start + 2);
      arr[j] = hexStringToHex(substring);
    }
    return arr;
  }

  /* Convert array of hexadecimal values to a string, each value is converted to two characters. */
  public static String arrayToString(int[] data) {
    String result = "";
    for(int i = 0; i < data.length; i++) {
      result += HexToString(data[i],2);
    }
    return result;
  }

  /* Copy given arrayFrom to given arrayTo, should start at given index (in array2)
    * Returns arr1 */
  public static void copyTo(int[] arrFrom, int[] arrTo, int startIndex) {
    if (arrTo.length >= arrFrom.length + startIndex) {
      for (int i = 0; i < arrFrom.length; i++) {
        arrTo[i + startIndex] = arrFrom[i];
      }
    }
  }

  /* Parse address from 'hex::hex:hex' to a string with 8 groups of hexadecimal numbers (of 1byte)*/
  public static String parseAddress(String address) {
    String[] splittedAddress = address.split(":");
    int[] addressInHexNumbers = new int[8*2];

    //Make sure there are 8 groups.
    int emptyGroups = 8 - splittedAddress.length;
    int count = 0;
    for(String group: splittedAddress){
      int[] tmp = Utils.stringToHexArr(Utils.padString(group,4));
      addressInHexNumbers[count] = tmp[0];
      addressInHexNumbers[count+1] = tmp[1];
      count+=2;

      if(group.length() == 0) {
        for(int i = 0; i < 2 * emptyGroups; i++) {
          addressInHexNumbers[count] = 0;
          count++;
        }
      }
    }

    return Utils.arrayToString(addressInHexNumbers);
  }



  public static int[] textToHexArr(String txt){
    String hexString = textToHexString(txt);
    return stringToHexArr(hexString);
  }
}
