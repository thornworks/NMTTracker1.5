package nmt;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 *
 * This is used to display a list of all team Numbers
 * currently stored in the database.  The teams are each
 * displayed as separate buttons.
 *
 *
 */
public class TeamListPanel
    extends JPanel implements ActionListener {
  NMTFrame parent;

  public TeamListPanel(NMTFrame frame) {
    super();
    parent = frame;
    this.setLayout(new GridLayout(10, 10));

    JButton teamButton;
    String number;
    for (int n = 100; n < 120; n++) {
      number = "" + n;
      teamButton = new JButton(number);
      teamButton.setActionCommand(number);
      teamButton.addActionListener(this);
      this.add(teamButton);
    }
  }

  public void actionPerformed(ActionEvent e) {
    System.out.println(e.getActionCommand());
    parent.swapCenterPanel();
  }

}
