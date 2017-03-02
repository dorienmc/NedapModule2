package protocol;

import java.util.Random;

/**
 * Simple version of CSMA, eg. transmit when server was idle, transmit with prob p when it was busy.
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class CSMASimplified implements IMACProtocol {
	static final int PROB = 20;
	private int remainingBackOffTime = 0;
	private int maxBackOffTime = 1;

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		// No data to send, just be quiet
		if (localQueueLength == 0) {
			System.out.println("SLOT - No data to send.");
			return new TransmissionInfo(TransmissionType.Silent, 0);
		}

		// Always send when server was idle in the prev. slot
		if(previousMediumState == MediumState.Idle) {
			System.out.println("SLOT - Server was idle, sending data and hope for no collision.");
			return new TransmissionInfo(TransmissionType.Data, 0);
		}

		// When server was busy, calculate backoff time

		if (new Random().nextInt(100) < PROB) {
			System.out.println("SLOT - Server was busy, sending data and hope for no collision.");
			return new TransmissionInfo(TransmissionType.Data, 0);
		} else {
			System.out.println("SLOT - Not sending data to give room for others.");
			return new TransmissionInfo(TransmissionType.Silent, 0);
		}

	}

}
