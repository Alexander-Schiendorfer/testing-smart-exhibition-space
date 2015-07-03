package isse.octrust.frames.domain;

import java.util.ArrayList;
import java.util.Collection;

public class FramesGraph {

	public static class FrameEdge {
		private Frame predecessor;
		private Frame successor;

		public FrameEdge(Frame predecessor, Frame successor, Collection<Frame> additionalPredecessors) {
			super();
			this.predecessor = predecessor;
			this.successor = successor;
			this.additionalPredecessors = additionalPredecessors;
			if (additionalPredecessors == null)
				this.additionalPredecessors = new ArrayList<Frame>(0);
		}

		private Collection<Frame> additionalPredecessors;

		public Frame getPredecessor() {
			return predecessor;
		}

		public Frame getSuccessor() {
			return successor;
		}

		public Collection<Frame> getAdditionalPredecessors() {
			return additionalPredecessors;
		}
	}

	private Collection<Frame> frames;
	private Collection<FrameEdge> edges;

	public FramesGraph(Collection<Frame> frames, Collection<FrameEdge> edges) {
		super();
		this.frames = frames;
		this.edges = edges;
	}

	public Frame lookup(int id) {
		for (Frame f : frames)
			if (f.getId() == id)
				return f;
		return null;
	}

	public Frame lookup(String name) {
		for (Frame f : frames)
			if (f.getName().equals(name))
				return f;
		return null;
	}

	public Collection<Frame> getFrames() {
		return frames;
	}

	public Collection<FrameEdge> getEdges() {
		return edges;
	}

	public Collection<FrameEdge> getEdgesForTarget(Frame selectedFrame) {
		Collection<FrameEdge> selectedEdges = new ArrayList<FramesGraph.FrameEdge>();
		for (FrameEdge edge : edges) {
			if (edge.getSuccessor().equals(selectedFrame)) {
				selectedEdges.add(edge);
			}
		}
		return selectedEdges;
	}
}
