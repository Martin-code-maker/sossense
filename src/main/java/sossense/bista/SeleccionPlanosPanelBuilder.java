package sossense.bista;

import java.awt.*;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import javax.swing.*;

import sossense.datubasea.Instalazioa;
import sossense.datubasea.PlanoInfo;
import sossense.datubasea.PlanoInstalacion;
import sossense.datubasea.PlanoRepository;
import sossense.kontrolatzailea.SOSsenseKontrolatzailea;
import sossense.utils.UIUtils;
import sossense.datubasea.AppContext;

public class SeleccionPlanosPanelBuilder {

    private final SOSsenseKontrolatzailea controller;
    private final PlanoRepository planoRepo;
    private final Navigator navigator;
    private final AppContext appContext;
    private final Map<String, PlanoInstalacion> planosCache = new HashMap<>();
    private final Map<String, JPanel> tarjetasCache = new HashMap<>();
    private Timer actualizacionTimer;

    public SeleccionPlanosPanelBuilder(SOSsenseKontrolatzailea controller,
                                       PlanoRepository planoRepo,
                                       Navigator navigator,
                                       AppContext appContext) {
        this.controller = controller;
        this.planoRepo = planoRepo;
        this.navigator = navigator;
        this.appContext = appContext;
    }

    public JPanel build(String nombreInstalacion) {
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

        List<PlanoInfo> planosInstalacion = planoRepo.cargarPlanosDeInstalacion(nombreInstalacion);
        if (planosInstalacion.isEmpty()) {
            JLabel errorLabel = new JLabel("Ez dago planorik instalazio honetarako: " + nombreInstalacion);
            errorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            mainPanel.add(errorLabel, BorderLayout.CENTER);
            return mainPanel;
        }

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

        JLabel titulo = new JLabel(nombreInstalacion);
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

        int numPlanos = planosInstalacion.size();
        int columnas = Math.min(2, numPlanos);
        int filas = (int) Math.ceil(numPlanos / 2.0);
        JPanel planosPanel = new JPanel(new GridLayout(filas, columnas, 25, 25));
        planosPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        planosPanel.setBackground(new Color(245, 245, 245));

        // Crear y cachear instancias de PlanoInstalacion
        tarjetasCache.clear();
        for (PlanoInfo planoInfo : planosInstalacion) {
            String claveCache = nombreInstalacion + "_" + planoInfo.getNombrePlano();
            
            // Usar instancia en caché o crear nueva
            PlanoInstalacion planoTemp = planosCache.computeIfAbsent(claveCache, k -> new PlanoInstalacion(planoInfo));
            
                JPanel tarjeta = crearTarjetaPlano(planoInfo.getNombrePlano(),
                    instalacion, nombreInstalacion, planoInfo, planoTemp, claveCache);
            tarjetasCache.put(claveCache, tarjeta);
            planosPanel.add(tarjeta);
        }

        JScrollPane scrollPane = new JScrollPane(planosPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setBackground(new Color(245, 245, 245));
        scrollPane.getViewport().setBackground(new Color(245, 245, 245));
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 20));
        bottomPanel.setBackground(new Color(245, 245, 245));
        JButton volverBtn = UIUtils.crearBotonEstilizado("ITZULI", new Color(0xE2, 0x80, 0x76), Color.WHITE);
        volverBtn.addActionListener(e -> {
            detenerActualizacion();
            navigator.navigateTo(new InstalacionesPanelBuilder(controller, navigator, planoRepo, appContext).build());
        });
        bottomPanel.add(volverBtn);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Iniciar temporizador de actualización
        iniciarActualizacion(planosPanel);

        return mainPanel;
    }

    private void iniciarActualizacion(JPanel planosPanel) {
        detenerActualizacion();
        
        actualizacionTimer = new Timer(500, e -> {
            for (Map.Entry<String, JPanel> entry : tarjetasCache.entrySet()) {
                entry.getValue().repaint();
            }
        });
        actualizacionTimer.start();
    }

    private void detenerActualizacion() {
        if (actualizacionTimer != null) {
            actualizacionTimer.stop();
            actualizacionTimer = null;
        }
    }

    private String lortuPlanoMotarenArabera(String mota) {
        if (mota == null) return null;
        switch (mota.toUpperCase()) {
            case "OSPITALEA":
            case "HOSPITAL": return "/sossense/img/plano_hospital.png";
            case "UNIBERTSITATEA":
            case "UNIVERSIDAD": return "/sossense/img/plano_universidad.png";
            case "IKASTOLA":
            case "ESCOLA":
            case "ESCUELA": return "/sossense/img/plano_escuela.png";
            case "FABRIKA":
            case "FABRICA": return "/sossense/img/plano_fabrica.png";
            case "LABORATORIO": return "/sossense/img/plano_laboratorio.png";
            default: System.out.println("⚠ Mota ezezaguna: " + mota); return null;
        }
    }

    private JPanel crearTarjetaPlano(String nombrePlano,
                                     Instalazioa instalacion, String nombreInstalacion,
                                     PlanoInfo planoInfo, PlanoInstalacion planoTemp, String claveCache) {
        final boolean[] hover = { false };
        final Image imagenPlano;
        Image temp = null;
        try {
            String ruta = lortuPlanoMotarenArabera(instalacion.getMota());
            if (ruta != null) {
                java.net.URL url = getClass().getResource(ruta);
                if (url != null) temp = new ImageIcon(url).getImage();
                else System.out.println("No se encontró: " + ruta);
            }
        } catch (Exception e) { System.out.println("Error cargando plano"); }
        imagenPlano = temp;

        Color verdeBase = Color.decode("#10c531");
        Color rojoBase = new Color(220, 30, 30);

        JPanel planoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Obtener estado actual desde AppContext (si hay datos en tiempo real)
                int sensoresCriticos = appContext.contarCriticos(nombreInstalacion, planoInfo.getNombrePlano());
                int sensoresAlerta = appContext.contarAlertas(nombreInstalacion, planoInfo.getNombrePlano());
                boolean enCritico = sensoresCriticos > 0;
                boolean enAlerta = sensoresAlerta > 0 || enCritico;
                Color colorReal = enCritico ? rojoBase : (enAlerta ? new Color(255,140,0) : verdeBase);
                
                int w = getWidth(); int h = getHeight();
                g2.setColor(hover[0] ? new Color(0, 0, 0, 60) : new Color(0, 0, 0, 30));
                g2.fillRoundRect(4, 4, w - 4, h - 4, 25, 25);
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
                g2.setColor(colorReal);
                g2.fillRoundRect(0, 0, w - 6, 60, 25, 25);
                g2.fillRect(0, 40, w - 6, 20);
                if (enCritico) {
                    g2.setColor(new Color(255, 0, 0, 70));
                    g2.fillRoundRect(0, 0, w - 6, h - 6, 25, 25);
                    g2.setFont(new Font("Arial", Font.BOLD, 22));
                    g2.setColor(Color.WHITE);
                    g2.drawString("ALERTA", 15, 35);
                }
                if (hover[0]) {
                    g2.setColor(new Color(255, 255, 255, 40));
                    g2.fillRoundRect(0, 0, w - 6, h - 6, 25, 25);
                }
                g2.setColor(enCritico ? Color.RED : (hover[0] ? colorReal : new Color(220, 220, 220)));
                g2.setStroke(new BasicStroke(enCritico ? 4 : (hover[0] ? 3 : 2)));
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
        
        // Actualizar color del nombre dinámicamente
        planoPanel.addPropertyChangeListener("paintComponent", evt -> {
            int sensoresCriticos = appContext.contarCriticos(nombreInstalacion, planoInfo.getNombrePlano());
            int sensoresAlerta = appContext.contarAlertas(nombreInstalacion, planoInfo.getNombrePlano());
            boolean enCritico = sensoresCriticos > 0;
            boolean enAlerta = sensoresAlerta > 0 || enCritico;
            if (enCritico) nombreLabel.setForeground(Color.RED.darker());
            else if (enAlerta) nombreLabel.setForeground(new Color(255,140,0));
            else nombreLabel.setForeground(new Color(50, 50, 50));
        });
        
        centerPanel.add(nombreLabel);
        planoPanel.add(centerPanel, BorderLayout.NORTH);

        planoPanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        planoPanel.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                navigator.navigateTo(new PlanoDetallePanelBuilder(controller, navigator, appContext, planoRepo)
                        .build(nombreInstalacion, nombrePlano, planoInfo));
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) { hover[0] = true; planoPanel.repaint(); }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) { hover[0] = false; planoPanel.repaint(); }
        });
        return planoPanel;
    }
}
