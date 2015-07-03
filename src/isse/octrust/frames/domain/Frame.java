package isse.octrust.frames.domain;

import java.util.Collection;

public class Frame extends NamedEntity {
	private KnowledgeLevel knowledgeLevel;
	private Collection<Topic> topics;
	private String name;

	public Frame(KnowledgeLevel knowledgeLevel, Collection<Topic> topics, String name) {
		super();
		this.setKnowledgeLevel(knowledgeLevel);
		this.setTopics(topics);
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Frame))
			return false;

		if (this.name == null)
			return ((Frame) obj).name == null;

		return this.name.equals(((Frame) obj).name);
	}

	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	public KnowledgeLevel getKnowledgeLevel() {
		return knowledgeLevel;
	}

	public void setKnowledgeLevel(KnowledgeLevel knowledgeLevel) {
		this.knowledgeLevel = knowledgeLevel;
	}

	public Collection<Topic> getTopics() {
		return topics;
	}

	public void setTopics(Collection<Topic> topics) {
		this.topics = topics;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return this.getName() + ";" + id;
	}
}
