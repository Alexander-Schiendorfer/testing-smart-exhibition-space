package isse.octrust.checker;

import java.util.Collection;

/**
 * Holds all information to display coverage statistics
 * 
 * @author alexander
 *
 */
public class CoverageInformation {
	private double coverageViolated;
	private double coverageSatisfied;
	private Collection<String> seenViolated;
	private Collection<String> seenSatisfied;
	private Collection<String> allSoftConstraints;

	public Collection<String> getAllSoftConstraints() {
		return allSoftConstraints;
	}

	public CoverageInformation(double coverageViolated, double coverageSatisfied, Collection<String> seenViolated, Collection<String> seenSatisfied,
			Collection<String> allSoftConstraints) {
		super();
		this.coverageViolated = coverageViolated;
		this.coverageSatisfied = coverageSatisfied;
		this.seenViolated = seenViolated;
		this.seenSatisfied = seenSatisfied;
		this.allSoftConstraints = allSoftConstraints;
	}

	public void setAllSoftConstraints(Collection<String> allSoftConstraints) {
		this.allSoftConstraints = allSoftConstraints;
	}

	public double getCoverageViolated() {
		return coverageViolated;
	}

	public void setCoverageViolated(double coverageViolated) {
		this.coverageViolated = coverageViolated;
	}

	public double getCoverageSatisfied() {
		return coverageSatisfied;
	}

	public void setCoverageSatisfied(double coverageSatisfied) {
		this.coverageSatisfied = coverageSatisfied;
	}

	public Collection<String> getSeenViolated() {
		return seenViolated;
	}

	public void setSeenViolated(Collection<String> seenViolated) {
		this.seenViolated = seenViolated;
	}

	public Collection<String> getSeenSatisfied() {
		return seenSatisfied;
	}

	public void setSeenSatisfied(Collection<String> seenSatisfied) {
		this.seenSatisfied = seenSatisfied;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Coverage information:\n");
		sb.append("---------------------\n");

		sb.append("All soft constraints : ");

		for (String softConstraintIdent : allSoftConstraints) {
			sb.append("\"" + softConstraintIdent + "\" ");
		}
		sb.append("\n");

		sb.append("Soft constraints seen violated: ");

		for (String vioSeen : seenViolated) {
			sb.append("\"" + vioSeen + "\" ");
		}
		sb.append("\n");

		sb.append("Soft constraints seen satisfied: ");
		for (String satSeen : seenSatisfied) {
			sb.append("\"" + satSeen + "\" ");
		}
		sb.append("\n");

		sb.append("Coverage violated : " + coverageViolated + " %\n");
		sb.append("Coverage satisfied: " + coverageSatisfied + " %\n");
		return sb.toString();

	}
}
