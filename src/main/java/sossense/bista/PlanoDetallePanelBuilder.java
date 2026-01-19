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
        appContext.setPanelPlanoActivo(panelPlano);

        JScrollPane scrollPane = new JScrollPane(panelPlano);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E), 3));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 15));
        controlPanel.setBackground(new Color(245, 245, 245));
        controlPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(2, 0, 0, 0, new Color(0xD3, 0x85, 0x7E)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JButton actualizarBtn = UIUtils.crearBotonEstilizado("🔄 EGUNERATU", new Color(0x52, 0xB7, 0x88), Color.WHITE);
        actualizarBtn.addActionListener(e -> panelPlano.repaint());

        JButton volverBtn = UIUtils.crearBotonEstilizado("⬅ ATZERA", new Color(0xE2, 0x80, 0x76), Color.WHITE);
        volverBtn.addActionListener(e -> {
            panelPlano.detenerActualizacion();
            appContext.setPanelPlanoActivo(null);
            appContext.setInstalacionActiva("");
            navigator.navigateTo(new SeleccionPlanosPanelBuilder(controller, planoRepo, navigator, appContext)
                    .build(izenaInstalacion));
        });

        controlPanel.add(actualizarBtn);
        controlPanel.add(volverBtn);

        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(controlPanel, BorderLayout.SOUTH);
        return mainPanel;
    }
}
