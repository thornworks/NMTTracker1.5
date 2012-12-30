package nmt;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.*;
/**
 * This listener is called by the menu GUI when the user selects edit - choose school
 * It is used to build a list dialog box with a list of team numbers.
 * 
 *??  NMTClient and NMTFrame share much of the same code - subclass should be made?
 *
 */
public class ChooseSchoolActionListener implements ActionListener {
   NMTracker studentDB;
   NMTCSocket studentDBSocket;
   NMTFrame parentServer;
   NMTClient parentClient;
   
   /**
    *  Server constructor (called by NMTFrame)
    *
    */
   public ChooseSchoolActionListener(NMTFrame w) {
      parentServer = w;
      parentClient = null;
   }
   /**
    *  Client Constructor
    *
    */
   public ChooseSchoolActionListener(NMTClient c) {
      parentClient = c;
      parentServer = null;
   }

   /**
    * Method actionPerformed asks the user which school we are searching for
    * and then builds a dialog box with all the team numbers that match
    * the name.
    * @param e - not used.
    *
    */
   public void actionPerformed(ActionEvent e) {
      if(parentServer!=null) {
         dialogServer();
      }
      else {
         dialogClient();
      }
    }
    
    public void dialogServer() {
      studentDB = parentServer.studentDB;
      String s = JOptionPane.showInputDialog(parentServer,
                                               "Enter the school name.",
                                               "NMT",
                                               JOptionPane.PLAIN_MESSAGE);
     if(s == null || s.equals(""))
     	return;
   //build a JOptionPane with the possible team numbers from that school
      int[] teamList=studentDB.getTeamsFromSchools(s);
//      System.out.println("Teams :");
//      for(int k : teamList)
//        System.out.print(k + "  ");
      Object[] choices=new Object[teamList.length];
      for(int i=0;i<teamList.length; i++)
      choices[i]=teamList[i];
      String msg;
      int teamChoice = -1;
      
      if(choices.length == 1)
      	parentServer.displayTeam(teamList[0]);
      	
      else if(choices.length>1){
        //are all the schools returned the same school?
        if(studentDB.getTeam(teamList[0]).getSchool().equals(
           studentDB.getTeam(teamList[teamList.length-1]).getSchool())) {
           s = studentDB.getTeam(teamList[0]).getSchool();
           msg = "Which team would you like from the school, " + s + "?";
        }
        else
           msg = "Multiple schools have names containing \"" + s + 
           "\".  Select the team number.";
        
        teamChoice = JOptionPane.showOptionDialog(parentServer,
             msg,
             new String("Teams from School: " + s),
             JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
             null, choices, choices[0]);
      }
      else {
        JOptionPane.showMessageDialog(parentServer,
                s + " not found in database.", "NMT",
                JOptionPane.ERROR_MESSAGE);
      }
      if(teamChoice >=0)
         parentServer.displayTeam(teamList[teamChoice]);
   }

   public void dialogClient() {
      studentDBSocket = parentClient.studentDB;

      String s = JOptionPane.showInputDialog(parentClient,
                                               "Enter the school name.",
                                               "NMT",
                                               JOptionPane.PLAIN_MESSAGE);
      
      if(s == null || s.equals(""))
     		return;                                      
   //build a JOptionPane with the possible team numbers from that school
      int [] teamList=studentDBSocket.getTeamsFromSchools(s);

      Object[] choices=new Object[teamList.length];
      for(int i=0;i<teamList.length; i++)
      choices[i]=teamList[i];
      String msg;
      int teamChoice = -1;
      String firstSchool,lastSchool;
      if(choices.length == 1)
      	parentClient.displayTeam(teamList[0]);
      	
      else if(choices.length > 1){
        //are all the schools returned the same school?
        firstSchool = studentDBSocket.getTeam(teamList[0]).getSchool();
        lastSchool = studentDBSocket.getTeam(teamList[teamList.length-1]).getSchool();
        if(firstSchool.equals(lastSchool)) {
           msg = "Which team would you like from the school, " + firstSchool + "?";
        }
        else
           msg = "Multiple schools have names containing \"" + s + 
           "\".  Select the team number.";
        
        teamChoice = JOptionPane.showOptionDialog(parentClient,
             msg,
             new String("Teams from School: " + s),
             JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
             null, choices, choices[0]);
      }
      
      else {
        JOptionPane.showMessageDialog(parentClient,
                s + " not found in database.", "NMT",
                JOptionPane.ERROR_MESSAGE);
      }
      if(teamChoice >=0)
         parentClient.displayTeam(teamList[teamChoice]);
    } //end Client method
}
