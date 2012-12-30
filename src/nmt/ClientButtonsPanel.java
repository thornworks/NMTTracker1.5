package nmt;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

/**
 *
 * This is used to display a list of all team Numbers
 * currently stored in the database.  The teams are each
 * displayed as separate buttons.
 *
 *
 */
public class ClientButtonsPanel
    extends JPanel implements ActionListener {
  NMTClient parent;
  int numTeamButtons;

  public static Color finishedColorML = new Color(150,0,0);
  public static Color finishedColorGL = new Color(230,0,0);
  public static Color emptyColor = new Color(60,60,60);
  //public static Color noScores
  public static Font doneFont = new Font("Dialog", Font.BOLD,15);
  
 private ArrayList<Integer> teamsUpper;
 private ArrayList<Integer> teamsLower;
 
  private ArrayList<Integer> completedGL;
  private ArrayList<Integer> completedML;
  private ArrayList<Integer> teamsCompleted;
  private ArrayList<Integer> noScores;
  
  
  private ArrayList<JButton> teamButtons;
  
  private ArrayList<Integer> allTeams;
  
  ClientButtonsFrame btnFrame = null;
  
  public ClientButtonsPanel(NMTClient frame) {
  	super();
    parent = frame;
    
    teamButtons = new ArrayList<JButton>();
    
    buildList(-1);
    
    
    numTeamButtons = parent.studentDB.numTeams();
    int gridSize = (int) (Math.sqrt(numTeamButtons) + 1);
    this.setLayout(new GridLayout(gridSize, gridSize));

    makeButtons(allTeams);
    
    
    for(int x = 0; x < teamButtons.size(); x++)
    	this.add(teamButtons.get(x));
    
    
  }
  
  public void buildList(int list) {
  	//List Codes
  	//-1 = all lists
  	// 0 = completedGL
  	// 1 = completedML
  	// 2 = completedTeams
  	// 3 = allTeams
  	// 4 = noScores
  	
  //	System.out.println("Building team lists");
  	
  	switch(list) {
  		case -1:
    		completedGL = parent.studentDB.getGLComplete();
    		completedML = parent.studentDB.getMLComplete();
    		teamsCompleted = parent.studentDB.getAllComplete();
    		noScores = parent.studentDB.getTeamsNoScore();
    		allTeams = new ArrayList<Integer>();
   			allTeams.addAll(parent.studentDB.getListTeamNum('L'));
   			allTeams.addAll(parent.studentDB.getListTeamNum('U'));
   			parent.teamsList = new ArrayList<Integer>(allTeams);
   			break;
   		case 0:
   			completedGL = parent.studentDB.getGLComplete();
   			break;
   		case 1:
   			completedML = parent.studentDB.getMLComplete();
   			break;
   		case 2:
   			teamsCompleted = parent.studentDB.getAllComplete();
   			break;
   		case 3:
   			allTeams = new ArrayList<Integer>();
   			allTeams.addAll(parent.studentDB.getListTeamNum('L'));
   			allTeams.addAll(parent.studentDB.getListTeamNum('U'));
   			parent.teamsList = new ArrayList<Integer>(allTeams);
   			break;
   		case 4:
   			noScores = parent.studentDB.getTeamsNoScore();	
  			
  	}
  	/*teamsUpper = parent.studentDB.getListTeamNum('U');
    teamsLower = parent.studentDB.getListTeamNum('L');

    completedGL = parent.studentDB.getGLComplete();
    completedML = parent.studentDB.getMLComplete();
    
    teamsCompleted = parent.studentDB.getAllComplete();
    
    allTeams = new ArrayList<Integer>();
    allTeams.addAll(teamsLower);
    allTeams.addAll(teamsUpper);
    
    parent.teamsList = new ArrayList<Integer>(allTeams);*/
  }
  
  /**
   *  Iterates through a list of team numbers and makes the buttons for
   *  each team.
   *
   *  Changelog:
   *
   *   2/14/09 - Efficiency improved
   *
   */
  private void makeButtons(ArrayList<Integer> list) {
    Font boldFont =  new Font("Dialog", Font.BOLD, 15);
    
    for(Integer n : list) {
    	JButton b = new JButton(Integer.toString(n));
    	b.setFont(boldFont);
    	b.addActionListener(this);
    	b.setActionCommand(Integer.toString(n));
    	teamButtons.add(b);
    }
    
  }
  
  /**
   *
   * Invokes the clearAllTeamButtons and makeButtons methods
   * It then adds the new buttons to the panel and redraws it.
   *
   * Changelog
   *	
   *	2/10/2009: Function created and sorting added
   *
   */
   
  public void doResort(int sortMethod) {
  	
  	////////////////
	// Sort Codes //
	///////////////
	
	// -1 - No Scores
	// 0 - All Teams
	// 1 - GL Completed
	// 2 - ML Completed
	// 3 - Completed Teams
  	
  	this.clearAllTeamButtons();
 	
 	if(sortMethod == 0) {
 		buildList(3);
 		makeButtons(allTeams);
 	}
 		
 	else if(sortMethod == -1) {
 		buildList(4);
 		makeButtons(noScores);
 	}
 	
 	else if(sortMethod == 1) {
 		buildList(0);
 		makeButtons(completedGL);
 	}
 	
 	else if(sortMethod == 2) {
 		buildList(1);
 		makeButtons(completedML);
 	}
 	
 	else if(sortMethod == 3) {
 		buildList(2);
 		makeButtons(teamsCompleted);
 	}  		
  	
  	for(JButton b : teamButtons)
  		this.add(b);
  	
  	btnFrame.pack();
  	
  	btnFrame.setTitle("NMT: " + teamButtons.size() + ( (teamButtons.size() == 1) ? " Team" : " Teams"));
  	
  	this.repaint();
  	
  } //doResort
  		
  	
  /**
   *
   * Removes all of the team buttons from the panel
   *
   * Changelog
   *	
   *	2/10/09: Function created
   *
   */
   
  public void clearAllTeamButtons() {
  	
  	this.removeAll();
  		
  	this.teamButtons.clear();
  	
  	this.repaint();
  	
  } // clearAllTeamButtons

  public void actionPerformed(ActionEvent e) {
    parent.displayTeam(Integer.parseInt(e.getActionCommand()));
  }
}
