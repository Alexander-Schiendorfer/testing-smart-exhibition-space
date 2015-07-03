package isse.octrust.examples;

import isse.octrust.export.ILOGExporter;
import isse.octrust.frames.ILOGSolver;
import isse.octrust.frames.constraints.Solution;
import isse.octrust.frames.domain.Frame;
import isse.octrust.frames.domain.Rendezvous;
import isse.octrust.softconstraints.PreferenceStructure;

import java.io.File;
import java.util.Collection;

public class OneShotMultipleSolutions {

	public static void main(String[] args) {
		// Start with exemplary content
		ExemplaryContent ec = new ExemplaryContent();
		Rendezvous rendezvous = ec.getExample();

		// make a few steps
		ILOGExporter exporter = new ILOGExporter();
		ILOGSolver solver = new ILOGSolver();

		File modelFile = new File("Constraints-Displays-Model.mod");

		PreferenceStructure preferences = new PreferenceStructure(); // unit preference structure Max-CSP

		preferences.setPenaltyContentNotSeen(3);
		preferences.setPenaltyKnowledgeFits(4);

		for (int steps = 0; steps < 1; ++steps) {

			String dataContent = exporter.getILOGDataFile(rendezvous);
			String preferencesContent = exporter.getPreferenceContent(preferences);

			Collection<Solution> solutions = solver.solveTopN(modelFile, dataContent + "\n" + preferencesContent, 5);

			for (Solution sol : solutions) {
				Frame f = rendezvous.getContent().getFrameGraph().lookup(sol.getSelectedFrameId());
				sol.setSelectedFrame(f);

				System.out.println("Next solution: ");
				System.out.println(sol);
			}
		}
	}

}
