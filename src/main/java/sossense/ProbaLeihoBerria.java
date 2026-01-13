package sossense;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class ProbaLeihoBerria implements ActionListener {
	JFrame leihoa;
	JLabel lbKontatzailea;
	int kont;
	
	public ProbaLeihoBerria () {
		leihoa = new JFrame ("Click kontatzailea");
		leihoa.setLocation(750,200);
		leihoa.setExtendedState(JFrame.MAXIMIZED_BOTH);
		leihoa.setSize(400,600);
		leihoa.setContentPane (sortuLeihokoPanela());
		leihoa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		kont = 0;
	}

    public void bistaratuKalku(){
        leihoa.setVisible(true);
    }
	
	private Container sortuLeihokoPanela() {
		JPanel panel = new JPanel(new BorderLayout(0,20));
		
		panel.setBorder(BorderFactory.createEmptyBorder(20,10,20,10));
		lbKontatzailea = new JLabel (String.valueOf(kont));
		lbKontatzailea.setFont(new Font("Arial",Font.BOLD, 180));
		lbKontatzailea.setHorizontalAlignment(JLabel.CENTER);
		lbKontatzailea.setForeground(Color.red);
		lbKontatzailea.setBackground(Color.GREEN);
		lbKontatzailea.setOpaque (true);
		panel.add(lbKontatzailea);
		panel.add(sortuBotoienPanela(),BorderLayout.EAST);
		return panel;
	}

	private Component sortuBotoienPanela() {
		JPanel panel = new JPanel(new FlowLayout (FlowLayout.CENTER,20,0));
		JButton botoiaGehi = new JButton ("Gehitu");

		botoiaGehi.addActionListener(this);
		botoiaGehi.setActionCommand("gehi");
		JButton botoiaKen = new JButton ("Kendu");
		botoiaKen.addActionListener(this);
		botoiaKen.setActionCommand("ken");

		panel.add(botoiaGehi);
		panel.add(botoiaKen);
		return panel;
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		String command = e.getActionCommand();
		if (command.equals("gehi")) kont++;
		else  kont--;

		lbKontatzailea.setText(String.valueOf(kont));
	}
}
