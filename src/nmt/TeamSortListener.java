package nmt;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import java.util.*;

public class TeamSortListener implements ActionListener {
	
	TeamButtonsPanel teamButtons = null;
	
	public TeamSortListener(TeamButtonsPanel buttons) {
		teamButtons = buttons;
	}
	
	public void actionPerformed(ActionEvent e) {
		
		////////////////
		// Sort Codes://
		///////////////
		
		// 0 - All Teams
		// 1 - GL Completed
		// 2 - ML Completed
		// 3 - Completed Teams
				
		// Code Here
		
		teamButtons.clearAllTeamButtons();
	}
}
