package isse.octrust.test;

import isse.octrust.checker.CoverageInformation;
import isse.octrust.checker.ResultChecker;
import isse.octrust.examples.ExemplaryContent;
import isse.octrust.frames.constraints.Solution;
import isse.octrust.frames.domain.Frame;
import isse.octrust.frames.domain.Rendezvous;
import isse.octrust.softconstraints.PreferenceStructure;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Random;
import java.util.Set;

/**
 * Returns a test suite (i.e., a set of solutions = frames in our case) that maximizes the coverage criteria
 * 
 * @author alexander
 *
 */
public class TestCaseGeneration {

	public class NeighborhoodPicker {

		private Set<Frame> originalSet;
		private Queue<Frame> addSet;
		private Queue<Frame> removeSet;

		public NeighborhoodPicker(Rendezvous rendezvous, Set<Frame> frames) {
			this.originalSet = new HashSet<Frame>(frames);
			this.addSet = new LinkedList<Frame>();
			this.removeSet = new LinkedList<Frame>();

			for (Frame candidateFrame : rendezvous.getContent().getFrameGraph().getFrames()) {
				if (frames.contains(candidateFrame)) {
					removeSet.add(candidateFrame);
				} else {
					addSet.add(candidateFrame);
				}
			}
		}

		public boolean hasNext() {
			return addSet.size() + removeSet.size() > 0;
		}

		public Set<Frame> nextNeighbor() {
			// try removing first
			if (!removeSet.isEmpty()) {
				Frame removeFrame = removeSet.poll();
				Set<Frame> newCandidate = new HashSet<Frame>(originalSet);
				newCandidate.remove(removeFrame);
				return newCandidate;
			} else {
				Frame addFrame = addSet.poll();
				Set<Frame> newCandidate = new HashSet<Frame>(originalSet);
				newCandidate.add(addFrame);
				return newCandidate;
			}
		}
	}

	Set<Frame> getTestSuite(Rendezvous rendezvous) {
		Set<Frame> frames = new HashSet<Frame>();

		// a very simple hill-climbing algorithm, shown for illustration
		int iterations = 100;

		Random r = new Random(1337);

		// initialize with a random set
		for (Frame f : rendezvous.getContent().getFrameGraph().getFrames()) {
			if (r.nextBoolean())
				frames.add(f);
		}

		for (int i = 0; i < iterations; ++i) {
			double fitness = evaluateFitness(rendezvous, frames);

			// first better neighbor is used
			NeighborhoodPicker picker = new NeighborhoodPicker(rendezvous, frames);
			while (picker.hasNext()) {
				Set<Frame> neighbor = picker.nextNeighbor();

				double neighborFitness = evaluateFitness(rendezvous, neighbor);
				if (neighborFitness > fitness)
					frames = neighbor;
			}
		}
		return frames;
	}

	private double evaluateFitness(Rendezvous rv, Set<Frame> frames) {
		// solve with all frames fixed, then evaluate coverage
		ResultChecker checker = new ResultChecker();
		checker.setCompareSolutions(false);
		checker.setVerbose(false);

		for (Frame f : frames) {
			Solution s = new Solution(f.getId(), f, 0, null, null);
			checker.check(s, rv, new PreferenceStructure());
		}

		CoverageInformation coverageInformation = checker.retrieveCoverageInformation();
		double fitness = 10 * (coverageInformation.getCoverageSatisfied() + coverageInformation.getCoverageViolated()) - frames.size();
		return fitness;
	}

	public static void main(String[] args) {
		ExemplaryContent ec = new ExemplaryContent();
		TestCaseGeneration generation = new TestCaseGeneration();
		Set<Frame> testSuite = generation.getTestSuite(ec.getExample());
		System.out.println("Found a test suite with fitness: " + generation.evaluateFitness(ec.getExample(), testSuite));
		System.out.println(testSuite);
	}
}
