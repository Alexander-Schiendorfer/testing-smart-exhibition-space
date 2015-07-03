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
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class GeneratedUnitTest {

	private Set<Frame> testSuite;
	private ExemplaryContent ec;
	private File modelFile;

	@Before
	public void setUp() throws Exception {
		ec = new ExemplaryContent();
		TestCaseGeneration generation = new TestCaseGeneration();
		testSuite = generation.getTestSuite(ec.getExample());
		modelFile = new File("Constraints-Displays-Model.mod");
		System.out.println("Obtained test suite: " + testSuite.toString());
	}

	@Test
	public void test() {
		// Start with exemplary content
		Rendezvous rendezvous = ec.getExample();

		// make a few steps
		ILOGExporter exporter = new ILOGExporter();
		ILOGSolver solver = new ILOGSolver();

		PreferenceStructure preferences = new PreferenceStructure(); // unit preference structure Max-CSP

		ResultChecker checker = new ResultChecker();

		Collection<Frame> testFrames = rendezvous.getContent().getFrameGraph().getFrames();

		testFrames = testSuite;
		for (Frame f : testFrames) {
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
				try {
					checker.check(solution, rendezvous, preferences);
				} catch (AssertionError ae) {
					ae.printStackTrace();
					Assert.fail("One assertion was violated in the checker: " + ae.getMessage());
				}
				solver.clearModifiers();
			}
		}

		// test suite has ended
		System.out.println(checker.retrieveCoverageInformation());

	}

}
