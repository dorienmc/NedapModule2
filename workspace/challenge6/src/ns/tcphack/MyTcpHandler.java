package ns.tcphack;

//2001:67c:2564:a125:1493:9083:7cbd:fc93
class MyTcpHandler extends TcpHandler {
	private float seqNumber = 0;
	private float ackNumber = 0;
	//2001:67c:2564:a183::1
	private static String SOURCE_ADDR = "2001:67c:2564:a125:cc2a:4556:4263:b0e2";//"2001067c2564a125149390837cbdfc93";
	private static String DEST_ADDR = "2001:67c:2564:a170:204:23ff:fede:4b2c"; //http://[2001:67c:2564:a170:204:23ff:fede:4b2c]:7710/s1234567
	private static int DEST_PORT = 7710;
	private static String STUDENT_NUMBER = "s166367";
	boolean done = false;

	public static void main(String[] args) {
		new MyTcpHandler();
	}

	public MyTcpHandler() {
		super();

		// Create new packet
		this.sendData(createFirstPacket());	// send the packet

		while (!done) {
			// check for reception of a packet, but wait at most 500 ms:
			int[] rxpkt = this.receiveData(500);
			//TODO: Create packet from int[]
			if (rxpkt.length==0) {
				// nothing has been received yet
				System.out.println("Nothing...");
				continue;
			}

			// something has been received
			int len=rxpkt.length;
			Packet incomingPacket = new Packet(rxpkt);

			// print the received bytes:
			System.out.print("Received "+ incomingPacket);

			//Process incoming packet
			processIncomingPacketArray(incomingPacket);
		}   
	}

	/* Process incoming packet and create response packet
	* Depends on flags of the incomming packet.
	* - ACK + SYN: Send HTTP get
	* - otherwise: Stop, eg. send FIN packet
	* - FIN: really stop.
	* */

	private void processIncomingPacketArray(Packet packet) {
		Packet responsePacket = new Packet(SOURCE_ADDR, DEST_ADDR, DEST_PORT);
//		System.out.println("Flags " + packet.getFlags());
//		if (packet.isFlagSet(Flag.ACK) && packet.isFlagSet(Flag.SYN)) {
//			//check acknumber?
//			System.out.println("ACK: " + packet.getAckNumber() + " SEQ: " + packet.getSeqNumber());
//			responsePacket.setSeqNumber(seqNumber);
//			responsePacket.setAckNumber(packet.getNextSeqNumber());
//			responsePacket.setFlag(Flag.ACK);
//		} else if(packet.isFlagSet(Flag.ACK)) {
//			System.out.println("ACK: " + packet.getAckNumber() + " SEQ: " + packet.getSeqNumber());
//			responsePacket.setSeqNumber(seqNumber);
//			responsePacket.setAckNumber(packet.getNextSeqNumber());
//			responsePacket.setFlag(Flag.ACK);
//			String requestURI = String.format("http://[%s]:%d/%s", DEST_ADDR, DEST_PORT, STUDENT_NUMBER);
//			responsePacket.setData(createGETrequest(requestURI));
//		} else if (packet.isFlagSet(Flag.FIN)) {
//			done = true;
//		} else {
//			done = true;
////			//Send FIN
////			ackNumber = packet.getAckNumber();
////			responsePacket.setSeqNumber(ackNumber);
////			responsePacket.setFlag(Flag.FIN); //Also set ack?
//		}
////		else if (packet.isFlagSet(Flag.ACK) ) {
////			//Get http response
////			System.out.println("Data: " + packet.getData());
////
////			//Send FIN
////			ackNumber = packet.getAckNumber();
////			responsePacket.setSeqNumber(ackNumber);
////			responsePacket.setFlag(Flag.FIN); //Also set ack?
////		}
//// else if(packet.isFlagSet(Flag.RST)) {//Server has a problem, so restart handshake.
////			sendData(createFirstPacket());
////			return;
////		}
////
//		if(!done){
//			System.out.println("Send packet " + responsePacket);
//			System.out.println("ACK: " + responsePacket.getAckNumber() + " SEQ: " + responsePacket.getSeqNumber());
//			sendData(responsePacket.getPkt());
//			seqNumber = responsePacket.getNextSeqNumber();
//		}
	}

	/* Create get request */
	public int[] createGETrequest(String Request_URI) {
//		int SP = 0x20; //Hex value of ' '
//		int CRLF = 0x0d0a; //Hex value of '\r\n'
		String requestLine = Utils.textToHexString("GET") + "20" + Utils.textToHexString(Request_URI)
				+ "20" + Utils.textToHexString("HTTP/1.1") + "0d0a";
		return Utils.stringToHexArr(requestLine);
	}

	/* Create first packet */
	public int[] createFirstPacket() {
		Packet firstPacket = new Packet(SOURCE_ADDR, DEST_ADDR, DEST_PORT);
		firstPacket.setFlag(Flag.SYN);
		firstPacket.setData(Utils.textToHexArr("Test"));
		//firstPacket.setSeqNumber(seqNumber);
		seqNumber = firstPacket.getNextSeqNumber();
		System.out.println(firstPacket);
		System.out.println("ACK: " + firstPacket.getAckNumber() + " SEQ: " + firstPacket.getSeqNumber());
		return firstPacket.getPkt();
	}

}
