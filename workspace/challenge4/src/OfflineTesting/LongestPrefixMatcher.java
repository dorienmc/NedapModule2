package OfflineTesting;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
//		#	Startup T1	T2		T3		T4	T5	T6	Total	Lookups/s	# Hints
//1. 53	2.04	0.17	1.46	10.25	DNF	-		-		11.88	22.2			0
//2. 53	2.04	0.17	1.46	10.25	DNF	-		-		11.88	22.2			0
//3. 53	2.11	0.03	1.19	10.10	DNF	-		-		11.32	23.3			0
//4. 53	2.11	0.03	1.19	10.10	DNF	-		-		11.32	23.3			0
class LongestPrefixMatcher extends AbstractPrefixMatcher {

	// TODO: Request access token from your student assistant
	public static final String ACCESS_TOKEN = "s0166367_vs7hk";
	public static final String ROUTES_FILE = "routes.txt";
	public static final String LOOKUP_FILE = "lookup.txt";
	public static final boolean DEBUG = true;
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
			routes.put(prefixLength, new LinkedList<>());
		}

		//Find index for this route
		if(routes.get(prefixLength).size() == 0) {
			routes.get(prefixLength).add(new Route(ip, prefixLength, portNumber));
		} else if (routes.get(prefixLength).peekLast().compareTo(ip) <= 0) {
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

		int idLow = Utils.bs_lower_bound(routes.get(prefixLength),Utils.roundIpDown(ip,prefixLength));
		if(idLow < nRoutes) {
			int idHigh = Utils.bs_upper_bound(routes.get(prefixLength),Utils.roundIpUp(ip,prefixLength),0,idLow,nRoutes);
			if(idHigh <= nRoutes) {
				for(int i = idLow; i < idHigh; i++) {
					Route r = routes.get(prefixLength).get(i);
					if(r.matchingBits(ip) >= prefixLength) {
						return r;
					}
				}
			}
		}

		return null;
	}





}