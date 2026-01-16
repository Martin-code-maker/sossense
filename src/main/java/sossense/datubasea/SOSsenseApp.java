package sossense.datubasea;

import javax.swing.*;

import sossense.kontrolatzailea.SOSsenseKontrolatzailea;
import sossense.modelo.SOSsenseModeloa;
import sossense.mqtt.Mqtt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
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

    public SOSsenseApp() {
        this.model = new SOSsenseModeloa();
        this.controller = new SOSsenseKontrolatzailea(model);
        crearInterfaz();
        // MQTT erabili gabe funtzionatzeko, zati hau komentatu
        /*
         * try {
         * Mqtt mqtt = new Mqtt();
         * } catch (Exception ex) {
         * ex.printStackTrace();
         * }
         */
    }

    public void bistaratuApp() {
        frame.setVisible(true);
    }

    private void crearInterfaz() {
        //
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

        // Título centrado
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
        centerPanel = crearPanelInstalaciones();

        // ==================== AÑADIR TODO AL FRAME ====================
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
                        cambiarPanelCentral(crearPanelInstalaciones());
                    });
                    break;
                case "GEHITU BERRIA":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        cambiarPanelCentral(crearPanelAgregarInstalacion());
                    });
                    break;
                case "ESTATISTIKAK":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        cambiarPanelCentral(crearPanelEstadisticas());
                    });
                    break;
                case "KONTAKTUA":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        cambiarPanelCentral(crearPanelContacto());
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

    private JPanel crearPanelInstalaciones() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblBuscar = new JLabel("Bilatu:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 14));

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton buscarBtn = new JButton("BILATU");
        buscarBtn.setFont(new Font("Arial", Font.BOLD, 14));
        buscarBtn.setPreferredSize(new Dimension(100, 30));

        searchPanel.add(lblBuscar);
        searchPanel.add(searchField);
        searchPanel.add(buscarBtn);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel instalacionesPanel = new JPanel();
        instalacionesPanel.setLayout(new BoxLayout(instalacionesPanel, BoxLayout.Y_AXIS));
        instalacionesPanel.setAlignmentY(Component.TOP_ALIGNMENT);

        // Variables para la paginación
        int[] paginaActual = {0}; // usar array para poder modificarlo en el listener
        int itemsPorPagina = 5;
        List<Instalazioa> todasInstalaciones = controller.lortuInstalazioak();

        // Panel de navegación (abajo)
        JPanel navegacionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navegacionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton anteriorBtn = new JButton("◀ AURREKO");
        anteriorBtn.setFont(new Font("Arial", Font.BOLD, 12));
        anteriorBtn.setPreferredSize(new Dimension(120, 35));

        JLabel paginaLabel = new JLabel();
        paginaLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton siguienteBtn = new JButton("HURRENGOA ▶");
        siguienteBtn.setFont(new Font("Arial", Font.BOLD, 12));
        siguienteBtn.setPreferredSize(new Dimension(140, 35));

        navegacionPanel.add(anteriorBtn);
        navegacionPanel.add(paginaLabel);
        navegacionPanel.add(siguienteBtn);

        // Crear un contenedor para poder reemplazar el scroll
        JPanel contenedorPrincipal = new JPanel(new BorderLayout());

        JPanel contenedorPanel = new JPanel();
        contenedorPanel.setLayout(new BoxLayout(contenedorPanel, BoxLayout.Y_AXIS));
        contenedorPanel.add(instalacionesPanel);
        contenedorPanel.add(Box.createVerticalGlue());

        JScrollPane scrollPane = new JScrollPane(contenedorPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);

        contenedorPrincipal.add(scrollPane, BorderLayout.CENTER);
        contenedorPrincipal.add(navegacionPanel, BorderLayout.SOUTH);

        mainPanel.add(contenedorPrincipal, BorderLayout.CENTER);

        // Función para actualizar la página
        Runnable actualizarPagina = () -> {
            int totalPages = (int) Math.ceil((double) todasInstalaciones.size() / itemsPorPagina);

            // Validar página actual
            if (paginaActual[0] < 0) paginaActual[0] = 0;
            if (paginaActual[0] >= totalPages && totalPages > 0) paginaActual[0] = totalPages - 1;

            // Obtener instalaciones de la página actual
            int inicio = paginaActual[0] * itemsPorPagina;
            int fin = Math.min(inicio + itemsPorPagina, todasInstalaciones.size());
            List<Instalazioa> instalaziakPagina = todasInstalaciones.subList(inicio, fin);

            // Mostrar instalaciones
            bistaratuInstalazioak(instalacionesPanel, instalaziakPagina);

            // Actualizar etiqueta de página
            paginaLabel.setText("Orria " + (paginaActual[0] + 1) + " / " + Math.max(1, totalPages));

            // Habilitar/deshabilitar botones
            anteriorBtn.setEnabled(paginaActual[0] > 0);
            siguienteBtn.setEnabled(paginaActual[0] < totalPages - 1);

            // Scroll hacia arriba
            scrollPane.getVerticalScrollBar().setValue(0);
        };

        // Acciones de los botones de navegación
        anteriorBtn.addActionListener(e -> {
            paginaActual[0]--;
            actualizarPagina.run();
        });

        siguienteBtn.addActionListener(e -> {
            paginaActual[0]++;
            actualizarPagina.run();
        });

        // Acción de búsqueda
        java.awt.event.ActionListener buscarAction = e -> {
            String filtro = searchField.getText();
            List<Instalazioa> filtradas = controller.bilatuInstalazioak(filtro);
            todasInstalaciones.clear();
            todasInstalaciones.addAll(filtradas);
            paginaActual[0] = 0; // Volver a la primera página
            actualizarPagina.run();
        };

        buscarBtn.addActionListener(buscarAction);
        searchField.addActionListener(buscarAction);

        // Mostrar la primera página
        actualizarPagina.run();

        return mainPanel;
    }

    private void bistaratuInstalazioak(JPanel instalacionesPanel, List<Instalazioa> instalazioak) {
        instalacionesPanel.removeAll();
        for (Instalazioa inst : instalazioak) {
            instalacionesPanel.add(crearPanelInstalacion(inst));
            instalacionesPanel.add(Box.createVerticalStrut(10));
        }
        instalacionesPanel.revalidate();
        instalacionesPanel.repaint();
    }

    private String lortuIrudiaMotarenArabera(String mota) {
        if (mota == null)
            return "/sossense/img/icon_default.png";

        switch (mota.toUpperCase()) {
            case "OSPITALEA":
                return "/sossense/img/icon_hospital.png";
            case "UNIBERTSITATEA":
                return "/sossense/img/icon_universidad.png";
            case "FABRIKA":
                return "/sossense/img/icon_fabrika.png";
            case "IKASTOLA":
                return "/sossense/img/icon_ikastola.png";
            case "LABORATORIO":
                return "/sossense/img/icon_laboratorio.png";
            default:
                return "/sossense/img/icon_default.png";
        }
    }

    private JPanel crearPanelInstalacion(Instalazioa inst) {
        JPanel panel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                g2.setColor(inst.getKolorFondo());
                g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);

                g2.setColor(Color.BLACK);
                g2.setStroke(new BasicStroke(2));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30);
            }
        };

        panel.setOpaque(false);
        panel.setLayout(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Dimension maxSize = new Dimension(Integer.MAX_VALUE, 250);
        panel.setMaximumSize(maxSize);
        panel.setPreferredSize(new Dimension(800, 250));

        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 30);

        ImageIcon iconOriginal = null;
        try {
            java.net.URL imgUrl = getClass().getResource(lortuIrudiaMotarenArabera(inst.getMota()));
            if (imgUrl != null) {
                iconOriginal = new ImageIcon(imgUrl);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        JLabel imageLabel;
        int tamanoImagen = 200;

        if (iconOriginal != null) {
            Image imgEscalada = iconOriginal.getImage().getScaledInstance(tamanoImagen, tamanoImagen,
                    Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(imgEscalada));
        } else {
            imageLabel = new JLabel("No Image");
            imageLabel.setPreferredSize(new Dimension(tamanoImagen, tamanoImagen));
        }
        panel.add(imageLabel, gbc);

        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 0, 15, 0);

        JLabel izenaLabel = new JLabel(inst.getIzena());
        izenaLabel.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(izenaLabel, gbc);

        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0);

        JLabel sensoresLabel = new JLabel("Sentsore kopurua: " + inst.getSentsoreak());
        sensoresLabel.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(sensoresLabel, gbc);

        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 0, 0);

        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelEstado.setOpaque(false);

        JLabel lblEgoeraTitulo = new JLabel("Egoera: ");
        lblEgoeraTitulo.setFont(new Font("Arial", Font.BOLD, 24));

        JLabel lblEgoeraValor = new JLabel(inst.getEgoera());
        lblEgoeraValor.setFont(new Font("Arial", Font.BOLD, 30));
        lblEgoeraValor.setForeground(inst.getKolorEgoera());

        panelEstado.add(lblEgoeraTitulo);
        panelEstado.add(lblEgoeraValor);

        panel.add(panelEstado, gbc);

        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.insets = new Insets(15, 0, 0, 0);

        JLabel helbideaLabel = new JLabel(inst.getHelbidea());
        helbideaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        helbideaLabel.setForeground(Color.DARK_GRAY);
        panel.add(helbideaLabel, gbc);

        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cambiarPanelCentral(crearPanelSeleccionPlanos(inst.getIzena()));
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(
                        Math.max(0, inst.getKolorFondo().getRed() - 20),
                        Math.max(0, inst.getKolorFondo().getGreen() - 20),
                        Math.max(0, inst.getKolorFondo().getBlue() - 20)));
                panel.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                panel.setBackground(inst.getKolorFondo());
                panel.repaint();
            }
        });

        return panel;
    }

    private void cambiarPanelCentral(JPanel nuevoPanel) {
        frame.getContentPane().remove(centerPanel);
        centerPanel = nuevoPanel;
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }

    // ==================== MÉTODOS MEJORADOS ====================

    private JPanel crearPanelSeleccionPlanos(String nombreInstalacion) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Instalazioa instalacion = controller.bilatuInstalazioa(nombreInstalacion);
        if (instalacion == null) {
            JLabel errorLabel = new JLabel("Ez da instalaziorik aurkitu: " + nombreInstalacion);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(errorLabel, BorderLayout.CENTER);
            return mainPanel;
        }

        // Header con degradado y sombra
        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                // Degradado de fondo
                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0xF6, 0xB2, 0xB2),
                        getWidth(), getHeight(), new Color(0xD3, 0x85, 0x7E));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                // Sombra interior sutil
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 25, 25);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel titulo = new JLabel("🏢 " + nombreInstalacion);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo, BorderLayout.NORTH);

        JLabel subtitulo = new JLabel("Aukeratu ikusi nahi duzun planoa");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        subtitulo.setForeground(new Color(255, 255, 255, 230));
        subtitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        headerPanel.add(subtitulo, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Panel con las tarjetas de planos
        JPanel planosPanel = new JPanel(new GridLayout(2, 2, 25, 25));
        planosPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        planosPanel.setBackground(new Color(245, 245, 245));

        // Lista de plantas con iconos y colores
        String[][] planoDatos = {
                { "Sotanoa", "🔦", "#B8860B" },
                { "Behe Solairua", "🏠", "#4169E1" },
                { "1. Solairua", "🏢", "#32CD32" },
                { "2. Solairua", "🏗️", "#FF6347" }
        };

        for (String[] datos : planoDatos) {
            String nombrePlano = datos[0];
            String icono = datos[1];
            String colorHex = datos[2];
            Color colorAccent = Color.decode(colorHex);

            // Plano temporal para contar sensores críticos
            PlanoInstalacion planoTemp = new PlanoInstalacion(nombreInstalacion);
            int sensoresCriticos = planoTemp.getSentsoreakCriticos();

            planosPanel.add(crearTarjetaPlano(
                    nombrePlano,
                    icono,
                    colorAccent,
                    instalacion,
                    nombreInstalacion,
                    sensoresCriticos));
        }

        JScrollPane scrollPane = new JScrollPane(planosPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(245, 245, 245));
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Botón volver estilizado
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        bottomPanel.setBackground(new Color(245, 245, 245));

        JButton volverBtn = crearBotonEstilizado("⬅ INSTALAZIOETARA ITZULI", new Color(0xE2, 0x80, 0x76), Color.WHITE);
        volverBtn.addActionListener(e -> cambiarPanelCentral(crearPanelInstalaciones()));

        bottomPanel.add(volverBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private String lortuPlanoMotarenArabera(String mota) {
        if (mota == null)
            return null;

        switch (mota.toUpperCase()) {

            // ===== HOSPITAL =====
            case "OSPITALEA":
            case "HOSPITAL":
                return "/sossense/img/plano_hospital.png";

            // ===== UNIVERSIDAD =====
            case "UNIBERTSITATEA":
            case "UNIVERSIDAD":
                return "/sossense/img/plano_universidad.png";

            // ===== ESCUELA =====
            case "IKASTOLA":
            case "ESCOLA":
            case "ESCUELA":
                return "/sossense/img/plano_escuela.png";

            // ===== FABRICA =====
            case "FABRIKA":
            case "FABRICA":
                return "/sossense/img/plano_fabrica.png";

            // ===== LABORATORIO =====
            case "LABORATORIO":
                return "/sossense/img/plano_laboratorio.png";

            default:
                System.out.println("⚠ Mota ezezaguna: " + mota);
                return null;
        }
    }

    private JPanel crearTarjetaPlano(String nombrePlano, String icono, Color colorAccent,
            Instalazioa instalacion, String nombreInstalacion, int sensoresCriticos) {

        final boolean[] hover = { false };
        final boolean enAlerta = sensoresCriticos >= 3;
        final Image imagenPlano;

        Image temp = null;
        try {
            String ruta = lortuPlanoMotarenArabera(instalacion.getMota());
            if (ruta != null) {
                java.net.URL url = getClass().getResource(ruta);
                if (url != null) {
                    temp = new ImageIcon(url).getImage();
                } else {
                    System.out.println("No se encontró: " + ruta);
                }
            }
        } catch (Exception e) {
            System.out.println("Error cargando plano");
        }
        imagenPlano = temp;

        JPanel planoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                int w = getWidth();
                int h = getHeight();

                Color colorReal = enAlerta ? new Color(180, 30, 30) : colorAccent;

                // ===== SOMBRA =====
                g2.setColor(hover[0] ? new Color(0, 0, 0, 60) : new Color(0, 0, 0, 30));
                g2.fillRoundRect(4, 4, w - 4, h - 4, 25, 25);

                // ===== IMAGEN DE FONDO =====
                if (imagenPlano != null) {
                    g2.setClip(new java.awt.geom.RoundRectangle2D.Float(0, 0, w - 6, h - 6, 25, 25));
                    g2.drawImage(imagenPlano, 0, 0, w - 6, h - 6, null);
                    g2.setClip(null);

                    g2.setColor(new Color(255, 255, 255, 170));
                    g2.fillRoundRect(0, 0, w - 6, h - 6, 25, 25);
                } else {
                    g2.setColor(Color.WHITE);
                    g2.fillRoundRect(0, 0, w - 6, h - 6, 25, 25);
                }

                // ===== BARRA SUPERIOR =====
                g2.setColor(colorReal);
                g2.fillRoundRect(0, 0, w - 6, 60, 25, 25);
                g2.fillRect(0, 40, w - 6, 20);

                // ===== ALERTA =====
                if (enAlerta) {
                    g2.setColor(new Color(255, 0, 0, 70));
                    g2.fillRoundRect(0, 0, w - 6, h - 6, 25, 25);

                    g2.setFont(new Font("Arial", Font.BOLD, 22));
                    g2.setColor(Color.WHITE);
                    g2.drawString("🚨 ALERTA", 15, 35);
                }

                // ===== HOVER =====
                if (hover[0]) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, w - 6, h - 6, 25, 25);
                }

                // ===== BORDE =====
                g2.setColor(enAlerta ? Color.RED : (hover[0] ? colorAccent : new Color(220, 220, 220)));
                g2.setStroke(new BasicStroke(enAlerta ? 4 : (hover[0] ? 3 : 2)));
                g2.drawRoundRect(0, 0, w - 6, h - 6, 25, 25);
            }
        };

        planoPanel.setOpaque(false);
        planoPanel.setLayout(new BorderLayout());
        planoPanel.setPreferredSize(new Dimension(280, 240));
        planoPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));

        JLabel nombreLabel = new JLabel(nombrePlano);
        nombreLabel.setFont(new Font("Arial", Font.BOLD, 24));
        nombreLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nombreLabel.setForeground(enAlerta ? Color.RED.darker() : new Color(50, 50, 50));
        centerPanel.add(nombreLabel);

        if (enAlerta) {
            JLabel alertaLabel = new JLabel("⚠ " + sensoresCriticos + " críticos");
            alertaLabel.setFont(new Font("Arial", Font.BOLD, 18));
            alertaLabel.setForeground(Color.RED);
            alertaLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            centerPanel.add(alertaLabel);
        }

        planoPanel.add(centerPanel, BorderLayout.NORTH);

        planoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        planoPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                cambiarPanelCentral(crearPanelPlanoEspecifico(nombreInstalacion, nombrePlano));
            }

            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                hover[0] = true;
                planoPanel.repaint();
            }

            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) {
                hover[0] = false;
                planoPanel.repaint();
            }
        });

        return planoPanel;
    }

    private JPanel crearPanelPlanoEspecifico(String izenaInstalacion, String nombrePlano) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        Instalazioa instalacion = controller.bilatuInstalazioa(izenaInstalacion);
        if (instalacion == null) {
            JLabel errorLabel = new JLabel("Ez da instalaziorik aurkitu: " + izenaInstalacion);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(errorLabel, BorderLayout.CENTER);
            return mainPanel;
        }

        PlanoInstalacion plano = new PlanoInstalacion(izenaInstalacion);

        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

                GradientPaint gradient = new GradientPaint(
                        0, 0, new Color(0xF6, 0xB2, 0xB2),
                        getWidth(), getHeight(), new Color(0xD3, 0x85, 0x7E));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);

                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 25, 25);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel titulo = new JLabel("📍 " + izenaInstalacion + " - " + nombrePlano);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(Color.WHITE);
        headerPanel.add(titulo, BorderLayout.NORTH);

        JLabel subtitulo = new JLabel("Sentsoreen denbora errealeko monitorizazioa");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        subtitulo.setForeground(new Color(255, 255, 255, 230));
        subtitulo.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        headerPanel.add(subtitulo, BorderLayout.CENTER);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        PanelPlano panelPlano = new PanelPlano(plano);
        JScrollPane scrollPane = new JScrollPane(panelPlano);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E), 3));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(0xD3, 0x85, 0x7E)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JButton actualizarBtn = crearBotonEstilizado("🔄 EGUNERATU", new Color(0x52, 0xB7, 0x88), Color.WHITE);
        actualizarBtn.addActionListener(e -> {
            plano.simularNivelesHumo();
            panelPlano.repaint();
            // Aquí puedes actualizar las estadísticas si las tuvieras en esta vista
        });

        JButton volverBtn = crearBotonEstilizado("⬅ ATZERA", new Color(0xE2, 0x80, 0x76), Color.WHITE);
        volverBtn.addActionListener(e -> {
            panelPlano.detenerActualizacion();
            cambiarPanelCentral(crearPanelSeleccionPlanos(izenaInstalacion));
        });

        controlPanel.add(actualizarBtn);
        controlPanel.add(volverBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel crearPanelAgregarInstalacion() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("GEHITU INSTALAZIO BERRIA");
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
        mainPanel.add(titulo, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        JLabel lblIzena = new JLabel("Izena:");
        lblIzena.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblIzena, gbc);

        gbc.gridx = 1;
        JTextField izenaField = new JTextField(25);
        izenaField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(izenaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        JLabel lblSentsoreak = new JLabel("Sentsore kopurua:");
        lblSentsoreak.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblSentsoreak, gbc);

        gbc.gridx = 1;
        JTextField sensoresField = new JTextField(25);
        sensoresField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(sensoresField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblEgoera = new JLabel("Egoera:");
        lblEgoera.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblEgoera, gbc);

        gbc.gridx = 1;
        String[] egoeras = { "NORMALA", "LARRIA", "ARINGARRI", "KALTEA", "MANTENIMIENTO" };
        JComboBox<String> egoeraCombo = new JComboBox<>(egoeras);
        egoeraCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(egoeraCombo, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        JLabel lblHelbidea = new JLabel("Helbidea:");
        lblHelbidea.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblHelbidea, gbc);

        gbc.gridx = 1;
        JTextField helbideaField = new JTextField(25);
        helbideaField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(helbideaField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        JLabel lblMota = new JLabel("Mota:");
        lblMota.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblMota, gbc);

        gbc.gridx = 1;
        String[] motas = { "HOSPITAL", "UNIVERSIDAD", "ESCOLA", "FABRICA", "LABORATORIO", "OFICINA", "ALMACEN" };
        JComboBox<String> motaCombo = new JComboBox<>(motas);
        motaCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(motaCombo, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));

        JButton agregarBtn = new JButton("GEHITU");
        agregarBtn.setFont(new Font("Arial", Font.BOLD, 16));
        agregarBtn.setPreferredSize(new Dimension(150, 40));
        agregarBtn.addActionListener(e -> {
            try {
                String izena = izenaField.getText();
                int sentsoreak = Integer.parseInt(sensoresField.getText());
                String egoera = (String) egoeraCombo.getSelectedItem();
                String helbidea = helbideaField.getText();
                String mota = (String) motaCombo.getSelectedItem();

                if (izena.isEmpty() || helbidea.isEmpty()) {
                    JOptionPane.showMessageDialog(frame,
                            "Mesedez, bete izena eta helbidea.",
                            "Errorea",
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }

                controller.gehituInstalazioa(izena, sentsoreak, egoera, helbidea, mota);

                JOptionPane.showMessageDialog(frame,
                        "Instalazioa ondo gehitu da!",
                        "Arrakasta",
                        JOptionPane.INFORMATION_MESSAGE);

                cambiarPanelCentral(crearPanelInstalaciones());

            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(frame,
                        "Sartu zenbaki balioduna sentsore kopururako.",
                        "Errorea",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelarBtn = new JButton("UTZI");
        cancelarBtn.setFont(new Font("Arial", Font.BOLD, 16));
        cancelarBtn.setPreferredSize(new Dimension(150, 40));
        cancelarBtn.addActionListener(e -> cambiarPanelCentral(crearPanelInstalaciones()));

        buttonPanel.add(agregarBtn);
        buttonPanel.add(cancelarBtn);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        return mainPanel;
    }

    private JPanel crearPanelEstadisticas() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("ESTATISTIKAK");
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
        mainPanel.add(titulo, BorderLayout.NORTH);

        JTextArea textArea = new JTextArea(controller.lortuEstadistikak());
        textArea.setEditable(false);
        textArea.setFont(new Font("Courier New", Font.PLAIN, 16));
        textArea.setMargin(new Insets(20, 20, 20, 20));

        JScrollPane scrollPane = new JScrollPane(textArea);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        return mainPanel;
    }

    private JPanel crearPanelContacto() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel titulo = new JLabel("KONTAKTUA");
        titulo.setFont(new Font("Arial", Font.BOLD, 40));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        titulo.setForeground(new Color(41, 128, 185));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 30, 10));
        mainPanel.add(titulo, BorderLayout.NORTH);

        JPanel centerWrapper = new JPanel(new BorderLayout());
        JPanel contactPanel = new JPanel();
        contactPanel.setLayout(new BoxLayout(contactPanel, BoxLayout.Y_AXIS));
        contactPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));

        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        headerPanel.setBackground(new Color(236, 240, 241));
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(189, 195, 199), 1),
                BorderFactory.createEmptyBorder(20, 20, 20, 20)));

        JLabel subtitulo = new JLabel("S.O.S.sense - Monitorizazio Sistema");
        subtitulo.setFont(new Font("Arial", Font.BOLD, 26));
        subtitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitulo.setForeground(new Color(52, 73, 94));
        headerPanel.add(subtitulo);

        headerPanel.add(Box.createVerticalStrut(10));

        JLabel descripcion = new JLabel(
                "<html><center>Sistema aurreratua instalazio industrialen monitorizaziorako.<br>Sentsore eta datu analisi bidezko irtenbide integrala.</center></html>");
        descripcion.setFont(new Font("Arial", Font.PLAIN, 14));
        descripcion.setAlignmentX(Component.CENTER_ALIGNMENT);
        descripcion.setForeground(new Color(127, 140, 141));
        headerPanel.add(descripcion);

        contactPanel.add(headerPanel);
        contactPanel.add(Box.createVerticalStrut(25));

        JPanel infoPanel = new JPanel(new GridLayout(0, 2, 20, 15));
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(52, 152, 219), 2),
                        "Kontaktu Informazioa",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(52, 152, 219)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        infoPanel.add(crearInfoLabel("📞 Telefonoa:", true));
        infoPanel.add(crearInfoLabel("+34 943 123 456", false));

        infoPanel.add(crearInfoLabel("📧 Email:", true));
        infoPanel.add(crearInfoLabel("info@sossense.eus", false));

        infoPanel.add(crearInfoLabel("🛠️ Laguntza Teknikoa:", true));
        infoPanel.add(crearInfoLabel("support@sossense.eus", false));

        infoPanel.add(crearInfoLabel("🌐 Web Orria:", true));
        infoPanel.add(crearInfoLabel("www.sossense.eus", false));

        infoPanel.add(crearInfoLabel("🚨 Larrialdiak:", true));
        infoPanel.add(crearInfoLabel("+34 943 999 888", false));

        infoPanel.add(crearInfoLabel("📠 Faxa:", true));
        infoPanel.add(crearInfoLabel("+34 943 123 457", false));

        contactPanel.add(infoPanel);
        contactPanel.add(Box.createVerticalStrut(25));

        JPanel direccionPanel = new JPanel();
        direccionPanel.setLayout(new BoxLayout(direccionPanel, BoxLayout.Y_AXIS));
        direccionPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(46, 204, 113), 2),
                        "Helbidea",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(46, 204, 113)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel direccion1 = new JLabel("📍 Nafarros Himbidea 16");
        direccion1.setFont(new Font("Arial", Font.PLAIN, 16));
        direccion1.setAlignmentX(Component.CENTER_ALIGNMENT);
        direccionPanel.add(direccion1);

        direccionPanel.add(Box.createVerticalStrut(8));

        JLabel direccion2 = new JLabel("20500 Arrasate, Gipuzkoa");
        direccion2.setFont(new Font("Arial", Font.PLAIN, 16));
        direccion2.setAlignmentX(Component.CENTER_ALIGNMENT);
        direccionPanel.add(direccion2);

        direccionPanel.add(Box.createVerticalStrut(8));

        JLabel pais = new JLabel("Euskadi, España");
        pais.setFont(new Font("Arial", Font.PLAIN, 16));
        pais.setAlignmentX(Component.CENTER_ALIGNMENT);
        direccionPanel.add(pais);

        contactPanel.add(direccionPanel);
        contactPanel.add(Box.createVerticalStrut(25));

        JPanel horariosPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        horariosPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(155, 89, 182), 2),
                        "Arreta Ordutegia",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(155, 89, 182)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        horariosPanel.add(crearInfoLabel("🕐 Astelehenetik Ostiralera:", true));
        horariosPanel.add(crearInfoLabel("08:00 - 18:00", false));

        horariosPanel.add(crearInfoLabel("🕐 Larunbata:", true));
        horariosPanel.add(crearInfoLabel("09:00 - 14:00", false));

        horariosPanel.add(crearInfoLabel("🕐 Igandea:", true));
        horariosPanel.add(crearInfoLabel("Itxita", false));

        horariosPanel.add(crearInfoLabel("🚨 Laguntza Teknikoa 24/7:", true));
        horariosPanel.add(crearInfoLabel("Beti eskuragarri", false));

        contactPanel.add(horariosPanel);
        contactPanel.add(Box.createVerticalStrut(25));

        JPanel socialPanel = new JPanel(new GridLayout(0, 2, 15, 10));
        socialPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder(
                        BorderFactory.createLineBorder(new Color(231, 76, 60), 2),
                        "Sare Sozialak",
                        javax.swing.border.TitledBorder.LEFT,
                        javax.swing.border.TitledBorder.TOP,
                        new Font("Arial", Font.BOLD, 16),
                        new Color(231, 76, 60)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        socialPanel.add(crearInfoLabel("📘 Facebook:", true));
        socialPanel.add(crearInfoLabel("@sossense.oficial", false));

        socialPanel.add(crearInfoLabel("🐦 Twitter:", true));
        socialPanel.add(crearInfoLabel("@sossense", false));

        socialPanel.add(crearInfoLabel("📷 Instagram:", true));
        socialPanel.add(crearInfoLabel("@sossense_monitorizazioa", false));

        socialPanel.add(crearInfoLabel("💼 LinkedIn:", true));
        socialPanel.add(crearInfoLabel("S.O.S.sense Sistemas", false));

        contactPanel.add(socialPanel);
        contactPanel.add(Box.createVerticalStrut(20));

        JPanel notaPanel = new JPanel();
        notaPanel.setLayout(new BoxLayout(notaPanel, BoxLayout.Y_AXIS));
        notaPanel.setBackground(new Color(255, 243, 205));
        notaPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(243, 156, 18), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));

        JLabel notaTitulo = new JLabel("ℹ️ Informazio Gehigarria");
        notaTitulo.setFont(new Font("Arial", Font.BOLD, 14));
        notaTitulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        notaTitulo.setForeground(new Color(243, 156, 18));
        notaPanel.add(notaTitulo);

        notaPanel.add(Box.createVerticalStrut(8));

        JLabel notaTexto = new JLabel(
                "<html><center>Instalazio berri baten interesa baduzu edo zalantzarik baduzu,<br>jar zaitez gurekin harremanetan. Pozik lagunduko dizugu!</center></html>");
        notaTexto.setFont(new Font("Arial", Font.PLAIN, 12));
        notaTexto.setAlignmentX(Component.CENTER_ALIGNMENT);
        notaPanel.add(notaTexto);

        contactPanel.add(notaPanel);

        JScrollPane scrollPane = new JScrollPane(contactPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        centerWrapper.add(scrollPane, BorderLayout.CENTER);

        mainPanel.add(centerWrapper, BorderLayout.CENTER);

        return mainPanel;
    }

    private JLabel crearInfoLabel(String texto, boolean negrita) {
        JLabel label = new JLabel(texto);
        if (negrita) {
            label.setFont(new Font("Arial", Font.BOLD, 15));
            label.setForeground(new Color(52, 73, 94));
        } else {
            label.setFont(new Font("Arial", Font.PLAIN, 15));
            label.setForeground(new Color(52, 73, 94));
        }
        return label;
    }

    // Método auxiliar para crear botones estilizados
    private JButton crearBotonEstilizado(String texto, Color fondo, Color textoColor) {
        JButton boton = new JButton(texto) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                if (getModel().isPressed()) {
                    g2.setColor(fondo.darker());
                } else if (getModel().isRollover()) {
                    g2.setColor(fondo.brighter());
                } else {
                    g2.setColor(fondo);
                }

                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 15, 15);

                super.paintComponent(g);
            }
        };

        boton.setFont(new Font("Arial", Font.BOLD, 16));
        boton.setForeground(textoColor);
        boton.setPreferredSize(new Dimension(220, 45));
        boton.setContentAreaFilled(false);
        boton.setBorderPainted(false);
        boton.setFocusPainted(false);
        boton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        return boton;
    }
}
