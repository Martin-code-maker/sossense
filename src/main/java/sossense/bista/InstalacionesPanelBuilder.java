package sossense.bista;

import java.awt.*;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import javax.swing.Timer;

import javax.swing.*;

import sossense.datubasea.Instalazioa;
import sossense.datubasea.PlanoInfo;
import sossense.datubasea.PlanoInstalacion;
import sossense.kontrolatzailea.SOSsenseKontrolatzailea;
import sossense.datubasea.PlanoRepository;
import sossense.datubasea.AppContext;

public class InstalacionesPanelBuilder {

    private final SOSsenseKontrolatzailea controller;
    private final Navigator navigator;
    private final PlanoRepository planoRepo;
    private final AppContext appContext;
    private Timer actualizacionTimer;
    private final Map<String, PlanoInstalacion> planosCache = new HashMap<>();

    public InstalacionesPanelBuilder(SOSsenseKontrolatzailea controller,
                                     Navigator navigator,
                                     PlanoRepository planoRepo,
                                     AppContext appContext) {
        this.controller = controller;
        this.navigator = navigator;
        this.planoRepo = planoRepo;
        this.appContext = appContext;
    }

    public JPanel build() {
        JPanel mainPanel = new JPanel(new BorderLayout());

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));
        searchPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblBuscar = new JLabel("Bilatu:");
        lblBuscar.setFont(new Font("Arial", Font.BOLD, 14));

        JTextField searchField = new JTextField(25);
        searchField.setFont(new Font("Arial", Font.PLAIN, 14));

        JButton buscarBtn = sossense.utils.UIUtils.crearBotonEstilizado("BILATU", new Color(0x52, 0xB7, 0x88), Color.WHITE);
        buscarBtn.setPreferredSize(new Dimension(150, 35));

        searchPanel.add(lblBuscar);
        searchPanel.add(searchField);
        searchPanel.add(buscarBtn);

        mainPanel.add(searchPanel, BorderLayout.NORTH);

        JPanel instalacionesPanel = new JPanel();
        instalacionesPanel.setLayout(new BoxLayout(instalacionesPanel, BoxLayout.Y_AXIS));
        instalacionesPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        Map<String, JLabel> egoeraLabels = new HashMap<>();
        Map<String, JPanel> panelesTarjetas = new HashMap<>();

        int[] paginaActual = {0};
        int itemsPorPagina = 5;
        List<Instalazioa> todasInstalaciones = controller.lortuInstalazioak();

        JPanel navegacionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        navegacionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton anteriorBtn = sossense.utils.UIUtils.crearBotonEstilizado("AURREKOA", new Color(0xE1, 0x9D, 0x8E), Color.WHITE);
        anteriorBtn.setPreferredSize(new Dimension(160, 40));

        JLabel paginaLabel = new JLabel();
        paginaLabel.setFont(new Font("Arial", Font.BOLD, 14));

        JButton siguienteBtn = sossense.utils.UIUtils.crearBotonEstilizado("HURRENGOA", new Color(0xE1, 0x9D, 0x8E), Color.WHITE);
        siguienteBtn.setPreferredSize(new Dimension(180, 40));

        navegacionPanel.add(anteriorBtn);
        navegacionPanel.add(paginaLabel);
        navegacionPanel.add(siguienteBtn);

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

        Runnable actualizarPagina = () -> {
            int totalPages = (int) Math.ceil((double) todasInstalaciones.size() / itemsPorPagina);
            if (paginaActual[0] < 0) paginaActual[0] = 0;
            if (paginaActual[0] >= totalPages && totalPages > 0) paginaActual[0] = totalPages - 1;
            int inicio = paginaActual[0] * itemsPorPagina;
            int fin = Math.min(inicio + itemsPorPagina, todasInstalaciones.size());
            List<Instalazioa> instalaziakPagina = todasInstalaciones.subList(inicio, fin);
            bistaratuInstalazioak(instalacionesPanel, instalaziakPagina, egoeraLabels, panelesTarjetas);
            paginaLabel.setText("Orria " + (paginaActual[0] + 1) + " / " + Math.max(1, totalPages));
            anteriorBtn.setEnabled(paginaActual[0] > 0);
            siguienteBtn.setEnabled(paginaActual[0] < totalPages - 1);
            scrollPane.getVerticalScrollBar().setValue(0);
        };

        anteriorBtn.addActionListener(e -> { paginaActual[0]--; actualizarPagina.run(); });
        siguienteBtn.addActionListener(e -> { paginaActual[0]++; actualizarPagina.run(); });

        java.awt.event.ActionListener buscarAction = e -> {
            String filtro = searchField.getText();
            List<Instalazioa> filtradas = controller.bilatuInstalazioak(filtro);
            todasInstalaciones.clear();
            todasInstalaciones.addAll(filtradas);
            paginaActual[0] = 0;
            actualizarPagina.run();
        };
        buscarBtn.addActionListener(buscarAction);
        searchField.addActionListener(buscarAction);

        actualizarPagina.run();
        
        // Iniciar temporizador de actualización de estado
        iniciarActualizacionEstado(egoeraLabels, panelesTarjetas);

        mainPanel.addHierarchyListener(new HierarchyListener() {
            @Override
            public void hierarchyChanged(HierarchyEvent e) {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    if (!mainPanel.isShowing()) {
                        detenerActualizacionEstado();
                    } else {
                        iniciarActualizacionEstado(egoeraLabels, panelesTarjetas);
                    }
                }
            }
        });
        return mainPanel;
    }

    private void iniciarActualizacionEstado(Map<String, JLabel> egoeraLabels, Map<String, JPanel> panelesTarjetas) {
        detenerActualizacionEstado();
        
        actualizacionTimer = new Timer(500, e -> {
            eguneratuEgoeraLabels(egoeraLabels);
            eguneratuTarjetas(panelesTarjetas);
        });
        actualizacionTimer.start();
    }

    private void detenerActualizacionEstado() {
        if (actualizacionTimer != null) {
            actualizacionTimer.stop();
            actualizacionTimer = null;
        }
    }

    private void bistaratuInstalazioak(JPanel instalacionesPanel, List<Instalazioa> instalazioak, Map<String, JLabel> egoeraLabels, Map<String, JPanel> panelesTarjetas) {
        instalacionesPanel.removeAll();
        panelesTarjetas.clear();
        for (Instalazioa inst : instalazioak) {
            JPanel panelInstalacion = crearPanelInstalacion(inst, egoeraLabels);
            panelesTarjetas.put(inst.getIzena(), panelInstalacion);
            instalacionesPanel.add(panelInstalacion);
            instalacionesPanel.add(Box.createVerticalStrut(10));
        }
        instalacionesPanel.revalidate();
        instalacionesPanel.repaint();
    }

    private JPanel crearPanelInstalacion(Instalazioa inst, Map<String, JLabel> egoeraLabels) {
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
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 250));
        panel.setPreferredSize(new Dimension(800, 250));
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = 0; gbc.gridy = 0; gbc.gridheight = 4; gbc.weightx = 0.0; gbc.anchor = GridBagConstraints.WEST; gbc.insets = new Insets(0, 0, 0, 30);
        ImageIcon iconOriginal = null;
        try {
            java.net.URL imgUrl = getClass().getResource(lortuIrudiaMotarenArabera(inst.getMota()));
            if (imgUrl != null) iconOriginal = new ImageIcon(imgUrl);
        } catch (Exception e) { e.printStackTrace(); }
        JLabel imageLabel; int tamanoImagen = 200;
        if (iconOriginal != null) {
            Image imgEscalada = iconOriginal.getImage().getScaledInstance(tamanoImagen, tamanoImagen, Image.SCALE_SMOOTH);
            imageLabel = new JLabel(new ImageIcon(imgEscalada));
        } else {
            imageLabel = new JLabel("Ez dago irudirik");
            imageLabel.setPreferredSize(new Dimension(tamanoImagen, tamanoImagen));
        }
        panel.add(imageLabel, gbc);

        gbc.gridx = 1; gbc.gridy = 0; gbc.gridheight = 1; gbc.weightx = 1.0; gbc.anchor = GridBagConstraints.NORTHWEST; gbc.insets = new Insets(5, 0, 15, 0);
        JLabel izenaLabel = new JLabel(inst.getIzena());
        izenaLabel.setFont(new Font("Arial", Font.BOLD, 28));
        panel.add(izenaLabel, gbc);

        gbc.gridy = 1; gbc.insets = new Insets(0, 0, 10, 0);
        JLabel sensoresLabel = new JLabel("Sentsore kopurua: " + inst.getSentsoreak());
        sensoresLabel.setFont(new Font("Arial", Font.BOLD, 22));
        panel.add(sensoresLabel, gbc);

        gbc.gridy = 2; gbc.insets = new Insets(5, 0, 0, 0);
        JPanel panelEstado = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panelEstado.setOpaque(false);
        JLabel lblEgoeraTitulo = new JLabel("Egoera: ");
        lblEgoeraTitulo.setFont(new Font("Arial", Font.BOLD, 24));
        JLabel lblEgoeraValor = new JLabel(inst.getEgoera());
        lblEgoeraValor.setFont(new Font("Arial", Font.BOLD, 30));
        lblEgoeraValor.setForeground(inst.getKolorEgoera());
        panelEstado.add(lblEgoeraTitulo);
        panelEstado.add(lblEgoeraValor);
        egoeraLabels.put(inst.getIzena(), lblEgoeraValor);
        panel.add(panelEstado, gbc);

        gbc.gridy = 3; gbc.weighty = 1.0; gbc.anchor = GridBagConstraints.SOUTHEAST; gbc.insets = new Insets(15, 0, 0, 0);
        JLabel helbideaLabel = new JLabel(inst.getHelbidea());
        helbideaLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        helbideaLabel.setForeground(Color.DARK_GRAY);
        panel.add(helbideaLabel, gbc);

        panel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        panel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigator.navigateTo(new SeleccionPlanosPanelBuilder(controller, planoRepo, navigator, appContext)
                        .build(inst.getIzena()));
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                panel.setBackground(new Color(Math.max(0, inst.getKolorFondo().getRed() - 20),
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

    private void eguneratuEgoeraLabels(Map<String, JLabel> egoeraLabels) {
        List<Instalazioa> instalazioak = controller.lortuInstalazioak();
        for (Instalazioa inst : instalazioak) {
            JLabel lbl = egoeraLabels.get(inst.getIzena());
            if (lbl != null) {
                int maila = kalkulatuInstalazioMaila(inst.getIzena());

                if (maila == 2) { // kritikoa
                    inst.setEgoera("LARRIA");
                    lbl.setText("LARRIA");
                    lbl.setForeground(Color.RED);
                } else if (maila == 1) { // alerta 30-69
                    inst.setEgoera("ALERTA");
                    lbl.setText("ALERTA");
                    lbl.setForeground(new Color(255, 140, 0)); // naranja
                } else {
                    inst.setEgoera("OK");
                    lbl.setText("OK");
                    lbl.setForeground(new Color(16, 197, 49)); // verde
                }
            }
        }
    }

    private void eguneratuTarjetas(Map<String, JPanel> panelesTarjetas) {
        for (JPanel panel : panelesTarjetas.values()) {
            panel.repaint();
        }
    }

    // 0 = OK, 1 = ALERTA (>=30), 2 = LARRIA (>=70)
    private int kalkulatuInstalazioMaila(String nombreInstalacion) {
        // Usar lecturas en tiempo real almacenadas en AppContext
        java.util.Map<String, Integer> valores = appContext.getSensorValoresSnapshot();
        int maila = 0;
        String prefix = nombreInstalacion.toLowerCase() + "|";
        for (java.util.Map.Entry<String, Integer> e : valores.entrySet()) {
            if (!e.getKey().toLowerCase().startsWith(prefix)) continue;
            int v = e.getValue();
            if (v >= 70) return 2; // crítico encontrado
            if (v >= 30) maila = Math.max(maila, 1);
        }
        return maila;
    }

    private String lortuIrudiaMotarenArabera(String mota) {
        if (mota == null) return "/sossense/img/icon_default.png";
        switch (mota.toUpperCase()) {
            case "OSPITALEA": return "/sossense/img/icon_ospitalea.png";
            case "UNIBERTSITATEA": return "/sossense/img/icon_unibertsitatea.png";
            case "FABRIKA": return "/sossense/img/icon_fabrika.png";
            case "ESKOLA": return "/sossense/img/icon_eskola.png";
            case "LABORATEGIA": return "/sossense/img/icon_laborategia.png";
            default: return "/sossense/img/icon_default.png";
        }
    }
}
