package nmt;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;

public class HistogramFrame extends JDialog implements ActionListener {
	
	JPanel controlsPanel;
	JLabel dataLabel, typeLabel;
	JComboBox dataDropdown, typeDropdown;
	BarChartComponent histogram;
	NMTFrame parent;
	
	public HistogramFrame(NMTFrame parent) {
		this.parent = parent;
		
		this.setTitle("NMT Histogram");
		this.setLayout(new BorderLayout());
		
		String[] dataOptions = { "9th grade", "10th grade", "11th grade", "12th grade", "Lower division", "Upper division" };
		String[] typeOptions = { "Grade Level Scores", "Mathletic Scores" };
		
		controlsPanel = new JPanel(new GridLayout(2,2));
		dataLabel = new JLabel("Graph Data");
		typeLabel = new JLabel("Graph Type");
		
		dataDropdown = new JComboBox(dataOptions);
		dataDropdown.addActionListener(this);
		
		typeDropdown = new JComboBox(typeOptions);
		typeDropdown.addActionListener(this);
		
		controlsPanel.add(dataLabel);
		controlsPanel.add(typeLabel);
		
		controlsPanel.add(dataDropdown);
		controlsPanel.add(typeDropdown);

		this.add(controlsPanel, BorderLayout.SOUTH);
		
		this.setSize(400,400);
		this.setVisible(true);
	}
	
	public void actionPerformed(ActionEvent e) {
		if(e.getSource() == dataDropdown) {
			int[] data = null;
			if(dataDropdown.getSelectedIndex() >= 4) { //LD or UD
				data = parent.studentDB.getTeamScoreTally(dataDropdown.getSelectedIndex() - 4);
				typeDropdown.setEnabled(false);
			}
			else {
				typeDropdown.setEnabled(true);
				if(typeDropdown.getSelectedIndex() == 0) //GL scores
					data = parent.studentDB.getGradeLevelTally(dataDropdown.getSelectedIndex()+9);
				else //ML scores
						data = parent.studentDB.getMathleticsScoreTally(dataDropdown.getSelectedIndex()+9);	
			}
			if(histogram == null && data != null) {
				histogram = new BarChartComponent(data, Color.BLUE);
				this.add(histogram, BorderLayout.CENTER);
				histogram.validate();
				this.validate();
			}
			else if(data != null)
				histogram.updateData(data);
			this.repaint();
			
		}
		else if(e.getSource() == typeDropdown) {
			int[] data = null;
			if(dataDropdown.getSelectedIndex() <= 3) {
				if(typeDropdown.getSelectedIndex() == 0) //GL scores
					data = parent.studentDB.getGradeLevelTally(dataDropdown.getSelectedIndex()+9);
				else //ML scores
						data = parent.studentDB.getMathleticsScoreTally(dataDropdown.getSelectedIndex()+9);	
			}
			
			if(histogram == null && data != null) {
				histogram = new BarChartComponent(data, Color.BLUE);
				this.add(histogram, BorderLayout.CENTER);
				histogram.validate();
				this.validate();
			}
			else if(data != null)
				histogram.updateData(data);
			this.repaint();
		}
	}
	
}