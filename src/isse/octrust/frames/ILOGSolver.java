package isse.octrust.frames;

import ilog.concert.IloConstraint;
import ilog.concert.IloException;
import ilog.concert.IloIntVar;
import ilog.concert.IloIntVarMap;
import ilog.concert.IloMapIndexArray;
import ilog.concert.IloSymbolSet;
import ilog.cp.IloCP;
import ilog.opl.IloOplDataSource;
import ilog.opl.IloOplErrorHandler;
import ilog.opl.IloOplFactory;
import ilog.opl.IloOplModel;
import ilog.opl.IloOplModelDefinition;
import ilog.opl.IloOplModelSource;
import ilog.opl.IloOplSettings;
import isse.octrust.frames.constraints.Solution;
import isse.octrust.frames.domain.Frame;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Provides an interface to ILOG CP solver
 * 
 * @author alexander
 *
 */
public class ILOGSolver {
	private IloOplFactory factory;
	private IloCP cp;
	private IloOplModel model;
	private Collection<String> groupSoftConstraints;
	private Collection<String> userSoftConstraints;
	private ArrayList<String> users;
	private ArrayList<String> groups;
	private Collection<ILogModelModifier> modifiers;

	public ILOGSolver() {
		modifiers = new LinkedList<ILogModelModifier>();
	}

	public void setup() {
		IloOplFactory.setDebugMode(false);

		this.factory = new IloOplFactory();

		try {
			this.cp = this.factory.createCP();
			this.cp.setOut(null);
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	private void modelCleanup() {
		try {
			this.model.end();
			this.cp.clearModel();
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	public void cleanup() {
		this.modelCleanup();
		this.factory.end();
		this.factory = null;
	}

	public void solve(String modelFile, String dataFile) {
		if (this.factory == null)
			setup();

		IloOplErrorHandler handler = this.factory.createOplErrorHandler();
		IloOplModelSource modelSource = this.factory.createOplModelSource(modelFile);
		IloOplSettings settings = this.factory.createOplSettings(handler);
		IloOplModelDefinition modelDefinition = this.factory.createOplModelDefinition(modelSource, settings);

		// maybe some settings here

		this.model = this.factory.createOplModel(modelDefinition, cp);
		IloOplDataSource dataSource = this.factory.createOplDataSource(dataFile);
		this.model.addDataSource(dataSource);

		this.model.generate();

		try {
			boolean solved = this.cp.solve();
			solved = this.cp.solve();
			if (solved) {
				System.out.println("We actually found a solution with objective " + cp.getObjValue());
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Returns the best n solutions; along with information about their reported violated soft constraints and objective
	 * value
	 * 
	 * @param modelFile
	 * @param dataContent
	 * @param n
	 * @return
	 */
	public Collection<Solution> solveTopN(File modelFile, String dataContent, int n) {

		if (this.factory == null)
			setup();

		Collection<Solution> solutions = new ArrayList<Solution>(n);

		IloOplErrorHandler handler = this.factory.createOplErrorHandler();
		IloOplModelSource modelSource = this.factory.createOplModelSource(modelFile.getAbsolutePath());
		IloOplSettings settings = this.factory.createOplSettings(handler);
		IloOplModelDefinition modelDefinition = this.factory.createOplModelDefinition(modelSource, settings);

		// maybe some settings here

		this.model = this.factory.createOplModel(modelDefinition, cp);
		IloOplDataSource dataSource = this.factory.createOplDataSourceFromString(dataContent, "content");

		this.model.addDataSource(dataSource);

		this.model.generate();
		translateSoftConstraints();
		IloIntVar nextFrameDecVar = this.model.getElement("nextFrame").asIntVar();
		try {

			while (n > 0) {
				if (cp.solve()) {
					double objective = cp.getObjValue();
					int selectedFrameId = this.model.getElement("nextFrame").asInt();

					IloConstraint neqConstraint = cp.neq(nextFrameDecVar, selectedFrameId);
					cp.add(neqConstraint);

					// soft constraints - as reported

					IloIntVarMap userPenalties = this.model.getElement("userPenalties").asIntVarMap();
					IloIntVarMap groupPenalties = this.model.getElement("groupPenalties").asIntVarMap();

					HashMap<String, HashMap<String, Integer>> userPenaltyMap = recordPenaltyMap(users, userSoftConstraints, userPenalties);
					HashMap<String, HashMap<String, Integer>> groupPenaltyMap = recordPenaltyMap(groups, groupSoftConstraints, groupPenalties);

					solutions.add(new Solution(selectedFrameId, null, objective, userPenaltyMap, groupPenaltyMap));
				} else
					break;
				--n;
			}

			this.cp.startNewSearch();

		} catch (IloException e) {
			e.printStackTrace();
		}
		return solutions;
	}

	private HashMap<String, HashMap<String, Integer>> recordPenaltyMap(ArrayList<String> patrons, Collection<String> softConstraintIdents,
			IloIntVarMap oplPenalties) {

		HashMap<String, HashMap<String, Integer>> allPatronsPenalties = new HashMap<String, HashMap<String, Integer>>();
		for (String patron : patrons) {
			HashMap<String, Integer> userPenaltyMap = new HashMap<String, Integer>();

			for (String softConstraintIdent : softConstraintIdents) {

				IloMapIndexArray id = null;
				try {
					id = this.factory.mapIndexArray(0);
					id.add(softConstraintIdent);
					id.add(patron);

					int penalty = (int) Math.round(this.model.getCP().getValue(oplPenalties.getAt(id)));

					userPenaltyMap.put(softConstraintIdent, penalty);
				} catch (IloException e) {
					e.printStackTrace();
				}
				allPatronsPenalties.put(patron, userPenaltyMap);
			}
		}
		return allPatronsPenalties;
	}

	private void translateSoftConstraints() {
		IloSymbolSet softConstraintsOPL = this.model.getElement("softConstraints").asSymbolSet();
		userSoftConstraints = new ArrayList<String>(softConstraintsOPL.getSize());
		translateOPLCollection(softConstraintsOPL, userSoftConstraints);

		IloSymbolSet groupSoftConstraintsOPL = this.model.getElement("groupSoftConstraints").asSymbolSet();
		groupSoftConstraints = new ArrayList<String>(groupSoftConstraintsOPL.getSize());
		translateOPLCollection(groupSoftConstraintsOPL, groupSoftConstraints);

		IloSymbolSet groupsOPL = this.model.getElement("groups").asSymbolSet();
		groups = new ArrayList<String>(groupsOPL.getSize());
		translateOPLCollection(groupsOPL, groups);

		IloSymbolSet usersOPL = this.model.getElement("users").asSymbolSet();
		users = new ArrayList<String>(usersOPL.getSize());
		translateOPLCollection(usersOPL, users);
	}

	private void translateOPLCollection(IloSymbolSet oplCollection, Collection<String> javaCollection) {
		Iterator<?> iter = oplCollection.iterator();
		while (iter.hasNext()) {
			String nextSym = (String) iter.next();
			javaCollection.add(nextSym);
		}

	}

	public Solution solve(File modelFile, String dataContent) {

		if (this.factory == null)
			setup();

		IloOplErrorHandler handler = this.factory.createOplErrorHandler();
		IloOplModelSource modelSource = this.factory.createOplModelSource(modelFile.getAbsolutePath());
		IloOplSettings settings = this.factory.createOplSettings(handler);
		IloOplModelDefinition modelDefinition = this.factory.createOplModelDefinition(modelSource, settings);

		// maybe some settings here

		this.model = this.factory.createOplModel(modelDefinition, cp);
		IloOplDataSource dataSource = this.factory.createOplDataSourceFromString(dataContent, "content");

		this.model.addDataSource(dataSource);

		this.model.generate();
		notifyModifiers();

		translateSoftConstraints();

		try {
			boolean solved = this.cp.solve();

			if (solved) {
				IloIntVarMap userPenalties = this.model.getElement("userPenalties").asIntVarMap();
				IloIntVarMap groupPenalties = this.model.getElement("groupPenalties").asIntVarMap();

				HashMap<String, HashMap<String, Integer>> userPenaltyMap = recordPenaltyMap(users, userSoftConstraints, userPenalties);
				HashMap<String, HashMap<String, Integer>> groupPenaltyMap = recordPenaltyMap(groups, groupSoftConstraints, groupPenalties);
				int selectedFrameId = this.model.getElement("nextFrame").asInt();
				double objective = cp.getObjValue();
				Solution solution = new Solution(selectedFrameId, null, objective, userPenaltyMap, groupPenaltyMap);

				return solution;
			} else {

				System.out.println("Failed to find a solution");
			}
		} catch (IloException e) {
			e.printStackTrace();
		}
		return null;
	}

	private void notifyModifiers() {
		for (ILogModelModifier modifier : modifiers) {
			modifier.modify(this.model);
		}
	}

	public void fixSolution(Frame f) {
		final Frame selectedFrame = f;
		modifiers.add(new ILogModelModifier() {

			@Override
			public void modify(IloOplModel model) {
				IloIntVar nextFrameDecVar = model.getElement("nextFrame").asIntVar();
				IloCP cp = model.getCP();
				Frame f = selectedFrame;
				System.out.println("Look for frame " + selectedFrame.getId());
				IloConstraint eqConstraint = cp.eq(nextFrameDecVar, selectedFrame.getId());
				try {
					cp.add(eqConstraint);
				} catch (IloException e) {
					e.printStackTrace();
				}
			}
		});

	}

	public void clearModifiers() {
		modifiers.clear();
	}
}
