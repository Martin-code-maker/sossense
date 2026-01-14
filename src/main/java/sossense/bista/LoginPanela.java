 package sossense.bista;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import sossense.datubasea.SOSsenseApp;
import sossense.kontrolatzailea.LoginKontrolatzailea;
import sossense.modelo.LoginModeloa;

public class LoginPanela implements PropertyChangeListener {

    private JFrame leihoa;
    private JTextField tfUser;
    private JPasswordField pfPass;

    private LoginModeloa model;
    private LoginKontrolatzailea controller;

    private Image fondo;
    private Image avatar;

    public LoginPanela(LoginModeloa m, LoginKontrolatzailea k) {
        this.model = m;
        this.controller = k;
        model.addPropertyChangeListener(this);
        model.setBista(this);

        fondo  = new ImageIcon(getClass().getResource("/sossense/img/fondo.jpg")).getImage();
        avatar = new ImageIcon(getClass().getResource("/sossense/img/avatar.jpg")).getImage();


        leihoa = new JFrame("S.O.S Sense");
        leihoa.setExtendedState(JFrame.MAXIMIZED_BOTH);
        leihoa.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        leihoa.setContentPane(crearFondo());
        leihoa.setVisible(true);
    }

    private JPanel crearFondo() {
        JPanel fondoPanel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            }
        };

        fondoPanel.setLayout(new GridBagLayout());
        fondoPanel.add(crearTarjeta());
        return fondoPanel;
    }

    private JPanel crearTarjeta() {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 550));
        card.setBackground(new Color(255, 255, 255, 245));
        card.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220), 2));
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        // Sombra elegante
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));

        card.add(crearAvatar());
        card.add(Box.createVerticalStrut(40));
        card.add(crearCampoUsuario());
        card.add(Box.createVerticalStrut(20));
        card.add(crearCampoPassword());
        card.add(Box.createVerticalStrut(40));
        card.add(crearBotones());

        return card;
    }

    private JComponent crearAvatar() {
        return new JComponent() {
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int size = 110;
                int x = (getWidth() - size) / 2;

                Shape clip = new Ellipse2D.Double(x, 0, size, size);
                g2.setClip(clip);
                g2.drawImage(avatar, x, 0, size, size, this);
            }

            public Dimension getPreferredSize() {
                return new Dimension(200, 110);
            }
        };
    }

    private JTextField crearCampoUsuario() {
        tfUser = new JTextField();
        tfUser.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        tfUser.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        tfUser.setBorder(BorderFactory.createTitledBorder("Usuario"));
        return tfUser;
    }

    private JPasswordField crearCampoPassword() {
        pfPass = new JPasswordField();
        pfPass.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        pfPass.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        pfPass.setBorder(BorderFactory.createTitledBorder("Contraseña"));
        
        pfPass.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    model.datuakKonprobatu();
                }
            }
        });
        
        return pfPass;
    }

    private JPanel crearBotones() {
        JPanel panel = new JPanel();
        panel.setOpaque(false);

        JButton entrar = new JButton("ENTRAR");
        JButton salir = new JButton("SALIR");

        entrar.addActionListener(controller);
        entrar.setActionCommand("ok");

        salir.addActionListener(controller);
        salir.setActionCommand("irten");

        panel.add(entrar);
        panel.add(salir);
        return panel;
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if ("itxi".equals(evt.getNewValue())) {
            leihoa.dispose();
        } 
        else if ("ok".equals(evt.getNewValue())) {
            JOptionPane.showMessageDialog(leihoa, "Ongi Etorri!", "Sarrera Onartuta", JOptionPane.INFORMATION_MESSAGE);
            SOSsenseApp soSsenseApp = new SOSsenseApp();
            leihoa.dispose();
            soSsenseApp.bistaratuApp();
        } 
        else if ("errorea".equals(evt.getNewValue())) {
            JOptionPane.showMessageDialog(leihoa, "Sarrera desegokia!!!", "Errorea", JOptionPane.ERROR_MESSAGE);
        }
    }



    public String getErabiltzailea() {
        return tfUser.getText();
    }

    public String getPassword() {
        return new String(pfPass.getPassword());
    }

	public void setPassword(String pass) {
		pfPass.setText(pass);
	}
}
