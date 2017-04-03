import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;


class LongestPrefixMatcher extends AbstractPrefixMatcher {

	// TODO: Request access token from your student assistant
	public static final String ACCESS_TOKEN = "s0166367_vs7hk";
	public static final String ROUTES_FILE = "routes.txt";
	public static final String LOOKUP_FILE = "lookup.txt";
	public static final boolean DEBUG = false;
	public static final boolean RAND = false;
	private int minPrefixLength = 8;
	private int maxPrefixLength = 8;

	public Map<Byte,LinkedList<Route>> routes = new HashMap<>();

	/**
	 * Main entry point
	 */
	public static void main(String[] args) {
		System.out.println(ACCESS_TOKEN);
		new LongestPrefixMatcher();
	}

	/**
	 * Constructs a new LongestPrefixMatcher and starts routing
	 */
	public LongestPrefixMatcher() {
		super(ROUTES_FILE, LOOKUP_FILE);
		super.readAndLookUp(DEBUG,RAND);
	}

  /**
	 * Constructs a new LongestPrefixMatcher for testing purposes.
	 */
	public LongestPrefixMatcher(String routeFile) {
		super(routeFile,"");
	}

	/**
	 * Adds a route to the routing tables
	 *
	 * @param ip The IP the block starts at in integer representation
	 * @param prefixLength The number of bits indicating the network part of the address range
	 * (notation ip/prefixLength)
	 * @param portNumber The port number the IP block should route to
	 */
	@Override
	public void addRoute(int ip, byte prefixLength, byte portNumber) {
		if(prefixLength > maxPrefixLength) { maxPrefixLength = prefixLength;}

		if(!routes.containsKey(prefixLength)) {
			routes.put(prefixLength, new LinkedList<Route>());
		}

		//Find index for this route
		if(routes.get(prefixLength).size() == 0) {
			routes.get(prefixLength).add(new Route(ip, prefixLength, portNumber));
		} else if (routes.get(prefixLength).peekLast().getLongIP() <= Utils.getUnsignedInt(ip)) {
			//else if(routes.get(prefixLength).peekLast().compareTo(Utils.toBinary32String(ip)) < 0) {
			routes.get(prefixLength).add(new Route(ip, prefixLength, portNumber));
		} else {
			int id = Utils.bs_lower_bound(routes.get(prefixLength),ip);
			routes.get(prefixLength).add(id, new Route(ip,prefixLength,portNumber));
		}
	}

	/**
	 * Looks up an IP address in the routing tables
	 *
	 * @param ip The IP address to be looked up in int representation
	 * @return The port number this IP maps to
	 */
	@Override
	int lookup(int ip) {
		if(DEBUG) {System.out.println("Find best route for " + Utils.ipToHuman(ip));}

		Route bestRoute = null;
		for(int i = maxPrefixLength; i >= minPrefixLength; i--) {
			bestRoute = findMatchWithPrefixLength(ip, (byte)i);
			if(bestRoute != null) {
				//System.out.println("Best route " + bestRoute);
				return bestRoute.getPort();
			}
		}

		return -1;
	}

	private Route findMatchWithPrefixLength(int ip, byte prefixLength) {
		if(!routes.containsKey(prefixLength)) {
			return null;
		}

		int nRoutes = routes.get(prefixLength).size();

		long ipLow = Utils.roundIpDown(ip,prefixLength);
		long ipHigh = Utils.roundIpUp(ip,prefixLength);
		//System.out.println("Prefix " + prefixLength + ", Search between " + Utils.ipToHuman((int)ipLow) + " and " + Utils.ipToHuman((int)ipHigh));

		int idLow = Utils.bs_lower_bound(routes.get(prefixLength),(int)ipLow);
		if(idLow < nRoutes) {
			int idHigh = Utils.bs_upper_bound(routes.get(prefixLength),(int)ipHigh,0,idLow,nRoutes);
			if(idHigh <= nRoutes) {
				for(int i = idLow; i < idHigh; i++) {
					Route r = routes.get(prefixLength).get(i);
					//System.out.println(routes.get(prefixLength).get(i) + " matches in " + r.matchingBits(ip) + " bits");
					if(r.matchingBits(ip) >= prefixLength) {
						return r;
					}
				}
			}
		}

		return null;
	}





}


/**
 * Created by dorien.meijercluwen on 31/03/2017.
 */
class Utils {

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

		//int cutOffIp = cutOff(ip,len);
		//String ipString = toBinary32String(ip).substring(0,len);
		long unsignedIp = Utils.getUnsignedInt(ip);
		while(low < high) {
			int mid = (low + high) / 2;
			//String val = arr.get(mid).getIPString(len);
			//if(cutOffIp <= cutOff(arr.get(mid).getIP(),len)) {//ipString.compareTo(val) <= 0) { //x <= a[mid]
			long midIp = arr.get(mid).getLongIP();
			if(unsignedIp <= midIp) {
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

		//int cutOffIp = cutOff(ip,len);
		//String ipString = toBinary32String(ip).substring(0,len);
		long unsignedIp = Utils.getUnsignedInt(ip);
		while(low < high) {
			int mid = (low + high) / 2;
			//String val = arr.get(mid).getIPString(len);
			long midIp = arr.get(mid).getLongIP();
			//if(cutOffIp >= cutOff(arr.get(mid).getIP(),len)) {//ipString.compareTo(val) >= 0) {
			if(unsignedIp >= midIp) {
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
		String diff = Integer.toBinaryString(routeIp ^ ip);
		int diffLength = (diff.equals("0") ? 0 : diff.length());
		//System.out.println("difference between routeIp and ip " + diff + " so they match in the first " + (32 - diff.length()) + " bits.");
		return 32 - diffLength;
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

	static long roundIpDown(int ip, int prefixLength) {
		int mask = (1 << (32-prefixLength)) - 1; //2^(32-prefixLength) - 1
		ip = ip | mask;
		return ip - mask;
	}

	static long roundIpUp(int ip, int prefixLength) {
		int mask = (1 << (32-prefixLength)) - 1; //2^(32-prefixLength) - 1
		return ip | mask;
	}
}



/**
 * Created by dorien.meijercluwen on 31/03/2017.
 */
abstract class AbstractPrefixMatcher {
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

/**
 * Created by dorien.meijercluwen on 31/03/2017.
 */
class Route implements Comparable<Route> {
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



