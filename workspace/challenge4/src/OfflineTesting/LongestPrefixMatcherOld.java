package OfflineTesting;

import java.util.ArrayList;
import java.util.Arrays;

class LongestPrefixMatcherOld extends AbstractPrefixMatcher {

	// TODO: Request access token from your student assistant
	public static final String ACCESS_TOKEN = "S1113747_ow7m6";
	public static final String ROUTES_FILE = "routes.txt";
	public static final String LOOKUP_FILE = "lookup.txt";

	ArrayList<Integer> ips = new ArrayList<>();
	ArrayList<Byte> prefixes = new ArrayList<>();
	ArrayList<Integer> ports = new ArrayList<>();
	int[] firstByteMap = new int[256];
	int[] secondByteMap = new int[256];

	int addedRoutes = 0;


	/**
	 * Main entry point
	 */
	public static void main(String[] args) {
		System.out.println(ACCESS_TOKEN);
		new LongestPrefixMatcherOld();
	}

	/**
	 * Constructs a new LongestPrefixMatcherOld3 and starts routing
	 */
	public LongestPrefixMatcherOld() {
		super(ROUTES_FILE,LOOKUP_FILE);
		Arrays.fill(firstByteMap, -1);
		Arrays.fill(secondByteMap, -1);
		this.readRoutes();
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
		firstByteMap[Utils.getNthByteBlock(ip,3)] = addedRoutes;
		addedRoutes++;
		ips.add(ip);
		prefixes.add(prefixLength);
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
		ArrayList<Integer> partialMatches = filterByFirstBits(ip);
		ArrayList<Integer> partialMatchesMore = filterBySecondBits(ip);

		int bestId = -1;
		if(partialMatchesMore.size() == 0) {
			bestId = getBestId(ip, partialMatches);
		} else {
			bestId = getBestId(ip, partialMatchesMore);
			if(bestId == -1) {
				ArrayList partialMatchesDiff = new ArrayList(partialMatches);
				partialMatchesDiff.removeAll(partialMatchesMore);
				bestId = getBestId(ip, partialMatchesDiff);
			}
		}


		return (bestId == -1 ? -1: ports.get(bestId));
	}

	private int getBestId(int ip, ArrayList<Integer> partialMatches) {
		int longestMatch = 0;
		int bestId = -1;
		for (Integer id : partialMatches) {
			int routeIp = ips.get(id);
			int matchingBits = Utils.matchingBits(routeIp, ip);
			int prefixLength = prefixes.get(id);
//			System.out.println(String.format(
//					"Found match: %s matches in the first %d bits with %s, and is withing prefixrange of %s",
//					ipToHuman(routeIp), matchingBits, ipToHuman(ip),prefixLength));
			if(matchingBits >= prefixLength) {
				if (prefixLength > longestMatch) {
					longestMatch = prefixLength;
					bestId = id;
//					System.out.println(String.format(
//							"Found better match: %s matches in the first %d bits with %s, and is withing prefixrange of %s",
//							ipToHuman(routeIp), matchingBits, ipToHuman(ip),prefixLength));
				}
			}
		}
		return bestId;
	}

	/*
	Return id of ips which match in the first 8 bits.
	 */
	private ArrayList<Integer> filterByFirstBits(int ip) {
		ArrayList<Integer> partialMatchIds = new ArrayList<>();
		int upperbound = firstByteMap[Utils.getNthByteBlock(ip, 3)];
		//System.out.println("Found last occurence of " + getNthByteBlock(ip,3) + " at " + upperbound);
		if (upperbound == -1) {
			return partialMatchIds;
		}

		int lowerbound = -1;
		int counter = Utils.getNthByteBlock(ip,3);
		while (lowerbound == -1 && counter > 0) {
			counter--;
			lowerbound = firstByteMap[counter];

		}

		for (int i = lowerbound + 1; i <= upperbound && i < ips.size(); i++) {
			partialMatchIds.add(i);
			secondByteMap[Utils.getNthByteBlock(ips.get(i),2)] = i;
			//System.out.println("match at row " + (i+1) + ": " + ipToHuman(ips.get(i)));
		}
		return partialMatchIds;
	}

	private ArrayList<Integer> filterBySecondBits(int ip) {
		ArrayList<Integer> partialMatchIds = new ArrayList<>();
		int counter = Utils.getNthByteBlock(ip,2);
		int upperbound = secondByteMap[counter];
		//System.out.println("Found last occurence of " + getNthByteBlock(ip,3) + "." + counter + " at " + upperbound);
		if (upperbound == -1) {
			return partialMatchIds;
		}

		int lowerbound = -1;
		while (lowerbound == -1 && counter > 0) {
			counter--;
			lowerbound = secondByteMap[counter];
		}
		//System.out.println(lowerbound);

		for (int i = lowerbound + 1; i <= upperbound && i < ips.size(); i++) {
			//System.out.println("match at row " + (i+1) + ": " + ipToHuman(ips.get(i)));
			partialMatchIds.add(i);
		}

		return partialMatchIds;
	}




}
