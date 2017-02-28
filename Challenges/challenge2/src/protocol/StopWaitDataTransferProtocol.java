package protocol;

import client.Utils;
import java.util.Arrays;

public class StopWaitDataTransferProtocol extends IRDTProtocol {
	static final int HEADERSIZE=1;   // number of header bytes in each packet
	static final int DATASIZE=64;   // max. number of user data bytes in each packet
	static int SEQ_NUMBER = 0; //sequence number of packet
	static final int ENDOFFILE_HEADER = 66;
	static final int ACK_HEADER = 42;
	static int EXPECTED_SEQNUMBER = 0;

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
			Integer[] pkt = createPacket(filePointer, fileContents, SEQ_NUMBER);

			// send the packet to the network layer
			getNetworkLayer().sendPacket(pkt);
			System.out.println("Sent one packet with header=" + pkt[0]);

			// Set time out and wait for ack
			waitForAck(pkt);

			//Update filePointer
			filePointer += DATASIZE;

			//Update sequence number
			updateSequenceNumber();
		}

		// Send end of file header
		sendEOF();
	}

	/**
	 * Update default_header eg sequence number.
	 */
	public void updateSequenceNumber() {
		SEQ_NUMBER++;
	}
	/**
   * Wait for acknowledgment of given packet, if received stop the timeout of that
	 * given packet.
	 * @param sendPacket Packet that is send.
	 */
	public void waitForAck(Integer[] sendPacket) {
		// schedule a timer for 1000 ms into the future, just to show how that works:
		Utils.Timeout.SetTimeout(500, this, sendPacket);

		boolean stop = false;
		while (!stop) {
      // Get packet from server
      Integer[] receivedPkt = getNetworkLayer().receivePacket();
      int seqNumber = sendPacket[0];
      if(receivedPkt != null && receivedPkt[0] == ACK_HEADER && receivedPkt[1] == seqNumber) {
      	Utils.Timeout.stopTimeOut(sendPacket);
        stop = true;
      }

      try {
        Thread.sleep(10);
      } catch (InterruptedException e) {
        stop = true;
      }
    }
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

	public Integer[] sendEOF() {
		Integer[] pkt = {ENDOFFILE_HEADER};
		getNetworkLayer().sendPacket(pkt);
		System.out.println("Sent EOF header");

		waitForAck(pkt);

		return pkt;
	}

	public void sendAck(int seqNumber) {
		Integer[] pkt = {ACK_HEADER, seqNumber};
		getNetworkLayer().sendPacket(pkt);
		System.out.println("Send ACK " + seqNumber);
	}

	@Override
	public void TimeoutElapsed(Object tag) {
		if(tag instanceof Integer[]) {
			Integer[] pkt = (Integer[])tag;
			//resend
			getNetworkLayer().sendPacket(pkt);
			System.out.println("Resent packet with header=" + pkt[0]);

			waitForAck(pkt);
		}
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
				// Get header (seq number)
				int seqNumber = packet[0];

				// tell the user
				System.out
						.println("Received packet, length=" + packet.length + "  first byte=" + seqNumber);

				// Send ACK back
				sendAck(packet[0]);

				// Check if packet only contains the EOF header
				if (packet.length == HEADERSIZE) {
					stop = true;
					break;
				}

				// Check if sequence number is expected
				if(seqNumber == EXPECTED_SEQNUMBER) {
					// append the packet's data part (excluding the header) to the fileContents array, first making it larger
					int oldlength = fileContents.length;
					int datalen = packet.length - HEADERSIZE;
					fileContents = Arrays.copyOf(fileContents, oldlength + datalen);
					System.arraycopy(packet, HEADERSIZE, fileContents, oldlength, datalen);
					EXPECTED_SEQNUMBER++;
				} else {
					System.out.println("Already received packet no. " + seqNumber);
				}


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
