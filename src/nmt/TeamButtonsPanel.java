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
public class TeamButtonsPanel
    extends JPanel implements ActionListener {
  NMTFrame parent;
  int numTeamButtons;

  public static Color finishedColorML = new Color(150,0,0);
  public static Color finishedColorGL = new Color(230,0,0);
  public static Color emptyColor = new Color(60,60,60);
  //public static Color noScores
  public static Font doneFont = new Font("Dialog", Font.BOLD, 15);
  
  private ArrayList<Integer> teamsUpper;
  private ArrayList<Integer> teamsLower;
  
  ArrayList<JButton> teamButtons;
  
  TeamButtonFrame btnFrame = null;
  
  public TeamButtonsPanel(NMTFrame frame) {
    super();
    parent = frame;
    
    teamButtons = new ArrayList<JButton>();
    
    teamsUpper = parent.studentDB.getListTeamNum('U');
    teamsLower = parent.studentDB.getListTeamNum('L');
    numTeamButtons = parent.studentDB.numTeams();
    int gridSize = (int) (Math.sqrt(numTeamButtons) + 1);
    System.out.print("Building button panel with size " + gridSize);
    this.setLayout(new GridLayout(gridSize, gridSize));

    makeButtons(teamsLower.iterator(), teamButtons, 0);
    makeButtons(teamsUpper.iterator(), teamButtons, 0);
    
    for(int x = 0; x < teamButtons.size(); x++)
    	this.add(teamButtons.get(x));
    
    parent.teamsList = parent.studentDB.getListTeamNum('L');
    parent.teamsList.addAll(parent.studentDB.getListTeamNum('U'));
  }
  
  /**
   *  Iterates through a list of team numbers and makes the buttons for
   *  each team.
   *
   */
  private void makeButtons(Iterator<Integer> iter, ArrayList<JButton> buttonList, int sortMethod) {
    String number;
    Integer n;
    Team t;
    JButton teamButton;
    Font plainFont = new Font("Dialog", Font.PLAIN, 15);
    Font boldFont =  new Font("Dialog", Font.BOLD, 15);

    while (iter.hasNext()) {
      n = iter.next();
      number = n.toString();
      t = parent.studentDB.getTeam(n.intValue());
      teamButton = new JButton(number);
      teamButton.setActionCommand(number);
      teamButton.addActionListener(this);
      teamButton.setToolTipText(t.getSchool());

      if(t.allDataEntered())
        teamButton.setFont(doneFont);
      else {
       	teamButton.setFont(plainFont);
       	if (t.completedGL() && !t.completedML()) {
        	teamButton.setForeground(finishedColorGL);
            teamButton.setFont(boldFont);
          }
          else if(t.completedGL() && t.completedML()) {
            teamButton.setForeground(finishedColorML);
			teamButton.setFont(boldFont);
          }

       }
      if(sortMethod == 0) // All Teams
     	 buttonList.add(teamButton);
     
      else if( (sortMethod == -1) && (!t.completedGL() && !t.completedML())) // No Scores entered
      	buttonList.add(teamButton);
      
      else if( (sortMethod == 1) && (t.completedGL() && !t.completedML())) // Only GL Scores
      	buttonList.add(teamButton);
      	
     else if( (sortMethod == 2) && (t.completedGL() && t.completedML() && !t.allDataEntered() ) )  // Only ML scores
      	buttonList.add(teamButton);
      
      else if( (sortMethod == 3) && (t.allDataEntered())) // All Scores Entered
      	buttonList.add(teamButton);
      	
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
  	
  	this.clearAllTeamButtons();

  	makeButtons(teamsLower.iterator(), teamButtons, sortMethod);
    makeButtons(teamsUpper.iterator(), teamButtons, sortMethod);

  	
  	// TODO: Add the rest of the sorting
  	
  	for(int x = 0; x < teamButtons.size(); x++)
  		this.add(teamButtons.get(x));
  	
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
  		
  	teamButtons.clear();
  	
  } // clearAllTeamButtons

  public void actionPerformed(ActionEvent e) {
    parent.displayTeam(Integer.parseInt(e.getActionCommand()));
  }
}
