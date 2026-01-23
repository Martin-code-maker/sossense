package sossense.bista;

import java.awt.*;
import javax.swing.*;

import sossense.datubasea.Instalazioa;
import sossense.datubasea.PanelPlano;
import sossense.datubasea.PlanoInfo;
import sossense.datubasea.PlanoInstalacion;
import sossense.kontrolatzailea.SOSsenseKontrolatzailea;
import sossense.utils.UIUtils;
import sossense.datubasea.AppContext;
import sossense.datubasea.PlanoRepository;

public class PlanoDetallePanelBuilder {

    private final SOSsenseKontrolatzailea controller;
    private final Navigator navigator;
    private final AppContext appContext;
    private final PlanoRepository planoRepo;

    public PlanoDetallePanelBuilder(SOSsenseKontrolatzailea controller,
                                    Navigator navigator,
                                    AppContext appContext,
                                    PlanoRepository planoRepo) {
        this.controller = controller;
        this.navigator = navigator;
        this.appContext = appContext;
        this.planoRepo = planoRepo;
    }

    public JPanel build(String izenaInstalacion, String nombrePlano, PlanoInfo planoInfo) {
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

        appContext.setInstalacionActiva(izenaInstalacion);
        appContext.setPlanoActivo(nombrePlano);
        PlanoInstalacion plano = new PlanoInstalacion(planoInfo);

        JPanel headerPanel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
                GradientPaint gradient = new GradientPaint(0, 0, new Color(0xF6, 0xB2, 0xB2), getWidth(), getHeight(), new Color(0xD3, 0x85, 0x7E));
                g2.setPaint(gradient);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 25, 25);
                g2.setColor(new Color(0, 0, 0, 30));
                g2.drawRoundRect(2, 2, getWidth() - 5, getHeight() - 5, 25, 25);
            }
        };
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 20, 25, 20));

        JLabel titulo = new JLabel(izenaInstalacion + " - " + nombrePlano);
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

        PanelPlano panelPlano = new PanelPlano(plano, planoInfo.getImagenFondo());
        appContext.setPanelPlanoActivo(panelPlano);

        JPanel mapaWrapper = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        mapaWrapper.setBackground(new Color(245, 245, 245));
        panelPlano.setBorder(BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E), 2));
        mapaWrapper.add(panelPlano);

        JPanel legendPanel = crearLeyendaPanel();
        JPanel infoPanel = crearInfoPanel(panelPlano);

        JPanel centro = new JPanel(new BorderLayout(15, 0));
        centro.setBackground(new Color(245, 245, 245));
        centro.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        centro.add(legendPanel, BorderLayout.WEST);
        centro.add(mapaWrapper, BorderLayout.CENTER);
        centro.add(infoPanel, BorderLayout.EAST);

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(0xD3, 0x85, 0x7E)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JButton volverBtn = UIUtils.crearBotonEstilizado("ATZERA", new Color(0xE2, 0x80, 0x76), Color.WHITE);
        volverBtn.addActionListener(e -> {
            panelPlano.detenerActualizacion();
            Runnable stopTimer = (Runnable) infoPanel.getClientProperty("stopTimer");
            if (stopTimer != null) stopTimer.run();
            appContext.setPanelPlanoActivo(null);
            appContext.setInstalacionActiva("");
            appContext.setPlanoActivo("");
            navigator.navigateTo(new SeleccionPlanosPanelBuilder(controller, planoRepo, navigator, appContext)
                    .build(izenaInstalacion));
        });

        controlPanel.add(volverBtn);

        mainPanel.add(centro, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private JPanel crearLeyendaPanel() {
        JPanel p = new JPanel();
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBackground(new Color(245, 245, 245));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E), 2),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)));
        JLabel title = new JLabel("LEGENDA");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(title);
        p.add(Box.createVerticalStrut(10));
        p.add(crearChip(Color.GREEN, "Normala (0-29%)"));
        p.add(Box.createVerticalStrut(8));
        p.add(crearChip(Color.ORANGE, "Alerta (30-69%)"));
        p.add(Box.createVerticalStrut(8));
        p.add(crearChip(Color.RED, "Kritikoa (70-100%)"));
        p.add(Box.createVerticalStrut(8));
        JLabel hint = new JLabel("Sakatu sentsorean informazio gehiago lortzeko");
        hint.setFont(new Font("Arial", Font.PLAIN, 12));
        hint.setForeground(new Color(70, 70, 70));
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(hint);
        return p;
    }

    private JPanel crearChip(Color color, String texto) {
        JPanel chip = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 2));
        chip.setOpaque(false);
        JLabel square = new JLabel();
        square.setOpaque(true);
        square.setBackground(color);
        square.setPreferredSize(new Dimension(16, 16));
        square.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        JLabel label = new JLabel(texto);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        chip.add(square);
        chip.add(label);
        return chip;
    }

    private JPanel crearInfoPanel(PanelPlano panelPlano) {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(new Color(245, 245, 245));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E), 2),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JLabel title = new JLabel("Sentsoreen egoera");
        title.setFont(new Font("Arial", Font.BOLD, 14));
        title.setHorizontalAlignment(SwingConstants.CENTER);
        p.add(title, BorderLayout.NORTH);

        DefaultListModel<String> model = new DefaultListModel<>();
        JList<String> list = new JList<>(model);
        list.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane sp = new JScrollPane(list);
        sp.setPreferredSize(new Dimension(220, 420));
        p.add(sp, BorderLayout.CENTER);

        JLabel resumen = new JLabel("-");
        resumen.setFont(new Font("Arial", Font.BOLD, 12));
        resumen.setHorizontalAlignment(SwingConstants.CENTER);
        resumen.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        p.add(resumen, BorderLayout.SOUTH);

        Runnable refrescar = () -> {
            java.util.List<sossense.datubasea.SensorPlano> sensores = panelPlano.getSensoresSnapshot();
            model.clear();
            int alerta = 0; int crit = 0;
            for (sossense.datubasea.SensorPlano s : sensores) {
                if (s.getNivelHumo() >= 70) crit++; else if (s.getNivelHumo() >= 30) alerta++;
                model.addElement(s.getId() + " | " + s.getNivelHumo() + "% | " + s.getUbicacion());
            }
            resumen.setText("Guztira: " + sensores.size() + " | Alerta: " + alerta + " | Kritiko: " + crit);
        };
        refrescar.run();

        // Crear timer para actualizar la lista cada 500ms
        Timer actualizacionTimer = new Timer(500, e -> refrescar.run());
        actualizacionTimer.start();

        p.putClientProperty("refresh", refrescar);
        p.putClientProperty("stopTimer", (Runnable) () -> {
            if (actualizacionTimer != null) {
                actualizacionTimer.stop();
            }
        });
        return p;
    }
}
