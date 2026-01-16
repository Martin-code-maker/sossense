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
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

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
    // Variable para guardar el plano que el usuario está viendo actualmente
    private PanelPlano panelPlanoActivo = null;
    private String instalacionActiva = "";


    public SOSsenseApp() {
        this.model = new SOSsenseModeloa();
        this.controller = new SOSsenseKontrolatzailea(model);
        crearInterfaz();
        
        // --- CONEXIÓN MQTT ---
        try {
            sossense.mqtt.Mqtt mqtt = new sossense.mqtt.Mqtt();
            
            // Cuando MQTT calcule la media de 10 valores...
            mqtt.addPropertyChangeListener(evt -> {
                if ("DATO_GAS_ACTUALIZADO".equals(evt.getPropertyName())) {
                    double valorMedia = (double) evt.getNewValue();
                    
                    // Solo actualizamos si el usuario está mirando el mapa de la Fábrica
                    if (panelPlanoActivo != null && "Mondragon Fabrika".equalsIgnoreCase(instalacionActiva)) {
                        
                        // Actualizar el sensor S1 (que siempre existe)
                        panelPlanoActivo.actualizarSensorEspecifico("S1", (int) valorMedia);
                        
                        System.out.println(">>> Mapa actualizado: S1 = " + valorMedia);
                    }
                }
            });
            
        } catch (Exception ex) {
            System.err.println("Error conectando MQTT: " + ex.getMessage());
        }
        // ---------------------
    }

    // Método nuevo para buscar el sensor y actualizarlo
    private void actualizarSensorConMqtt(String nombreInstalacion, double valorGas) {
        // Buscamos la instalación
        // NOTA: Esto requiere que tengas acceso a los planos desde aquí o a través del controlador.
        // Como ejemplo rápido, si tuviéramos acceso al objeto 'plano' activo:
        
        System.out.println("Actualizando mapa con valor medio: " + valorGas);
        
        // Lógica ideal: Pasar este valor al controlador para que actualice el SensorPlano específico
        // controller.actualizarSensorGas(nombreInstalacion, "S1", valorGas);
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

        // Cargar planos desde el archivo planos.txt
        List<PlanoInfo> planosInstalacion = cargarPlanosDeInstalacion(nombreInstalacion);
        if (planosInstalacion.isEmpty()) {
            JLabel errorLabel = new JLabel("Ez dago planorik instalazio honetarako: " + nombreInstalacion);
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

        // Panel con las tarjetas de planos - dinámico según cantidad
        int numPlanos = planosInstalacion.size();
        int columnas = Math.min(2, numPlanos); // Máximo 2 columnas
        int filas = (int) Math.ceil(numPlanos / 2.0);
        JPanel planosPanel = new JPanel(new GridLayout(filas, columnas, 25, 25));
        planosPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        planosPanel.setBackground(new Color(245, 245, 245));

        // Iconos y colores por defecto para variar
        String[] iconos = {"🏠", "🏢", "🏗️", "🔦", "📍", "🏛️"};
        String[] coloresHex = {"#4169E1", "#32CD32", "#FF6347", "#B8860B", "#9370DB", "#FF69B4"};

        for (int i = 0; i < planosInstalacion.size(); i++) {
            PlanoInfo planoInfo = planosInstalacion.get(i);
            String icono = iconos[i % iconos.length];
            Color colorAccent = Color.decode(coloresHex[i % coloresHex.length]);

            // Crear PlanoInstalacion para contar sensores críticos
            PlanoInstalacion planoTemp = new PlanoInstalacion(planoInfo);
            int sensoresCriticos = planoTemp.getSentsoreakCriticos();

            planosPanel.add(crearTarjetaPlano(
                    planoInfo.getNombrePlano(),
                    icono,
                    colorAccent,
                    instalacion,
                    nombreInstalacion,
                    sensoresCriticos,
                    planoInfo));
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
            Instalazioa instalacion, String nombreInstalacion, int sensoresCriticos, PlanoInfo planoInfo) {

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
                cambiarPanelCentral(crearPanelPlanoEspecifico(nombreInstalacion, nombrePlano, planoInfo));
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

    private JPanel crearPanelPlanoEspecifico(String izenaInstalacion, String nombrePlano, PlanoInfo planoInfo) {
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

<<<<<<< HEAD
        // 1. Guardamos la instalación activa para el MQTT
        this.instalacionActiva = izenaInstalacion;
=======
        PlanoInstalacion plano = new PlanoInstalacion(planoInfo);
>>>>>>> dbddbfabb7af2e55351aaa40ac23adf45991c2ba

        // 2. Creamos el plano y desactivamos la simulación para recibir datos reales
        PlanoInstalacion plano = new PlanoInstalacion(izenaInstalacion);
        plano.setSimulacionActiva(false); // <--- IMPORTANTE: Desactivar simulación aleatoria

        // 3. Crear Header (Título y degradado)
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

        // 4. Creamos el Panel visual del plano
        PanelPlano panelPlano = new PanelPlano(plano);
        
        // 5. Guardamos la referencia global para que el MQTT sepa dónde pintar
        this.panelPlanoActivo = panelPlano; 

        JScrollPane scrollPane = new JScrollPane(panelPlano);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E), 3));

        // 6. Panel de control (Botones)
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(0xD3, 0x85, 0x7E)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JButton actualizarBtn = crearBotonEstilizado("🔄 EGUNERATU", new Color(0x52, 0xB7, 0x88), Color.WHITE);
        actualizarBtn.addActionListener(e -> {
            // Si quieres forzar una actualización manual (opcional si usas MQTT)
            // plano.simularNivelesHumo(); 
            panelPlano.repaint();
        });

        JButton volverBtn = crearBotonEstilizado("⬅ ATZERA", new Color(0xE2, 0x80, 0x76), Color.WHITE);
        volverBtn.addActionListener(e -> {
            panelPlano.detenerActualizacion();
            // Limpiamos referencias al salir
            this.panelPlanoActivo = null;
            this.instalacionActiva = "";
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

    // Panel de contacto minimalista - VERSIÓN GRANDE
    private JPanel crearPanelContacto() {
        // 1. Panel Principal
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(250, 250, 250)); 

        // 2. Tarjeta Central (Card)
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        
        // CAMBIO: Mucho más padding (antes 50/80 -> ahora 70/130)
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(70, 130, 70, 130) 
        ));
        
        // Título (Fuente aumentada a 32)
        JLabel titulo = new JLabel("KONTAKTUA");
        titulo.setFont(new Font("Arial", Font.BOLD, 32)); 
        titulo.setForeground(new Color(50, 50, 50));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Separador (Un poco más ancho)
        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(60, 5));
        separator.setForeground(new Color(0xE1, 0x9D, 0x8E)); 
        separator.setBackground(new Color(0xE1, 0x9D, 0x8E));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Panel de datos (Más espacio vertical entre elementos: 50px)
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 50, 50));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));
        
        // Añadimos la info
        infoPanel.add(crearFilaContacto("📍", "HELBIDEA", "Nafarroa Hiribidea 16, Arrasate"));
        infoPanel.add(crearFilaContacto("📧", "EMAILA", "info@sossense.eus"));
        infoPanel.add(crearFilaContacto("📞", "TELEFONOA", "+34 943 123 456"));

        // Pie de página (Fuente aumentada a 16)
        JLabel webLabel = new JLabel("www.sossense.eus");
        webLabel.setFont(new Font("Arial", Font.BOLD, 16));
        webLabel.setForeground(new Color(0xE1, 0x9D, 0x8E));
        webLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Montaje
        card.add(titulo);
        card.add(Box.createVerticalStrut(25));
        card.add(separator);
        card.add(infoPanel);
        card.add(Box.createVerticalGlue()); 
        card.add(webLabel);

        mainPanel.add(card);
        return mainPanel;
    }

    // Helper con fuentes aumentadas
    private JPanel crearFilaContacto(String icono, String titulo, String texto) {
        JPanel panel = new JPanel(new BorderLayout(25, 0)); // Más separación icono-texto
        panel.setBackground(Color.WHITE);
        
        // Icono gigante (40px)
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42)); 
        
        // Panel de texto
        JPanel txtPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        txtPanel.setBackground(Color.WHITE);
        
        // Título pequeño (12px)
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitulo.setForeground(Color.GRAY);
        
        // Texto principal grande (22px)
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Arial", Font.PLAIN, 22)); 
        lblTexto.setForeground(new Color(40, 40, 40));
        
        txtPanel.add(lblTitulo);
        txtPanel.add(lblTexto);
        
        panel.add(lblIcono, BorderLayout.WEST);
        panel.add(txtPanel, BorderLayout.CENTER);
        
        return panel;
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

    // Método para cargar los planos de una instalación específica desde planos.txt
    private List<PlanoInfo> cargarPlanosDeInstalacion(String nombreInstalacion) {
        List<PlanoInfo> planos = new ArrayList<>();
        String archivo = "datos/planos.txt";
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Ignorar líneas vacías y comentarios
                if (linea.trim().isEmpty() || linea.trim().startsWith("#")) {
                    continue;
                }
                
                // Formato: nombre_instalacion|nombre_plano|imagen_fondo|ancho|alto|sensores_min|sensores_max
                String[] partes = linea.split("\\|");
                if (partes.length >= 7) {
                    String instalacion = partes[0].trim();
                    
                    // Solo añadir si coincide con la instalación buscada
                    if (instalacion.equalsIgnoreCase(nombreInstalacion)) {
                        String nombrePlano = partes[1].trim();
                        String imagenFondo = partes[2].trim();
                        int ancho = Integer.parseInt(partes[3].trim());
                        int alto = Integer.parseInt(partes[4].trim());
                        int sensoresMin = Integer.parseInt(partes[5].trim());
                        int sensoresMax = Integer.parseInt(partes[6].trim());
                        
                        planos.add(new PlanoInfo(instalacion, nombrePlano, imagenFondo, 
                                                ancho, alto, sensoresMin, sensoresMax));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de planos: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear números del archivo de planos: " + e.getMessage());
        }
        
        return planos;
    }
}
