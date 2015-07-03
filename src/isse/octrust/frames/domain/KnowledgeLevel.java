package isse.octrust.frames.domain;

/**
 * Represents the level of detail of a frame; use of enum asserts correct range (1-3)
 * 
 * @author alexander
 *
 */
public enum KnowledgeLevel {
	EASY(1), MEDIUM(2), HARD(3);

	private int literalValue;

	private KnowledgeLevel(int literalValue) {
		this.literalValue = literalValue;
	}

	public int getLiteralValue() {
		return literalValue;
	}
}
