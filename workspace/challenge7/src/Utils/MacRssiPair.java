package Utils;

/**
 * Represents a MAC address - RSSI pair
 * @author Bernd
 *
 */
public class MacRssiPair {
	private byte[] mac;
	private int rssi;
	public static final int REFERENCE_RSSI = -40;
	public static final double N = 2.4;
	public static final double SCALING = 8.6;

	
	public MacRssiPair(byte[] mac, int rssi){
		this.mac = mac;
		this.rssi = rssi;
	}

	public byte[] getMac() {
		return mac;
	}
	
	public long getMacAsLong(){
		return Utils.macToLong(mac);
	}
	
	public String getMacAsString(){
		return bytesToMAC(mac);
	}

	public int getRssi() {
		return rssi;
	}

	/* Get weight based on reference RSSI and measured RSSI; */
	public double getWeight() {
		if(this.rssi == 0) {
			return 0;
		} else {
			return Math.exp(10 * REFERENCE_RSSI/(double)this.rssi);
		}
	}

	/* Get estimated distance (in 'position scale') from RSSI */
	public double getDistance() {
		return SCALING * Math.pow(10,(REFERENCE_RSSI - this.rssi)/(10.0 * N));
	}


	@Override
	public String toString(){
		return bytesToMAC(mac)+"  "+rssi;		
	}
	
	final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
	/**
	 * Helper method to convert the 6 bytes of the MAC address to a human readable string
	 * @param bytes
	 * @return
	 */
	public static String bytesToMAC(byte[] bytes) {
	    char[] hexChars = new char[(6 * 3) - 1];
	    for ( int i = 0; i < 6; i++ ) {
	        int v = bytes[i] & 0xFF;
	        hexChars[i * 3] = hexArray[v >>> 4];
	        hexChars[i * 3 + 1] = hexArray[v & 0x0F];
	        if(i<5)hexChars[i * 3 + 2] = ':';
	    }
	    return new String(hexChars);
	}
}
