package isse.octrust.frames.domain;

import java.util.Collection;

public class User extends NamedEntity {
	private KnowledgeLevel preferredKnowledgeLevel;
	private Collection<Topic> preferredTopics;
	private Collection<Frame> seenFrames;

	public User(KnowledgeLevel preferredKnowledgeLevel, String name) {
		super();
		this.preferredKnowledgeLevel = preferredKnowledgeLevel;
		this.name = name;
	}

	private String name;

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Topic))
			return false;

		if (this.name == null)
			return ((User) obj).name == null;

		return this.name.equals(((User) obj).name);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode() + this.preferredKnowledgeLevel.hashCode();
	}

	public KnowledgeLevel getPreferredKnowledgeLevel() {
		return preferredKnowledgeLevel;
	}

	public void setPreferredKnowledgeLevel(KnowledgeLevel preferredKnowledgeLevel) {
		this.preferredKnowledgeLevel = preferredKnowledgeLevel;
	}

	public Collection<Topic> getPreferredTopics() {
		return preferredTopics;
	}

	public void setPreferredTopics(Collection<Topic> preferredTopics) {
		this.preferredTopics = preferredTopics;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Collection<Frame> getSeenFrames() {
		return seenFrames;
	}

	public void setSeenFrames(Collection<Frame> seenFrames) {
		this.seenFrames = seenFrames;
	}
}
