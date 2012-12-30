package nmt;

import java.awt.*;
import javax.swing.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

/**
 *  This class includes the GUI for the panel that allows the input of a team.
 *  TODO - Where did the panel go???
 *  TODO - add the ability to change the font to make the fields more visible.
 *  Maybe do this through Nimbus Look and Feel Properties
 */
public class TeamPanel extends JPanel implements FocusListener {
    JLabel studentLabel,jLabelIndv, jLabelML, jLabelGrade, jTotal;
    JTextField[] studentTextField; // = new JTextField(30);
    JTextField[] gradeTextField; // = new JTextField(5);
    JTextField[] gradeScoreTextField; // = new JTextField(5);
    JTextField[] mathleticsTextField; // = new JTextField(5)
    JTextField[] individualTotalField;
    JTextField teamRoundTextField, totalScoreField;
    Team displayTeam;  //the team we are currently viewing
    JFrame parentFrame;

    UpdateTotalListener scoreListener;
    JTextField highlightedField, lasthighlightedField;
    //public final Color highlight = new Color(255,255,190);
    Color highLight; // = new Color(255,240,90);

    public TeamPanel(JFrame window) {
        parentFrame = window;
        highLight = new Color(255,255,150);
        studentTextField = new JTextField[5];
        gradeTextField = new JTextField[5];
        gradeScoreTextField = new JTextField[5];
        mathleticsTextField = new JTextField[5];
        individualTotalField = new JTextField[5];
        jbInit();
    }

    /**
     * The GUI elements are built and layed out by this method.
     * Legacy code generated from JBuilder
     * @throws Exception
     */
    void jbInit() { //throws Exception {
        studentLabel = new JLabel("Student Name");
        studentLabel.setDebugGraphicsOptions(0);
        this.setLayout(new GridBagLayout());
        //GridBagConstraints(gridx, gridy, gridwidth, gridheight, double weightx, double weighty,
        //                   int anchor, int fill, Insets insets, int ipadx, int ipady)
        jLabelIndv = new JLabel("GL");
        jLabelIndv.setToolTipText("Individual Grade Level Score (0-15)");
        jLabelML = new JLabel("ML");
        jLabelML.setToolTipText("Individual Mathletics Score (0-10)");
        jTotal = new JLabel("Total");
        jTotal.setToolTipText("Individual Total");
        jLabelGrade = new JLabel("Grade");
        jLabelGrade.setToolTipText("Competing grade(9-12)");
        
        this.add(studentLabel, new GridBagConstraints(0, 0, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(jLabelGrade, new GridBagConstraints(4, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 10, 0));
        this.add(jLabelIndv, new GridBagConstraints(5, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(jLabelML, new GridBagConstraints(6, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));
        this.add(jTotal, new GridBagConstraints(7, 0, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(0, 0, 0, 0), 0, 0));

        scoreListener = new UpdateTotalListener(this);

        for (int i = 0; i < 5; i++) {
            studentTextField[i] = new JTextField(30);
            studentTextField[i].setMinimumSize(new Dimension(20, 20));
            gradeTextField[i] = new JTextField(4);
            gradeScoreTextField[i] = new JTextField(4);
            mathleticsTextField[i] = new JTextField(4);
            individualTotalField[i] = new JTextField(4);
            individualTotalField[i].setEditable(false);
            mathleticsTextField[i].addActionListener(scoreListener);
            gradeScoreTextField[i].addActionListener(scoreListener);
            mathleticsTextField[i].addFocusListener(this);
            gradeScoreTextField[i].addFocusListener(this);
            gradeTextField[i].addFocusListener(this);
            this.add(studentTextField[i],
                    new GridBagConstraints(0, i + 1, 4, 1, 1.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE,
                    new Insets(2, 3, 0, 3), 10, 0));
            this.add(gradeTextField[i],
                    new GridBagConstraints(4, i + 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE,
                    new Insets(2, 3, 0, 3), 5, 0));
            this.add(gradeScoreTextField[i],
                    new GridBagConstraints(5, i + 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE,
                    new Insets(2, 3, 0, 3), 0, 0));
            this.add(mathleticsTextField[i],
                    new GridBagConstraints(6, i + 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE,
                    new Insets(2, 3, 0, 3), 5, 0));
            this.add(individualTotalField[i],
                    new GridBagConstraints(7, i + 1, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                    GridBagConstraints.NONE,
                    new Insets(2, 3, 0, 3), 5, 0));


        }
        //now add the team data under the students
        this.add(new JLabel("Team Round:"),
                new GridBagConstraints(0, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER,
                GridBagConstraints.NONE,
                new Insets(2, 3, 0, 3), 5, 0));
        teamRoundTextField = new JTextField(4);
        teamRoundTextField.setToolTipText("Raw score: 0-20");
        teamRoundTextField.addActionListener(scoreListener);

        this.add(teamRoundTextField, new GridBagConstraints(1, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 3, 0, 3), 5, 0));

        JLabel totalTeamScoreLabel = new JLabel("Total Team Score:");
        Font f = totalTeamScoreLabel.getFont();
        f.deriveFont(Font.BOLD);

        totalTeamScoreLabel.setFont(new Font("Dialog", Font.BOLD, 14));

        this.add(totalTeamScoreLabel, new GridBagConstraints(4, 6, 2, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 3, 0, 3), 5, 0));
        totalScoreField = new JTextField(6);
        totalScoreField.setEditable(false);
        totalScoreField.setFont(new Font("Dialog", Font.BOLD, 14));

        this.add(totalScoreField, new GridBagConstraints(6, 6, 1, 1, 0.0, 0.0, GridBagConstraints.CENTER, GridBagConstraints.NONE,
                new Insets(2, 3, 0, 3), 5, 0));

    }
    /**
     *   This method allows us to change the view by picking the font for team display.
     *   For projecting a team, a large font is preferable.
     * @param f - the font to use in the panel.
     */
   // public void setFont(Font f) {

   // }

// -------------Methods to extract data from text fields--------------
    /**
     *  Gets the student names that were entered in the text fields.
     *  As soon as a blank name is found, we assume that the rest of the fields
     *  are blank.  Blank fields are returned as null Strings, not as ""
     */
    public String[] getStudentNames() {
        String[] names = new String[5];
        for (int i = 0; i < 5; i++) {
            names[i] = this.studentTextField[i].getText().trim();
            ;
            if (names[i].length() == 0) {
                break;
            }
        }
        return names;
    }

    /**
     *  Returns the 5 student grades (9-12)
     *  This method is used to retrieve data from the fields that the user typed in.
     */
    public int[] getGradeLevels() throws NumberFormatException, DataRangeException {
        int[] grades = new int[5];
        String temp;
        for (int i = 0; i < 5; i++) {
            temp = this.gradeTextField[i].getText().trim();
            if (temp.equals("")) {
                continue;
            }
            if (Integer.parseInt(temp) >= 9 && Integer.parseInt(temp) <= 12) {
                grades[i] = Integer.parseInt(temp);
            } else {
                JOptionPane.showMessageDialog(this, "Grade must be between 9 and 12.");
                throw new DataRangeException("Grade level out of range");
            }
        }
        return grades;
    }

    public int[] getGradeScores() throws NumberFormatException, DataRangeException {
        //teamPanel.gradeScoreTextField[0].getText();
        int[] scores = new int[5];
        String temp;
        for (int i = 0; i < 5; i++) {
            temp = this.gradeScoreTextField[i].getText().trim();
            if (temp.equals("")) {
                scores[i] = Student.NOSCORE;
            } else if (Integer.parseInt(temp) >= 0 && Integer.parseInt(temp) <= 15) {
                scores[i] = Integer.parseInt(temp);
            } else {
                //JOptionPane.showMessageDialog(this,"Grade Level Score must be between 0 and 15.");
                throw new DataRangeException("Grade level scores out of range.  Must be between 0 and 15.");
            }
        }
        return scores;
    }

    public int[] getMathleticsScores() throws NumberFormatException, DataRangeException {
        int[] scores = new int[5];
        String temp;
        for (int i = 0; i < 5; i++) {
            temp = this.mathleticsTextField[i].getText().trim();
            if (temp.equals("")) {
                scores[i] = Student.NOSCORE;
            } else if (Integer.parseInt(temp) >= 0 && Integer.parseInt(temp) <= 10) {
                scores[i] = Integer.parseInt(temp);
            } else {
                throw new DataRangeException("Mathletics scores out of range.  Score must be between 0 and 10.");
            }
        }
        return scores;
    }

    public int getTeamScore() throws NumberFormatException, DataRangeException {
        String temp = this.teamRoundTextField.getText().trim();
        if (temp.equals("")) {
            return 0;
        } else if (Integer.parseInt(temp) < 0 || Integer.parseInt(temp) > 20) {
            //JOptionPane.showMessageDialog(this, "Team Score entered too high. Score must be between 0 and 20.");
            throw new DataRangeException("Team score out of range.  Must be between 0 and 20.");
        } else {
            return Integer.parseInt(temp);
        }
    }

    /**
     *
     *  Deletes all of the text stored in the text fields of this panel.
     *  This method should be called whenever a new team is created.
     */
    public void clearAllFields() {
        for (int i = 0; i < studentTextField.length; i++) {
            studentTextField[i].setText(""); // = new JTextField(30);
            gradeTextField[i].setText(""); // = new JTextField(5);
            gradeScoreTextField[i].setText(""); // = new JTextField(5);
            mathleticsTextField[i].setText(""); // = new JTextField(5);
        }
        teamRoundTextField.setText("");
        totalScoreField.setText("");
        //some Text Field.requestFocus()
    }

    /**
     *  Updates the TeamPanel display to represent the input team.
     *
     * @param t Team - the team to be displayed.
     */
    public void setTeam(Team t) {
        this.displayTeam = t;
        refresh();
    }

    /**
     * Refreshes the current display.  If the user makes a change to a gui element,
     * this gets called.
     *
     */
    public void refresh() {
        Student s;
        boolean nonStudent;
        for (int i = 0; i < 5; i++) {
            s = displayTeam.getStudent(i);
            nonStudent = ((s == null) || s.getName() == null || s.getName().length() == 0);
            if (nonStudent) {
                studentTextField[i].setText("");
                gradeTextField[i].setText("");
                gradeScoreTextField[i].setText("");
                mathleticsTextField[i].setText("");
                individualTotalField[i].setText("");
            } else {
                studentTextField[i].setText(s.getName());
                gradeTextField[i].setText("" + s.getGrade());
                if (s.getScoreGL() == Student.NOSCORE) {
                    gradeScoreTextField[i].setText("");
                } else {
                    gradeScoreTextField[i].setText("" + s.getScoreGL());
                    individualTotalField[i].setText("" + s.getScore());
                }
                if (s.getScoreML() == Student.NOSCORE) {
                    mathleticsTextField[i].setText("");
                } else {
                    mathleticsTextField[i].setText("" + s.getScoreML());
                }

            }
        }
        if (displayTeam.getScore() == Student.NOSCORE) {
            teamRoundTextField.setText("");
        } else {
            teamRoundTextField.setText("" + displayTeam.getScore());
        }

        totalScoreField.setText("" + displayTeam.getTotalScore());
    }
    @Override
    public void focusGained(FocusEvent e) {
        JTextField nameField = null;
        if(highlightedField!=null)
            lasthighlightedField = highlightedField;
        for (int i = 0; i < 5; i++) {
            if (e.getSource() == this.gradeScoreTextField[i] ||
                e.getSource() == this.mathleticsTextField[i] ||
                e.getSource() == this.gradeTextField[i]) {
                nameField = this.studentTextField[i];
                break;
            }
        }
        if(nameField!=null) {
            nameField.setBackground(highLight);
            highlightedField = nameField;
        }
    }

    @Override
    public void focusLost(FocusEvent e) {
        JTextField nameField = null;
        int studentIndex = -1;
        for (int i = 0; i < 5; i++) {
            if (e.getSource() == this.gradeScoreTextField[i] || 
                e.getSource() == this.mathleticsTextField[i] ||
                e.getSource() == this.gradeTextField[i]) {
                    nameField = this.studentTextField[i];
                    studentIndex = i;
                    break;
            }
        }
        if(nameField!=null) {
            nameField.setBackground(Color.white);
            highlightedField = nameField;
            //update the field we left.
            java.awt.event.ActionEvent event = new java.awt.event.ActionEvent(e.getSource(),99,"");
            scoreListener.actionPerformed(event);
        }
    }
}
