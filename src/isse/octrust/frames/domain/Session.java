package isse.octrust.frames.domain;

/*
 * An open session of a topic - if applicable
 */
public class Session {
	private Topic topic;

	public Topic getTopic() {
		return topic;
	}

	public void setTopic(Topic topic) {
		this.topic = topic;
	}

	public Session(Topic topic) {
		super();
		this.topic = topic;
	}

}
