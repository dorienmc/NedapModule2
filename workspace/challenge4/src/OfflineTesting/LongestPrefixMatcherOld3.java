package OfflineTesting;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class LongestPrefixMatcherOld3 extends AbstractPrefixMatcher {

	// TODO: Request access token from your student assistant
	public static final String ACCESS_TOKEN = "s0179841_b1r1f";//"S1113747_ow7m6";
	public static final String ROUTES_FILE = "routes_short.txt";
	public static final String LOOKUP_FILE = "lookup.txt";
	public static final boolean DEBUG = true;
	public static final boolean RAND = false;

	LinkedList<Route> routes = new LinkedList<>();

	/**
	 * Main entry point
	 */
	public static void main(String[] args) {
		System.out.println(ACCESS_TOKEN);
		new LongestPrefixMatcherOld3();
	}

	/**
	 * Constructs a new LongestPrefixMatcherOld3 and starts routing
	 */
	public LongestPrefixMatcherOld3() {
		super(ROUTES_FILE,LOOKUP_FILE);
		super.readAndLookUp(DEBUG,RAND);
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
	void addRoute(int ip, byte prefixLength, byte portNumber) {
		//Find index for this route
		if(routes.size() == 0) {
			routes.add(new Route(ip, prefixLength, portNumber));
		} else if(routes.peekLast().compareTo(Utils.toBinary32String(ip)) < 0) {
			routes.add(new Route(ip, prefixLength, portNumber));
		} else {
			int id = Utils.bs_lower_bound(routes,ip);
			routes.add(id, new Route(ip,prefixLength,portNumber));
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
		int bestMatch = 0;

		bestRoute = findBestRouteOfLengthAtLeastN(routes,8,ip,bestMatch);

		//System.out.println("bestRoute " + bestRoute);
		return (bestRoute == null ? -1 : bestRoute.getPort());
	}

	private Route findBestRouteOfLengthAtLeastN(LinkedList<Route> arr, int n, int ip,int bestMatch) {
		LinkedList subArr = filterOnFirstNBits(routes, n, ip);

		if(subArr.size() == 0) {
			return null;
		} else if(subArr.size() <= Math.sqrt(routes.size())) {
			return getBestMatch(subArr, ip);
		} else {
			Route bestRoute = null;

			//Get best route with prefix length 'n'
			Iterator<Route> routeIt = subArr.iterator();
			while(routeIt.hasNext()) {
				Route r = routeIt.next();
				if(r.getPrefixLength() == 8) {
					int nMatchingBits = r.matchingBits(ip);
					if(nMatchingBits >= 8 && nMatchingBits > bestMatch) {
						bestRoute = r;
						bestMatch = nMatchingBits;
					}
					routeIt.remove();
				}
			}

			//Find best route with prefix length > 'n'
			Route bestRoute2 = findBestRouteOfLengthAtLeastN(subArr,n+1,ip,bestMatch);
			if(bestRoute2 != null && bestRoute2.matchingBits(ip) > bestMatch) {
				bestRoute = bestRoute2;
			}
			//System.out.println("bestRoute " + bestRoute);
			return bestRoute;

		}
	}

	private LinkedList<Route> filterOnFirstNBits(List<Route> routeArr, int len, int ip){
		LinkedList<Route> subArr = new LinkedList<>();

		//System.out.println("Find routes that start with " + toBinary32String(ip).substring(0,len));
		int lb = Utils.bs_lower_bound(routeArr,ip);//,len,0,routeArr.size());
		int ub = Utils.bs_upper_bound(routeArr,ip,len,lb,routeArr.size());
		if(DEBUG) {
			System.out.println(String.format("lb=%d, ub=%d", lb, ub));
		}
		if(ub - lb > 0) {
			subArr = new LinkedList<>(routes.subList(lb,ub));
		}
		return subArr;
	}

	private Route getBestMatch(List<Route> routeArr, int ip) {
		int bestMatch = 0;
		Route bestRoute = null;
		for(Route r: routeArr) {
			int matchLength = r.compareTo(ip);
			if(matchLength > bestMatch) {
				bestMatch = matchLength;
				bestRoute = r;
			}
		}
		return bestRoute;
	}
}
