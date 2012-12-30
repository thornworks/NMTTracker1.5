package nmt;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

public class TeamButtonFrame extends JFrame implements ActionListener {

    NMTFrame parent;
    TeamButtonsPanel teamButtons;
    Dimension frameSize;

    public TeamButtonFrame(NMTFrame mainProgram) {
        parent = mainProgram;

        JPanel bottom = buttonKeyPanel();

        teamButtons = new TeamButtonsPanel(mainProgram);

        teamButtons.btnFrame = this;

        this.getContentPane().add(bottom, BorderLayout.SOUTH);
        this.getContentPane().add(teamButtons, BorderLayout.CENTER);
        this.pack();
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        int x = (int) (mainProgram.getLocation().getX());
        int y = (int) (mainProgram.getLocation().getY());

        this.setLocation(x + 10, y + mainProgram.getHeight() - 25);
        this.setTitle("NMT: " + teamButtons.numTeamButtons + ((teamButtons.numTeamButtons == 1) ? " Team" : " Teams"));

        this.setVisible(true);
        frameSize = this.getSize();
    }

    /**
     *   Builds a simple panel with a few buttons on it to indicate
     *   what the various colors represent.
     *
     *   Changelog:
     *	2/10/2009 - Added  new "All Teams" button
     *
     */
    JPanel buttonKeyPanel() {

        ////////////////
        // Sort Codes //
        ///////////////

        // -1 - No Scores
        // 0 - All Teams
        // 1 - GL Completed
        // 2 - ML Completed
        // 3 - Completed Teams

        JButton noScores = new JButton("NO Scores Entered");
        JButton scores1 = new JButton("GL entered");
        JButton scores2 = new JButton("ML entered");
        JButton completed = new JButton("Team Completed");
        JButton allTeams = new JButton("All Teams");

        noScores.setForeground(TeamButtonsPanel.emptyColor);
        noScores.addActionListener(this);
        noScores.setActionCommand("-1");

        allTeams.addActionListener(this);
        allTeams.setActionCommand("0");

        scores1.setForeground(TeamButtonsPanel.finishedColorGL);
        scores1.addActionListener(this);
        scores1.setActionCommand("1");

        scores2.setForeground(TeamButtonsPanel.finishedColorML);
        scores2.addActionListener(this);
        scores2.setActionCommand("2");

        completed.setFont(TeamButtonsPanel.doneFont);
        completed.addActionListener(this);
        completed.setActionCommand("3");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(allTeams);
        buttonPanel.add(noScores);
        buttonPanel.add(scores1);
        buttonPanel.add(scores2);
        buttonPanel.add(completed);

        return buttonPanel;
    }

    /**
     *
     * Handles the team panel button presses
     * and updates the sorting accordingly
     *
     * Changelog:
     *
     *   2/10/2009: Basic sorting code implemented
     *
     */
    public void actionPerformed(ActionEvent e) {

        ////////////////
        // Sort Codes ://
        ///////////////

        // -1 - No Scores
        // 0 - All Teams
        // 1 - GL Completed
        // 2 - ML Completed
        // 3 - Completed Teams

        int sortOption = Integer.parseInt(e.getActionCommand());

        teamButtons.doResort(sortOption);

        if (teamButtons.teamButtons.size() == 0) {
            this.setSize(frameSize);
        } else {
            frameSize = this.getSize();
        }

    }
}
