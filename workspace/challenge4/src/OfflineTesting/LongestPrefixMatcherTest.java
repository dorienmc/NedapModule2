package OfflineTesting;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by dorien.meijercluwen on 31/03/2017.
 */
public class LongestPrefixMatcherTest {
  LongestPrefixMatcher matcher;


  @Before
  public void setUp() throws Exception {
    matcher = new LongestPrefixMatcher("routes.txt");
    matcher.readRoutes();
  }

  @Test
  public void initialisation() throws Exception {
    matcher = new LongestPrefixMatcher("routes_short.txt");
    matcher.readRoutes();

    Assert.assertEquals(false,matcher.routes.isEmpty());

    //There is one route with prefix length 17, it has port 11
    Assert.assertEquals(1,matcher.routes.get((byte)17).size());
    Assert.assertEquals(11,(matcher.routes.get((byte)17).get(0).getPort()));

    //There are two routes with prefix length 22
    Assert.assertEquals(2,matcher.routes.get((byte)22).size());

    //There is one route with prefix length 9 and it starts with 000011001
    Route r = matcher.routes.get((byte)9).get(0);
    Assert.assertEquals(9,r.matchingBits(Utils.parseIP("12.255.0.0")));
  }

  @Test
  public void lookup() throws Exception {
    //1.0.0.0 can be found, and is closest to 1.0.0.0/24 with port 1
    //Assert.assertEquals(1, matcher.lookup(Utils.parseIP("1.0.0.0")));

    String[] lookupIp = {"130.89.1.1","1.0.0.36","1.0.19.6","1.2.2.1","223.255.254.7","4.4.4.4",
        "219.99.224.1","62.207.128.1","12.129.129.129","1.0.0.1"};
    int[] expectedPort = {70,1,4,-1,72,92,15,69,53,1};
    for(int i =0 ; i < lookupIp.length; i++) {
      lookupIp(lookupIp[i], expectedPort[i]);
    }

  }

  public void lookupIp(String ip, int expectedPort) throws Exception {
    System.out.println("Looking for match with route " + ip + " expected port " + expectedPort);

    int port = matcher.lookup(Utils.parseIP(ip));
    System.out.println("Found port " + port);
    Assert.assertEquals(expectedPort, port);
  }

}