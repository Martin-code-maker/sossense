package sossense.datubasea;

import javax.swing.*;

import sossense.mqtt.Mqtt;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.ArrayList;

public class SOSsenseApp {

    private KudeatuInstalazioak gestion;
    JFrame frame = new JFrame("S.O.S.sense");
    private JPanel menuPanel;
    private boolean menuExpanded = true;
    private final int menuExpandedWidth = 180;
    private final int menuCollapsedWidth = 18;
    private List<JButton> menuButtons = new ArrayList<>();
    
    public SOSsenseApp() {
        gestion = new KudeatuInstalazioak();
        crearInterfaz();
        // MQTT erabili gabe funtzionatzeko, zati hau komentatu
        /*try {
            Mqtt mqtt = new Mqtt();
        } catch (Exception ex) {
            ex.printStackTrace();
        }*/
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

        ImageIcon rawIcon = new ImageIcon(getClass().getResource("/sossense/img/image-removebg-preview.png"));
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
        
        // Botón de cerrar a la derecha
        JButton closeButton = new JButton("Irten");
        closeButton.setFont(new Font("Arial", Font.BOLD, 12));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBackground(Color.RED);
        closeButton.setBorderPainted(false);
        closeButton.setFocusPainted(false);
        closeButton.setPreferredSize(new Dimension(70, 50));
        
        // Efecto hover para el botón de cerrar
        closeButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(new Color(200, 0, 0));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                closeButton.setBackground(Color.RED);
            }
        });
        
        // Acción para cerrar la aplicación
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int confirm = JOptionPane.showConfirmDialog(
                    frame,
                    "Aplikazioa itxi nahi duzu?",
                    "Amaitu",
                    JOptionPane.YES_NO_OPTION
                );
                if (confirm == JOptionPane.YES_OPTION) {
                    System.exit(0);
                }
            }
        });
        
        // Panel para el botón
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(0xF6, 0xB2, 0xB2));
        buttonPanel.add(closeButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);

        // ==================== PANEL DEL MENÚ LATERAL ====================
        JPanel menuPanel = crearMenuPanel(frame);

        // ==================== PANEL PRINCIPAL ====================
        JPanel centerPanel = crearPanelInstalaciones();

        // ==================== PANEL INFERIOR ====================
        JPanel bottomPanel = crearPanelInferior(frame);

        // ==================== AÑADIR TODO AL FRAME ====================
        frame.add(topPanel, BorderLayout.NORTH);
        frame.add(menuPanel, BorderLayout.WEST);
        frame.add(centerPanel, BorderLayout.CENTER);
        frame.add(bottomPanel, BorderLayout.SOUTH);

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
        String[] itemsMenu = {"INSTALACION GUZTIAK", "PLANOAK", "GEHITU BERRIA", "ESTATISTIKAK", "BILATU", "KONTAKTUA"};
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
                        mostrarTodasInstalaciones(frame);
                    });
                    break;
                case "PLANOAK":  // NUEVO BOTÓN
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        mostrarPlanos(frame);
                    });
                    break;
                case "GEHITU BERRIA":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        agregarNuevaInstalacion(frame);
                    });
                    break;
                case "ESTATISTIKAK":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        mostrarEstadisticas(frame);
                    });
                    break;
                case "BILATU":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        buscarInstalacion(frame);
                    });
                    break;
                case "KONTAKTUA":
                    menuButton.addActionListener(e -> {
                        selectButton(menuButton);
                        mostrarContacto(frame);
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
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        
        // Panel con scroll para las instalaciones
        JPanel instalacionesPanel = new JPanel();
        instalacionesPanel.setLayout(new BoxLayout(instalacionesPanel, BoxLayout.Y_AXIS));
        
        // Obtener todas las instalaciones
        List<Instalazioa> instalaciones = gestion.getInstalaciones();
        
        for (Instalazioa inst : instalaciones) {
            instalacionesPanel.add(crearPanelInstalacion(inst));
            instalacionesPanel.add(Box.createVerticalStrut(10));
        }
        
        JScrollPane scrollPane = new JScrollPane(instalacionesPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel crearPanelInstalacion(Instalazioa inst) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(inst.getKolorFondo());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(inst.getKolorea(), 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        // Panel superior con izena y mota
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);
        
        JLabel izenaLabel = new JLabel(inst.getIzena());
        izenaLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        JLabel motaLabel = new JLabel("[" + inst.getMota() + "]");
        motaLabel.setFont(new Font("Arial", Font.ITALIC, 14));
        motaLabel.setForeground(Color.DARK_GRAY);
        
        topPanel.add(izenaLabel, BorderLayout.WEST);
        topPanel.add(motaLabel, BorderLayout.EAST);
        
        // Panel central con información
        JPanel centerPanel = new JPanel(new GridLayout(2, 1, 5, 5));
        centerPanel.setOpaque(false);
        
        JLabel sensoresLabel = new JLabel("Sentsore kopurua: " + inst.getSentsoreak(), SwingConstants.CENTER);
        sensoresLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        JLabel egoeraLabel = new JLabel("Egoera: " + inst.getEgoera(), SwingConstants.CENTER);
        egoeraLabel.setFont(new Font("Arial", Font.BOLD, 20));
        egoeraLabel.setForeground(inst.getKolorEgoera());
        
        centerPanel.add(sensoresLabel);
        centerPanel.add(egoeraLabel);
        
        // Panel inferior con dirección
        JPanel bottomPanel = new JPanel();
        bottomPanel.setOpaque(false);
        JLabel helbideaLabel = new JLabel(inst.getHelbidea());
        helbideaLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        helbideaLabel.setForeground(Color.DARK_GRAY);
        bottomPanel.add(helbideaLabel);
        
        // Añadir todo al panel principal
        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(centerPanel, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel crearPanelInferior(JFrame frame) {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.LIGHT_GRAY);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Información de resumen
        JLabel resumenLabel = new JLabel(
            "Instalazioak: " + gestion.getTotalInstalaciones() + 
            " | Sentsore guztira: " + gestion.getTotalSensores()
        );
        resumenLabel.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Botón de actualizar
        JButton actualizarButton = new JButton("EGUNERATU");
        actualizarButton.addActionListener(e -> {
            frame.dispose();
            new SOSsenseApp(); // Reiniciar la aplicación para actualizar
        });
        
        bottomPanel.add(resumenLabel, BorderLayout.WEST);
        bottomPanel.add(actualizarButton, BorderLayout.EAST);
        
        return bottomPanel;
    }
    
    // Métodos para las acciones del menú
    
    private void mostrarTodasInstalaciones(JFrame frame) {
        StringBuilder sb = new StringBuilder();
        sb.append("=== INSTALAZIO GUZTIAK ===\n\n");
        
        List<Instalazioa> instalaciones = gestion.getInstalaciones();
        for (int i = 0; i < instalaciones.size(); i++) {
            Instalazioa inst = instalaciones.get(i);
            sb.append(i + 1).append(". ").append(inst.toString()).append("\n");
        }
        
        JTextArea textArea = new JTextArea(sb.toString());
        textArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        JOptionPane.showMessageDialog(frame, scrollPane, "Instalazio Guztiak", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void mostrarPlanos(JFrame frame) {
        // Crear diálogo para seleccionar instalación
        List<Instalazioa> instalaciones = gestion.getInstalaciones();
        String[] opciones = new String[instalaciones.size()];
        
        for (int i = 0; i < instalaciones.size(); i++) {
            opciones[i] = instalaciones.get(i).getIzena();
        }
        
        String seleccion = (String) JOptionPane.showInputDialog(
            frame,
            "Aukeratu instalazio bat planoak ikusteko:",
            "PLANOAK - Instalazioen zerrenda",
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        
        if (seleccion != null) {
            // Crear plano para la instalación seleccionada
            PlanoInstalacion plano = new PlanoInstalacion(seleccion);
            
            // Crear ventana para mostrar el plano
            JDialog planoDialog = new JDialog(frame, "Plano: " + seleccion, true);
            planoDialog.setLayout(new BorderLayout());
            planoDialog.setSize(900, 700);
            planoDialog.setLocationRelativeTo(frame);
            
            // Crear panel del plano
            PanelPlano panelPlano = new PanelPlano(plano);
            JScrollPane scrollPane = new JScrollPane(panelPlano);
            planoDialog.add(scrollPane, BorderLayout.CENTER);
            
            // Panel de controles inferior
            JPanel controlPanel = new JPanel(new FlowLayout());
            
            JButton actualizarBtn = new JButton("EGUNERATU DATUAK");
            actualizarBtn.addActionListener(e -> {
                plano.simularNivelesHumo();
                panelPlano.repaint();
            });
            
            JButton cerrarBtn = new JButton("ITXI");
            cerrarBtn.addActionListener(e -> {
                panelPlano.detenerActualizacion();
                planoDialog.dispose();
            });
            
            controlPanel.add(actualizarBtn);
            controlPanel.add(cerrarBtn);
            
            // Panel de información
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            JLabel infoLabel = new JLabel(
                "<html><div style='text-align: center;'>" +
                "<b>INSTRUKZIOAK:</b><br>" +
                "1. Klikatu sentsore baten gainean informazioa ikusteko<br>" +
                "2. Sentsoreak kolorez aldatzen dira humo mailaren arabera<br>" +
                "3. Datuak automatikoki eguneratzen dira 3 segundoro</div></html>"
            );
            
            JLabel statsLabel = new JLabel(
                "Sentsoreak: " + plano.getTotalSentsoreak() + 
                " | Alerta egoeran: " + plano.getSentsoreakAlerta() + 
                " | Larriak: " + plano.getSentsoreakCriticos()
            );
            statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
            statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            
            infoPanel.add(infoLabel);
            infoPanel.add(statsLabel);
            
            // Añadir todo al diálogo
            planoDialog.add(infoPanel, BorderLayout.NORTH);
            planoDialog.add(controlPanel, BorderLayout.SOUTH);
            
            planoDialog.setVisible(true);
        }
    }
    
    private void agregarNuevaInstalacion(JFrame frame) {
        // Diálogo para añadir nueva instalación
        JDialog addDialog = new JDialog(frame, "Gehitu Instalazio Berria", true);
        addDialog.setLayout(new BorderLayout());
        addDialog.setSize(500, 400);
        addDialog.setLocationRelativeTo(frame);
        
        // Panel principal con GridBagLayout
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Campos del formulario
        gbc.gridx = 0;
        gbc.gridy = 0;
        mainPanel.add(new JLabel("Izena:"), gbc);
        
        gbc.gridx = 1;
        JTextField izenaField = new JTextField(20);
        mainPanel.add(izenaField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        mainPanel.add(new JLabel("Sentsore kopurua:"), gbc);
        
        gbc.gridx = 1;
        JTextField sensoresField = new JTextField(10);
        mainPanel.add(sensoresField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        mainPanel.add(new JLabel("Egoera:"), gbc);
        
        gbc.gridx = 1;
        String[] egoeras = {"NORMALA", "LARRIA", "ARINGARRI", "KALTEA", "MANTENIMIENTO"};
        JComboBox<String> egoeraCombo = new JComboBox<>(egoeras);
        mainPanel.add(egoeraCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 3;
        mainPanel.add(new JLabel("Helbidea:"), gbc);
        
        gbc.gridx = 1;
        JTextField helbideaField = new JTextField(20);
        mainPanel.add(helbideaField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 4;
        mainPanel.add(new JLabel("Mota:"), gbc);
        
        gbc.gridx = 1;
        String[] motas = {"HOSPITAL", "UNIVERSIDAD", "ESCOLA", "FABRICA", "LABORATORIO", "OFICINA", "ALMACEN"};
        JComboBox<String> motaCombo = new JComboBox<>(motas);
        mainPanel.add(motaCombo, gbc);
        
        // Panel de botones
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton agregarBtn = new JButton("GEHITU");
        JButton cancelarBtn = new JButton("UTZI");
        
        agregarBtn.addActionListener(e -> {
            try {
                String izena = izenaField.getText();
                int sentsoreak = Integer.parseInt(sensoresField.getText());
                String egoera = (String) egoeraCombo.getSelectedItem();
                String helbidea = helbideaField.getText();
                String mota = (String) motaCombo.getSelectedItem();
                
                if (izena.isEmpty() || helbidea.isEmpty()) {
                    JOptionPane.showMessageDialog(addDialog, 
                        "Mesedez, bete izena eta helbidea.", 
                        "Errorea", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Crear y agregar la nueva instalación
                Instalazioa nuevaInst = new Instalazioa(izena, sentsoreak, egoera, helbidea, mota);
                gestion.agregarInstalacion(nuevaInst);
                
                JOptionPane.showMessageDialog(addDialog, 
                    "Instalazioa ondo gehitu da!", 
                    "Arrakasta", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                addDialog.dispose();
                frame.dispose();
                new SOSsenseApp(); // Recargar la aplicación
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(addDialog, 
                    "Sartu zenbaki balioduna sentsore kopururako.", 
                    "Errorea", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        
        cancelarBtn.addActionListener(e -> addDialog.dispose());
        
        buttonPanel.add(agregarBtn);
        buttonPanel.add(cancelarBtn);
        
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(buttonPanel, gbc);
        
        addDialog.add(mainPanel, BorderLayout.CENTER);
        addDialog.setVisible(true);
    }
    
    private void mostrarEstadisticas(JFrame frame) {
        JOptionPane.showMessageDialog(frame, 
            gestion.getEstadisticas(), 
            "Estatistikak", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void buscarInstalacion(JFrame frame) {
        String izena = JOptionPane.showInputDialog(frame, 
            "Sartu instalazioaren izena:", 
            "Bilatu", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (izena != null && !izena.trim().isEmpty()) {
            Instalazioa inst = gestion.buscarInstalacion(izena);
            if (inst != null) {
                // Mostrar diálogo con más información
                JDialog infoDialog = new JDialog(frame, "Instalazioa Aurkituta", true);
                infoDialog.setLayout(new BorderLayout());
                infoDialog.setSize(400, 300);
                infoDialog.setLocationRelativeTo(frame);
                
                JPanel infoPanel = new JPanel(new GridLayout(6, 1, 10, 10));
                infoPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
                
                infoPanel.add(crearEtiquetaInformacion("Izena:", inst.getIzena()));
                infoPanel.add(crearEtiquetaInformacion("Mota:", inst.getMota()));
                infoPanel.add(crearEtiquetaInformacion("Sentsoreak:", String.valueOf(inst.getSentsoreak())));
                infoPanel.add(crearEtiquetaInformacion("Egoera:", inst.getEgoera()));
                infoPanel.add(crearEtiquetaInformacion("Helbidea:", inst.getHelbidea()));
                
                // Botón para ver plano
                JButton verPlanoBtn = new JButton("IKUSI PLANOA");
                verPlanoBtn.addActionListener(e -> {
                    infoDialog.dispose();
                    mostrarPlanoEspecifico(frame, inst.getIzena());
                });
                infoPanel.add(verPlanoBtn);
                
                JButton cerrarBtn = new JButton("ITXI");
                cerrarBtn.addActionListener(e -> infoDialog.dispose());
                
                infoDialog.add(infoPanel, BorderLayout.CENTER);
                infoDialog.add(cerrarBtn, BorderLayout.SOUTH);
                infoDialog.setVisible(true);
                
            } else {
                JOptionPane.showMessageDialog(frame, 
                    "Ez da instalaziorik aurkitu '" + izena + "' izenarekin.", 
                    "Ez Aurkituta", 
                    JOptionPane.WARNING_MESSAGE);
            }
        }
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
    
    private void mostrarPlanoEspecifico(JFrame frame, String izenaInstalacion) {
        // Crear plano para la instalación específica
        PlanoInstalacion plano = new PlanoInstalacion(izenaInstalacion);
        
        // Crear ventana para mostrar el plano
        JDialog planoDialog = new JDialog(frame, "Plano: " + izenaInstalacion, true);
        planoDialog.setLayout(new BorderLayout());
        planoDialog.setSize(900, 700);
        planoDialog.setLocationRelativeTo(frame);
        
        // Crear panel del plano
        PanelPlano panelPlano = new PanelPlano(plano);
        JScrollPane scrollPane = new JScrollPane(panelPlano);
        planoDialog.add(scrollPane, BorderLayout.CENTER);
        
        // Panel de controles inferior
        JPanel controlPanel = new JPanel(new FlowLayout());
        
        JButton actualizarBtn = new JButton("EGUNERATU DATUAK");
        actualizarBtn.addActionListener(e -> {
            plano.simularNivelesHumo();
            panelPlano.repaint();
        });
        
        JButton cerrarBtn = new JButton("ITXI");
        cerrarBtn.addActionListener(e -> {
            panelPlano.detenerActualizacion();
            planoDialog.dispose();
        });
        
        controlPanel.add(actualizarBtn);
        controlPanel.add(cerrarBtn);
        
        // Panel de información
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JLabel infoLabel = new JLabel(
            "<html><div style='text-align: center;'>" +
            "<b>" + izenaInstalacion + "</b><br>" +
            "Haz clic en los sentsoreak para ver información detallada</div></html>"
        );
        
        JLabel statsLabel = new JLabel(
            "Total sentsoreak: " + plano.getTotalSentsoreak() + 
            " | En alerta: " + plano.getSentsoreakAlerta() + 
            " | Críticos: " + plano.getSentsoreakCriticos()
        );
        statsLabel.setFont(new Font("Arial", Font.BOLD, 14));
        statsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        infoPanel.add(infoLabel);
        infoPanel.add(statsLabel);
        
        // Añadir todo al diálogo
        planoDialog.add(infoPanel, BorderLayout.NORTH);
        planoDialog.add(controlPanel, BorderLayout.SOUTH);
        
        planoDialog.setVisible(true);
    }
    
    private void mostrarContacto(JFrame frame) {
        JOptionPane.showMessageDialog(frame,
            "S.O.S.sense - Monitorizazio Sistema\n\n" +
            "Kontaktua:\n" +
            "Telefonoa: +34 943 123 456\n" +
            "Email: info@sossense.eus\n" +
            "Web: www.sossense.eus\n\n" +
            "Helbidea: Nafarros Himbidea 16\n20500 Arrasate, Gipuzkoa",
            "Kontaktua",
            JOptionPane.INFORMATION_MESSAGE);
    }
    

}
