package isse.octrust.export;

import isse.octrust.examples.ExemplaryContent;
import isse.octrust.frames.domain.Content;
import isse.octrust.frames.domain.Display;
import isse.octrust.frames.domain.Frame;
import isse.octrust.frames.domain.FramesGraph.FrameEdge;
import isse.octrust.frames.domain.Group;
import isse.octrust.frames.domain.Meetup;
import isse.octrust.frames.domain.NamedEntity;
import isse.octrust.frames.domain.Rendezvous;
import isse.octrust.frames.domain.Session;
import isse.octrust.frames.domain.Topic;
import isse.octrust.frames.domain.User;
import isse.octrust.softconstraints.PreferenceStructure;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class ILOGExporter {

	private String getNamedEntitySet(String varName, Collection<? extends NamedEntity> entities) {
		StringBuilder sb = new StringBuilder();
		sb.append(varName + " = {");
		boolean first = true;
		for (NamedEntity ne : entities) {
			if (first) {
				first = false;
			} else
				sb.append(", ");
			sb.append(quoted(ne.getName()));
		}
		sb.append("};");
		return sb.toString();
	}

	private String getDynamicILOGDataContent(Rendezvous rendezvous) {
		StringBuilder sb = new StringBuilder();
		// users
		Set<User> seenUsers = new HashSet<User>();
		StringBuilder memberBuilder = new StringBuilder();
		memberBuilder.append("members = #[");

		boolean memberFirst = true;
		List<NamedEntity> userList = new LinkedList<NamedEntity>();
		List<NamedEntity> groupList = new LinkedList<NamedEntity>();

		Meetup meetup = rendezvous.getMeetup();
		Content content = rendezvous.getContent();
		List<String> sessionStrings = new ArrayList<String>(meetup.getGroups().size());

		// topics
		Collection<Topic> topics = content.getTopics();

		// groups and membership
		for (Group g : meetup.getGroups()) {
			groupList.add(g);
			if (memberFirst)
				memberFirst = false;
			else
				memberBuilder.append(", ");

			memberBuilder.append(quoted(g.getName()) + " : {");
			boolean userFirst = true;
			for (User u : g.getMembers()) {
				if (userFirst)
					userFirst = false;
				else
					memberBuilder.append(", ");

				if (!seenUsers.contains(u)) {
					userList.add(u);
					seenUsers.add(u);
				}
				memberBuilder.append(quoted(u.getName()));
			}
			memberBuilder.append("}");

			if (g.getOpenSession() != null) {
				Session s = g.getOpenSession();
				String sessionString = "\"" + g.getName() + "\" : {<" + s.getTopic().getId() + ">}";
				sessionStrings.add(sessionString);
			}
		}

		memberBuilder.append("]#;\n");

		sb.append(getNamedEntitySet("users", userList) + "\n");
		sb.append(getNamedEntitySet("groups", groupList) + "\n");
		sb.append(memberBuilder.toString());

		sb.append("openSessions = #[" + join(sessionStrings, ",") + "]#;\n");

		// user data
		Collection<String> userDataStrings = new ArrayList<String>(seenUsers.size());
		for (User u : seenUsers) {
			// "u1" : <1, {2}, {}>,
			String userDataStr = "\"" + u.getName() + "\" : <" + u.getPreferredKnowledgeLevel().getLiteralValue() + ", " + getIdSet(u.getPreferredTopics())
					+ ", " + getIdSet(u.getSeenFrames()) + ">";
			userDataStrings.add(userDataStr);
		}

		String userDataString = "userData = #[ " + join(userDataStrings, ", ") + "]#;\n";
		sb.append(userDataString);

		return sb.toString();
	}

	private String getStaticILOGDataContent(Content content, Display d) {
		StringBuilder sb = new StringBuilder();
		Collection<Topic> topics = content.getTopics();
		assignIds(topics);

		sb.append(getNamedEntitySet("topics", topics) + "\n");

		// possible at this display
		sb.append(getNamedEntitySet("possibleTopicsClear", d.getTopics()) + "\n");

		// frames
		assignIds(content.getFrameGraph().getFrames());
		sb.append(getNamedEntitySet("frames", content.getFrameGraph().getFrames()) + "\n");

		Collection<String> edges = new ArrayList<String>(content.getFrameGraph().getEdges().size());
		Collection<String> frameStrings = new ArrayList<String>(content.getFrameGraph().getFrames().size());

		for (Frame f : content.getFrameGraph().getFrames()) {
			String frameString = "  <" + f.getKnowledgeLevel().getLiteralValue() + ", " + getIdSet(f.getTopics()) + ">";
			frameStrings.add(frameString);
		}

		String frameString = "frame = [" + join(frameStrings, ",\n") + "];\n";
		sb.append(frameString);

		// now for edges
		for (FrameEdge edge : content.getFrameGraph().getEdges()) {
			String edgeString = "  <" + edge.getPredecessor().getId() + ", " + edge.getSuccessor().getId() + ", " + getIdSet(edge.getAdditionalPredecessors())
					+ ">";
			edges.add(edgeString);
		}

		String edgeString = "setEdges = {" + join(edges, ",\n") + "};\n";
		sb.append(edgeString);

		return sb.toString();
	}

	public String getPreferenceContent(PreferenceStructure preferences) {
		StringBuilder sb = new StringBuilder();
		sb.append("penalty_frameFitsTopic = " + preferences.getPenaltyFrameFitsTopic() + ";\n");
		sb.append("penalty_contentNotSeen = " + preferences.getPenaltyContentNotSeen() + ";\n");
		sb.append("penalty_predecessorsOkay = " + preferences.getPenaltyPredecessorsOkay() + ";\n");
		sb.append("penalty_knowledgeFits = " + preferences.getPenaltyKnowledgeFits() + ";\n");
		sb.append("penalty_userInterested = " + preferences.getPenaltyUserInterested() + ";\n");

		return sb.toString();
	}

	public String getILOGDataFile(Rendezvous rendezvous) {
		String staticData = getStaticILOGDataContent(rendezvous.getContent(), rendezvous.getMeetup().getDisplay());
		String dynamicData = getDynamicILOGDataContent(rendezvous);
		return staticData + "\n" + dynamicData;
	}

	private String join(Collection<String> frameStrings, String delim) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (String fs : frameStrings) {
			if (first)
				first = false;
			else
				sb.append(delim);
			sb.append(fs);
		}
		return sb.toString();
	}

	private String getIdSet(Collection<? extends NamedEntity> namedEntities) {
		StringBuilder ilogSet = new StringBuilder();
		ilogSet.append("{");
		boolean first = true;
		for (NamedEntity ne : namedEntities) {
			if (first)
				first = false;
			else
				ilogSet.append(", ");
			ilogSet.append(ne.getId());
		}
		ilogSet.append("}");
		return ilogSet.toString();
	}

	private void assignIds(Collection<? extends NamedEntity> collection) {
		int i = 0;
		for (NamedEntity ne : collection) {
			ne.setId(++i);
			int j = i;
			int x = j;
		}
	}

	private String quoted(String name) {
		return "\"" + name + "\"";
	}

	public static void main(String[] args) {
		ExemplaryContent ec = new ExemplaryContent();
		Rendezvous rv = ec.getExample();
		ILOGExporter exporter = new ILOGExporter();

		String dataModel = exporter.getILOGDataFile(rv);

		System.out.println(dataModel);
	}

}
