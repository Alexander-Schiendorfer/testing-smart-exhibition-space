package isse.octrust.softconstraints;

/**
 * For now, a rather simple structure assigning weights to (violated) constraints as penalties
 * 
 * @author alexander
 *
 */
public class PreferenceStructure {
	private int penaltyFrameFitsTopic;
	private int penaltyContentNotSeen;
	private int penaltyPredecessorsOkay;
	private int penaltyKnowledgeFits;
	private int penaltyUserInterested;

	// some default unity
	public PreferenceStructure() {
		penaltyFrameFitsTopic = 1;
		penaltyContentNotSeen = 1;
		penaltyPredecessorsOkay = 1;
		penaltyKnowledgeFits = 1;
		penaltyUserInterested = 1;
	}

	public int getPenaltyFrameFitsTopic() {
		return penaltyFrameFitsTopic;
	}

	public void setPenaltyFrameFitsTopic(int penaltyFrameFitsTopic) {
		this.penaltyFrameFitsTopic = penaltyFrameFitsTopic;
	}

	public int getPenaltyContentNotSeen() {
		return penaltyContentNotSeen;
	}

	public void setPenaltyContentNotSeen(int penaltyContentNotSeen) {
		this.penaltyContentNotSeen = penaltyContentNotSeen;
	}

	public int getPenaltyPredecessorsOkay() {
		return penaltyPredecessorsOkay;
	}

	public void setPenaltyPredecessorsOkay(int penaltyPredecessorsOkay) {
		this.penaltyPredecessorsOkay = penaltyPredecessorsOkay;
	}

	public int getPenaltyKnowledgeFits() {
		return penaltyKnowledgeFits;
	}

	public void setPenaltyKnowledgeFits(int penaltyKnowledgeFits) {
		this.penaltyKnowledgeFits = penaltyKnowledgeFits;
	}

	public int getPenaltyUserInterested() {
		return penaltyUserInterested;
	}

	public void setPenaltyUserInterested(int penaltyUserInterested) {
		this.penaltyUserInterested = penaltyUserInterested;
	}
}
