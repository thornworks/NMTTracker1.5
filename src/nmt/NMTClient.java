package nmt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import java.util.*;
import java.net.*;

/**
 *
 * This class is almost the same as NMTFrame but
 * instead of linking to the database, this class
 * links to a socket to send data to the server
 */
 
public class NMTClient extends JFrame implements ActionListener{
  int lastTeamNum;
  int currentTeamIndex;
  JTextField teamNumberField, schoolField;
  JButton updateTeamBtn;
  JButton enterTeamBtn;
  JButton removeTeamBtn;
  JButton prevTeamBtn = new JButton("<");
  JButton nextTeamBtn = new JButton(">");
  File myDirectory = null; //default director to load and save from,
  TeamPanel teamPanel = null;
  NMTCSocket studentDB; //object with all of the functionality for storing and sorting data.
  JPanel topPanel, TeamButtonsPanel;
  //delete teamsButtonPanel.
  JFrame buttonsFrame = null;
  static JFrame mainWindow; //there will only be one window open, do other classes need it?
  ClientFocusTraversalPolicy policy;
  ArrayList<Integer> teamsList;

  public NMTClient() {
    super("Nassau Math Tournament Score Tracker Client");
    buildMenu();
    //setNativeLookAndFeel();
    topPanel = new JPanel();
    topPanel.add(prevTeamBtn);
    topPanel.add(new JLabel("Team #"));
    teamNumberField = new JTextField(3);
    teamNumberField.addActionListener(new ClientFieldListener(this));
    topPanel.add(teamNumberField);
    topPanel.add(new JLabel("School: "));
    schoolField = new JTextField(30);
    topPanel.add(schoolField);
    topPanel.add(nextTeamBtn);
    this.getContentPane().add(topPanel, BorderLayout.NORTH);
    teamPanel = new TeamPanel(this);
    this.getContentPane().add(teamPanel, BorderLayout.CENTER);
    
    JPanel buttonPanel = new JPanel();
    enterTeamBtn = new JButton("Create Team");
    enterTeamBtn.addActionListener(this);
    updateTeamBtn=new JButton("Update Team");
    updateTeamBtn.addActionListener(new ActionListener(){
        public void actionPerformed(ActionEvent e){
            updateTeam();
        }
    });
    removeTeamBtn = new JButton("Remove Team");
    removeTeamBtn.addActionListener(this);
    
    buttonPanel.add(enterTeamBtn);
    buttonPanel.add(updateTeamBtn);
    buttonPanel.add(removeTeamBtn);
    
    prevTeamBtn.setActionCommand("P");
    prevTeamBtn.addActionListener(new TeamChangeActionListener(this));
    
    nextTeamBtn.setActionCommand("N");
    nextTeamBtn.addActionListener(new TeamChangeActionListener(this));
    
    currentTeamIndex = -1;
    
    this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
    //policy=new ClientFocusTraversalPolicy(this, teamPanel);
    //this.setFocusTraversalPolicy(policy);
    InetAddress ip=ServerLocator.clientFindServer(3141, 4);
    studentDB = new NMTCSocket(ip, 4444);
    mainWindow = this;
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    
    teamsList = studentDB.getListTeamNum('L');
    teamsList.addAll(studentDB.getListTeamNum('U'));
  }

  public void dispose() {
    if(buttonsFrame != null)
       buttonsFrame.dispose();
    System.out.println("Closing connection.");
    studentDB.closeConnection();
    System.out.println("Connection Closed.");
    super.dispose();
  }
  /**
   *
   *  Builds a menu bar with the following JMenus:
   *  File: Quit
   *  Goto: Team Number
   *  Merge:  SetServer, sendData
   *  Help: About
   */

  public void buildMenu() {
    JMenuBar bar = new JMenuBar();
    this.setJMenuBar(bar);
    // create the File menu---------------------------------
    JMenu fileMenu = new JMenu("File");
    fileMenu.setMnemonic('F');
    // to exit out of the program
    JMenuItem exitItem = new JMenuItem("Quit", 'Q');
    exitItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        System.exit(0);
      }
    });
    fileMenu.add(exitItem);
    bar.add(fileMenu);

    // -------create the Goto menu---------------------------------
    JMenu gotoMenu = new JMenu("Edit");
    gotoMenu.setMnemonic('E');
    JMenuItem gotoTeam = new JMenuItem("Goto Team#", 'G');
    JMenuItem gotoSchool = new JMenuItem("Goto School", 'S');
    gotoSchool.setAccelerator(KeyStroke.getKeyStroke("ctrl F"));
    gotoMenu.add(gotoTeam);
    gotoTeam.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        if (studentDB.numTeams() > 0) {
          swapCenterPanel("TeamButtonsPanel");
        }
      }
    });

    gotoSchool.addActionListener( new ChooseSchoolActionListener(this));
 /*   
    gotoSchool.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String s = JOptionPane.showInputDialog(topPanel,
                                               "Enter the school name.",
                                               "NMT",
                                               JOptionPane.PLAIN_MESSAGE);
   //build a JOptionPane with the possible team numbers from that school
        int[] teamList=studentDB.getTeamsFromSchools(s);
        Object[] choices=new Object[teamList.length];
        for(int i=0;i<teamList.length; i++)
          choices[i]=teamList[i];
        if(choices.length>0){
          int choice = JOptionPane.showOptionDialog(mainWindow,
            new String("Which team would you like from the school, " + s + "?"),
            new String("Teams from School: " + s),
            JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE,
            null, choices, choices[0]);
          displayTeam(teamList[choice]);
        }
        else{
          JOptionPane.showMessageDialog(topPanel,
                  s + " not found in database.", "NMT",
                  JOptionPane.ERROR_MESSAGE);
        }

        //Component parentComponent, Object message,
        //String title, int messageType)
      }
    }); */
    gotoMenu.add(gotoSchool);
    
    gotoMenu.addSeparator();
    JMenuItem delete=new JMenuItem("Remove Team", 'R');
    gotoMenu.add(delete);
    delete.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        String s = JOptionPane.showInputDialog(
            topPanel,
            "Enter number of team to be removed.",
            "NMT",
            JOptionPane.PLAIN_MESSAGE);
        try {
          if(studentDB.removeTeam(Integer.parseInt(s)))
            JOptionPane.showMessageDialog(topPanel, s + " has been removed.");
          else
            JOptionPane.showMessageDialog(topPanel,
                  s + " not found in database.", "NMT",
                  JOptionPane.ERROR_MESSAGE);
        }
        catch (NumberFormatException nfe) {   }
      } //end action
    });
    
    gotoMenu.addSeparator();
    JMenuItem clear = new JMenuItem("Clear All Fields",'C');
    clear.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
      	teamPanel.clearAllFields();
      	schoolField.setText("");
      	teamNumberField.setText("");
      	currentTeamIndex = -1;
      }
    });
    gotoMenu.add(clear);
    bar.add(gotoMenu); // add Goto menu


// --------------------------- Merge Menu:  SetServer, sendData, data file
    JMenu mergeMenu = new JMenu("Server");
    mergeMenu.setMnemonic('M');
    JMenuItem setServer = new JMenuItem("Set Server", 'S');
    mergeMenu.add(setServer);

    /*JMenuItem sendData = new JMenuItem("Send Data to Server", 'D');
    mergeMenu.add(sendData);
    sendData.setEnabled(false);
    JMenuItem mergeFile = new JMenuItem("Merge with Data File", 'F');
    mergeFile.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        try{
         //  openFileMerge();
        }
        catch(Exception a){
           a.printStackTrace();
        }
      }
    });
    mergeMenu.add(mergeFile);
    */

    bar.add(mergeMenu);

    JMenu helpMenu = new JMenu("Help");
    JMenuItem aboutItem = new JMenuItem("About", 'A');
    aboutItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) {
        showAboutMessage();
      }
    });
    helpMenu.add(aboutItem);
    bar.add(helpMenu); // adds the helpMenu
  } //end of buildMenu method.

//-----------Methods to handle menu functions
//-----------Help menu-------------
  public void showAboutMessage() {
    JOptionPane.showMessageDialog(this,
                                  "The Nassau Math Tournament Score Tracker was designed to help\n " +
                                  "improve the efficiency of the scoring room for the NMT.\n" +
                                  "Version 1.3 written by:\n" +
                                  "  L. La Spina (NMT Committee member)\n" +
                                  "  Russell Kraner (4 time NMT participant, Bethpage HS class of 2007)\n" +
                                  "  Ryan Adams (Bethpage HS class of 2007)\n" +
                                  "  Kyle Dayton (Bethpage HS class of 2010)\n",
                                  "About", JOptionPane.PLAIN_MESSAGE);
  }

  /**
   *  This method gets called whent the enter team button is pressed.
   *  Some simple error checking is performed before we attempt to create a new team.
   */
  public void actionPerformed(ActionEvent e) {
  	if(e.getSource() == enterTeamBtn) {
	     int teamNum = 0;
	     String school, studentName;
	     try {
	        teamNum = Integer.parseInt(teamNumberField.getText());
	     }
	     //if the number was not entered or there is a letter in the field, end method.
	     catch(NumberFormatException n) {
	        JOptionPane.showMessageDialog(this,
	          "Please enter a team number.",
	          "Data Entry Error",
	          JOptionPane.ERROR_MESSAGE);
	        return;
	     }
	     school = this.schoolField.getText();
	     if(school.length() < 2) {
	        JOptionPane.showMessageDialog(this,
	          "Please enter a school name.",
	          "Data Entry Error",
	          JOptionPane.ERROR_MESSAGE);
	        return;
	     }
	     char d;
	     Student[] students=new Student[5];
	     try {
	       String[] sN=teamPanel.getStudentNames();
	       int[] grades=teamPanel.getGradeLevels();
	       if(grades[0]==9 || grades[0]==10)
	         d='L';
	       else
	         d='U';
	       int[] GL=teamPanel.getGradeScores();
	       int[] ML=teamPanel.getMathleticsScores();
	       for(int c=0; c<5; c++) {
	          students[c]=new Student(sN[c]);
	          students[c].setGrade(grades[c]);
	          students[c].setScoreGL(GL[c]);
	          students[c].setScoreML(ML[c]);
	          students[c].setSchool(school);
	       }
	     }
	     catch(NumberFormatException n) {
	        JOptionPane.showMessageDialog(this,
	          "Please enter numbers for grade levels, GL scores, and ML scores.",
	          "Data Entry Error",
	          JOptionPane.ERROR_MESSAGE);
	        return;
	     }
	     catch(DataRangeException f){
	       JOptionPane.showMessageDialog(this,
	                 "Grade level scores are between 0 and 15;\n" +
	                 "Mathletics scores range from 0 to 10.",
	                 "Data Entry Error",
	          JOptionPane.ERROR_MESSAGE);
	        return;
	     }
	     int teamScore=0;
	     try {
	         teamScore=teamPanel.getTeamScore();
	     }
	     catch(NumberFormatException n){
	        JOptionPane.showMessageDialog(this,
	          "Please enter a number for team score.",
	          "Data Entry Error",
	          JOptionPane.ERROR_MESSAGE);
	     }
	     catch(DataRangeException f){
	       JOptionPane.showMessageDialog(this,
	         "Please enter the raw team score for the team round: 0 - 20.",
	         "Data Entry Error",
	         JOptionPane.ERROR_MESSAGE);
	
	        return;
	     }
	     Team t=new Team(students, teamNum, school, d, teamScore);
	     if(studentDB.containsTeamNum(d, teamNum)){
	        JOptionPane.showMessageDialog(this, "Team "+teamNum+" already exists.");
	        return;
	     }
	     studentDB.addTeam(t);
	     teamPanel.clearAllFields();
	     teamNumberField.setText("");
	     schoolField.setText("");
	     teamNumberField.requestFocusInWindow();
	     //also clear the fields in this class.
  	}
  	else if(e.getSource() == removeTeamBtn) {
  		if(!teamNumberField.getText().trim().equals("")) {
	  		int option = JOptionPane.showConfirmDialog(this, "Permanently remove team #" + teamNumberField.getText() + " (" + schoolField.getText() + ")", "Remove Team",  
	  													JOptionPane.YES_NO_OPTION);
	  		if(option == JOptionPane.YES_OPTION) {
	  			studentDB.removeTeam(Integer.parseInt(teamNumberField.getText()));
	  			teamPanel.clearAllFields();	
	  			teamNumberField.setText("");
	  			schoolField.setText("");
	  		}
  		}
  	}
  }

  public void updateTeam() {
     int teamNum = 0;
     String school, studentName;
     try {
        teamNum = Integer.parseInt(teamNumberField.getText());
     }
     //if the number was not entered or there is a letter in the field, end method.
     catch(NumberFormatException n) {
        JOptionPane.showMessageDialog(this,
          "Please enter a team number.",
          "Data Entry Error",
          JOptionPane.ERROR_MESSAGE);
        return;
     }
     if(teamNum!=lastTeamNum){
        JOptionPane.showMessageDialog(this, "Do not change the team number.");
        return;
     }
     school = this.schoolField.getText();
     if(school.length() < 2) {
        JOptionPane.showMessageDialog(this,
          "Please enter a school name.",
          "Data Entry Error",
          JOptionPane.ERROR_MESSAGE);
        return;
     }
     char d;
     Student[] students=new Student[5];
     try {
       String[] sN=teamPanel.getStudentNames();
       int[] grades=teamPanel.getGradeLevels();
       if(grades[0]==9 || grades[0]==10)
         d='L';
       else
         d='U';
       int[] GL=teamPanel.getGradeScores();
       int[] ML=teamPanel.getMathleticsScores();
       for(int c=0; c<5; c++) {
          students[c]=new Student(sN[c]);
          students[c].setGrade(grades[c]);
          students[c].setScoreGL(GL[c]);
          students[c].setScoreML(ML[c]);
          students[c].setSchool(school);
       }
     }
     catch(NumberFormatException n) {
        JOptionPane.showMessageDialog(this,
          "Please enter numbers for grade levels, GL scores, and ML scores.",
          "Data Entry Error",
          JOptionPane.ERROR_MESSAGE);
        return;
     }
     catch(DataRangeException f){
       JOptionPane.showMessageDialog(this,
                 "Grade level scores are between 0 and 15;\n" +
                 "Mathletics scores range from 0 to 10.",
                 "Data Entry Error",
          JOptionPane.ERROR_MESSAGE);
        return;
     }
     int teamScore=0;
     try {
         teamScore=teamPanel.getTeamScore();
     }
     catch(NumberFormatException n){
        JOptionPane.showMessageDialog(this,
          "Please enter a number for team score.",
          "Data Entry Error",
          JOptionPane.ERROR_MESSAGE);
     }
     catch(DataRangeException f){
       JOptionPane.showMessageDialog(this,
         "Please enter the raw team score for the team round: 0 - 20.",
         "Data Entry Error",
         JOptionPane.ERROR_MESSAGE);

        return;
     }
     Team t=new Team(students, teamNum, school, d, teamScore);
     studentDB.addTeam(t);
     String s="Team #"+teamNum+" updated. \n";
     if(studentDB.getTeam(teamNum).completedGL())
        s+=("Grade Level scores saved.\n");
     else
        s+=("Grade Level scores not present.\n");
     if(studentDB.getTeam(teamNum).completedML())
        s+=("Mathletics scores saved.\n");
     else
        s+=("Mathletics scores not present.\n");
     if(studentDB.getTeam(teamNum).getScore()>0)
        s+=("Team score saved.\n");
     else
        s+=("Team score not present.\n");
     JOptionPane.showMessageDialog(this, s);
     teamPanel.clearAllFields();
     teamNumberField.setText("");
     schoolField.setText("");
     teamNumberField.requestFocusInWindow();
     //also clear the fields in this class.
  }

  /**
   *  Replaces the center panel.
   *  The center panel can display all of the buttons
   *  or it can display a single team.
   */
  public void swapCenterPanel(String panel) {
    if(buttonsFrame == null || !buttonsFrame.isVisible())
   		buttonsFrame = new ClientButtonsFrame(this);
   	else
   		buttonsFrame.setVisible(true);

/*
     if (panel.equals("TeamButtonsPanel")) {
      TeamButtonsPanel = new TeamButtonsPanel(this);
      this.getContentPane().remove(teamPanel);
      this.getContentPane().add(TeamButtonsPanel, BorderLayout.CENTER);
    }
    else {
      this.getContentPane().remove(TeamButtonsPanel);
      this.getContentPane().add(teamPanel, BorderLayout.CENTER);
    }
    pack();
    repaint();
*/
  }

  /**
   * Updates the fields in the TeamPanel to display a specified team.
   * This method does not save any data currently entered in the text fields,
   * so whatever the user typed will be lost when this method is called.
   *
   */
  public void displayTeam(int teamNumber) {
    Team t = studentDB.getTeam(teamNumber);
    if (t != null) {
      this.teamNumberField.setText(Integer.toString(teamNumber));
      this.schoolField.setText(t.getSchool());
      this.teamPanel.setTeam(t);
      lastTeamNum=t.getTeamNum();
      currentTeamIndex = teamsList.indexOf(teamNumber);
    }
    else {
      JOptionPane.showMessageDialog(this,
                                    "Team #" + teamNumber +
                                    " was not found in the database.",
                                    "Input Error",
                                    JOptionPane.ERROR_MESSAGE);
    }
  }

  public static void setNativeLookAndFeel() {
    try {
      UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
    }
    catch (Exception e) {
      System.out.println("Error setting native LAF: " + e);
    }
  }

  public static void setMotifLookAndFeel() {
    try {
      UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
    }
    catch (Exception e) {
      System.out.println("Error setting Motif LAF: " + e);
    }
  }

  public static void main(String[] args) {
    NMTClient window = new NMTClient();
    window.pack();
    window.setVisible(true);
  }
}