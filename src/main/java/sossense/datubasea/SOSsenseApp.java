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
        try {
            Mqtt mqtt = new Mqtt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        */
    }
    
    public void bistaratuApp(){
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
        
        // Botón de volver al inicio de sesión (mismo icono que el menú)
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
                JOptionPane.YES_NO_OPTION
            );
            if (confirm == JOptionPane.YES_OPTION) {
                frame.dispose();
                // Volver al login
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
                
                // Dibujar círculo de fondo blanco
                g2.setColor(Color.WHITE);
                g2.fillOval(0, 0, getWidth() - 1, getHeight() - 1);
                
                // Recortar la imagen en forma circular
                g2.setClip(new java.awt.geom.Ellipse2D.Float(0, 0, getWidth() - 1, getHeight() - 1));
                
                super.paintComponent(g2);
                
                // Dibujar borde circular
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

        // Centrar la ventana
        frame.setLocationRelativeTo(null);
        //frame.setVisible(true);
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
                g2.fillRect(0, 0, arc / 2, getHeight()); // flatten left edge
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
        
        // Botones del menú - AÑADIDO "PLANOAK"
        String[] itemsMenu = {"INSTALACION GUZTIAK", "GEHITU BERRIA", "ESTATISTIKAK", "KONTAKTUA"};
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
                        g2.fillRoundRect(2, 2, getWidth()-4, getHeight()-4, 10, 10);
                    }
                    
                    g2.setColor(bgColor);
                    g2.fillRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
                    g2.setColor(Color.BLACK);
                    g2.setStroke(new BasicStroke(1.5f));
                    g2.drawRoundRect(1, 1, getWidth()-2, getHeight()-2, 10, 10);
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
            
            // Asignar acciones a los botones
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
        
        bistaratuInstalazioak(instalacionesPanel, controller.lortuInstalazioak());
        
        JPanel contenedorPanel = new JPanel();
        contenedorPanel.setLayout(new BoxLayout(contenedorPanel, BoxLayout.Y_AXIS));
        contenedorPanel.add(instalacionesPanel);
        contenedorPanel.add(Box.createVerticalGlue());
        
        JScrollPane scrollPane = new JScrollPane(contenedorPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        scrollPane.getVerticalScrollBar().setUnitIncrement(25);
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        java.awt.event.ActionListener buscarAction = e -> {
            String filtro = searchField.getText();
            List<Instalazioa> filtradas = controller.bilatuInstalazioak(filtro);
            bistaratuInstalazioak(instalacionesPanel, filtradas);
        };
        
        buscarBtn.addActionListener(buscarAction);
        searchField.addActionListener(buscarAction);
        
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
    
    // Método auxiliar para obtener la ruta de la imagen según el tipo (mota)
    private String lortuIrudiaMotarenArabera(String mota) {
        if (mota == null) return "/sossense/img/icon_default.png";
        
        switch (mota.toUpperCase()) {
            case "OSPITALEA":    return "/sossense/img/icon_hospital.png";
            case "UNIBERTSITATEA": return "/sossense/img/icon_universidad.png";
            case "FABRIKA":      return "/sossense/img/icon_fabrika.png";
            case "IKASTOLA":     return "/sossense/img/icon_ikastola.png";
            case "LABORATORIO":  return "/sossense/img/icon_laboratorio.png";
            default:             return "/sossense/img/icon_default.png";
        }
    }

    private JPanel crearPanelInstalacion(Instalazioa inst) {
        // 1. Panel personalizado (Igual que antes pero quizás con radio un poco mayor si quieres)
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
        // Aumentamos el padding interno del panel (antes 15, ahora 20)
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Limitar el tamaño máximo del panel de instalación
        // Altura máxima: 250 píxeles (aproximadamente un tercio del panel)
        Dimension maxSize = new Dimension(Integer.MAX_VALUE, 250);
        panel.setMaximumSize(maxSize);
        panel.setPreferredSize(new Dimension(800, 250)); 
        
        GridBagConstraints gbc = new GridBagConstraints();

        // ---------------------------------------------------------
        // A. IMAGEN A LA IZQUIERDA (MÁS GRANDE)
        // ---------------------------------------------------------
        gbc.gridx = 0; 
        gbc.gridy = 0;
        gbc.gridheight = 4;
        gbc.weightx = 0.0;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(0, 0, 0, 30); // Más separación entre imagen y texto
        
        ImageIcon iconOriginal = null;
        try {
            java.net.URL imgUrl = getClass().getResource(lortuIrudiaMotarenArabera(inst.getMota()));
            if (imgUrl != null) {
                iconOriginal = new ImageIcon(imgUrl);
            }
        } catch (Exception e) { e.printStackTrace(); }

        JLabel imageLabel;
        // TAMAÑO AUMENTADO: de 140x140 a 200x200
        int tamanoImagen = 200; 
        
        if (iconOriginal != null) {
            Image imgEscalada = iconOriginal.getImage().getScaledInstance(tamanoImagen, tamanoImagen, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(imgEscalada));
        } else {
            imageLabel = new JLabel("No Image");
            imageLabel.setPreferredSize(new Dimension(tamanoImagen, tamanoImagen));
        }
        panel.add(imageLabel, gbc);

        // ---------------------------------------------------------
        // B. INFORMACIÓN A LA DERECHA (FUENTES MÁS GRANDES)
        // ---------------------------------------------------------
        
        // 1. Título
        gbc.gridx = 1; 
        gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.weightx = 1.0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.insets = new Insets(5, 0, 15, 0); // Más espacio debajo del título
        
        JLabel izenaLabel = new JLabel(inst.getIzena());
        // FUENTE AUMENTADA: de 22 a 28
        izenaLabel.setFont(new Font("Arial", Font.BOLD, 28)); 
        panel.add(izenaLabel, gbc);

        // 2. Sensores
        gbc.gridy = 1;
        gbc.insets = new Insets(0, 0, 10, 0); // Más espacio debajo
        
        JLabel sensoresLabel = new JLabel("Sentsore kopurua: " + inst.getSentsoreak());
        // FUENTE AUMENTADA: de 18 a 22
        sensoresLabel.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(sensoresLabel, gbc);
        
        // 3. Egoera
        gbc.gridy = 2;
        gbc.insets = new Insets(5, 0, 0, 0);
        
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelEstado.setOpaque(false);
        
        JLabel lblEgoeraTitulo = new JLabel("Egoera: ");
        // FUENTE AUMENTADA: de 20 a 24
        lblEgoeraTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        
        JLabel lblEgoeraValor = new JLabel(inst.getEgoera());
        // FUENTE AUMENTADA: de 24 a 30
        lblEgoeraValor.setFont(new Font("Arial", Font.BOLD, 30)); 
        lblEgoeraValor.setForeground(inst.getKolorEgoera());
        
        panelEstado.add(lblEgoeraTitulo);
        panelEstado.add(lblEgoeraValor);
        
        panel.add(panelEstado, gbc);

        // 4. Dirección
        gbc.gridy = 3;
        gbc.weighty = 1.0;
        gbc.anchor = GridBagConstraints.SOUTHEAST;
        gbc.insets = new Insets(15, 0, 0, 0);
        
        JLabel helbideaLabel = new JLabel(inst.getHelbidea());
        // FUENTE AUMENTADA: de 11 a 14
        helbideaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        helbideaLabel.setForeground(Color.DARK_GRAY);
        panel.add(helbideaLabel, gbc);

        // Hacer el panel completo clickeable para abrir la vista de planos
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
                    Math.max(0, inst.getKolorFondo().getBlue() - 20)
                ));
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
    
    
    // Método para cambiar el contenido del panel central
    private void cambiarPanelCentral(JPanel nuevoPanel) {
        frame.getContentPane().remove(centerPanel);
        centerPanel = nuevoPanel;
        frame.getContentPane().add(centerPanel, BorderLayout.CENTER);
        frame.revalidate();
        frame.repaint();
    }
    
    // Métodos para las acciones del menú
    
    // Panel para seleccionar planos de una instalación específica
    private JPanel crearPanelSeleccionPlanos(String nombreInstalacion) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Buscar la instalación
        Instalazioa instalacion = controller.bilatuInstalazioa(nombreInstalacion);
        if (instalacion == null) {
            JLabel errorLabel = new JLabel("Ez da instalaziorik aurkitu: " + nombreInstalacion);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(errorLabel, BorderLayout.CENTER);
            return mainPanel;
        }
        
        // Panel superior con título e info de la instalación
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        
        JLabel titulo = new JLabel("PLANOAK - " + nombreInstalacion);
        titulo.setFont(new Font("Arial", Font.BOLD, 28));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        headerPanel.add(titulo, BorderLayout.NORTH);
        
        JLabel subtitulo = new JLabel("Aukeratu ikusi nahi duzun planoa");
        subtitulo.setFont(new Font("Arial", Font.PLAIN, 16));
        subtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        subtitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        headerPanel.add(subtitulo, BorderLayout.CENTER);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Panel con las tarjetas de planos
        JPanel planosPanel = new JPanel(new GridLayout(0, 2, 20, 20));
        planosPanel.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        
        // De momento, crear una lista de planos posibles para esta instalación
        // En el futuro, esto podría venir de una lista en la clase Instalazioa
        String[] nombrePlanos = {"Planta Baja", "Planta 1", "Planta 2", "Sótano"};
        
        for (String nombrePlano : nombrePlanos) {
            JPanel planoPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g;
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    g2.setColor(instalacion.getKolorFondo());
                    g2.fillRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                    
                    g2.setColor(instalacion.getKolorea());
                    g2.setStroke(new BasicStroke(3));
                    g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 20, 20);
                }
            };
            
            planoPanel.setOpaque(false);
            planoPanel.setLayout(new BorderLayout());
            planoPanel.setPreferredSize(new Dimension(300, 200));
            planoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Icono de plano
            JLabel iconoLabel = new JLabel("🗺️");
            iconoLabel.setFont(new Font("Arial", Font.PLAIN, 60));
            iconoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            // Nombre del plano
            JLabel nombreLabel = new JLabel(nombrePlano);
            nombreLabel.setFont(new Font("Arial", Font.BOLD, 20));
            nombreLabel.setHorizontalAlignment(SwingConstants.CENTER);
            nombreLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            
            // Info adicional
            JLabel infoLabel = new JLabel("Click para ver el plano");
            infoLabel.setFont(new Font("Arial", Font.ITALIC, 12));
            infoLabel.setHorizontalAlignment(SwingConstants.CENTER);
            infoLabel.setForeground(Color.GRAY);
            
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setOpaque(false);
            contentPanel.add(iconoLabel);
            contentPanel.add(nombreLabel);
            contentPanel.add(infoLabel);
            
            planoPanel.add(contentPanel, BorderLayout.CENTER);
            
            // Hacer clickeable
            planoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            planoPanel.addMouseListener(new java.awt.event.MouseAdapter() {
                @Override
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    cambiarPanelCentral(crearPanelPlanoEspecifico(nombreInstalacion, nombrePlano));
                }
                
                @Override
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    planoPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(20, 20, 20, 20),
                        BorderFactory.createLineBorder(instalacion.getKolorea(), 2)
                    ));
                }
                
                @Override
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    planoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                }
            });
            
            planosPanel.add(planoPanel);
        }
        
        JScrollPane scrollPane = new JScrollPane(planosPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Botón volver
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton volverBtn = new JButton("⬅ ITZULI");
        volverBtn.setFont(new Font("Arial", Font.BOLD, 14));
        volverBtn.setPreferredSize(new Dimension(150, 40));
        volverBtn.addActionListener(e -> cambiarPanelCentral(crearPanelInstalaciones()));
        bottomPanel.add(volverBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    // Panel para mostrar un plano específico
    private JPanel crearPanelPlanoEspecifico(String izenaInstalacion, String nombrePlano) {
        JPanel mainPanel = new JPanel(new BorderLayout());
        
        PlanoInstalacion plano = new PlanoInstalacion(izenaInstalacion);
        
        JPanel infoPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel tituloLabel = new JLabel("PLANOA: " + izenaInstalacion + " - " + nombrePlano);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 18));
        tituloLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel statsLabel = new JLabel("Sentsoreak: " + plano.getTotalSentsoreak() + 
            " | Alerta egoeran: " + plano.getSentsoreakAlerta() + 
            " | Larriak: " + plano.getSentsoreakCriticos());
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        infoPanel.add(tituloLabel);
        infoPanel.add(statsLabel);
        
        PanelPlano panelPlano = new PanelPlano(plano);
        JScrollPane scrollPane = new JScrollPane(panelPlano);
        
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JButton actualizarBtn = new JButton("EGUNERATU DATUAK");
        actualizarBtn.addActionListener(e -> {
            plano.simularNivelesHumo();
            panelPlano.repaint();
            statsLabel.setText("Sentsoreak: " + plano.getTotalSentsoreak() + 
                " | Alerta egoeran: " + plano.getSentsoreakAlerta() + 
                " | Larriak: " + plano.getSentsoreakCriticos());
        });
        
        JButton volverBtn = new JButton("ITZULI");
        volverBtn.addActionListener(e -> {
            panelPlano.detenerActualizacion();
            cambiarPanelCentral(crearPanelSeleccionPlanos(izenaInstalacion));
        });
        
        controlPanel.add(actualizarBtn);
        controlPanel.add(volverBtn);
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    // Panel para agregar nueva instalación
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
        String[] egoeras = {"NORMALA", "LARRIA", "ARINGARRI", "KALTEA", "MANTENIMIENTO"};
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
        String[] motas = {"HOSPITAL", "UNIVERSIDAD", "ESCOLA", "FABRICA", "LABORATORIO", "OFICINA", "ALMACEN"};
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
    
    // Panel para mostrar estadísticas
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
    
    // Panel para mostrar contacto
    // Panel de contacto minimalista y elegante
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

    // Helper para crear filas de contacto con estilo uniforme
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
    
    private JPanel crearEtiquetaInformacion(String titulo, String valor) {
        JPanel panel = new JPanel(new BorderLayout());
        JLabel tituloLabel = new JLabel(titulo);
        tituloLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        JLabel valorLabel = new JLabel(valor);
        valorLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        
        panel.add(tituloLabel, BorderLayout.WEST);
        panel.add(valorLabel, BorderLayout.CENTER);
        return panel;
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

}
