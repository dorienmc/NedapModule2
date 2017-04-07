package protocol;

import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 * Transmit new frame in next slot (no matter the state of the server), retransmit in subseq. slot with prob PROB.
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class SlottedAlohaDynamicProb implements IMACProtocol {
	static int PROB = 0;
	NodeState prevStatus = NodeState.Idle;
	NodeState status = NodeState.Idle;

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		//Update status
		updateStatus(previousMediumState, localQueueLength);

		//Update probability
		switch(previousMediumState) {
			case Collision: {
				PROB = 25;
//				PROB -= 30;
//				PROB = (PROB < 25 ? 25 : PROB);
				break;
			}
			case Idle: {
				PROB += (localQueueLength > 0 ? 30 : 5);
			}
			default: {
				PROB += (prevStatus == NodeState.Sending ? 40 : -1);
			}
		}
		PROB = (PROB > 100 ? 100 : PROB);
		PROB = (PROB < 0 ? 0 : PROB);
		System.out.println("Send prob:" + PROB);

		// No data to send, just be quiet
		if (localQueueLength == 0) {
			System.out.println(localQueueLength + " SLOT - No data to send.");
			return new TransmissionInfo(TransmissionType.Silent, 0);
		}

		// Sent frame with PROB% probability
		if (new Random().nextInt(100) < PROB) {
			System.out.println(localQueueLength + " SLOT - Sending new frame and hope for no collision.");
			status = NodeState.Sending;
			return new TransmissionInfo(TransmissionType.Data, 0);
		} else {
			System.out.println(localQueueLength + " SLOT - Not sending data to give room for others.");
			return new TransmissionInfo(TransmissionType.Silent, 0);
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
