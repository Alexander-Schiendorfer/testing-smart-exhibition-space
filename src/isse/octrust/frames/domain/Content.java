package isse.octrust.frames.domain;

import java.util.Collection;

/**
 * Provides the static information, i.e., contents and their dependencies
 * 
 * @author alexander
 *
 */
public class Content {

	// static information
	private FramesGraph frames;

	public Content(FramesGraph frames, Collection<Topic> topics) {
		super();
		this.frames = frames;
		this.topics = topics;
	}

	private Collection<Topic> topics;

	public FramesGraph getFrameGraph() {
		return frames;
	}

	public void setFrames(FramesGraph frames) {
		this.frames = frames;
	}

	public void setTopics(Collection<Topic> topics) {
		this.topics = topics;
	}

	public Collection<Topic> getTopics() {
		return topics;
	}

}
