package protocol;

import java.util.Random;
import javax.xml.soap.Node;

/**
 * A fairly trivial Medium Access Control scheme.
 * Transmit new frame in next slot (no matter the state of the server), retransmit in subseq. slot with prob PROB.
 * @author Jaco ter Braak, Twente University
 * @version 05-12-2013
 */
public class SlottedAloha implements IMACProtocol {
	static int PROB = 25;
	NodeState prevStatus = NodeState.Idle;
	NodeState status = NodeState.Idle;
	private int NodeCount = 0;

	@Override
	public TransmissionInfo TimeslotAvailable(MediumState previousMediumState,
			int controlInformation, int localQueueLength) {
		//Update status
		if(previousMediumState == MediumState.Succes) {
			NodeCount = controlInformation;
		}
		updateStatus(previousMediumState, localQueueLength);
		System.out.println("Expected no waiting nodes: " + NodeCount);

		// No data to send, just be quiet
		if (localQueueLength == 0) {
			System.out.println(NodeCount + " SLOT - No data to send.");

			//Tell server the when queue becomes empty (by sending an updated NodeCount)
			if(prevStatus == NodeState.Sending) {
				if(NodeCount > 0) {
					NodeCount--;
				}
				return new TransmissionInfo(TransmissionType.NoData, NodeCount);
			} else {
				return new TransmissionInfo(TransmissionType.Silent, 0);
			}

		}

		// Sent new frame immediately
		if(status == NodeState.Idle) {
			System.out.println(NodeCount + " SLOT - Sending new frame and hope for no collision.");
			status = NodeState.Sending;
			return new TransmissionInfo(TransmissionType.Data, NodeCount);
		}

		// If waiting, randomly transmit with PROB% probability
		if (new Random().nextInt(100) < PROB) {
			System.out.println(NodeCount + " SLOT - Sending data and hope for no collision.");
			return new TransmissionInfo(TransmissionType.Data, NodeCount);
		} else {
			if (prevStatus == NodeState.Sending && previousMediumState == MediumState.Collision) {
				System.out.println(NodeCount + " SLOT - Tell server we are waiting.");
			} else {
				System.out.println(NodeCount + " SLOT - Not sending data to give room for others.");
			}
			return new TransmissionInfo(TransmissionType.Silent, NodeCount);
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
				if(NodeCount < 4) {
					NodeCount++;
				}
				status = NodeState.Waiting;
			}
		}

		PROB = 100/(NodeCount+1);
	}

}
