package isse.octrust.frames.domain;

public class Topic extends NamedEntity {
	private String name;

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Topic))
			return false;

		if (this.name == null)
			return ((Topic) obj).name == null;

		return this.name.equals(((Topic) obj).name);
	}

	public Topic(String name) {
		super();
		this.name = name;
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
