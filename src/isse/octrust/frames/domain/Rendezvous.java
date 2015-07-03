package isse.octrust.frames.domain;

/**
 * One particular encounter of groups in front of a display
 * 
 * @author alexander
 *
 */
public class Rendezvous {
	private Meetup meetup;
	private Content content;

	public Rendezvous(Meetup meetup, Content content) {
		super();
		this.meetup = meetup;
		this.content = content;
	}

	public Meetup getMeetup() {
		return meetup;
	}

	public void setMeetup(Meetup meetup) {
		this.meetup = meetup;
	}

	public Content getContent() {
		return content;
	}

	public void setContent(Content content) {
		this.content = content;
	}

}
