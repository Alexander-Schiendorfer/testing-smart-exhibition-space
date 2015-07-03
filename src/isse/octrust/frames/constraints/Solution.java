package isse.octrust.frames.constraints;

import isse.octrust.frames.domain.Frame;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Represents one solution found by the constraint solver
 * 
 * @author alexander
 *
 */
public class Solution {
	private int selectedFrameId;
	private Frame selectedFrame;
	private double objectiveValue;
	private Map<String, HashMap<String, Integer>> userSoftConstraintPenalties;
	private Map<String, HashMap<String, Integer>> groupSoftConstraintPenalties;

	// violated soft constraints

	public Solution(int selectedFrameId, Frame selectedFrame, double objectiveValue, Map<String, HashMap<String, Integer>> userSoftConstraintPenalties,
			Map<String, HashMap<String, Integer>> groupSoftConstraintPenalties) {
		super();
		this.selectedFrameId = selectedFrameId;
		this.selectedFrame = selectedFrame;
		this.objectiveValue = objectiveValue;
		this.userSoftConstraintPenalties = userSoftConstraintPenalties;
		this.groupSoftConstraintPenalties = groupSoftConstraintPenalties;
	}

	public int getSelectedFrameId() {
		return selectedFrameId;
	}

	public void setSelectedFrameId(int selectedFrameId) {
		this.selectedFrameId = selectedFrameId;
	}

	public Frame getSelectedFrame() {
		return selectedFrame;
	}

	public void setSelectedFrame(Frame selectedFrame) {
		this.selectedFrame = selectedFrame;
	}

	public double getObjectiveValue() {
		return objectiveValue;
	}

	public void setObjectiveValue(double objectiveValue) {
		this.objectiveValue = objectiveValue;
	}

	public Map<String, HashMap<String, Integer>> getUserSoftConstraintPenalties() {
		return userSoftConstraintPenalties;
	}

	public void setUserSoftConstraintPenalties(Map<String, HashMap<String, Integer>> userSoftConstraintPenalties) {
		this.userSoftConstraintPenalties = userSoftConstraintPenalties;
	}

	public Map<String, HashMap<String, Integer>> getGroupSoftConstraintPenalties() {
		return groupSoftConstraintPenalties;
	}

	public void setGroupSoftConstraintPenalties(Map<String, HashMap<String, Integer>> groupSoftConstraintPenalties) {
		this.groupSoftConstraintPenalties = groupSoftConstraintPenalties;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("+++++++++++++++++++++\n");
		sb.append("+ Selected Frame: " + selectedFrameId + "\n");
		sb.append("+ Objective: " + objectiveValue + "\n");
		sb.append("++ Violated Soft Constraints \n");
		if (userSoftConstraintPenalties != null) {
			for (Entry<String, HashMap<String, Integer>> entry : userSoftConstraintPenalties.entrySet()) {
				sb.append("+  User " + entry.getKey() + "\n");
				for (Entry<String, Integer> sc : entry.getValue().entrySet()) {
					if (sc.getValue() > 0) {
						sb.append("+    " + sc.getKey() + "\n");
					}
				}
			}
		}
		if (groupSoftConstraintPenalties != null) {
			for (Entry<String, HashMap<String, Integer>> entry : groupSoftConstraintPenalties.entrySet()) {
				sb.append("+  Group " + entry.getKey() + "\n");
				for (Entry<String, Integer> sc : entry.getValue().entrySet()) {
					if (sc.getValue() > 0) {
						sb.append("+    " + sc.getKey() + "\n");
					}
				}
			}
		}
		sb.append("+++++++++++++++++++++\n");
		return sb.toString();
	}
}
