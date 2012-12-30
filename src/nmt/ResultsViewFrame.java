package nmt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.text.html.*;
import java.io.*;
import java.net.URL;

/**
 *
 *  This class is used to create a window that serves as a simple
 *  HTML viewer.  This allows the NMTFrame to easily display one of the
 *  ouput files without launching an additional program.
 *  ResultsViewFrame should be created after a results file is created and
 *  then the results file can be displayed with the following:
 *  ResultsViewFrame = new ResultsViewFrame("TopGrade10.html");
 *
 *
 */
public class ResultsViewFrame extends JFrame {
  JEditorPane	htmlPane;


/**
 *  Loads the HTML file specified by fileName into an HTML viewer, JEditorPane.
 *  The window is made visible, resized, and un-editable all from within
 *  the constructor.  On closing, the window should be disposed.
 *
 */
  public ResultsViewFrame(String fileName) {
    super("NMT Results Viewer");
    loadFile(fileName);
    JScrollPane scrollPane = new JScrollPane(htmlPane);
    this.getContentPane().add(scrollPane, BorderLayout.CENTER);
    this.setSize(600,400);
    this.setLocation(50,100);
    this.setVisible(true);
    this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
  }

/**
 *
 * Initialize the Editor Pane to display the html file specified
 * by the constructor.
 *
 */
  private void loadFile(String f) {
    htmlPane = new JEditorPane();
    htmlPane.setContentType("text/html");
    try {
      InputStream in = new FileInputStream(f);
      HTMLDocument decription = new HTMLDocument();
      htmlPane.read(in, decription);
    }
    catch(Exception i) {
      htmlPane.setText("Could not load file.<br>" + i.toString() + "<br>");
      i.printStackTrace();
    }
    htmlPane.setEditable(false);
  }

   public static void main(String[] a) {
    ResultsViewFrame f = new ResultsViewFrame("TopGrade9.html");
   }
}
