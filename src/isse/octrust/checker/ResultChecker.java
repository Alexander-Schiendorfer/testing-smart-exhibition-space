package isse.octrust.checker;

import isse.octrust.frames.constraints.Solution;
import isse.octrust.frames.domain.Frame;
import isse.octrust.frames.domain.FramesGraph;
import isse.octrust.frames.domain.FramesGraph.FrameEdge;
import isse.octrust.frames.domain.Group;
import isse.octrust.frames.domain.KnowledgeLevel;
import isse.octrust.frames.domain.Meetup;
import isse.octrust.frames.domain.Rendezvous;
import isse.octrust.frames.domain.Topic;
import isse.octrust.frames.domain.User;
import isse.octrust.softconstraints.PreferenceStructure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

/**
 * Checks the results obtained by the constraint solver to detect possible inconsistencies
 * 
 * @author alexander
 *
 */
public class ResultChecker {

	private HashMap<String, HashMap<String, Integer>> violatedUserSoftConstraints;
	private HashMap<String, HashMap<String, Integer>> violatedGroupSoftConstraints;

	private List<String> userSoftConstraintIdents;
	private List<String> groupSoftConstraintIdents;

	// used for coverage information
	private HashSet<String> seenViolated;
	private HashSet<String> seenSatisfied;

	private boolean onlineCheck = true;
	private boolean verbose = true;

	public boolean isCompareSolutions() {
		return onlineCheck;
	}

	public void setCompareSolutions(boolean compareSolutions) {
		this.onlineCheck = compareSolutions;
	}

	public ResultChecker() {
		userSoftConstraintIdents = Arrays.asList("knowledgeFits", "contentNotSeen", "userInterested", "predecessorsOkay");
		groupSoftConstraintIdents = Arrays.asList("frameFitsTopic");

		seenSatisfied = new HashSet<String>();
		seenViolated = new HashSet<String>();

		violatedUserSoftConstraints = new HashMap<String, HashMap<String, Integer>>();
		violatedGroupSoftConstraints = new HashMap<String, HashMap<String, Integer>>();
	}

	public void check(Solution solution, Rendezvous rendezvous, PreferenceStructure preferenceStructure) {
		if (verbose) {
			System.out.println("Checking solution ... ");
			System.out.println(solution);
		}
		int penalty = 0;

		Meetup meetup = rendezvous.getMeetup();
		for (Group g : meetup.getGroups()) {
			for (User u : g.getMembers()) {

				penalty += checkProperty(knowledgeFits(u, solution.getSelectedFrame()), preferenceStructure.getPenaltyKnowledgeFits(), "knowledgeFits",
						solution, u);
				penalty += checkProperty(contentNew(u, solution.getSelectedFrame()), preferenceStructure.getPenaltyContentNotSeen(), "contentNotSeen",
						solution, u);
				penalty += checkProperty(userInterested(u, solution.getSelectedFrame()), preferenceStructure.getPenaltyUserInterested(), "userInterested",
						solution, u);
				penalty += checkProperty(predecessorsOkay(u, solution.getSelectedFrame(), rendezvous.getContent().getFrameGraph()),
						preferenceStructure.getPenaltyPredecessorsOkay(), "predecessorsOkay", solution, u);

			}
			penalty += checkPropertyGroup(frameFitsTopic(g, solution.getSelectedFrame()), preferenceStructure.getPenaltyFrameFitsTopic(), "frameFitsTopic",
					solution, g);

		}

		int reportedPenalty = (int) Math.round(solution.getObjectiveValue());
		if (onlineCheck)
			assert reportedPenalty == penalty;
	}

	private boolean predecessorsOkay(User u, Frame selectedFrame, FramesGraph framesGraph) {
		// filter out edges leading to the selected frame
		Collection<FramesGraph.FrameEdge> possibleEdges = framesGraph.getEdgesForTarget(selectedFrame);
		Collection<Frame> seenFrames = u.getSeenFrames();

		// if no incoming edges are possible, then it should be okay * DESCRIBE AS CASE SCENARIO - UNCOMMENT TO
		// REPRODUCE ERROR *

		if (possibleEdges.isEmpty())
			return true;

		for (FrameEdge edge : possibleEdges) {
			Collection<Frame> neededFrames = new ArrayList<Frame>(edge.getAdditionalPredecessors());
			neededFrames.add(edge.getPredecessor());

			// if neededFrames is a subset of the seen frames - the world viss fine
			if (isSubset(neededFrames, seenFrames))
				return true;
		}
		return false;
	}

	private boolean isSubset(Collection<Frame> neededFrames, Collection<Frame> seenFrames) {
		for (Frame f : neededFrames) {
			if (!seenFrames.contains(f))
				return false;
		}
		return true;
	}

	private boolean frameFitsTopic(Group g, Frame selectedFrame) {
		return selectedFrame.getTopics().contains(g.getOpenSession().getTopic());
	}

	private int checkPropertyGroup(boolean constraintSatisfied, int penalty, String softConstraintIdent, Solution solution, Group g) {
		int penaltyReported = 0;
		if (onlineCheck)
			penaltyReported = solution.getGroupSoftConstraintPenalties().get(g.getName()).get(softConstraintIdent);

		if (!constraintSatisfied) {
			if (onlineCheck)
				assert penaltyReported > 0 : "We disagree on soft constraint " + softConstraintIdent + " for group " + g.getName() + ". It should be violated.";
			addViolatedSoftConstraint(violatedGroupSoftConstraints, softConstraintIdent, g.getName(), penalty);
			seenViolated.add(softConstraintIdent);
			return penalty;
		} else {
			if (onlineCheck)
				assert penaltyReported == 0 : "We disagree on soft constraint " + softConstraintIdent + " for group " + g.getName()
						+ ". It should not be violated.";
			seenSatisfied.add(softConstraintIdent);
			return 0;
		}
	}

	private void addViolatedSoftConstraint(HashMap<String, HashMap<String, Integer>> violatedSoftConstraints, String softConstraintIdent, String name,
			int penalty) {
		HashMap<String, Integer> penaltyMap = null;
		if (!violatedSoftConstraints.containsKey(name)) {
			penaltyMap = new HashMap<String, Integer>();
			violatedSoftConstraints.put(name, penaltyMap);
		} else {
			penaltyMap = violatedSoftConstraints.get(name);
		}
		penaltyMap.put(softConstraintIdent, penalty);
	}

	private int checkProperty(boolean constraintSatisfied, int penalty, String softConstraintIdent, Solution solution, User u) {
		int penaltyReported = 0;

		if (onlineCheck)
			penaltyReported = solution.getUserSoftConstraintPenalties().get(u.getName()).get(softConstraintIdent);

		if (!constraintSatisfied) {
			if (onlineCheck)
				assert penaltyReported > 0 : "We disagree on soft constraint " + softConstraintIdent + " for user " + u.getName() + ". It should be violated.";
			addViolatedSoftConstraint(violatedUserSoftConstraints, softConstraintIdent, u.getName(), penalty);
			seenViolated.add(softConstraintIdent);
			return penalty;
		} else {
			if (onlineCheck)
				assert penaltyReported == 0 : "We disagree on soft constraint " + softConstraintIdent + " for user " + u.getName()
						+ ". It should not be violated.";
			seenSatisfied.add(softConstraintIdent);
			return 0;
		}
	}

	private boolean userInterested(User u, Frame selectedFrame) {
		for (Topic t : u.getPreferredTopics()) {
			if (selectedFrame.getTopics().contains(t))
				return true;
		}
		return false;
	}

	private boolean contentNew(User u, Frame selectedFrame) {
		Collection<Frame> seenFrames = u.getSeenFrames();
		boolean alreadySeen = seenFrames.contains(selectedFrame);
		return !alreadySeen;
	}

	private boolean knowledgeFits(User u, Frame selectedFrame) {

		KnowledgeLevel userKnowledge = u.getPreferredKnowledgeLevel();
		KnowledgeLevel frameLevel = selectedFrame.getKnowledgeLevel();
		// switch to equals
		return userKnowledge.getLiteralValue() >= frameLevel.getLiteralValue();
	}

	public CoverageInformation retrieveCoverageInformation() {
		int softConstraintsViolated = 0;
		int softConstraintsSatisfied = 0;
		int softConstraintsTotal = userSoftConstraintIdents.size() + groupSoftConstraintIdents.size();

		for (String expected : userSoftConstraintIdents) {
			if (seenViolated.contains(expected))
				++softConstraintsViolated;
			if (seenSatisfied.contains(expected))
				++softConstraintsSatisfied;
		}

		for (String expected : groupSoftConstraintIdents) {
			if (seenViolated.contains(expected))
				++softConstraintsViolated;
			if (seenSatisfied.contains(expected))
				++softConstraintsSatisfied;
		}

		double coverageViolated = (100.0 * softConstraintsViolated) / softConstraintsTotal;
		double coverageSatisfied = (100.0 * softConstraintsSatisfied) / softConstraintsTotal;

		ArrayList<String> allSoftConstraints = new ArrayList<String>(groupSoftConstraintIdents);
		allSoftConstraints.addAll(userSoftConstraintIdents);
		CoverageInformation coverageInformation = new CoverageInformation(coverageViolated, coverageSatisfied, seenViolated, seenSatisfied, allSoftConstraints);
		return coverageInformation;
	}

	public boolean isVerbose() {
		return verbose;
	}

	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}

}
