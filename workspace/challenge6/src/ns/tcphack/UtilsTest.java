package ns.tcphack;

import static org.junit.Assert.*;

import java.util.Arrays;
import org.junit.Test;

/**
 * Created by dorien.meijercluwen on 06/04/2017.
 */
public class UtilsTest {

  @org.junit.Test
  public void hexToString() throws Exception {
    assertEquals("0018",Utils.HexToString(24,4));
    assertEquals("f8b92650",Utils.HexToString(4172883536L,8));
  }

  @Test
  public void textToHexString() throws Exception {
    assertEquals("48545450",Utils.textToHexString("HTTP"));
  }

  @Test
  public void padString() throws Exception {
    assertEquals("0018",Utils.padString("18",4));
  }

  @Test
  public void hexStringToHex() throws Exception {
    assertEquals(9292,Utils.hexStringToHex("244c"));
  }

  @Test
  public void stringToHexArr() throws Exception {
    int[] expected = {248,185,38,80};
    assertArrayEquals(expected,Utils.stringToHexArr("f8b92650"));
  }

  @Test
  public void arrayToString() throws Exception {
    int[] data = {248,185,38,80};
    assertEquals("f8b92650",Utils.arrayToString(data));
  }

  @Test
  public void copyTo() throws Exception {
    int[] pktTo = {1,2,3,4,5,6,7,8};
    int[] expected = {1,2,1,2,3,6,7,8};
    int[] pktFrom = {1,2,3};
    Utils.copyTo(pktFrom,pktTo,2);
    assertArrayEquals(expected,pktTo);
  }

  @Test
  public void parseAddress() throws Exception {
    String address = "2001:67c:2564:a125:cc2a::b0e2";
    String expected = "2001067c2564a125cc2a00000000b0e2";
    assertEquals(expected,Utils.parseAddress(address));
  }

  @Test
  public void textToHexArr() throws Exception {
    int[] expected = {72,84,84,80};
    assertArrayEquals(expected,Utils.textToHexArr("HTTP"));
  }

}