package nmt;
import java.awt.event.*;

/*
 *	Action Listener class for the Previous and Next team buttons
 *  in the main Frame.
 *
 */
public class TeamChangeActionListener implements ActionListener {
	
	NMTFrame s = null;
	NMTClient c = null;
	
	/*
	 *	Server constructor
	 */
	public TeamChangeActionListener(NMTFrame f) {
		s = f;
	}
	
	/*
	 * Client constructor
	 */
	 public TeamChangeActionListener(NMTClient f) {
	 	c = f;
	 }
	 
	 public void actionPerformed(ActionEvent e) {
	 	if(e.getActionCommand().equals("P")) {
	 		if(s != null) {
	 			s.currentTeamIndex = Math.max(0, s.currentTeamIndex-1);
	 			s.displayTeam(s.teamsList.get(s.currentTeamIndex));
	 		}
	 		else {
	 			c.currentTeamIndex = Math.max(0, c.currentTeamIndex-1);
	 			c.displayTeam(c.teamsList.get(c.currentTeamIndex));
	 		}
	 	}
	 	else if(e.getActionCommand().equals("N")) {
	 		if(s != null) {
	 			s.currentTeamIndex = Math.min(s.teamsList.size()-1, s.currentTeamIndex+1);
	 			s.displayTeam(s.teamsList.get(s.currentTeamIndex));
	 		}
	 		else {
	 			c.currentTeamIndex = Math.min(c.teamsList.size()-1, c.currentTeamIndex+1);
	 			c.displayTeam(c.teamsList.get(c.currentTeamIndex));
	 		}
	 	}
	 }
}