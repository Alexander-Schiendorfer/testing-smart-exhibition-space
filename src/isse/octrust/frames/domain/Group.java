package isse.octrust.frames.domain;

import java.util.Collection;

public class Group extends NamedEntity {

	private Collection<User> members;
	private String name;
	private Session openSession;

	public Session getOpenSession() {
		return openSession;
	}

	public void setOpenSession(Session openSession) {
		this.openSession = openSession;
	}

	public Group(Collection<User> members, String name) {
		super();
		this.members = members;
		this.name = name;
	}

	public Collection<User> getMembers() {
		return members;
	}

	public void setMembers(Collection<User> members) {
		this.members = members;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
