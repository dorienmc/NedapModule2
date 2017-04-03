package OfflineTesting;

import java.util.ArrayList;

class LongestPrefixMatcherOld2 extends AbstractPrefixMatcher {

	// TODO: Request access token from your student assistant
	public static final String ACCESS_TOKEN = "S1113747_ow7m6";
	public static final String ROUTES_FILE = "routes_short.txt";
	public static final String LOOKUP_FILE = "lookup.txt";

	ArrayList<Integer> ips = new ArrayList<>();
	ArrayList<Integer> ports = new ArrayList<>();

	/**
	 * Main entry point
	 */
	public static void main(String[] args) {
		System.out.println(ACCESS_TOKEN);
		new LongestPrefixMatcherOld2();
	}

	/**
	 * Constructs a new LongestPrefixMatcherOld3 and starts routing
	 */
	public LongestPrefixMatcherOld2() {
		super(ROUTES_FILE, LOOKUP_FILE);
		this.readRoutes();
		System.out.println("Routes");
		for(Integer i: ips) {
			System.out.println(Integer.toBinaryString(i));
		}
		this.readLookup();
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
		ips.add(ip >>> (int) (32 - prefixLength));
		//Collections.sort(ips);
		ports.add((int)portNumber);
	}

	/**
	 * Looks up an IP address in the routing tables
	 *
	 * @param ip The IP address to be looked up in int representation
	 * @return The port number this IP maps to
	 */
	@Override
	int lookup(int ip) {
		System.out.println("Compare");
		int bestMatch = 0;
		int bestId = -1;
		int id = 0;
		for(Integer routeIp: ips) {
			int routeIPLength = Integer.toBinaryString(routeIp).length();
			int choppedIp = (ip >>> (32 - routeIPLength));

			System.out.println(Integer.toBinaryString(choppedIp) + " ?= " + Integer.toBinaryString(routeIp));
			//System.out.println(choppedIp + "? = " + routeIp);
			if(routeIp == choppedIp) {
				System.out.println(routeIp + " = " + choppedIp);
				if(routeIPLength > bestMatch) {
					bestMatch = routeIPLength;
					bestId = id;
				}
			}
			id++;
		}
		if(bestId > -1) {
			System.out.println(bestMatch + " " + ips.get(bestId) + " " + ports.get(bestId));
		}
		//ArrayList<Integer> partialMatches = filterByFirstBits(ip);

		//int bestId =getBestId(ip, partialMatches);// -1;
//		if(partialMatchesMore.size() == 0) {
//			bestId = getBestId(ip, partialMatches);
//		} else {
//			bestId = getBestId(ip, partialMatchesMore);
//			if(bestId == -1) {
//				ArrayList<Integer> tmp = new ArrayList<Integer>();
//				for(Integer i: partialMatches) {
//					//if(partialMatchesMore.)
//				}
//				ArrayList partialMatchesDiff = new ArrayList(partialMatches);
//				partialMatchesDiff.removeAll(partialMatchesMore);
//				bestId = getBestId(ip, partialMatchesDiff);
//			}
//		}


		return -1;//return (bestId == -1 ? -1: ports.get(bestId));
	}

//	private int getBestId(int ip, ArrayList<Integer> partialMatches) {
//		int longestMatch = 0;
//		int bestId = -1;
//		for (Integer id : partialMatches) {
//			int routeIp = ips.get(id);
//			int matchingBits = matchingBits(routeIp, ip);
//			int prefixLength = prefixes.get(id);
//			System.out.println(String.format(
//					"Found match: %s matches in the first %d bits with %s, and is withing prefixrange of %s",
//					ipToHuman(routeIp), matchingBits, ipToHuman(ip),prefixLength));
//			if(matchingBits >= prefixLength) {
//				if (prefixLength > longestMatch) {
//					longestMatch = prefixLength;
//					bestId = id;
////					System.out.println(String.format(
////							"Found better match: %s matches in the first %d bits with %s, and is withing prefixrange of %s",
////							ipToHuman(routeIp), matchingBits, ipToHuman(ip),prefixLength));
//				}
//			}
//		}
//		return bestId;
//	}

//	/*
//	Return id of ips which match in the first 8 bits.
//	Sort on prefix size.
//	 */
//	private ArrayList<Integer> filterByFirstBits(int ip) {
//		ArrayList<Integer> partialMatchIds = new ArrayList<>();
//		int upperbound = firstByteMap[getNthByteBlock(ip, 3)];
//		//System.out.println("Found last occurence of " + getNthByteBlock(ip,3) + " at " + upperbound);
//		if (upperbound == -1) {
//			return partialMatchIds;
//		}
//
//		int lowerbound = -1;
//		int counter = getNthByteBlock(ip,3);
//		while (lowerbound == -1 && counter > 0) {
//			counter--;
//			lowerbound = firstByteMap[counter];
//
//		}
//
//		for (int i = lowerbound + 1; i <= upperbound && i < ips.size(); i++) {
//			if(partialMatchIds.size() == 0) {
//				partialMatchIds.add(i);
//			} else {
//				int count = 0;
//				for(Integer j: partialMatchIds) {
//					if(prefixes.get(j) > prefixes.get(i)) {
//						partialMatchIds.add(count, i);
//						break;
//					} else if(prefixes.get(j) == prefixes.get(i) && ips.get(i) < ips.get(j)) {
//						partialMatchIds.add(count, i);
//						break;
//					}
//					count++;
//				}
//				if(count == partialMatchIds.size()) {
//					partialMatchIds.add(i);
//				}
//			}
//
//			secondByteMap[getNthByteBlock(ips.get(i),2)] = i;
//			//System.out.println("match at row " + (i+1) + ": " + ipToHuman(ips.get(i)));
//		}
//
//		return partialMatchIds;
//	}
//
//
//
//	private ArrayList<Integer> filterBySecondBits(int ip) {
//		ArrayList<Integer> partialMatchIds = new ArrayList<>();
//		int counter = getNthByteBlock(ip,2);
//		int upperbound = secondByteMap[counter];
//		//System.out.println("Found last occurence of " + getNthByteBlock(ip,3) + "." + counter + " at " + upperbound);
//		if (upperbound == -1) {
//			return partialMatchIds;
//		}
//
//		int lowerbound = -1;
//		while (lowerbound == -1 && counter > 0) {
//			counter--;
//			lowerbound = secondByteMap[counter];
//		}
//		//System.out.println(lowerbound);
//
//		for (int i = lowerbound + 1; i <= upperbound && i < ips.size(); i++) {
//			//System.out.println("match at row " + (i+1) + ": " + ipToHuman(ips.get(i)));
//			partialMatchIds.add(i);
//		}
//
//		return partialMatchIds;
//	}

}
