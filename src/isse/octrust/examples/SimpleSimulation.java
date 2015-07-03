package isse.octrust.examples;

import isse.octrust.checker.ResultChecker;
import isse.octrust.export.ILOGExporter;
import isse.octrust.frames.ILOGSolver;
import isse.octrust.frames.constraints.Solution;
import isse.octrust.frames.domain.Frame;
import isse.octrust.frames.domain.Group;
import isse.octrust.frames.domain.Rendezvous;
import isse.octrust.frames.domain.User;
import isse.octrust.softconstraints.PreferenceStructure;

import java.io.File;

/**
 * Only a simple group of visitors standing at a display for some steps
 * 
 * @author alexander
 *
 */
public class SimpleSimulation {

	public static void main(String[] args) {
		// Start with exemplary content
		ExemplaryContent ec = new ExemplaryContent();
		Rendezvous rendezvous = ec.getExample();

		// make a few steps
		ILOGExporter exporter = new ILOGExporter();
		ILOGSolver solver = new ILOGSolver();

		File modelFile = new File("Constraints-Displays-Model.mod");

		PreferenceStructure preferences = new PreferenceStructure(); // unit preference structure Max-CSP

		ResultChecker checker = new ResultChecker();
		for (int steps = 0; steps < 5; ++steps) {

			String dataContent = exporter.getILOGDataFile(rendezvous);
			String preferencesContent = exporter.getPreferenceContent(preferences);

			Solution solution = solver.solve(modelFile, dataContent + "\n" + preferencesContent);

			Frame selectedFrame = rendezvous.getContent().getFrameGraph().lookup(solution.getSelectedFrameId());
			solution.setSelectedFrame(selectedFrame);

			System.out.println("Selected " + solution.getSelectedFrameId());

			checker.check(solution, rendezvous, preferences);

			// add to the seen frames
			for (Group g : rendezvous.getMeetup().getGroups()) {
				for (User u : g.getMembers()) {
					u.getSeenFrames().add(selectedFrame);
				}
			}
		}
		// test suite has ended
		System.out.println(checker.retrieveCoverageInformation());
	}

}
