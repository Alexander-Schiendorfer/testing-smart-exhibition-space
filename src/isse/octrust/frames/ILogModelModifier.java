package isse.octrust.frames;

import ilog.opl.IloOplModel;

/**
 * Interface for dynamic modification to a generated ILOG model
 * (fixing constraints, etc.)
 * @author alexander
 *
 */
public interface ILogModelModifier {
	void modify(IloOplModel model);
}
