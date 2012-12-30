package nmt;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class ClientButtonsFrame extends JFrame implements ActionListener {

    NMTClient parent;
    ClientButtonsPanel teamButtons;
    public int currentSorting = 0;
    Dimension frameSize;

    public ClientButtonsFrame(NMTClient mainProgram) {
        super();
        parent = mainProgram;

        JPanel bottom = buttonKeyPanel();

        teamButtons = new ClientButtonsPanel(mainProgram);

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

        noScores.setForeground(ClientButtonsPanel.emptyColor);
        noScores.addActionListener(this);
        noScores.setActionCommand("-1");

        allTeams.addActionListener(this);
        allTeams.setActionCommand("0");

        scores1.setForeground(ClientButtonsPanel.finishedColorGL);
        scores1.addActionListener(this);
        scores1.setActionCommand("1");

        scores2.setForeground(ClientButtonsPanel.finishedColorML);
        scores2.addActionListener(this);
        scores2.setActionCommand("2");

        completed.setFont(ClientButtonsPanel.doneFont);
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

        if (this.getSize().getWidth() < frameSize.getWidth() || this.getSize().getHeight() < frameSize.getHeight()) {
            this.setSize(frameSize);
        } else {
            frameSize = this.getSize();
        }


    }
}
