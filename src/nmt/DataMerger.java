package nmt;

import java.net.*;
import java.util.*;
import java.io.*;

/**
 * <p>Title: Nassau Math Tournament Tracker</p>
 *
 * <p>Description: DateMerger is used to merge two nmt objects together over
 *    a network, using a TCP socket.  It also contains functionality </p>
 *
 * <p>Copyright: Copyright (c) 2007</p>
 *
 * <p>Company: Nassau County Interscholastic Math League</p>
 *
 * @author L. La Spina
 * @version 1.0
 */
public class DataMerger {

  //not sure if we are going to use this or keep things static:
  NMTracker studentDB; //object with all of the functionality for storing and sorting data.
  ObjectOutputStream out; //used to send data to the server
  boolean connected=false; //shows if connected to the server
  //what data should this class contain?
  //Maybe the TCP objects will be field variables.

  // add to constructor when we figure out what field variables to use...
  public DataMerger(NMTracker t) {
    studentDB=t;
  }

  /**
   *   Atttempts to locate another PC running the NMT Tracker.   Once it is
   *   located, a request to connect is sent.
   *
   */
  public void connectToServer() {
    InetAddress serverIP = ServerLocator.clientFindServer(3141, 3);
    if(serverIP==null)
    	return;
    //now that we have ip, establish a TCP connection
    try{
		Socket s=new Socket(serverIP, 3142);
		out=new ObjectOutputStream(s.getOutputStream());
		connected=true;
		System.out.println("Connected to server");
	}
	catch(Exception e){
		e.printStackTrace();
		return;
	}
  }

  /**
   *   Precondition: Must already be connected to a "server," another PC
   *   running the NMT Tracker.  This method sends the data stored in the
   *   student Database on this PC to the other machine using a TCP socket.
   *
   */

  public void sendDataBase() throws Exception{
	//check precondition
	if(!connected){
	   throw new Exception("No connection error: must connect to server before merging");
	}
	//send database
    out.writeObject(studentDB);
  }

  /**
   * Used to merge two NMTracker objects. This method can be used either by
   * two different files that we wish to merge or two different data objects
   * contained in memory on different workstations.

   * The number of teams stored can increase by as many as stored in teamsToAdd,
   * but it may remain the same.  When the two parameters contain the same team
   * number, the data from these two teams is merged using the Team merge method.
   * We would hope that if two teams are referenced by the same team number
   * then they contain the same students, in the same order, from the same school.
   * The only difference should be the score fields.
   * If two teams with the same number are not identical, a detailed error is displayed.
   * Note that the Team merge method already displays an error if two teams
   * have different schools. (This can easily happen by adding a space, say "McArthur HS"
   * vs. "Mc Arthur HS" or "BHS" vs. "Bethpage HS"
   *
   * All of the merging error checking is thus handled by the Team merge method.
   *
   * @param studentDB NMTracker - the database that will contain the merged teams.
   * @param teamsToAdd NMTTracker - this object will not be modified.  It will be
   *        incorporated into the first parameter.
   *
   * NOTE: Only the data stored in the first object will be modified.
   */
  public static void merge(NMTracker studentDB, NMTracker teamsToAdd) {
    TreeMap<Integer, Team> lowerTeamsToAdd = teamsToAdd.lowerDivisionTeams;
    TreeMap<Integer, Team> upperTeamsToAdd = teamsToAdd.upperDivisionTeams;
    //iterate through these Team Maps and add one Team at a time to studentDB.
    Set<Integer> lowerSet=lowerTeamsToAdd.keySet();//A set of team numbers
    //iterate through the maps
    for(Integer i: lowerSet){
        //if the team already exists in studentDB
        if(studentDB.containsTeamNum('L', i.intValue())){
            studentDB.lowerDivisionTeams.get(i).mergeTeam(lowerTeamsToAdd.get(i));
        }
        //if the team does not yet exist in studentDB
        else{
            studentDB.addTeam(lowerTeamsToAdd.get(i));
        }
    }
    Set<Integer> upperSet=upperTeamsToAdd.keySet();//A set of team numbers
    //iterate through the maps
    for(Integer i: upperSet) {
        //if the team already exists in studentDB
        if(studentDB.containsTeamNum('U', i.intValue()))
            studentDB.upperDivisionTeams.get(i).mergeTeam(upperTeamsToAdd.get(i));
        //if the team does not yet exist in studentDB
        else
            studentDB.addTeam(upperTeamsToAdd.get(i)); 
    }    

    //check if studentDB alrady contains that team, and if so, Team.merger 'em.
  }
  

  public static void main(String[] args) {
    DataMerger datemerger = new DataMerger(null);
  }
}
