package sossense.datubasea;

import javax.swing.*;

import sossense.kontrolatzailea.SOSsenseKontrolatzailea;
import sossense.modelo.SOSsenseModeloa;
import sossense.mqtt.Mqtt;
import sossense.mqtt.MqttIntegration;
import sossense.bista.Navigator;
import sossense.bista.InstalacionesPanelBuilder;
import sossense.bista.ContactoPanelBuilder;
import sossense.bista.EstadisticasPanelBuilder;

import java.awt.*;
import java.util.List;
import java.util.ArrayList;

public class SOSsenseApp {

    private final SOSsenseModeloa model;
    private final SOSsenseKontrolatzailea controller;
    JFrame frame = new JFrame("S.O.S.sense");
    private JPanel menuPanel;
    private JPanel centerPanel;
    private boolean menuExpanded = true;
    private final int menuExpandedWidth = 180;
    private final int menuCollapsedWidth = 18;
    private final List<JButton> menuButtons = new ArrayList<>();
    private final AppContext appContext = new AppContext();
    private final PlanoRepository planoRepo = new PlanoRepository("datos/planos.txt");
    private final Navigator navigator = panel -> cambiarPanelCentral(panel);

    public SOSsenseApp() {
        this.model = new SOSsenseModeloa();
        this.controller = new SOSsenseKontrolatzailea(model);
        crearInterfaz();

        // --- Integración MQTT separada de la UI ---
        try {
            Mqtt mqtt = new Mqtt();
                MqttIntegration.attachPlanUpdater(mqtt,
                    () -> appContext.getPanelPlanoActivo(),
                    () -> appContext.getInstalacionActiva(),
                    () -> appContext.getPlanoActivo(),
                    appContext);
        } catch (Exception ex) {
            System.err.println("Error conectando MQTT: " + ex.getMessage());
        }
        System.out.println("[APP] Modo offline - MQTT desactivado");
    }

    public void bistaratuApp() {
        frame.setVisible(true);
    }

    private void crearInterfaz() {

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        frame.setSize(900, 700);
        frame.setLayout(new BorderLayout());

        // ==================== PANEL SUPERIOR ====================
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setBackground(new Color(0xF6, 0xB2, 0xB2));
        topPanel.setPreferredSize(new Dimension(0, 80));

        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/sossense/img/hamburguesa_menua.png"));
        Image scaled = rawIcon.getImage().getScaledInstance(32, 32, Image.SCALE_SMOOTH);
        JButton menuToggle = new JButton(new ImageIcon(scaled));
        menuToggle.setBorderPainted(false);
        menuToggle.setFocusPainted(false);
        menuToggle.setContentAreaFilled(false);
        menuToggle.setPreferredSize(new Dimension(48, 48));
        menuToggle.addActionListener(e -> toggleMenu());

        JPanel leftTopPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 15));
        leftTopPanel.setBackground(new Color(0xF6, 0xB2, 0xB2));
        leftTopPanel.add(menuToggle);
        topPanel.add(leftTopPanel, BorderLayout.WEST);

        JLabel title = new JLabel("S.O.S.sense - Monitorización de Instalaciones", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 36));
        title.setForeground(Color.BLACK);
        topPanel.add(title, BorderLayout.CENTER);

        // ==================== PANEL DERECHO CON BOTONES ====================
        JPanel rightTopPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 20, 10));
        rightTopPanel.setBackground(new Color(0xF6, 0xB2, 0xB2));

        // Botón de volver al inicio de sesión
        ImageIcon logoutIcon = new ImageIcon(getClass().getResource("/sossense/img/saioa_itxi.jpg"));
        Image logoutScaled = logoutIcon.getImage().getScaledInstance(42, 42, Image.SCALE_SMOOTH);
        JButton logoutButton = new JButton(new ImageIcon(logoutScaled));
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.setPreferredSize(new Dimension(60, 60));
        logoutButton.setToolTipText("Volver al inicio de sesión");
        logoutButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Saioa itxi nahi duzu?",
                    "Saioa Itxi",
                    JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                frame.dispose();
                sossense.modelo.LoginModeloa m = new sossense.modelo.LoginModeloa();
                sossense.kontrolatzailea.LoginKontrolatzailea k = new sossense.kontrolatzailea.LoginKontrolatzailea(m);
                new sossense.bista.LoginPanela(m, k);
            }
        });

        // Botón de perfil (avatar)
        ImageIcon avatarIcon = new ImageIcon(getClass().getResource("/sossense/img/user.jpg"));
        Image avatarScaled = avatarIcon.getImage().getScaledInstance(55, 55, Image.SCALE_SMOOTH);
        JButton profileButton = new JButton(new ImageIcon(avatarScaled)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);

                g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, getWidth() - 1, getHeight() - 1));

                super.paintComponent(g2);

                g2.setClip(null);
                g2.setColor(new Color(0xD3, 0x85, 0x7E));
                g2.setStroke(new BasicStroke(2));
                g2.drawOval(0, 0, getWidth() - 1, getHeight() - 1);

                g2.dispose();
            }
        };
        profileButton.setBorderPainted(false);
        profileButton.setFocusPainted(false);
        profileButton.setContentAreaFilled(false);
        profileButton.setPreferredSize(new Dimension(60, 60));
        profileButton.setToolTipText("Perfil de usuario");
        profileButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(frame,
                    "Erabiltzaile Profila\n\n" +
                            "Izena: Admin\n" +
                            "Rola: Administratzailea\n" +
                            "Email: admin@sossense.eus",
                    "Profila",
                    JOptionPane.INFORMATION_MESSAGE);
        });

        rightTopPanel.add(profileButton);
        rightTopPanel.add(logoutButton);

        topPanel.add(rightTopPanel, BorderLayout.EAST);

        // ==================== PANEL DEL MENÚ LATERAL ====================
        JPanel menuPanel = crearMenuPanel(frame);

        // ==================== PANEL PRINCIPAL ====================
        centerPanel = new InstalacionesPanelBuilder(controller, navigator, planoRepo, appContext).build();

        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(menuPanel, BorderLayout.WEST);
        frame.add(centerPanel, BorderLayout.CENTER);

        frame.setLocationRelativeTo(null);
    }

    private JPanel crearMenuPanel(JFrame frame) {
        JPanel menuPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color base = new Color(0xD3, 0x85, 0x7E);
                int arc = 26;
                g2.setColor(base);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), arc, arc);
                g2.fillRect(0, 0, arc / 2, getHeight());
                g2.dispose();
            }
        };
        this.menuPanel = menuPanel;
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setPreferredSize(new Dimension(menuExpandedWidth, 0));
        menuPanel.setBorder(BorderFactory.createEmptyBorder(20, 12, 20, 12));

        JLabel menuTitle = new JLabel("MENUA");
        menuTitle.setFont(new Font("Arial", Font.BOLD, 20));
        menuTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        menuPanel.add(Box.createVerticalStrut(10));
        menuPanel.add(menuTitle);
        menuPanel.add(Box.createVerticalStrut(24));

        String[] itemsMenu = { "INSTALACION GUZTIAK", "GEHITU BERRIA", "ESTATISTIKAK", "KONTAKTUA" };
        for (String item : itemsMenu) {
            JButton menuButton = new JButton(item) {
                private boolean isSelected = false;

                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    Color bgColor = new Color(0xF6, 0xB2, 0xB2);
                    if (isSelected) {
                        bgColor = new Color(0xD6, 0x92, 0x92);
                        g2.setColor(new Color(0, 0, 0, 80));
                        g2.fillRoundRect(2, 2, getWidth() - 4, getHeight() - 4, 10, 10);
                    }

                    g2.setColor(bgColor);
                    g2.fillRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth() - 2, getHeight() - 2, 10, 10);
                    g2.dispose();

                    super.paintComponent(g);
                }

                public void setSelected(boolean selected) {
                    isSelected = selected;
                    repaint();
                }
            };
            menuButton.setFont(new Font("Arial", Font.PLAIN, 14));
            menuButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            menuButton.setMaximumSize(new Dimension(160, 50));
            menuButton.setContentAreaFilled(false);
            menuButton.setForeground(Color.BLACK);
            menuButton.setFocusPainted(false);

            switch (item) {
                case "INSTALACION GUZTIAK":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        cambiarPanelCentral(
                                new InstalacionesPanelBuilder(controller, navigator, planoRepo, appContext).build());
                    });
                    break;
                case "GEHITU BERRIA":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        cambiarPanelCentral(new sossense.bista.AgregarInstalacionPanelBuilder(controller, navigator,
                                planoRepo, appContext).build());
                    });
                    break;
                case "ESTATISTIKAK":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        cambiarPanelCentral(new EstadisticasPanelBuilder(controller).build());
                    });
                    break;
                case "KONTAKTUA":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        cambiarPanelCentral(new ContactoPanelBuilder().build());
                    });
                    break;
            }

            menuButtons.add(menuButton);
            menuPanel.add(menuButton);
            menuPanel.add(Box.createVerticalStrut(10));
        }

        return menuPanel;
    }

    private void toggleMenu() {
        menuExpanded = !menuExpanded;
        int width = menuExpanded ? menuExpandedWidth : menuCollapsedWidth;
        menuPanel.setPreferredSize(new Dimension(width, 0));

        for (Component component : menuPanel.getComponents()) {
            component.setVisible(menuExpanded);
        }

        menuPanel.revalidate();
        frame.revalidate();
        frame.repaint();
    }

    private void selectButton(JButton button) {
        for (JButton btn : menuButtons) {
            try {
                btn.getClass().getMethod("setSelected", boolean.class).invoke(btn, false);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            button.getClass().getMethod("setSelected", boolean.class).invoke(button, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void cambiarPanelCentral(JPanel nuevoPanel) {
        frame.getContentPane().remove(centerPanel);
        centerPanel = nuevoPanel;
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
}
