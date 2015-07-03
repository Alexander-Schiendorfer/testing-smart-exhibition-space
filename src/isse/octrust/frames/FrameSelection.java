package isse.octrust.frames;

/**
 * Provides a basic java interface taking a .mod file
 * 
 * @author alexander
 *
 */
public class FrameSelection {

	public static void main(String[] args) {
		if (args.length < 2) {
			System.out.println("Usage: modelFile.mod dataFile.dat");
			System.exit(1);
		}

		// Constraints-Displays-Model.mod Constraints-Displays-Data.dat
		String modelFile = args[0];
		String dataFile = args[1];

		System.out.println("Model File: " + modelFile);
		System.out.println("Data File: " + dataFile);

		ILOGSolver solver = new ILOGSolver();
		solver.solve(modelFile, dataFile);

	}
}
