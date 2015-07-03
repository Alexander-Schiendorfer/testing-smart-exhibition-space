package isse.octrust.frames.domain;

import java.util.Collection;

public class Display {
	private Collection<Topic> topics;

	public Display(Collection<Topic> topics) {
		super();
		this.topics = topics;
	}

	public Collection<Topic> getTopics() {
		return topics;
	}

}
