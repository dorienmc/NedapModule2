package OfflineTesting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.junit.*;

/**
 * Created by dorien.meijercluwen on 31/03/2017.
 */
public class UtilsTest {

  @Test
  public void parseIP() throws Exception {
    Assert.assertEquals(0, Utils.parseIP("0.0.0.0"));
    Assert.assertEquals(255, Utils.parseIP("0.0.0.255"));
    Assert.assertEquals(256, Utils.parseIP("0.0.1.0"));
    Assert.assertEquals(65536, Utils.parseIP("0.1.0.0"));
    Assert.assertEquals(16777216, Utils.parseIP("1.0.0.0"));
    Assert.assertEquals(16843008, Utils.parseIP("1.1.1.0"));
    Assert.assertEquals(209813889, Utils.parseIP("12.129.129.129"));
  }


  @Test
  public void ipToHuman() throws Exception {
    String ipAddress= "12.129.129.129";
    Assert.assertEquals(ipAddress, Utils.ipToHuman(Utils.parseIP(ipAddress)));
  }

  @Test
  public void cutOff() throws Exception {
    int a = 255;
    for(int i = 8; i >= 0; i--) {
      Assert.assertEquals((int)Math.pow(2,i) - 1,Utils.cutOff(a,i));
      a = Utils.cutOff(a,i);
    }

    Assert.assertEquals(27, Utils.cutOff(55,5));
    Assert.assertEquals(13, Utils.cutOff(55,4));
    Assert.assertEquals(6, Utils.cutOff(55,3));
    Assert.assertEquals(3, Utils.cutOff(55,2));
    Assert.assertEquals(1, Utils.cutOff(55,1));
  }

  @Test
  public void toBinary32String() throws Exception {
    Assert.assertEquals("00000000000000000000000000000000", Utils.toBinary32String(0));
    Assert.assertEquals("00000000000000000000000011111111", Utils.toBinary32String(255));
    Assert.assertEquals("00000000000000000000000100000000", Utils.toBinary32String(256));

    Assert.assertEquals("00000000000000010000000000000000", Utils.toBinary32String(65536));
    Assert.assertEquals("00000001000000000000000000000000", Utils.toBinary32String(16777216));
    Assert.assertEquals("00000001000000010000000100000000", Utils.toBinary32String(16843008));
    Assert.assertEquals("00001100100000011000000110000001", Utils.toBinary32String(209813889));
  }

  @Test
  public void matchingBits() throws Exception {
    int ip1 = Utils.parseIP("1.0.0.0");
    int ip2 = Utils.parseIP("1.0.0.1");
    int ip3 = Utils.parseIP("1.0.1.0");
    int ip4 = Utils.parseIP("1.0.6.0");
    int ip5 = Utils.parseIP("1.4.0.0");
    int ip6 = Utils.parseIP("3.0.0.0");
    int ip7 = Utils.parseIP("129.129.0.0");

    Assert.assertEquals(32,Utils.matchingBits(ip1,ip1));
    Assert.assertEquals(31,Utils.matchingBits(ip1,ip2));
    Assert.assertEquals(30,Utils.matchingBits(ip1,ip2 + 1));
    Assert.assertEquals(23,Utils.matchingBits(ip1,ip3));
    Assert.assertEquals(22,Utils.matchingBits(ip1,ip3 + 256));
    Assert.assertEquals(21,Utils.matchingBits(ip1,ip4));
    Assert.assertEquals(13,Utils.matchingBits(ip1,ip5));
    Assert.assertEquals(6,Utils.matchingBits(ip1,ip6));
    Assert.assertEquals(0,Utils.matchingBits(ip1,ip7));
  }

  @Test
  public void isIPinPrefixRange() throws Exception {
    int ip1 = Utils.parseIP("1.0.0.0");
    int ip2 = Utils.parseIP("1.0.6.0");

    Assert.assertEquals(true,Utils.isIPinPrefixRange(ip2,20,ip1));
    Assert.assertEquals(true,Utils.isIPinPrefixRange(ip2,21,ip1));
    Assert.assertEquals(false,Utils.isIPinPrefixRange(ip2,22,ip1));
  }

  @Test
  public void getNthByteBlock() throws Exception {
    for(int j = 0; j < 10; j++) {
      int[] r = {(int) Math.random() * 255, (int) Math.random() * 255,
          (int) Math.random() * 255, (int) Math.random() * 255};
      int ip = Utils.parseIP(String.format("%d.%d.%d.%d", r[0], r[1], r[2], r[3]));

      for (int i = 0; i < 4; i++) {
        Assert.assertEquals(r[3 - i], Utils.getNthByteBlock(ip, i));
      }
    }
  }

  //TODO
  @Test
  public void bs_lower_bound() throws Exception {
    List<Route> routes = getRoutes("routes.txt");
    Assert.assertEquals(0,Utils.bs_lower_bound(routes, Utils.parseIP("1.0.0.0")));
    Assert.assertEquals(4,Utils.bs_lower_bound(routes,Utils.parseIP("1.0.19.6")));
  }

  private List<Route> getRoutes(String filename) {
    List<Route> routes = new ArrayList<>();
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(filename));
      String line;
      while ((line = br.readLine()) != null) {
        routes.add(this.parseRoute(line));
      }
    } catch (IOException e) {
      System.err.println("Could not open " + filename);
    }
    return routes;
  }

  private Route parseRoute(String line) {
    String[] split = line.split("\t");
    int portNumber = Integer.parseInt(split[1]);

    split = split[0].split("/");
    byte prefixLength = Byte.parseByte(split[1]);

    int ip = Utils.parseIP(split[0]);
    return new Route(ip,prefixLength,(byte) portNumber);
  }
//
//  @Test
//  public void bs_upper_bound() throws Exception {
//
//  }

  @Test
  public void roundIpUpDown() {
    int ip = Utils.parseIP("130.89.1.1");

    Assert.assertEquals(ip, Utils.roundIpDown(ip,32));
    Assert.assertEquals(ip, Utils.roundIpUp(ip,32));

    Assert.assertEquals(ip - 1, Utils.roundIpDown(ip,31));
    Assert.assertEquals(ip, Utils.roundIpUp(ip,31));

    Assert.assertEquals(ip - 1, Utils.roundIpDown(ip,30)); //01 -> //00
    Assert.assertEquals(ip + 2, Utils.roundIpUp(ip,30)); //01 -> //11

    Assert.assertEquals(ip - 1, Utils.roundIpDown(ip,29)); //001 -> //000
    Assert.assertEquals(ip + 6, Utils.roundIpUp(ip,29)); //001 -> //111

    Assert.assertEquals(ip - 1, Utils.roundIpDown(ip,28)); //0001 -> //00000
    Assert.assertEquals(ip + 14, Utils.roundIpUp(ip,28)); //0001 -> //1111

    Assert.assertEquals(ip - 1, Utils.roundIpDown(ip,27)); //00001 -> //00000
    Assert.assertEquals(ip + 30, Utils.roundIpUp(ip,27)); //00001 -> //11111

    Assert.assertEquals(ip - 1, Utils.roundIpDown(ip,26)); //00 0001 -> //000000
    Assert.assertEquals(ip + 62, Utils.roundIpUp(ip,26)); //000001 -> //111111

    Assert.assertEquals(ip - 1, Utils.roundIpDown(ip,25)); //000 0001 -> //000 0000
    Assert.assertEquals(ip + 126, Utils.roundIpUp(ip,25)); //000 0001 -> //111 1111

    Assert.assertEquals(ip - 1, Utils.roundIpDown(ip,24)); //00000001 -> //0000 0000
    Assert.assertEquals(ip + 254, Utils.roundIpUp(ip,24)); //00000001 -> //1111 1111

    Assert.assertEquals(ip - 257, Utils.roundIpDown(ip,23)); //1.1 -> //0 00000000
    Assert.assertEquals(ip + 254, Utils.roundIpUp(ip,23)); //1.1 -> //1 11111111
  }
}