package protocol;

import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 * Transmit new frame in next slot (no matter the state of the server), retransmit in subseq. slot with prob PROB.
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class SlottedAloha2 implements IMACProtocol {
	static int PROB = 25;
	NodeState prevStatus = NodeState.Idle;
	NodeState status = NodeState.Idle;
	private int prevQueueLength = 0;

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {

		//Update status
		updateStatus(previousMediumState, localQueueLength);
		System.out.println("Prev in own queue" + prevQueueLength
				+ ", Currently in own queue: " + localQueueLength);

		//Determine what to do
		TransmissionInfo info = DetermineMessage(previousMediumState, controlInformation, localQueueLength);

		//Update queueLength
		prevQueueLength = localQueueLength;

		return info;
	}

	TransmissionInfo DetermineMessage(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {

		// No data to send, just be quiet
		if (localQueueLength == 0) {
			System.out.println(" SLOT - No data to send.");
			return new TransmissionInfo(TransmissionType.Silent, 0);
		}

		// Calculate PROB based on totalQueued and in own queue
		PROB = 100 * localQueueLength / (localQueueLength + controlInformation);
		if(controlInformation == localQueueLength || controlInformation == localQueueLength - 1) {
			PROB *= 1.5;
		}
		if(previousMediumState == MediumState.Collision) {
			PROB /= 4;
		}
		System.out.println("P:" + PROB);

		// Sent frame with PROB% prob
		if (new Random().nextInt(100) < PROB) {
			System.out.println("SLOT - (Re)sending frame and hope for no collision.");
			status = NodeState.Sending;
			return new TransmissionInfo(TransmissionType.Data, localQueueLength);
		} else {
			if (prevStatus == NodeState.Sending && previousMediumState == MediumState.Collision) {
				System.out.println("SLOT - Tell server we are waiting.");
			} else {
				System.out.println("SLOT - Not sending data to give room for others.");
			}
			return new TransmissionInfo(TransmissionType.Silent, localQueueLength);
		}
	}

	void updateStatus(MediumState previousMediumState, int localQueueLength) {
		prevStatus = status;

		//If queue is empty, set to idle
		if(localQueueLength == 0) {
			status = NodeState.Idle;
		} else {

			//If node was sending and previous medium state is success set to idle (ready for new transmit)
			if (prevStatus == NodeState.Sending && previousMediumState == MediumState.Succes) {
				status = NodeState.Idle;
			}

			//If node was sending and previous medium state was collission, set to waiting (for retransmission)
			if (prevStatus == NodeState.Sending && previousMediumState == MediumState.Collision) {
				status = NodeState.Waiting;
			}
		}

	}

}
