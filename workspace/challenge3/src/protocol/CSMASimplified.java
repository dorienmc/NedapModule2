package protocol;

import java.util.Random;

/**
 * Simple version of CSMA, eg. transmit when server was idle, transmit with prob p when it was busy.
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class CSMASimplified implements IMACProtocol {
	private int remainingBackOffTime = 0;
	private int retransmitCounter = 1;
	NodeState prevStatus = NodeState.Idle;
	NodeState status = NodeState.Idle;

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		// Update status
		updateStatus(previousMediumState, localQueueLength);

		// No data to send, just be quiet
		if (localQueueLength == 0) {
			System.out.println("SLOT - No data to send.");
			return new TransmissionInfo(TransmissionType.Silent, 0);
		}

		// Always send (new frame) when server was idle in the prev. slot
		if(previousMediumState == MediumState.Idle) {
			if(prevStatus == NodeState.Idle || status == NodeState.Idle) {
				updateMaxBackOffTime(true);
				System.out.println("SLOT - Server was idle, sending data and hope for no collision.");
				return new TransmissionInfo(TransmissionType.Data, 0);
			} else if(prevStatus != NodeState.Idle && new Random().nextInt(100) < (100 / localQueueLength)) {
				System.out.println("SLOT - Server was idle, sending data and hope for no collision.");
				return new TransmissionInfo(TransmissionType.Data, 0);
			}
		}

		// When server was busy, wait
		// If we start waiting, determine new backofftime.
		// Otherwise, decrease it.
		if (prevStatus == NodeState.Idle || (prevStatus == NodeState.Sending && status == NodeState.Waiting)) {
			updateMaxBackOffTime(false);
			setBackOffTime();
		} else if (prevStatus == NodeState.Waiting) {
			remainingBackOffTime--;
		}


		// Send if allowed
		if (remainingBackOffTime == 0) {
			System.out.println("SLOT - Done waiting, sending data and hope for no collision.");
			return new TransmissionInfo(TransmissionType.Data, 0);
		} else {
			System.out.println("SLOT - Not sending data to give room for others. Remaining wait time " + remainingBackOffTime);
			return new TransmissionInfo(TransmissionType.Silent, 0);
		}


	}

	void updateMaxBackOffTime(boolean reset) {
		if(reset) {
			retransmitCounter = 1;
		} else if (retransmitCounter < 10) {
			retransmitCounter++;
		}
		System.out.println("Max #retransmit time " + retransmitCounter);
	}

	void setBackOffTime() {
		remainingBackOffTime = new Random().nextInt((int) Math.pow(2,retransmitCounter));
		System.out.println("Picked new backoff time " + remainingBackOffTime);
	}

	void updateStatus(MediumState previousMediumState, int localQueueLength) {
		prevStatus = status;

		//If queue is empty, set to idle
		if(localQueueLength == 0) {
			status = NodeState.Idle;
			return;
		}

		//If node was sending and previous medium state is success set to idle (ready for new transmit)
		if(status == NodeState.Sending && previousMediumState == MediumState.Succes) {
			status = NodeState.Idle;
			updateMaxBackOffTime(true);
		}

		//If node was sending and previous medium state was collission, set to waiting (for retransmission)
		if(status == NodeState.Sending && previousMediumState == MediumState.Collision) {
			status = NodeState.Waiting;
		}
	}

}
