/*********************************************
 * OPL 12.4 Model
 * Author: alexander
 * Creation Date: Nov 18, 2014 at 12:10:36 PM
 *********************************************/
using CP; // probably need the constraint programming interface rather than mathematical programming

// set of users currently at the display
{string} users = ...;
{string} groups = ...;
{string} members[groups] = ...;

// set of overall topics
{string} topics = ...;
range topicRange = 1..card(topics);

// now this has to be set for the display this solver is run on
{string} possibleTopicsClear = ...;
{int} possibleTopics; 

// frames by names - will be mapped to ints - from 1 to 14
{string} frames = ...;

range frameRange = 1..card(frames);
int frameId[frames];

execute frameInit {
    
    var i = 1;
    for (var f in frames) {
    	frameId[f] = i++;
    }  
    
    var j = 1;
    for(var t in topics) {
       if(possibleTopicsClear.contains(t)) {
         possibleTopics.add(j); 
       }         
       j++;
    }          
}                     

// data to describe a single frame
tuple FrameData {
  int knowledgeLevel;
  {int} topics;
};

// knowledge levels are given on a scale {1,2,3}, 3 being the hardest
FrameData frame[frameRange] = ... ;

int frameToTopic[f in frameRange][t in topicRange] = (t in frame[f].topics);
int knowledgeLevel[f in frameRange] = frame[f].knowledgeLevel;

tuple SessionData {
  int topicId; 
}  

// assume we're in an OC-trust session
{SessionData} openSessions[groups] = ...; // could be no session as well

//{SessionData} openSessions[groups] = [ {} ];

// knowledge levels are given on a scale {1,2,3}, 3 being the hardest
tuple UserData {
   int preferredKnowledgeLevel;
   {int} preferredTopics;
   {int} seenFrames;
}; 

UserData userData[users] = ...;

// predecessor handling
tuple Edge {
    int pred;
    int succ; // in minNodeId .. maxNodeId (consistency needs to be enforced elsewhere)
    {int} setPreds; // a guard expression
}; 

// this will be the parameterized content
{Edge} setEdges = ...;
int numberOfEdges = card(setEdges);

range edgeIds = 1 .. numberOfEdges+1;

Edge edges[edgeIds];
Edge defaultEdge = <0, 1, {}>;
execute edgeInitialization {
    edges[1] = defaultEdge;
    var i = 2;
	for(var e in setEdges) {
	  edges[i] = e;
	  i++;
    }	  
    for(var u in users) {
      userData[u].seenFrames.add(0); // 0 is by default always seen 
    }      
}  

// auxiliary data structures to map edge successors
int successors[e in edgeIds] = edges[e].succ;
int isSuccessor[e in edgeIds][f in frameRange] = (successors[e] == f);

// provide auxiliary data structures for each user in fact
int edgePredecessorSum[e in edgeIds][u in users] =  card(edges[e].setPreds inter userData[u].seenFrames) ;
int predCount[e in edgeIds] = card(edges[e].setPreds);
int edgePredecessorValid[e in edgeIds][u in users] = (predCount[e] == edgePredecessorSum[e][u]);
int isValid[e in edgeIds][u in users] = ((edgePredecessorValid[e][u] == true) && (edges[e].pred in  userData[u].seenFrames));

// it's all about the next frame to be displayed at this display
dvar int nextFrame in frameRange; 


// also select an edge for each group
dvar int nextEdge[users] in edgeIds;
{string} softConstraints = {"contentNotSeen",
                            "predecessorsOkay",
                            "knowledgeFits",
                            "userInterested"};
                            
// weights for penalties
int penalty_frameFitsTopic = ...;
int penalty_contentNotSeen = ...;
int penalty_predecessorsOkay = ...;
int penalty_knowledgeFits = ...;
int penalty_userInterested = ...;

{string} groupSoftConstraints = {"frameFitsTopic"};

dvar int nextTopic in topicRange;
dvar int+ userPenalties[softConstraints][users];
dvar int+ groupPenalties[groupSoftConstraints][groups]; 


dexpr int softConstraintViolation = sum(s in softConstraints, u in users) 
                                     (userPenalties[s][u]) + sum(s in groupSoftConstraints, g in groups) groupPenalties[s][g]; 
minimize softConstraintViolation;

subject to {
  // basically a channeling constraint
  frameToTopic[nextFrame][nextTopic] == 1;
  //nextFrame == 7;
  // 
  // topic has to be available at this instance
  nextTopic in possibleTopics;
  // if there is an open session, then the next topic should also be in a session
  forall(g in groups) {
    forall(s in openSessions[g]) {
        (frameToTopic[nextFrame][s.topicId] == 1 && groupPenalties["frameFitsTopic"][g] == 0) ||
        (frameToTopic[nextFrame][s.topicId] == 0 && groupPenalties["frameFitsTopic"][g] == penalty_frameFitsTopic);
    }      
  }    
  forall(g in groups) {   
    // not seen constraint 
    forall(userId in members[g]) {
      ( !(nextFrame in userData[userId].seenFrames) && userPenalties["contentNotSeen"][userId] == 0) ||
      ( (nextFrame in userData[userId].seenFrames) && userPenalties["contentNotSeen"][userId] == penalty_contentNotSeen); 
    }      
  }
  
  // every group member should have seen a predecessor of the next frame
  forall(u in users) { 
    // the selected next frame is reached by an appropriate edge for each group
    ((isSuccessor[nextEdge[u]][nextFrame] == true && isValid[nextEdge[u]][u] == true) && 
      userPenalties["predecessorsOkay"][u] == 0) || 
    ((isSuccessor[nextEdge[u]][nextFrame] == false || isValid[nextEdge[u]][u] == false) && 
      userPenalties["predecessorsOkay"][u] == penalty_predecessorsOkay);
  }        
  
  forall(g in groups) { 
    // it should not become too difficult for each member 
    forall(userId in members[g]) {
      (knowledgeLevel[nextFrame] == 1 && userPenalties["knowledgeFits"][userId] == 0) ||
      (knowledgeLevel[nextFrame] != 1 && userPenalties["knowledgeFits"][userId] == penalty_knowledgeFits);
//      (knowledgeLevel[nextFrame] <= userData[userId].preferredKnowledgeLevel && userPenalties["knowledgeFits"][userId] == 0) ||
//      (knowledgeLevel[nextFrame] > userData[userId].preferredKnowledgeLevel && userPenalties["knowledgeFits"][userId] == penalty_knowledgeFits);
    }   
  }
  
  forall(g in groups) { 
    // the topic shall fit the user's preference 
    forall(userId in members[g]) {
      (nextTopic in userData[userId].preferredTopics && 
          userPenalties["userInterested"][userId] == 0) ||
      (!(nextTopic in userData[userId].preferredTopics) && 
          userPenalties["userInterested"][userId] == penalty_userInterested);
    }   
  }     
}

execute {
  writeln('Post proc');
  writeln('Our next suggested frame is: ', nextFrame);
  writeln('Our selected edges were:');
  for(var u in users) {
    var edge = edges[nextEdge[u]];
    writeln("  ", u, ": ", edge);      
  }  
  writeln('Our next topic is: ', nextTopic);
  writeln("Inspecting violated user soft constraints");
  for(u in users) {
    for(var sc in softConstraints) {
    	if(userPenalties[sc][u] > 0) {
     		writeln("  Violated soft constraint: ", sc, " for user ", u);
     	}    	  
    }      
  }

  writeln("Inspecting violated group soft constraints");
  
  for(g in groups) {
    for(sc in groupSoftConstraints) {
    	if(groupPenalties[sc][g] > 0) {
     		writeln("  Violated soft constraint: ", sc, " for group ", g);
     	}    	  
    }      
  }
}    
