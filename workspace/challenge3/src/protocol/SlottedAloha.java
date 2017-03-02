package protocol;

import java.util.Random;

/**
 * A fairly trivial Medium Access Control scheme.
 * Transmit new frame in next slot (no matter the state of the server), retransmit in subseq. slot with prob PROB.
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class SlottedAloha implements IMACProtocol {
	static final int PROB = 19;
	NodeState status = NodeState.Idle;

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		//Update status
		updateStatus(previousMediumState, localQueueLength);

		// No data to send, just be quiet
		if (localQueueLength == 0) {
			System.out.println(this + " SLOT - No data to send.");
			return new TransmissionInfo(TransmissionType.Silent, 0);
		}

		// Sent new frame immediately
		if(status == NodeState.Idle) {
			System.out.println(this + " SLOT - Sending new frame and hope for no collision.");
			status = NodeState.Sending;
		}

		// If waiting, randomly transmit with PROB% probability
		if (new Random().nextInt(100) < PROB) {
			System.out.println(this + " SLOT - Sending data and hope for no collision.");
			return new TransmissionInfo(TransmissionType.Data, 0);
		} else {
			System.out.println(this + " SLOT - Not sending data to give room for others.");
			return new TransmissionInfo(TransmissionType.Silent, 0);
		}

	}

	void updateStatus(MediumState previousMediumState, int localQueueLength) {
		//If queue is empty, set to idle
		if(localQueueLength == 0) {
			status = NodeState.Idle;
			return;
		}

		//If node was sending and previous medium state is success set to idle (ready for new transmit)
		if(status == NodeState.Sending && previousMediumState == MediumState.Succes) {
			status = NodeState.Idle;
		}

		//If node was sending and previous medium state was collission, set to waiting (for retransmission)
		if(status == NodeState.Sending && previousMediumState == MediumState.Collision) {
			status = NodeState.Waiting;
		}
	}

}
