package nmt;

import java.awt.event.*;
import javax.swing.*;

/**
 *  This listener is used to update the total field in the team Panel.
 *  Whenever text is changed in a field that is attached to this listener,
 *  the total value is modified.
 *  The action is envoked whenever enter is pressed on the
 *  corresponding textField.
 *
 */
public class ClientFieldListener
    implements ActionListener {
  NMTClient mainFrame;

  public ClientFieldListener(NMTClient parent) {
    super();
    mainFrame = parent;
  }

  /**
   *
   *  The action is envoked whenever enter is pressed on the
   *  corresponding Team number text field.
   *  The total displayed in teamPanel is updated.
   */
  public void actionPerformed(ActionEvent e) {
    JTextField f;
    try {
      String team = mainFrame.teamNumberField.getText().trim();
      f = (JTextField) e.getSource();
      mainFrame.displayTeam(Integer.parseInt(f.getText()));
    }
    catch (NumberFormatException nfe) {
      //do nothing since we did not actually attempt to create a team.
      // or display an error JOptionPane.
      System.out.println(nfe);
    }
    catch (ClassCastException utoh) {
      System.out.println(utoh);
      System.out.println("The event for TeamFieldListener was triggered by:");
      System.out.println(e.getSource());
    }
  }
}
