package protocol;

import client.Utils;
import java.util.Arrays;

public class SimpleDataTransferProtocol extends IRDTProtocol {
	static final int HEADERSIZE=1;   // number of header bytes in each packet
	static final int DATASIZE=128;   // max. number of user data bytes in each packet
	static final int DEFAULT_HEADER = 123;
	static final int ENDOFFILE_HEADER = 66;

	@Override
	public void sender() {
		System.out.println("Sending...");

		// read from the input file
		Integer[] fileContents = Utils.getFileContents(getFileID());

		// keep track of where we are in the data
		int filePointer = 0;

		// send packets
		while(filePointer < fileContents.length) {
			// create a new packet of appropriate size
			Integer[] pkt = createPacket(filePointer, fileContents, DEFAULT_HEADER);

			// send the packet to the network layer
			getNetworkLayer().sendPacket(pkt);
			System.out.println("Sent one packet with header=" + pkt[0]);

			// schedule a timer for 1000 ms into the future, just to show how that works:
			//Utils.Timeout.SetTimeout(1000, this, 28);

			// and loop and sleep; you may use this loop to check for incoming acks...
//			boolean stop = false;
//			while (!stop) {
			//HERE LOOK FOR ACK
//				try {
//					Thread.sleep(10);
//				} catch (InterruptedException e) {
//					stop = true;
//				}
//			}

			//Update filePointer
			filePointer += DATASIZE;
		}

		// Send end of file header
		Integer[] pkt = {ENDOFFILE_HEADER};
		getNetworkLayer().sendPacket(pkt);
		System.out.println("Sent EOF header");


	}

	public Integer[] createPacket(int filePointer, Integer[] fileContents, int header) {
		// create a new packet of appropriate size
		int datalen = Math.min(DATASIZE, fileContents.length - filePointer);
		Integer[] pkt = new Integer[HEADERSIZE + datalen];
		// create header
		pkt[0] = header;
		// copy databytes from the input file into data part of the packet, i.e., after the header
		System.arraycopy(fileContents, filePointer, pkt, HEADERSIZE, datalen);

		return pkt;
	}


	@Override
	public void TimeoutElapsed(Object tag) {
		int z=(Integer)tag;
		// handle expiration of the timeout:
		System.out.println("Timer expired with tag="+z);
	}

	@Override
	public void receiver() {
		System.out.println("Receiving...");

		// create the array that will contain the file contents
		// note: we don't know yet how large the file will be, so the easiest (but not most efficient)
		//   is to reallocate the array every time we find out there's more data
		Integer[] fileContents = new Integer[0];

		// loop until we are done receiving the file
		boolean stop = false;
		while (!stop) {

			// try to receive a packet from the network layer
			Integer[] packet = getNetworkLayer().receivePacket();

			// if we indeed received a packet
			if (packet != null) {

				// tell the user
				System.out.println("Received packet, length="+packet.length+"  first byte="+packet[0] );

				// Check if packet only contains the EOF header
				if(packet.length == HEADERSIZE){
					stop = true;
					break;
				}

				// append the packet's data part (excluding the header) to the fileContents array, first making it larger
				int oldlength=fileContents.length;
				int datalen= packet.length - HEADERSIZE;
				fileContents = Arrays.copyOf(fileContents, oldlength+datalen);
				System.arraycopy(packet, HEADERSIZE, fileContents, oldlength, datalen);

			}else{
				// wait ~10ms (or however long the OS makes us wait) before trying again
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					stop = true;
				}
			}
		}

		// write to the output file
		Utils.setFileContents(fileContents, getFileID());
	}
}
