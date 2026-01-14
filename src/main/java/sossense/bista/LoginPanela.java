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
        JPanel card = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);

                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int arc = 28;
                int blur = 10;
                int w = getWidth();
                int h = getHeight();

                // Difuminado suave (tipo sombra interna) alrededor del borde
                for (int i = 0; i < blur; i++) {
                    float t = i / (float) blur;
                    float alpha = 0.16f * (1f - t);
                    g2.setColor(new Color(0, 0, 0, Math.round(alpha * 255)));
                    g2.drawRoundRect(i, i, w - 1 - (2 * i), h - 1 - (2 * i), arc, arc);
                }

                // Fondo principal de la tarjeta
                int inset = blur / 2;
                g2.setColor(new Color(255, 255, 255, 245));
                g2.fillRoundRect(inset, inset, w - (2 * inset), h - (2 * inset), arc, arc);

                g2.dispose();
            }
        };

        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setPreferredSize(new Dimension(420, 550));
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(40, 40, 40, 40));

        card.add(crearAvatar());
        card.add(Box.createVerticalStrut(14));
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

                int size = 130;
                int x = (getWidth() - size) / 2;

                int y = 0;

                Shape clip = new Ellipse2D.Double(x, y, size, size);
                g2.setClip(clip);
                g2.drawImage(avatar, x, y, size, size, this);

                g2.setClip(null);
                g2.setStroke(new BasicStroke(3f));
                g2.setColor(new Color(190, 196, 204));
                g2.drawOval(x, y, size, size);
            }

            public Dimension getPreferredSize() {
                return new Dimension(200, 130);
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
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 28, 0));

        JButton entrar = new JButton("ENTRAR");
        JButton salir = new JButton("SALIR");

        entrar.addActionListener(controller);
        entrar.setActionCommand("ok");

        estilizarBoton(entrar, new Color(0xE1, 0x9D, 0x8E), Color.WHITE, Color.BLACK);

        salir.addActionListener(controller);
        salir.setActionCommand("irten");

        estilizarBoton(salir, new Color(0xE2, 0x80, 0x76), Color.WHITE, Color.BLACK);

        panel.add(entrar);
        panel.add(salir);
        leihoa.getRootPane().setDefaultButton(entrar);
        return panel;
    }

    private void estilizarBoton(JButton boton, Color fondo, Color texto, Color borde) {
        boton.setFont(new Font("Segoe UI", Font.BOLD, 16));
        boton.setForeground(texto);
        boton.setBackground(fondo);
        boton.setFocusPainted(false);
        boton.setBorderPainted(false);
        boton.setContentAreaFilled(true);
        boton.setOpaque(true);
        boton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        boton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borde, 1),
                new EmptyBorder(12, 28, 12, 28)
        ));
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
