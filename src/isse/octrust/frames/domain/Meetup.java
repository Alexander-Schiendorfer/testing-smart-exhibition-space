package isse.octrust.frames.domain;

import java.util.Collection;

/**
 * Models a dynamic snapshot of groups (and their seen contents) at a particular instance
 * 
 * @author alexander
 *
 */
public class Meetup {

	// dynamic information
	private Collection<Group> groups;
	private Display display;

	public Meetup(Collection<Group> groups, Display display) {
		super();
		this.groups = groups;
		this.display = display;

	}

	public Collection<Group> getGroups() {
		return groups;
	}

	public void setGroups(Collection<Group> groups) {
		this.groups = groups;
	}

	public Display getDisplay() {
		return display;
	}

	public void setDisplay(Display display) {
		this.display = display;
	}

}
