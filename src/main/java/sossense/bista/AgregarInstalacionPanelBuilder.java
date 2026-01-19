package sossense.bista;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.border.TitledBorder;

import sossense.kontrolatzailea.SOSsenseKontrolatzailea;
import sossense.datubasea.PlanoRepository;
import sossense.datubasea.AppContext;
import sossense.datubasea.PlanoInfo;
import sossense.datubasea.SensorLayout;

public class AgregarInstalacionPanelBuilder {

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

        // Datos básicos
        gbc.gridx = 0; gbc.gridy = 0;
        JLabel lblIzena = new JLabel("Izena:");
        lblIzena.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblIzena, gbc);

        gbc.gridx = 1;
        JTextField izenaField = new JTextField(25);
        izenaField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(izenaField, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        JLabel lblEgoera = new JLabel("Egoera:");
        lblEgoera.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblEgoera, gbc);

        gbc.gridx = 1;
        String[] egoeras = { "NORMALA", "LARRIA", "ARINGARRI", "KALTEA", "MANTENIMIENTO" };
        JComboBox<String> egoeraCombo = new JComboBox<>(egoeras);
        egoeraCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(egoeraCombo, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        JLabel lblHelbidea = new JLabel("Helbidea:");
        lblHelbidea.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblHelbidea, gbc);

        gbc.gridx = 1;
        JTextField helbideaField = new JTextField(25);
        helbideaField.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(helbideaField, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        JLabel lblMota = new JLabel("Mota:");
        lblMota.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblMota, gbc);

        gbc.gridx = 1;
        String[] motas = { "HOSPITAL", "UNIVERSIDAD", "ESCOLA", "FABRICA", "LABORATORIO", "OFICINA", "ALMACEN" };
        JComboBox<String> motaCombo = new JComboBox<>(motas);
        motaCombo.setFont(new Font("Arial", Font.PLAIN, 14));
        formPanel.add(motaCombo, gbc);

        // Planta eta sentsoreak
        gbc.gridx = 0; gbc.gridy = 4;
        gbc.gridwidth = 2;
        JPanel plantaPanel = new JPanel(new GridBagLayout());
        plantaPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY),
                "Planten konfigurazioa", TitledBorder.LEFT, TitledBorder.TOP, new Font("Arial", Font.BOLD, 14)));

        GridBagConstraints pg = new GridBagConstraints();
        pg.insets = new Insets(6, 6, 6, 6);
        pg.fill = GridBagConstraints.HORIZONTAL;

        pg.gridx = 0; pg.gridy = 0;
        JLabel lblPlantaIzena = new JLabel("Planta izena:");
        lblPlantaIzena.setFont(new Font("Arial", Font.BOLD, 13));
        plantaPanel.add(lblPlantaIzena, pg);

        pg.gridx = 1;
        JTextField plantaIzenaField = new JTextField(18);
        plantaIzenaField.setFont(new Font("Arial", Font.PLAIN, 13));
        plantaPanel.add(plantaIzenaField, pg);

        pg.gridx = 0; pg.gridy = 1;
        JLabel lblTamaina = new JLabel("Tamaina (zabal/altu):");
        lblTamaina.setFont(new Font("Arial", Font.BOLD, 13));
        plantaPanel.add(lblTamaina, pg);

        pg.gridx = 1;
        JPanel sizePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        JTextField anchoField = new JTextField("800", 6);
        JTextField altoField = new JTextField("600", 6);
        sizePanel.add(anchoField);
        sizePanel.add(new JLabel("x"));
        sizePanel.add(altoField);
        plantaPanel.add(sizePanel, pg);

        pg.gridx = 0; pg.gridy = 2;
        JLabel lblSensores = new JLabel("Sentsoreak (id,x,y,kokapena):");
        lblSensores.setFont(new Font("Arial", Font.BOLD, 13));
        plantaPanel.add(lblSensores, pg);

        pg.gridx = 1;
        JTextArea sensoresArea = new JTextArea(4, 25);
        sensoresArea.setLineWrap(true);
        sensoresArea.setWrapStyleWord(true);
        JScrollPane sensoresScroll = new JScrollPane(sensoresArea);
        sensoresScroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        plantaPanel.add(sensoresScroll, pg);

        pg.gridx = 1; pg.gridy = 3;
        JLabel hintLabel = new JLabel("Adibidea: S1,120,80,Pasilo nagusia; S2,300,210,Bulegoak");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        hintLabel.setForeground(Color.DARK_GRAY);
        plantaPanel.add(hintLabel, pg);

        DefaultListModel<String> plantasModel = new DefaultListModel<>();
        JList<String> plantasList = new JList<>(plantasModel);
        plantasList.setVisibleRowCount(5);
        JScrollPane plantasScroll = new JScrollPane(plantasList);
        plantasScroll.setPreferredSize(new Dimension(360, 100));

        List<PlanoInfo> planoDefinituak = new ArrayList<>();
        JLabel resumenLabel = new JLabel("0 planta, 0 sentsore");
        resumenLabel.setFont(new Font("Arial", Font.BOLD, 12));

        pg.gridx = 0; pg.gridy = 4;
        pg.gridwidth = 2;
        JButton gehituPlantaBtn = new JButton("Planta gehitu");
        gehituPlantaBtn.setFont(new Font("Arial", Font.BOLD, 13));
        gehituPlantaBtn.addActionListener(e -> {
            try {
                String izenaPlanta = plantaIzenaField.getText().trim();
                if (izenaPlanta.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Idatzi planta izena.", "Abisua", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                int ancho = Integer.parseInt(anchoField.getText().trim());
                int alto = Integer.parseInt(altoField.getText().trim());
                List<SensorLayout> sentsoreakPlanta = parsearSensores(sensoresArea.getText().trim(), izenaPlanta, mainPanel);
                if (sentsoreakPlanta.isEmpty()) {
                    JOptionPane.showMessageDialog(mainPanel, "Gutxienez sentsore bat gehitu behar da.", "Abisua", JOptionPane.WARNING_MESSAGE);
                    return;
                }

                PlanoInfo planoInfo = new PlanoInfo("", izenaPlanta, "", ancho, alto,
                        sentsoreakPlanta.size(), sentsoreakPlanta.size(), sentsoreakPlanta);
                planoDefinituak.add(planoInfo);
                plantasModel.addElement(izenaPlanta + " (" + sentsoreakPlanta.size() + " sentsore)");

                plantaIzenaField.setText("");
                sensoresArea.setText("");

                int totalSens = planoDefinituak.stream().mapToInt(p -> p.getSensoresDefinidos().size()).sum();
                resumenLabel.setText(planoDefinituak.size() + " planta, " + totalSens + " sentsore");
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(mainPanel, "Ancho/Alto balio osoak izan behar dira.", "Errorea", JOptionPane.ERROR_MESSAGE);
            }
        });
        plantaPanel.add(gehituPlantaBtn, pg);

        pg.gridx = 0; pg.gridy = 5; pg.gridwidth = 2;
        plantaPanel.add(plantasScroll, pg);

        pg.gridy = 6;
        plantaPanel.add(resumenLabel, pg);

        formPanel.add(plantaPanel, gbc);

        // Resumen final de sentsoreak kalkulatzeko eremu irakurgarria
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        JLabel lblSentsoreak = new JLabel("Sentsore kopurua (autom.):");
        lblSentsoreak.setFont(new Font("Arial", Font.BOLD, 16));
        formPanel.add(lblSentsoreak, gbc);

        gbc.gridx = 1;
        JTextField sensoresField = new JTextField(25);
        sensoresField.setFont(new Font("Arial", Font.PLAIN, 14));
        sensoresField.setEditable(false);
        sensoresField.setToolTipText("Kontua automatikoki kalkulatzen da sartutako sentsoreen arabera");
        formPanel.add(sensoresField, gbc);

        mainPanel.add(formPanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        JButton agregarBtn = new JButton("GEHITU");
        agregarBtn.setFont(new Font("Arial", Font.BOLD, 16));
        agregarBtn.setPreferredSize(new Dimension(150, 40));
        agregarBtn.addActionListener(e -> {
            String izena = izenaField.getText().trim();
            String egoera = (String) egoeraCombo.getSelectedItem();
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
                        "Gutxienez planta bat definitu behar da sentsoreekin.",
                        "Errorea",
                        JOptionPane.ERROR_MESSAGE);
                return;
            }

            int totalSentsoreak = planoDefinituak.stream().mapToInt(p -> p.getSensoresDefinidos().size()).sum();
            sensoresField.setText(String.valueOf(totalSentsoreak));

            try {
                controller.gehituInstalazioa(izena, totalSentsoreak, egoera, helbidea, mota);
                // Establecer imagen de plano por tipo antes de persistir
                String imagenTipo = lortuIrudiaMotarenArabera(mota);
                List<PlanoInfo> eguneratuak = new ArrayList<>();
                for (PlanoInfo pi : planoDefinituak) {
                    eguneratuak.add(new PlanoInfo(izena, pi.getNombrePlano(), imagenTipo,
                            pi.getAncho(), pi.getAlto(), pi.getSensoresMin(), pi.getSensoresMax(),
                            pi.getSensoresDefinidos()));
                }

                planoRepo.guardarPlanos(izena, eguneratuak);

                JOptionPane.showMessageDialog(mainPanel, "Instalazioa eta planoak gorde dira!", "Arrakasta", JOptionPane.INFORMATION_MESSAGE);
                navigator.navigateTo(new InstalacionesPanelBuilder(controller, navigator, planoRepo, appContext).build());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(mainPanel,
                        "Ezin izan da instalazioa gorde: " + ex.getMessage(),
                        "Errorea",
                        JOptionPane.ERROR_MESSAGE);
            }
        });

        JButton cancelarBtn = new JButton("UTZI");
        cancelarBtn.setFont(new Font("Arial", Font.BOLD, 16));
        cancelarBtn.setPreferredSize(new Dimension(150, 40));
        cancelarBtn.addActionListener(e -> navigator.navigateTo(new InstalacionesPanelBuilder(controller, navigator, planoRepo, appContext).build()));

        buttonPanel.add(agregarBtn);
        buttonPanel.add(cancelarBtn);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        return mainPanel;
    }

    private List<SensorLayout> parsearSensores(String raw, String nombrePlanta, Component parent) {
        List<SensorLayout> sensores = new ArrayList<>();
        if (raw == null || raw.trim().isEmpty()) {
            return sensores;
        }

        String[] items = raw.split(";");
        for (String item : items) {
            String limpio = item.trim();
            if (limpio.isEmpty()) {
                continue;
            }
            String[] partes = limpio.split(",", 4);
            if (partes.length < 4) {
                JOptionPane.showMessageDialog(parent,
                        "Sentsorearen formatua: id,x,y,kokapena",
                        "Errorea",
                        JOptionPane.ERROR_MESSAGE);
                return new ArrayList<>();
            }
            try {
                String id = partes[0].trim();
                int x = Integer.parseInt(partes[1].trim());
                int y = Integer.parseInt(partes[2].trim());
                String ubicacion = partes[3].trim();
                if (!ubicacion.toLowerCase().contains(nombrePlanta.toLowerCase())) {
                    ubicacion = nombrePlanta + " - " + ubicacion;
                }
                sensores.add(new SensorLayout(id, x, y, ubicacion));
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(parent,
                        "X eta Y balio osoak izan behar dira.",
                        "Errorea",
                        JOptionPane.ERROR_MESSAGE);
                return new ArrayList<>();
            }
        }
        return sensores;
    }

    private String lortuIrudiaMotarenArabera(String mota) {
        if (mota == null) return "plano_default.png";
        switch (mota.toUpperCase()) {
            case "HOSPITAL":
            case "OSPITALEA":
                return "plano_hospital.png";
            case "UNIVERSIDAD":
            case "UNIBERTSITATEA":
                return "plano_universidad.png";
            case "ESCOLA":
            case "ESCUELA":
            case "IKASTOLA":
                return "plano_escuela.png";
            case "FABRICA":
            case "FABRIKA":
                return "plano_fabrica.png";
            case "LABORATORIO":
                return "plano_laboratorio.png";
            case "OFICINA":
            case "ALMACEN":
                return "plano_default.png";
            default:
                return "plano_default.png";
        }
    }
}
