package OfflineTesting;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Random;

/**
 * Created by dorien.meijercluwen on 31/03/2017.
 */
public abstract class AbstractPrefixMatcher {
  private String routesFile;
  private String lookupFile;
  public int nRoutes;

  public AbstractPrefixMatcher(String routesFile, String lookUpFile) {
    this.routesFile = routesFile;
    this.lookupFile = lookUpFile;
  }

  public void readAndLookUp(boolean debug, boolean random) {
    long t = System.currentTimeMillis();

    //Read routes
    this.readRoutes();

    //Print debug info
    if(debug) {
      System.out.println("Added " + nRoutes + " Routes");
      System.out.println("Took: " + (System.currentTimeMillis() - t) + "ms");
//      for (Route r : routes) {
//        System.out.println(r);
//      }
    }

    //Lookup using either 'readLookup()' or 'readLookUpRandom()'
    if(random) {
      int n = 1000;
      t = System.currentTimeMillis();

      this.readLookUpRandom(n);
      System.out.println(
          String.format("Searching for %d ips took %d ms", n, (System.currentTimeMillis() - t)));
    } else {
      this.readLookup();
    }
  }

  /**
   * Reads routes from routes.txt and parses each
   */
  public void readRoutes() {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(routesFile));
      String line;
      while ((line = br.readLine()) != null) {
        this.parseRoute(line);
        nRoutes++;
      }
    } catch (IOException e) {
      System.err.println("Could not open " + routesFile);
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
        }
      }
    }
  }

  /**
   * Parses a route and passes it to this.addRoute
   */
  void parseRoute(String line) {
    String[] split = line.split("\t");
    int portNumber = Integer.parseInt(split[1]);

    split = split[0].split("/");
    byte prefixLength = Byte.parseByte(split[1]);

    int ip = Utils.parseIP(split[0]);

    addRoute(ip, prefixLength, (byte) portNumber);
  }


  /**
   * Reads IPs to look up from lookup.bin and passes them to this.lookup
   */
  void readLookup() {
    BufferedReader br = null;
    try {
      br = new BufferedReader(new FileReader(lookupFile));
      int count = 0;
      StringBuilder sb = new StringBuilder(1024 * 4);
      // writing each lookup result to disk separately is very slow;
      // therefore, we collect up to 1024 results into a string and
      // write that all at once.

      String line;
      while ((line = br.readLine()) != null) {
        sb.append(Integer.toString(this.lookup(Utils.parseIP(line))) + "\n");
        count++;

        if (count >= 1024) {
          System.out.print(sb);
          sb.delete(0, sb.capacity());
          count = 0;
        }
      }

      System.out.print(sb);
    } catch (IOException e) {
      System.err.println("Could not open " + lookupFile);
    } finally {
      if (br != null) {
        try {
          br.close();
        } catch (IOException e) {
        }
      }
    }
  }

  /**
   * Create n random ips to lookup and tries to find a corresponding route for them.
   */
  void readLookUpRandom(int n) {
    int count = 0;
    StringBuilder sb = new StringBuilder(1024 * 4);
    // writing each lookup result to disk separately is very slow;
    // therefore, we collect up to 1024 results into a string and
    // write that all at once.

    Random rand = new Random();
    for(int i = 0; i < n; i++){
      int randomIp = rand.nextInt();
      sb.append(Integer.toString(this.lookup(randomIp)) + "\n");
      count++;

      if (count >= 1024) {
        System.out.print(sb);
        sb.delete(0, sb.capacity());
        count = 0;
      }
    }
  }

  /**
   * Add parsed route.
   */
  abstract void addRoute(int ip, byte prefixLength, byte portNumber);


  /**
   * Looks up an IP address in the routing tables
   *
   * @param ip The IP address to be looked up in int representation
   * @return The port number this IP maps to
   */
  abstract int lookup(int ip);
}
