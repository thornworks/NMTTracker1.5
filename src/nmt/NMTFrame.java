package nmt;

import java.awt.*;
import java.util.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.*;
import javax.swing.UIManager.*;

// ANY-ACCESS-MODIFIER static final long serialVersionUID = 42L;
// add this to all Serializable classes.
/**
 *  This is the main class that includes the GUI components, the menu, and
 *  the main method.
 *
 */
public class NMTFrame extends JFrame implements ActionListener {

    int lastTeamNum;
    JTextField teamNumberField, schoolField;
    JButton updateTeamBtn;
    JButton enterTeamBtn;
    JButton removeTeamBtn;
    JButton prevTeamBtn; //= new JButton("<");
    JButton nextTeamBtn; //= new JButton(">");
    int currentTeamIndex;
    File myDirectory = null; //default director to load and save from,
    TeamPanel teamPanel = null;
    NMTracker studentDB; //object with all of the functionality for storing and sorting data.
    JPanel topPanel, TeamButtonsPanel;
    public JFrame buttonsFrame = null;
    NMTServer server;
    ServerLocator findMe;
    ArrayList<Integer> teamsList;
    HistogramFrame histogram;
    String currentFile;
    AutoSaveThread autoSaver;
    JCheckBoxMenuItem autosaveItem;
    public static JFrame mainWindow;
    //used to reference this JFrame from within other classes and inner classes.
    //we need a way to get to THIS from dialog boxes to anchor them to the main program window.

    /* useless constructor:
    public NMTFrame(String s) {
        super(s);
        mainWindow = this;
    }*/

    public NMTFrame() {
        super("Nassau Math Tournament Score Tracker");
        mainWindow = this;
        studentDB = new NMTracker();
        buildMenu();
        topPanel = new JPanel();
        Font arrowFont = new Font("Symbol", Font.BOLD, 14);
        prevTeamBtn = new JButton("\uF0AC"); 
        nextTeamBtn = new JButton("\uF0AE");  //also unicode 2190, 2192
        prevTeamBtn.setFont(arrowFont);
        nextTeamBtn.setFont(arrowFont);
        prevTeamBtn.setActionCommand("P");
        prevTeamBtn.addActionListener(new TeamChangeActionListener(this));
        nextTeamBtn.setActionCommand("N");
        nextTeamBtn.addActionListener(new TeamChangeActionListener(this));

        topPanel.add(prevTeamBtn);
        topPanel.add(new JLabel("Team #"));
        teamNumberField = new JTextField(3);
        teamNumberField.addActionListener(new TeamFieldListener(this));
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
        updateTeamBtn = new JButton("Update Team");
        removeTeamBtn = new JButton("Remove Team");
        removeTeamBtn.addActionListener(this);
        updateTeamBtn.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                updateTeam();
            }
        });
        buttonPanel.add(enterTeamBtn);
        buttonPanel.add(updateTeamBtn);
        buttonPanel.add(removeTeamBtn);
        this.getContentPane().add(buttonPanel, BorderLayout.SOUTH);
        policy = new TeamFocusTraversalPolicy(this, teamPanel);
        this.setFocusTraversalPolicy(policy);
        findMe = new ServerLocator();
        findMe.start();
        server = new NMTServer(4444, studentDB);
        server.start();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        this.addWindowListener(new WindowAdapter() {
                public void windowClosing(WindowEvent we) {
                    exitConfirm();
                }  });
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        currentTeamIndex = -1;
        teamsList = studentDB.getListTeamNum('L');
        teamsList.addAll(studentDB.getListTeamNum('U'));
    }

    /**
     *  This is called when the window is closed?
     *  Here we end the server and close the buttons window if open.
     *  Since this is the main window, closing it causes the whole program to exit.
     */
    public void dispose() {
        if (buttonsFrame != null) {
            buttonsFrame.dispose();
        }
        System.out.println("Stopping server");
        server.stopServer();
        super.dispose();
        System.exit(0);
    }

    /**
     *  Builds a menu bar with the following JMenus:
     *  File: Open, Save, ExportHTML, Quit
     *  Goto: Team Number
     *  Stats: Upper Division Team, Lower Division Team, Grade 9,...,Grade 12
     *  Merge:  SetServer, sendData
     *  Help: About
     */
    public void buildMenu() {
        JMenuBar bar = new JMenuBar();
        this.setJMenuBar(bar);
        // create the File menu---------------------------------
        JMenu fileMenu = new JMenu("File");
        fileMenu.setMnemonic('F');
        // to make a new question
        JMenuItem openMenuItem = new JMenuItem("Open");
        openMenuItem.setMnemonic('O');
        openMenuItem.setAccelerator(KeyStroke.getKeyStroke("ctrl O"));
        openMenuItem.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                try {
                    openFile();
                } catch (Exception a) {
                    a.printStackTrace();
                }
            }
        });
        fileMenu.add(openMenuItem);

        // to save changes
        JMenuItem saveItem = new JMenuItem("Save");
        saveItem.setMnemonic('S');
        saveItem.setAccelerator(KeyStroke.getKeyStroke("ctrl S"));
        saveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    saveCurrentList();
                } catch (Exception a) {
                    JOptionPane.showMessageDialog(teamPanel, "An error ocured while trying to save:\n" +
                            a.toString(), "NMTracker Error",JOptionPane.ERROR_MESSAGE);
                    a.printStackTrace();
                }
            }
        });
        fileMenu.add(saveItem);

        JMenuItem saveAsItem = new JMenuItem("Save As");
        saveAsItem.setMnemonic('A');
        saveAsItem.setAccelerator(KeyStroke.getKeyStroke("ctrl shift S"));
        saveAsItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    saveList();
                } catch (Exception a) {
                    showFileError(a);
                }
            }
        });
        fileMenu.add(saveAsItem);
        fileMenu.addSeparator();

        //Export to HTML
        JMenuItem exportItem = new JMenuItem("Export HTML", 'E');
        exportItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exportHTML();
            }
        });
        fileMenu.add(exportItem);
        fileMenu.addSeparator();
        // to exit out of the program
        JMenuItem exitItem = new JMenuItem("Quit", 'Q');
        exitItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                exitConfirm();
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
                    swapCenterPanel();
                }
            }
        });

        gotoSchool.addActionListener(new ChooseSchoolActionListener(this));
        gotoMenu.add(gotoSchool);
        gotoMenu.addSeparator();

        JMenuItem delete = new JMenuItem("Remove Team", 'R');
        gotoMenu.add(delete);
        delete.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                String s = JOptionPane.showInputDialog(topPanel,
                        "Enter number of team to be removed.",
                        "NMT", JOptionPane.PLAIN_MESSAGE);
                try {
                    if (studentDB.removeTeam(Integer.parseInt(s))) {
                        JOptionPane.showMessageDialog(topPanel, s + " has been removed.");
                    } else {
                        JOptionPane.showMessageDialog(topPanel,
                                s + " not found in database.", "NMT",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException nfe) {
                }
            } //end action
        });
        gotoMenu.addSeparator();

        JMenuItem clear = new JMenuItem("Clear All Fields", 'C');
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

// -------------------Create Stats menu-------------------
// Upper Division Team, Lower Division Team, Grade 9,...,Grade 12
        JMenu statsMenu = new JMenu("Stats");
        statsMenu.setMnemonic('S');
        JMenuItem upperDivision = new JMenuItem("Upper Division Teams", 'U');
        upperDivision.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                displayStats(true, 1);
            }
        });
        statsMenu.add(upperDivision);
        JMenuItem lowerDivision = new JMenuItem("Lower Division Teams", 'L');
        lowerDivision.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                displayStats(true, 0);
            }
        });
        statsMenu.add(lowerDivision);

        statsMenu.addSeparator();

        
        JMenuItem grade9 = new JMenuItem("Grade 9");
        grade9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayStats(false, 9);
            }        });
        JMenuItem grade10 = new JMenuItem("Grade 10");
        grade10.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayStats(false, 10);
            }        });
        JMenuItem grade11 = new JMenuItem("Grade 11");
        grade11.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayStats(false, 11);
            }
        });
        JMenuItem grade12 = new JMenuItem("Grade 12", '9');
        grade12.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                displayStats(false, 12);
            }
        });

        JMenuItem histograms = new JMenuItem("Display Score Histogram");
        histograms.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                showHistograms();
            }
        });
        statsMenu.add(grade9);
        statsMenu.add(grade10);
        statsMenu.add(grade11);
        statsMenu.add(grade12);
        statsMenu.addSeparator();
        statsMenu.add(histograms);
        bar.add(statsMenu);

// --------------------------- Merge Menu:  SetServer, sendData, data file
        JMenu mergeMenu = new JMenu("Merge");
        mergeMenu.setMnemonic('M');

        /*
        JMenuItem setServer = new JMenuItem("Set Server", 'S');
        mergeMenu.add(setServer);

        JMenuItem sendData = new JMenuItem("Send Data to Server", 'D');
        mergeMenu.add(sendData);
        sendData.setEnabled(false);
         */
        JMenuItem mergeFile = new JMenuItem("Merge with File", 'F');
        mergeFile.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    openFileMerge();
                } catch (Exception a) {
                    a.printStackTrace();
                }
            }
        });
        mergeMenu.add(mergeFile);
        bar.add(mergeMenu);
        //-----------------------Options Menu---------------------
        JMenu optionsMenu = new JMenu("Options");
        optionsMenu.setMnemonic('O');

        //Enable / Disable autosave
        autosaveItem = new JCheckBoxMenuItem("Auto Save File");
        autosaveItem.setMnemonic('A');
        autosaveItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                try {
                    toggleAutoSave();
                } catch (Exception a) {
                    a.printStackTrace();
                }
            }
        });

        JMenuItem fontSizeItem = new JMenuItem("Increase Font Size");
        fontSizeItem.setMnemonic('I');
        fontSizeItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                //increaseFontSize();
                //System.out.println("Font size changed.");
                setLookAndFeel(1);
            }
        });

        optionsMenu.add(autosaveItem);
        optionsMenu.add(fontSizeItem);

        bar.add(optionsMenu);

        JMenu helpMenu = new JMenu("Help");
        JMenuItem aboutItem = new JMenuItem("About", 'A');
        aboutItem.addActionListener(new ActionListener() {
            @Override
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
                "The Nassau Math Tournament Score Tracker was designed to help\n "
                + "improve the efficiency of the scoring room for the NMT.\n"
                + "Version 1.3 written by:\n"
                + "  L. La Spina (NMT Committee member)\n"
                + "  Russell Kraner (4 time NMT participant, Bethpage HS class of 2007)\n"
                + "  Ryan Adams (Bethpage HS class of 2007)\n"
                + "  Kyle Dayton (Bethpage HS class of 2010)\n",
                "About", JOptionPane.PLAIN_MESSAGE);
    }

    //-----------File Menu------------
    /**
     * Used to open a file, why does this throw an exception?
     */
    public void openFile() throws FileNotFoundException {
        JFileChooser chooser = new JFileChooser("Open a data file");

        File myFilename;
        if (myDirectory == null) {
            chooser = new JFileChooser(
                    new File(System.getProperty("user.dir")));
        } else {
            chooser = new JFileChooser(myDirectory);
        }

        chooser.addChoosableFileFilter(new OpenFileFilter());

        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            myFilename = chooser.getSelectedFile();
            System.out.println("Loading file: " + myFilename.toString());
            currentFile = myFilename.toString();
            myDirectory = chooser.getCurrentDirectory();
            studentDB = new NMTracker(NMTParser.parse(myFilename));
            System.out.println(studentDB.numTeams() + " teams loaded.");
            server.changeDB(studentDB);
            this.teamPanel.clearAllFields();
            teamNumberField.setText("");
            schoolField.setText("");

            currentTeamIndex = -1;
            teamsList = studentDB.getListTeamNum('L');
            teamsList.addAll(studentDB.getListTeamNum('U'));
        } //endIf
    }

    /**
     *  A file dialog is displayed for the user to pick a data file.
     *  When the file is opened, it is merged with the current database.
     *  If two teams (identical numbers) are found in the data file and
     *  in memory, then the two teams are merged.  This should mean that
     *  whichever Team object contains more data will be used.
     */
    public void openFileMerge() throws Exception {
        JFileChooser chooser = new JFileChooser();
        File myFilename;
        if (myDirectory == null) {
            chooser = new JFileChooser(
                    new File(System.getProperty("user.dir")));
        } else {
            chooser = new JFileChooser(myDirectory);
        }

        chooser.addChoosableFileFilter(new OpenFileFilter());
        int returnVal = chooser.showOpenDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            myFilename = chooser.getSelectedFile();
            System.out.println(myFilename);
            myDirectory = chooser.getCurrentDirectory();
            NMTracker t = new NMTracker(NMTParser.parse(myFilename));
            DataMerger.merge(studentDB, t);
        } //endIf
    }

    /**
     *  This method displays a FileChooser dialog which lets the user select where to save the file.
     *  If a file has already been selected and we are just re-saving, use saveCurrentList instead.
     */
    public void saveList() {
        JFileChooser chooser;
        File myFilename;
        if (myDirectory == null) {
            chooser = new JFileChooser(
                    new File(System.getProperty("user.dir")));
        } else {
            chooser = new JFileChooser(myDirectory);
        }

        chooser.addChoosableFileFilter(new OpenFileFilter());
        int returnVal = chooser.showSaveDialog(this);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            myFilename = chooser.getSelectedFile();

            if (myFilename.getName().lastIndexOf(".") == -1) {
                myFilename = new java.io.File(myFilename.getAbsolutePath() + ".csv");
            }

            System.out.println(myFilename);
            myDirectory = chooser.getCurrentDirectory();
            if (currentFile == null || currentFile.equals("")) {
                currentFile = myFilename.toString();
            }
            //studentDB=new NMTracker(NMTParser.parse(myFilename));
            try {
                FileOutputStream fstream = new FileOutputStream(myFilename);
                NMTParser.saveList(fstream, studentDB);

            } catch (Exception ioe) {
                showFileError(ioe);
            }
        } //endIf
    }
    private void showFileError(Exception ioe) {
        JOptionPane.showMessageDialog(this,"An error occured while attempting to save.  Unable to save file.\n" +
                ioe.toString(), "NMTracker File Error", JOptionPane.ERROR_MESSAGE);
        ioe.printStackTrace();
    }
    public void saveCurrentList() {
        if (currentFile != null && !currentFile.equals("")) {
            try {
                NMTParser.saveList(new FileOutputStream(new File(currentFile)), studentDB);
            } catch (Exception ioe) {
                showFileError(ioe);
            }
        } else {
            saveList();
        }
    }

    /**
     *  Generates all of the HTML files for top student lists
     *  as well as the image files for the histogram data.
     *
     *  TODO: Place all files in a zip.
     */
    public void exportHTML() {
        NMTParser.exportStudent("TopGrade9.html", studentDB, 9, 20);
        NMTParser.exportStudent("TopGrade10.html", studentDB, 10, 20);
        NMTParser.exportStudent("TopGrade11.html", studentDB, 11, 20);
        NMTParser.exportStudent("TopGrade12.html", studentDB, 12, 20);
        NMTParser.exportTeam("TopLowerTeams.html", studentDB, 0, 20);
        NMTParser.exportTeam("TopUpperTeams.html", studentDB, 1, 20);
        JOptionPane.showMessageDialog(this, "Web pages generated for available data", "NMT",
                JOptionPane.INFORMATION_MESSAGE);
    } //end exportHTML

    /**
     *  This method gets called when one of the team buttons is pressed.
     *  Some simple error checking is performed before we attempt to create a new team.
     */
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == enterTeamBtn) {
            try {
                createNewTeam();
            }
            catch(nmt.DataRangeException oops) {
                JOptionPane.showMessageDialog(this,
                        oops.getMessage(), "Data Entry Error",JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == removeTeamBtn) {
            if (!teamNumberField.getText().trim().equals("")) {
                int option = JOptionPane.showConfirmDialog(this, "Permanently remove team #" + teamNumberField.getText() + " (" + schoolField.getText() + ")", "Remove Team",
                        JOptionPane.YES_NO_OPTION);
                if (option == JOptionPane.YES_OPTION) {
                    studentDB.removeTeam(Integer.parseInt(teamNumberField.getText()));
                    teamPanel.clearAllFields();
                    teamNumberField.setText("");
                    schoolField.setText("");
                }
            }
        }
    }

    /**
     * This method is called whenever the EnteTeamButton is pressed to create a new team.
     * Elements from the GUI are processed and validated here.
     */
    private void createNewTeam() throws DataRangeException {
        int teamNum = 0;
        String school, studentName;
        try {
            teamNum = Integer.parseInt(teamNumberField.getText());
        } //if the number was not entered or there is a letter in the field, end method.
        catch (NumberFormatException n) {
            throw new DataRangeException("Please enter a team number.");
        }
        school = this.schoolField.getText();
        if (school.length() < 2) {
            throw new DataRangeException("Please enter a school name.");
        }
        char division;
        Student[] students = new Student[5];
        try {
            String[] sN = teamPanel.getStudentNames();
            int[] grades = teamPanel.getGradeLevels();
            if (grades[0] == 9 || grades[0] == 10) {
                division = 'L';
            } else {
                division = 'U';
            }

            int[] GL = teamPanel.getSitupScores();
            int[] ML = teamPanel.getPushupScores();
            for (int c = 0; c < 5; c++) {
                if(division=='L') {
                    if(grades[c]>10)
                        throw new DataRangeException("All students on a lower divsion team must be in grade 10 or lower.");
                }
                if(division == 'U') {
                    if(grades[c]<=10)
                        throw new DataRangeException("All students on an upper divsion team must compete as juniors or seniors.\n"+
                              "If " + sN[c] + " is competing as a junior today, you can indicate his/her real grade by writing\n" +
                              sN[c] + "(" + grades[c] + ").");
                }
                students[c] = new Student(sN[c]);
                students[c].setGrade(grades[c]);
                students[c].setScoreGL(GL[c]);
                students[c].setScoreML(ML[c]);
                students[c].setSchool(school);
            }
        } catch (NumberFormatException n) {
            throw new DataRangeException("Please enter numbers for grade levels, GL scores, and ML scores.");
        }
        int teamScore = 0;
        try {
            teamScore = teamPanel.getTeamScore();
        } catch (NumberFormatException n) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a number for team score.",
                    "Data Entry Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DataRangeException f) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the raw team score for the team round: 0 - 20.",
                    "Data Entry Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }
        Team t = new Team(students, teamNum, school, division, teamScore);
        if (studentDB.containsTeamNum(division, teamNum)) {
            JOptionPane.showMessageDialog(this, "Team " + teamNum + " already exists.  Use the update button to change the data for this team.");
            return;
        }
        studentDB.addTeam(t);
        teamPanel.clearAllFields();
        teamNumberField.setText("");
        schoolField.setText("");
        teamNumberField.requestFocusInWindow();
    }

    public void updateTeam() {
        int teamNum = 0;
        String school, studentName;
        try {
            teamNum = Integer.parseInt(teamNumberField.getText());
        } //if the number was not entered or there is a letter in the field, end method.
        catch (NumberFormatException n) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a team number.",
                    "Data Entry Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (teamNum != lastTeamNum) {
            JOptionPane.showMessageDialog(this, "Do not change the team number.");
            return;
        }
        school = this.schoolField.getText();
        if (school.length() < 2) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a school name.",
                    "Data Entry Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        char d;
        Student[] students = new Student[5];
        try {
            String[] sN = teamPanel.getStudentNames();
            int[] grades = teamPanel.getGradeLevels();
            if (grades[0] == 9 || grades[0] == 10) {
                d = 'L';
            } else {
                d = 'U';
            }
            int[] GL = teamPanel.getSitupScores();
            int[] ML = teamPanel.getPushupScores();
            for (int c = 0; c < 5; c++) {
                students[c] = new Student(sN[c]);
                students[c].setGrade(grades[c]);
                students[c].setScoreGL(GL[c]);
                students[c].setScoreML(ML[c]);
                students[c].setSchool(school);
            }
        } catch (NumberFormatException n) {
            JOptionPane.showMessageDialog(this,
                    "Please enter numbers for grade levels, GL scores, and ML scores.",
                    "Data Entry Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        } catch (DataRangeException f) {
            JOptionPane.showMessageDialog(this,
                    "Grade level scores are between 0 and 15;\n"
                    + "Mathletics scores range from 0 to 10.",
                    "Data Entry Error",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        int teamScore = 0;
        try {
            teamScore = teamPanel.getTeamScore();
        } catch (NumberFormatException n) {
            JOptionPane.showMessageDialog(this,
                    "Please enter a number for team score.",
                    "Data Entry Error",
                    JOptionPane.ERROR_MESSAGE);
        } catch (DataRangeException f) {
            JOptionPane.showMessageDialog(this,
                    "Please enter the raw team score for the team round: 0 - 20.",
                    "Data Entry Error",
                    JOptionPane.ERROR_MESSAGE);

            return;
        }
        //TODO - why not just update the existing team object rather than making a new one?
        Team t = new Team(students, teamNum, school, d, teamScore);
        studentDB.addTeam(t);
        String s = "Team #" + teamNum + " updated. \n";
        if (studentDB.getTeam(teamNum).completedGL()) {
            s += ("Grade Level scores saved.\n");
        } else {
            s += ("Grade Level scores not present.\n");
        }
        if (studentDB.getTeam(teamNum).completedML()) {
            s += ("Mathletics scores saved.\n");
        } else {
            s += ("Mathletics scores not present.\n");
        }
        if (studentDB.getTeam(teamNum).getScore() > 0) {
            s += ("Team score saved.\n");
        } else {
            s += ("Team score not present.\n");
        }
        JOptionPane.showMessageDialog(this, s);
        //teamPanel.clearAllFields();
        //teamNumberField.setText("");
        //schoolField.setText("");
        teamNumberField.requestFocusInWindow();
        teamPanel.setTeam(t);
    }

    /**
     *  Replaces the center panel.
     *  The center panel can display all of the buttons
     *  or it can display a single team.
     *  Depricated?  Replaced by a new frame of team # buttons.
     */
    public void swapCenterPanel() {
        if (buttonsFrame == null || !buttonsFrame.isVisible()) {
            buttonsFrame = new TeamButtonFrame(this);
        } else {
            buttonsFrame.setVisible(true);
        }

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
     * displays stats
     */
    public void displayStats(boolean isTeam, int level) {
        try {
            if (isTeam) {
                if (level == 0) {
                    NMTParser.dispTStats(level, studentDB, "TopLowerTeams.html");
                } else {
                    NMTParser.dispTStats(level, studentDB, "TopUpperTeams.html");
                }
            } else {
                NMTParser.dispIStats(level, studentDB, "TopGrade" + level + ".html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *  Displays a JOptionPane with choices for grade level, mathletics, or team histograms.
     *  A New window is displayed with the selected histograms to summarize scores.
     */
    public void showHistograms() {
        if (histogram == null || !histogram.isVisible()) {
            histogram = new HistogramFrame(this);
        } else {
            histogram.setVisible(true);
        }
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
            lastTeamNum = t.getTeamNum();
            currentTeamIndex = teamsList.indexOf(teamNumber);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Team #" + teamNumber
                    + " was not found in the database.",
                    "Input Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void setNativeLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.out.println("Error setting native LAF: " + e);
        }
    }

    public static void setMotifLookAndFeel() {
        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.motif.MotifLookAndFeel");
        } catch (Exception e) {
            System.out.println("Error setting Motif LAF: " + e);
        }
    }

    public void toggleAutoSave() {
        //	System.out.println(autosaveItem.isSelected());
        if (autosaveItem.isSelected()) {
            if (autoSaver == null) {
                autoSaver = new AutoSaveThread(this);
                autoSaver.start();
            } else {
                autoSaver.autoSaveOn = true;
            }
        } else {
            if (autoSaver != null) {
                autoSaver.autoSaveOn = false;
            }
        }
    }

    /**
     * Called whenever the user exits the program, either by closing the main window
     * or by selecting quit.
     */
    public void exitConfirm() {
        int choice = JOptionPane.showConfirmDialog(this,"Do you want to save first before exiting?",
                "Exit NMTracker",JOptionPane.YES_NO_CANCEL_OPTION);
        System.out.println(choice);
        if(choice==2) //cancel
            return;
        if(choice==0)  //yes was chosen, save first.
            saveCurrentList();
        System.exit(0);
    }

    public void increaseFontSize() {
        Font panelFont = new Font(Font.DIALOG, Font.PLAIN, 18);
        UIManager.put("defaultFont", panelFont);
        UIManager.put("TextPane.font", panelFont);
        UIManager.put("TextField.font", panelFont);
        UIManager.put("OptionPane.font", panelFont);
        UIManager.put("Label.font", panelFont);
        UIManager.put("Button.font", panelFont);


    }
    /**
     *  This is the main entry point fot the entire application.
     *  If there is a command line argument present, the first argument is a log
     *  filename.  If no argument is present, then error and debugging messages
     *  are sent to the console (which may not be visible when running from a jar)
     *
     */
    public static void setLookAndFeel(int fontSize) {
       /* UIManager.setLookAndFeel(new NimbusLookAndFeel() {
          @Override public UIDefaults getDefaults() {
               UIDefaults ret = super.getDefaults();
               ret.put("defaultFont", new Font(Font.MONOSPACED, Font.BOLD, 16));
               return ret;
          }   }); */
        Font panelFont;
        if(fontSize==1) {
            panelFont = UIManager.getFont("TextField.font");
            float oldSize = panelFont.getSize2D();
            panelFont = panelFont.deriveFont(oldSize+2);
        }
        else
            panelFont = new Font(Font.DIALOG, Font.PLAIN, fontSize);
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            UIManager.put("defaultFont", panelFont);
            UIManager.put("TextPane.font", panelFont);
            UIManager.put("TextField.font", panelFont);
            UIManager.put("OptionPane.font", panelFont);
            UIManager.put("Label.font", panelFont);
            UIManager.put("Button.font", panelFont);

            for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Nimbus is not available.
        }
    }
    public static void main(String[] args) {
        String logName = "NMTlog";
        System.out.println("NMTFrame is the main server class.");
        if (args.length > 0)
            logName = args[0];
        // If no log file is specified, setup the default logging
        else {
            try {
                //System.setOut(new PrintStream(logName + ".log"));
                System.setErr(new PrintStream(logName + "-error.log"));
            } catch (IOException e) {
                System.out.println(e);
            }
        }
        setLookAndFeel(14);
        NMTFrame window = new NMTFrame();
        window.pack();
        window.setVisible(true);
    }
}
