package isse.octrust.examples;

import isse.octrust.frames.domain.Content;
import isse.octrust.frames.domain.Display;
import isse.octrust.frames.domain.Frame;
import isse.octrust.frames.domain.FramesGraph;
import isse.octrust.frames.domain.FramesGraph.FrameEdge;
import isse.octrust.frames.domain.Group;
import isse.octrust.frames.domain.KnowledgeLevel;
import isse.octrust.frames.domain.Meetup;
import isse.octrust.frames.domain.NamedEntity;
import isse.octrust.frames.domain.Rendezvous;
import isse.octrust.frames.domain.Session;
import isse.octrust.frames.domain.Topic;
import isse.octrust.frames.domain.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Provides an object graph for the discussed example
 * 
 * @author alexander
 *
 */
public class ExemplaryContent {

	private User u1;
	private User u2;
	private Group g1;

	private Topic ocTrust;
	private Topic dynaLearn;
	private Topic robotics;
	private Frame f1;
	private Frame f2;
	private Frame f3;
	private Frame f4;
	private Frame f5;
	private Frame f6;
	private Frame f7;
	private Frame f8;
	private Frame f9;
	private Frame f10;
	private Frame f11;
	private Frame f12;
	private Frame f13;
	private Frame f14;

	public Rendezvous getExample() {

		// topics and dependencies (static information)
		ocTrust = new Topic("OC-Trust");
		dynaLearn = new Topic("DynaLearn");
		robotics = new Topic("Humanoid Robotics");

		// users
		u1 = new User(KnowledgeLevel.EASY, "u1");
		u1.setPreferredTopics(Arrays.asList(dynaLearn));
		u1.setSeenFrames(new LinkedList<Frame>());

		u2 = new User(KnowledgeLevel.MEDIUM, "u2");
		u2.setPreferredTopics(new ArrayList<Topic>(Arrays.asList(ocTrust, dynaLearn)));

		// groups
		g1 = new Group(Arrays.asList(u1, u2), "g1");

		// session (say, OC-Trust is on for group 1)
		Session openSession = new Session(ocTrust);
		g1.setOpenSession(openSession);

		// display (assume we are at display d1
		Display d1 = new Display(Arrays.asList(ocTrust, dynaLearn));
		FramesGraph framesGraph = buildFramesGraph();
		u2.setSeenFrames(new ArrayList<Frame>(Arrays.asList(f1, f2, f3, f4, f5, f6)));

		Meetup meetup = new Meetup(Arrays.asList(g1), d1);
		Content content = new Content(framesGraph, Arrays.asList(ocTrust, dynaLearn, robotics));
		Rendezvous rendezvous = new Rendezvous(meetup, content);
		return rendezvous;
	}

	private FramesGraph buildFramesGraph() {

		f1 = new Frame(KnowledgeLevel.EASY, Arrays.asList(dynaLearn, ocTrust, robotics), "Intro");
		f2 = new Frame(KnowledgeLevel.EASY, Arrays.asList(ocTrust), "Intro OC-Trust");
		f3 = new Frame(KnowledgeLevel.EASY, Arrays.asList(dynaLearn, ocTrust, robotics), "Intro Bayesian Networks");
		f4 = new Frame(KnowledgeLevel.EASY, Arrays.asList(ocTrust), "User Trust Model");
		f5 = new Frame(KnowledgeLevel.EASY, Arrays.asList(ocTrust), "Group Recommender");
		f6 = new Frame(KnowledgeLevel.EASY, Arrays.asList(ocTrust), "Outro OC‐Trust");

		f7 = new Frame(KnowledgeLevel.EASY, Arrays.asList(dynaLearn), "Intro DynaLearn");
		f8 = new Frame(KnowledgeLevel.EASY, Arrays.asList(dynaLearn), "Learner Modeling");
		f9 = new Frame(KnowledgeLevel.EASY, Arrays.asList(dynaLearn), "Virtual Characters");
		f10 = new Frame(KnowledgeLevel.MEDIUM, Arrays.asList(dynaLearn), "Text‐To‐Speech");
		f11 = new Frame(KnowledgeLevel.EASY, Arrays.asList(dynaLearn), "Outro DynaLearn");

		f12 = new Frame(KnowledgeLevel.EASY, Arrays.asList(robotics), "Intro Humanoide Roboter");
		f13 = new Frame(KnowledgeLevel.EASY, Arrays.asList(robotics), "Dynamisches Blickverhalten");
		f14 = new Frame(KnowledgeLevel.EASY, Arrays.asList(robotics), "Outro Humanoide Roboter");

		Collection<Frame> frames = Arrays.asList(f1, f2, f3, f4, f5, f6, f7, f8, f9, f10, f11, f12, f13, f14);

		assignIds(frames);

		// now for some edges
		// Area OC-Trust
		FramesGraph.FrameEdge e1 = new FrameEdge(f1, f2, null);
		FramesGraph.FrameEdge e2 = new FrameEdge(f2, f3, null);
		FramesGraph.FrameEdge e3 = new FrameEdge(f2, f4, null);
		FramesGraph.FrameEdge e4 = new FrameEdge(f2, f5, null);
		FramesGraph.FrameEdge e5 = new FrameEdge(f3, f6, Arrays.asList(f4, f5));
		FramesGraph.FrameEdge e6 = new FrameEdge(f4, f6, Arrays.asList(f3, f5));
		FramesGraph.FrameEdge e7 = new FrameEdge(f5, f6, Arrays.asList(f4, f3));

		// Area DynaLearn
		FramesGraph.FrameEdge e8 = new FrameEdge(f1, f7, null);
		FramesGraph.FrameEdge e9 = new FrameEdge(f7, f9, null);
		FramesGraph.FrameEdge e10 = new FrameEdge(f7, f3, null);
		FramesGraph.FrameEdge e11 = new FrameEdge(f3, f8, null);
		FramesGraph.FrameEdge e12 = new FrameEdge(f9, f10, null);
		FramesGraph.FrameEdge e13 = new FrameEdge(f9, f3, null);
		FramesGraph.FrameEdge e14 = new FrameEdge(f8, f11, Arrays.asList(f9));
		FramesGraph.FrameEdge e15 = new FrameEdge(f9, f11, Arrays.asList(f8, f3));

		// Area robotics
		FramesGraph.FrameEdge e16 = new FrameEdge(f1, f12, null);
		FramesGraph.FrameEdge e17 = new FrameEdge(f12, f3, null);
		FramesGraph.FrameEdge e18 = new FrameEdge(f3, f13, null);
		FramesGraph.FrameEdge e19 = new FrameEdge(f13, f14, null);

		Collection<FrameEdge> edges = Arrays.asList(e1, e2, e3, e4, e5, e6, e7, e8, e9, e10, e11, e12, e13, e14, e15, e16, e17, e18, e19);
		return new FramesGraph(frames, edges);
	}

	private void assignIds(Collection<? extends NamedEntity> collection) {
		int i = 0;
		for (NamedEntity ne : collection) {
			ne.setId(++i);
		}
	}

}
