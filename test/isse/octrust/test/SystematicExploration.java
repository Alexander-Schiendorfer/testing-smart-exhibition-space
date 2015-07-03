package isse.octrust.test;

import isse.octrust.checker.ResultChecker;
import isse.octrust.examples.ExemplaryContent;
import isse.octrust.export.ILOGExporter;
import isse.octrust.frames.ILOGSolver;
import isse.octrust.frames.constraints.Solution;
import isse.octrust.frames.domain.Display;
import isse.octrust.frames.domain.Frame;
import isse.octrust.frames.domain.Rendezvous;
import isse.octrust.frames.domain.Topic;
import isse.octrust.softconstraints.PreferenceStructure;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Tests each possible frame (by adding it as an explicit constraint)
 * 
 * @author alexander
 *
 */
public class SystematicExploration {
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

		for (Frame f : rendezvous.getContent().getFrameGraph().getFrames()) {
			// frame has to match display
			Display d = ec.getExample().getMeetup().getDisplay();
			Collection<Topic> displayTopics = new ArrayList<Topic>(d.getTopics());
			Collection<Topic> frameTopics = f.getTopics();
			displayTopics.retainAll(frameTopics);

			if (!displayTopics.isEmpty()) {
				// fix frame as solution
				solver.fixSolution(f);

				String dataContent = exporter.getILOGDataFile(rendezvous);
				String preferencesContent = exporter.getPreferenceContent(preferences);

				Solution solution = solver.solve(modelFile, dataContent + "\n" + preferencesContent);
				// check result
				solution.setSelectedFrame(rendezvous.getContent().getFrameGraph().lookup(solution.getSelectedFrameId()));
				checker.check(solution, rendezvous, preferences);

				solver.clearModifiers();
			}
		}

		// test suite has ended
		System.out.println(checker.retrieveCoverageInformation());
	}
}
