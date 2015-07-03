package isse.octrust.frames.domain;

public abstract class NamedEntity {

	protected int id;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public abstract String getName();
}
