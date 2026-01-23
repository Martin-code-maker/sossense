package sossense.bista;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.io.FileWriter;
import java.io.IOException;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;

import javax.swing.*;

import sossense.kontrolatzailea.SOSsenseKontrolatzailea;
import sossense.datubasea.PlanoRepository;
import sossense.datubasea.AppContext;
import sossense.datubasea.PlanoInfo;
import sossense.datubasea.SensorLayout;
import sossense.utils.UIUtils;

public class AgregarInstalacionPanelBuilder {

    private static final int DEFAULT_ANCHO = 800;
    private static final int DEFAULT_ALTO = 600;

    private final SOSsenseKontrolatzailea controller;
    private final Navigator navigator;
    private final PlanoRepository planoRepo;
    private final AppContext appContext;

    public AgregarInstalacionPanelBuilder(SOSsenseKontrolatzailea controller,
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
        mainPanel.setBackground(new Color(250, 250, 250));

        // Título con estilo acorde al proyecto
        JPanel tituloPanel = new JPanel(new BorderLayout());
        tituloPanel.setBackground(new Color(250, 250, 250));
        tituloPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titulo = new JLabel("GEHITU INSTALAZIO BERRIA");
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setForeground(new Color(50, 50, 50));
        titulo.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Línea separadora con color del proyecto
        JSeparator separador = new JSeparator();
        separador.setForeground(new Color(0xE1, 0x9D, 0x8E));
        separador.setBackground(new Color(0xE1, 0x9D, 0x8E));
        
        tituloPanel.add(titulo, BorderLayout.CENTER);
        tituloPanel.add(separador, BorderLayout.SOUTH);
        mainPanel.add(tituloPanel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(12, 15, 12, 15);

        // Datos básicos
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblIzena = new JLabel("Izena:");
        lblIzena.setFont(new Font("Arial", Font.BOLD, 14));
        lblIzena.setForeground(new Color(50, 50, 50));
        formPanel.add(lblIzena, gbc);

        gbc.gridx = 1;
        JTextField izenaField = crearCampoEstilizado(25);
        formPanel.add(izenaField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblHelbidea = new JLabel("Helbidea:");
        lblHelbidea.setFont(new Font("Arial", Font.BOLD, 14));
        lblHelbidea.setForeground(new Color(50, 50, 50));
        formPanel.add(lblHelbidea, gbc);

        gbc.gridx = 1;
        JTextField helbideaField = crearCampoEstilizado(25);
        formPanel.add(helbideaField, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblMota = new JLabel("Mota:");
        lblMota.setFont(new Font("Arial", Font.BOLD, 14));
        lblMota.setForeground(new Color(50, 50, 50));
        formPanel.add(lblMota, gbc);

        gbc.gridx = 1;
        String[] motas = { "FABRIKA", "OSPITALEA", "LABORATEGIA", "ESKOLA", "UNIBERTSITATEA" };
        JComboBox<String> motaCombo = crearComboEstilizado(motas);
        formPanel.add(motaCombo, gbc);

        // Planta eta sentsoreak
        gbc.gridx = 0; gbc.gridy = 3;
        gbc.gridwidth = 2;
        JPanel plantaPanel = new JPanel(new GridBagLayout());
        plantaPanel.setBackground(new Color(248, 248, 248));
        plantaPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(0xE1, 0x9D, 0x8E), 2),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));

        GridBagConstraints pg = new GridBagConstraints();
        pg.insets = new Insets(8, 8, 8, 8);
        pg.fill = GridBagConstraints.HORIZONTAL;

        pg.gridx = 0; pg.gridy = 0;
        JLabel lblPlantaIzena = new JLabel("Solairuaren izena:");
        lblPlantaIzena.setFont(new Font("Arial", Font.BOLD, 13));
        lblPlantaIzena.setForeground(new Color(50, 50, 50));
        plantaPanel.add(lblPlantaIzena, pg);

        pg.gridx = 1;
        JTextField plantaIzenaField = crearCampoEstilizado(18);
        plantaPanel.add(plantaIzenaField, pg);

        pg.gridx = 0; pg.gridy = 1;
        JLabel mapaLabel = new JLabel("Sentsoreak mapan kokatu:");
        mapaLabel.setFont(new Font("Arial", Font.BOLD, 13));
        mapaLabel.setForeground(new Color(50, 50, 50));
        plantaPanel.add(mapaLabel, pg);

        pg.gridx = 1;
        JLabel mapaHint = new JLabel("Sentsorea (borobila) mugitu mapan kokapena finkatzeko");
        mapaHint.setFont(new Font("Arial", Font.ITALIC, 11));
        mapaHint.setForeground(new Color(120, 120, 120));
        plantaPanel.add(mapaHint, pg);

        pg.gridx = 0; pg.gridy = 2; pg.gridwidth = 2;
        MapaEditorPanel mapaPanel = new MapaEditorPanel(DEFAULT_ANCHO, DEFAULT_ALTO);
        mapaPanel.setBackgroundImage(lortuIrudiaMotarenArabera((String) motaCombo.getSelectedItem()));
        motaCombo.addActionListener(e -> mapaPanel.setBackgroundImage(lortuIrudiaMotarenArabera((String) motaCombo.getSelectedItem())));
        plantaPanel.add(mapaPanel, pg);

        pg.gridwidth = 1;
        pg.gridx = 0; pg.gridy = 3;
        JLabel lblSensorId = new JLabel("Sentsorearen ID:");
        lblSensorId.setFont(new Font("Arial", Font.BOLD, 13));
        lblSensorId.setForeground(new Color(50, 50, 50));
        plantaPanel.add(lblSensorId, pg);

        pg.gridx = 1;
        JTextField sensorIdField = crearCampoEstilizado(12);
        plantaPanel.add(sensorIdField, pg);

        pg.gridx = 0; pg.gridy = 4;
        JLabel lblSensorUbic = new JLabel("Deskribapena (aukerakoa):");
        lblSensorUbic.setFont(new Font("Arial", Font.BOLD, 13));
        lblSensorUbic.setForeground(new Color(50, 50, 50));
        plantaPanel.add(lblSensorUbic, pg);

        pg.gridx = 1;
        JTextField sensorUbicField = crearCampoEstilizado(18);
        plantaPanel.add(sensorUbicField, pg);

        JLabel sensorCountLabel = new JLabel("0 sentsore mapan");
        sensorCountLabel.setFont(new Font("Arial", Font.BOLD, 12));
        sensorCountLabel.setForeground(new Color(0xE1, 0x9D, 0x8E));

        pg.gridx = 0; pg.gridy = 5;
        JButton gehituSensorBtn = UIUtils.crearBotonEstilizado("MAPAN GEHITU", new Color(0xE1, 0x9D, 0x8E), Color.WHITE);
        gehituSensorBtn.setPreferredSize(new Dimension(170, 32));
        gehituSensorBtn.addActionListener(e -> {
            String id = sensorIdField.getText().trim();
            String ubic = sensorUbicField.getText().trim();
            String plantaIzena = plantaIzenaField.getText().trim();
            if (plantaIzena.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Idatzi solairuaren izena lehenik.", "Abisua", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (id.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Idatzi sentsorearen IDa.", "Abisua", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (mapaPanel.existeSensor(id)) {
                JOptionPane.showMessageDialog(mainPanel, "ID hori dagoeneko mapan dago.", "Abisua", JOptionPane.WARNING_MESSAGE);
                return;
            }
            String kokapena = ubic.isEmpty() ? plantaIzena : plantaIzena + " - " + ubic;
            mapaPanel.addSensor(id, kokapena);
            sensorIdField.setText("");
            sensorUbicField.setText("");
            sensorCountLabel.setText(mapaPanel.getSensorCount() + " sentsore mapan");
        });
        plantaPanel.add(gehituSensorBtn, pg);

        pg.gridx = 1;
        plantaPanel.add(sensorCountLabel, pg);

        DefaultListModel<String> plantasModel = new DefaultListModel<>();
        JList<String> plantasList = new JList<>(plantasModel);
        plantasList.setVisibleRowCount(5);
        plantasList.setFont(new Font("Arial", Font.PLAIN, 12));
        JScrollPane plantasScroll = new JScrollPane(plantasList);
        plantasScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        plantasScroll.setPreferredSize(new Dimension(360, 100));

        List<PlanoInfo> planoDefinituak = new ArrayList<>();
        JLabel resumenLabel = new JLabel("0 solairu, 0 sentsore");
        resumenLabel.setFont(new Font("Arial", Font.BOLD, 12));
        resumenLabel.setForeground(new Color(0xE1, 0x9D, 0x8E));

        pg.gridx = 0; pg.gridy = 6;
        pg.gridwidth = 2;
        JButton gehituPlantaBtn = UIUtils.crearBotonEstilizado("SOLAIRUA GEHITU", new Color(0xE1, 0x9D, 0x8E), Color.WHITE);
        gehituPlantaBtn.setPreferredSize(new Dimension(180, 35));
        gehituPlantaBtn.addActionListener(e -> {
            String izenaPlanta = plantaIzenaField.getText().trim();
            if (izenaPlanta.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Idatzi solairuaren izena.", "Abisua", JOptionPane.WARNING_MESSAGE);
                return;
            }

            List<SensorLayout> sentsoreakPlanta = mapaPanel.toSensorLayouts(izenaPlanta);
            if (sentsoreakPlanta.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel, "Gutxienez sentsore bat kokatu behar da mapan.", "Abisua", JOptionPane.WARNING_MESSAGE);
                return;
            }

            PlanoInfo planoInfo = new PlanoInfo("", izenaPlanta, "", DEFAULT_ANCHO, DEFAULT_ALTO,
                sentsoreakPlanta.size(), sentsoreakPlanta.size(), sentsoreakPlanta);
            planoDefinituak.add(planoInfo);
            plantasModel.addElement(izenaPlanta + " (" + sentsoreakPlanta.size() + " sentsore)");

            plantaIzenaField.setText("");
            mapaPanel.clearSensors();
            sensorCountLabel.setText("0 sentsore mapan");

            int totalSens = planoDefinituak.stream().mapToInt(p -> p.getSensoresDefinidos().size()).sum();
            resumenLabel.setText(planoDefinituak.size() + " solairu, " + totalSens + " sentsore");
        });
        plantaPanel.add(gehituPlantaBtn, pg);

        pg.gridx = 0; pg.gridy = 7; pg.gridwidth = 2;
        plantaPanel.add(plantasScroll, pg);

        pg.gridy = 8;
        plantaPanel.add(resumenLabel, pg);

        formPanel.add(plantaPanel, gbc);

        // Resumen final de sentsoreak kalkulatzeko eremu irakurgarria
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        JLabel lblSentsoreak = new JLabel("Sentsore kopurua :");
        lblSentsoreak.setFont(new Font("Arial", Font.BOLD, 14));
        lblSentsoreak.setForeground(new Color(50, 50, 50));
        formPanel.add(lblSentsoreak, gbc);

        gbc.gridx = 1;
        JTextField sensoresField = crearCampoEstilizado(25);
        sensoresField.setEditable(false);
        sensoresField.setBackground(new Color(240, 240, 240));
        sensoresField.setToolTipText("Zenbaketa automatikoki egiten da sartutako sentsoreen arabera");
        formPanel.add(sensoresField, gbc);

        JPanel scrollFormPanel = new JPanel(new BorderLayout());
        scrollFormPanel.setBackground(new Color(250, 250, 250));
        scrollFormPanel.add(formPanel, BorderLayout.NORTH);
        scrollFormPanel.add(Box.createVerticalGlue(), BorderLayout.CENTER);

        JScrollPane scrollPane = new JScrollPane(scrollFormPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        buttonPanel.setBackground(new Color(250, 250, 250));
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JButton agregarBtn = UIUtils.crearBotonEstilizado("GEHITU", new Color(0xE1, 0x9D, 0x8E), Color.WHITE);
        agregarBtn.setPreferredSize(new Dimension(160, 45));
        agregarBtn.addActionListener(e -> {
            String izena = izenaField.getText().trim();
            String helbidea = helbideaField.getText().trim();
            String mota = (String) motaCombo.getSelectedItem();

            if (izena.isEmpty() || helbidea.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Mesedez, bete izena eta helbidea.",
                        "Errorea",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (planoDefinituak.isEmpty()) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Gutxienez solairu bat definitu behar da sentsoreekin.",
                        "Errorea",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int totalSentsoreak = planoDefinituak.stream().mapToInt(p -> p.getSensoresDefinidos().size()).sum();
            sensoresField.setText(String.valueOf(totalSentsoreak));

            try {
                controller.gehituInstalazioa(izena, totalSentsoreak, "", helbidea, mota);
                // Establecer imagen de plano por tipo antes de persistir
                String imagenTipo = lortuIrudiaMotarenArabera(mota);
                List<PlanoInfo> eguneratuak = new ArrayList<>();
                for (PlanoInfo pi : planoDefinituak) {
                    eguneratuak.add(new PlanoInfo(izena, pi.getNombrePlano(), imagenTipo,
                            pi.getAncho(), pi.getAlto(), pi.getSensoresMin(), pi.getSensoresMax(),
                            pi.getSensoresDefinidos()));
                }

                planoRepo.guardarPlanos(izena, eguneratuak);
                guardarSensoresTxt(izena, eguneratuak);

                JOptionPane.showMessageDialog(mainPanel, "Instalazioa eta planoak gorde dira!", "Arrakasta", JOptionPane.INFORMATION_MESSAGE);
                
                // Limpiar el formulario en lugar de redirigir
                izenaField.setText("");
                helbideaField.setText("");
                motaCombo.setSelectedIndex(0);
                sensoresField.setText("");
                plantasModel.clear();
                planoDefinituak.clear();
                plantaIzenaField.setText("");
                mapaPanel.clearSensors();
                sensorCountLabel.setText("0 sentsore mapan");
                resumenLabel.setText("0 solairu, 0 sentsore");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Ezin izan da instalazioa gorde: " + ex.getMessage(),
                        "Errorea",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelarBtn = UIUtils.crearBotonEstilizado("UTZI", new Color(180, 180, 180), new Color(50, 50, 50));
        cancelarBtn.setPreferredSize(new Dimension(160, 45));
        cancelarBtn.addActionListener(e -> navigator.navigateTo(new InstalacionesPanelBuilder(controller, navigator, planoRepo, appContext).build()));

        buttonPanel.add(agregarBtn);
        buttonPanel.add(cancelarBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private JTextField crearCampoEstilizado(int columnas) {
        JTextField campo = new JTextField(columnas);
        campo.setFont(new Font("Arial", Font.PLAIN, 13));
        campo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        return campo;
    }

    private JComboBox<String> crearComboEstilizado(String[] items) {
        JComboBox<String> combo = new JComboBox<>(items);
        combo.setFont(new Font("Arial", Font.PLAIN, 13));
        return combo;
    }

    private void guardarSensoresTxt(String instalacion, List<PlanoInfo> planos) {
        if (planos == null || planos.isEmpty()) {
            return;
        }
        java.nio.file.Path ruta = java.nio.file.Paths.get("datos/sensores.txt");
        boolean necesitaSaltoInicial = false;
        try {
            if (java.nio.file.Files.exists(ruta) && java.nio.file.Files.size(ruta) > 0) {
                // Si el fichero no termina en salto, añadimos uno antes de escribir
                byte[] ult = java.nio.file.Files.readAllBytes(ruta);
                if (ult.length > 0) {
                    byte last = ult[ult.length - 1];
                    necesitaSaltoInicial = last != '\n' && last != '\r';
                }
            }
        } catch (IOException e) {
            // Si falla la lectura no interrumpimos el guardado
        }

        try (FileWriter fw = new FileWriter(ruta.toFile(), true)) {
            if (necesitaSaltoInicial) {
                fw.write(System.lineSeparator());
            }
            for (PlanoInfo plano : planos) {
                for (SensorLayout sensor : plano.getSensoresDefinidos()) {
                    fw.write(instalacion + "|" + plano.getNombrePlano() + "|" + sensor.getId() + "|" + sensor.getX() + "|" + sensor.getY() + "|" + sensor.getUbicacion());
                    fw.write(System.lineSeparator());
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null,
                    "Ezin izan da sensores.txt eguneratu: " + e.getMessage(),
                    "Errorea",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private static class SensorDrag {
        private final String id;
        private final String ubicacion;
        private Point posizioa;

        SensorDrag(String id, String ubicacion, Point posizioa) {
            this.id = id;
            this.ubicacion = ubicacion;
            this.posizioa = posizioa;
        }
    }

    private static class MapaEditorPanel extends JPanel {
        private static final int RADIO = 12;
        private final int ancho;
        private final int alto;
        private final List<SensorDrag> sensores = new ArrayList<>();
        private Image fondo;
        private SensorDrag arrastrando;
        private Point offset = new Point();

        MapaEditorPanel(int ancho, int alto) {
            this.ancho = ancho;
            this.alto = alto;
            setPreferredSize(new Dimension(ancho, alto));
            setBackground(Color.WHITE);

            addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                    arrastrando = sensorEn(e.getX(), e.getY());
                    if (arrastrando != null) {
                        offset = new Point(e.getX() - arrastrando.posizioa.x, e.getY() - arrastrando.posizioa.y);
                    }
                }

                @Override
                public void mouseReleased(MouseEvent e) {
                    arrastrando = null;
                }
            });

            addMouseMotionListener(new MouseMotionAdapter() {
                @Override
                public void mouseDragged(MouseEvent e) {
                    if (arrastrando != null) {
                        Point berria = new Point(e.getX() - offset.x, e.getY() - offset.y);
                        arrastrando.posizioa = mugatu(berria);
                        repaint();
                    }
                }
            });
        }

        void setBackgroundImage(String imageName) {
            if (imageName == null || imageName.isEmpty()) {
                fondo = null;
                repaint();
                return;
            }
            try {
                java.net.URL imgUrl = getClass().getResource("/sossense/img/" + imageName);
                fondo = imgUrl != null ? new ImageIcon(imgUrl).getImage() : null;
            } catch (Exception e) {
                fondo = null;
            }
            repaint();
        }

        void addSensor(String id, String ubicacion) {
            int px = getWidth() > 0 ? getWidth() / 2 : ancho / 2;
            int py = getHeight() > 0 ? getHeight() / 2 : alto / 2;
            sensores.add(new SensorDrag(id, ubicacion, new Point(px, py)));
            repaint();
        }

        boolean existeSensor(String id) {
            return sensores.stream().anyMatch(s -> s.id.equalsIgnoreCase(id));
        }

        int getSensorCount() {
            return sensores.size();
        }

        void clearSensors() {
            sensores.clear();
            repaint();
        }

        List<SensorLayout> toSensorLayouts(String plantaIzena) {
            List<SensorLayout> layouts = new ArrayList<>();
            for (SensorDrag sd : sensores) {
                String kokapena = sd.ubicacion;
                if (!kokapena.toLowerCase().contains(plantaIzena.toLowerCase())) {
                    kokapena = plantaIzena + " - " + kokapena;
                }
                layouts.add(new SensorLayout(sd.id, sd.posizioa.x, sd.posizioa.y, kokapena));
            }
            return layouts;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (fondo != null) {
                g2.drawImage(fondo, 0, 0, getWidth(), getHeight(), this);
            } else {
                g2.setColor(new Color(240, 240, 240));
                g2.fillRect(0, 0, getWidth(), getHeight());
            }

            g2.setColor(Color.BLACK);
            g2.setStroke(new BasicStroke(2));
            g2.drawRect(1, 1, getWidth() - 3, getHeight() - 3);

            for (SensorDrag sd : sensores) {
                g2.setColor(new Color(0x52, 0xB7, 0x88));
                g2.fillOval(sd.posizioa.x - RADIO, sd.posizioa.y - RADIO, RADIO * 2, RADIO * 2);
                g2.setColor(Color.BLACK);
                g2.drawOval(sd.posizioa.x - RADIO, sd.posizioa.y - RADIO, RADIO * 2, RADIO * 2);
                g2.setFont(new Font("Arial", Font.BOLD, 10));
                FontMetrics fm = g2.getFontMetrics();
                int w = fm.stringWidth(sd.id);
                g2.drawString(sd.id, sd.posizioa.x - w / 2, sd.posizioa.y - RADIO - 4);
            }
        }

        private SensorDrag sensorEn(int x, int y) {
            for (SensorDrag sd : sensores) {
                int dx = x - sd.posizioa.x;
                int dy = y - sd.posizioa.y;
                if ((dx * dx) + (dy * dy) <= (RADIO + 2) * (RADIO + 2)) {
                    return sd;
                }
            }
            return null;
        }

        private Point mugatu(Point p) {
            int m = RADIO + 4;
            int nx = Math.max(m, Math.min(getWidth() - m, p.x));
            int ny = Math.max(m, Math.min(getHeight() - m, p.y));
            return new Point(nx, ny);
        }
    }

    private String lortuIrudiaMotarenArabera(String mota) {
        if (mota == null) return "plano_default.png";
        switch (mota.toUpperCase()) {
            case "OSPITALEA":
                return "plano_ospitalea.png";
            case "UNIBERTSITATEA":
                return "plano_unibertsitatea.png";
            case "ESKOLA":
                return "plano_eskola.png";
            case "FABRIKA":
                return "plano_fabrika.png";
            case "LABORATEGIA":
                return "plano_laborategia.png";
            default:
                return "plano_default.png";
        }
    }
}
