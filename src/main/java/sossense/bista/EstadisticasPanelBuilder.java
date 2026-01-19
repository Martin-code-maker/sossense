package sossense.bista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableModel;

import sossense.datubasea.Instalazioa;
import sossense.kontrolatzailea.SOSsenseKontrolatzailea;
import sossense.mqtt.Mqtt;

public class EstadisticasPanelBuilder {
    private final SOSsenseKontrolatzailea controller;
    private final Mqtt mqtt;

    public EstadisticasPanelBuilder(SOSsenseKontrolatzailea controller, Mqtt mqtt) {
        this.controller = controller;
        this.mqtt = mqtt;
    }

    public JPanel build() {
        List<Instalazioa> instalazioak = controller.lortuInstalazioak();
        int totalInstalaciones = instalazioak.size();
        int totalSensores = instalazioak.stream().mapToInt(Instalazioa::getSentsoreak).sum();
        double mediaSensores = totalInstalaciones == 0 ? 0 : (double) totalSensores / totalInstalaciones;

        Map<String, Long> porTipo = instalazioak.stream()
                .collect(Collectors.groupingBy(inst -> normalize(inst.getMota(), "Ezezaguna"), Collectors.counting()));

        Map<String, Long> porEstado = instalazioak.stream()
                .collect(Collectors.groupingBy(inst -> normalize(inst.getEgoera(), "Egoera ezezaguna"), Collectors.counting()));

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("ESTATISTIKAK", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        mainPanel.add(titulo, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JPanel resumenPanel = new JPanel(new GridLayout(1, 3, 12, 0));
        resumenPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        resumenPanel.add(createStatCard("Instalazio kopurua", String.valueOf(totalInstalaciones), new Color(0xD3, 0x85, 0x7E)));
        resumenPanel.add(createStatCard("Sentsoreak guztira", String.valueOf(totalSensores), new Color(0xF6, 0xB2, 0xB2)));
        resumenPanel.add(createStatCard("Sentsoreak/inst.", new DecimalFormat("0.0").format(mediaSensores), new Color(0xD6, 0x92, 0x92)));
        content.add(resumenPanel);

        content.add(Box.createVerticalStrut(16));

        JPanel tablasPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        JPanel motaPanel = new JPanel();
        motaPanel.setLayout(new BoxLayout(motaPanel, BoxLayout.Y_AXIS));
        motaPanel.add(wrapTable("Mota bakoitzeko", buildTableFromMap(porTipo)));
        motaPanel.add(Box.createVerticalStrut(10));
        motaPanel.add(createBarChart("Mota grafikoa", porTipo));
        tablasPanel.add(motaPanel);

        JPanel egoeraPanel = new JPanel();
        egoeraPanel.setLayout(new BoxLayout(egoeraPanel, BoxLayout.Y_AXIS));
        egoeraPanel.add(wrapTable("Egoeraren arabera", buildTableFromMap(porEstado)));
        egoeraPanel.add(Box.createVerticalStrut(10));
        egoeraPanel.add(createRealtimeAveragePanel());
        tablasPanel.add(egoeraPanel);
        content.add(tablasPanel);

        JScrollPane scrollPane = new JScrollPane(content);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        return mainPanel;
    }

    private JPanel createStatCard(String titulo, String valor, Color baseColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(baseColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(baseColor.darker(), 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(Color.BLACK);

        JLabel valueLabel = new JLabel(valor);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        valueLabel.setForeground(Color.BLACK);

        card.add(titleLabel, BorderLayout.WEST);
        card.add(valueLabel, BorderLayout.EAST);
        return card;
    }

    private JTable buildTableFromMap(Map<String, Long> data) {
        DefaultTableModel model = new DefaultTableModel(new Object[] { "Elementua", "Kopurua" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        data.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .forEach(entry -> model.addRow(new Object[] { entry.getKey(), entry.getValue() }));

        JTable table = new JTable(model);
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        table.setFont(new Font("Arial", Font.PLAIN, 13));
        return table;
    }

    private JPanel createBarChart(String title, Map<String, Long> data) {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E)), title));

        JPanel canvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (data.isEmpty()) {
                    g.drawString("Daturik ez", 10, 20);
                    return;
                }

                int width = getWidth();
                int height = getHeight();
                int padding = 24;
                int barWidth = Math.max(20, (width - padding * 2) / Math.max(data.size(), 1) - 8);
                long maxValue = data.values().stream().mapToLong(Long::longValue).max().orElse(1);

                int index = 0;
                for (Map.Entry<String, Long> entry : data.entrySet()) {
                    int x = padding + index * (barWidth + 8);
                    int barHeight = (int) ((height - padding * 2) * (entry.getValue() / (double) maxValue));
                    int y = height - padding - barHeight;

                    g.setColor(new Color(0xD6, 0x92, 0x92));
                    g.fillRect(x, y, barWidth, barHeight);
                    g.setColor(Color.BLACK);
                    g.drawRect(x, y, barWidth, barHeight);

                    String label = entry.getKey();
                    g.drawString(label, x, height - padding + 14);
                    String value = String.valueOf(entry.getValue());
                    g.drawString(value, x + barWidth / 2 - g.getFontMetrics().stringWidth(value) / 2, y - 4);
                    index++;
                }
            }
        };
        canvas.setPreferredSize(new Dimension(0, 220));
        chartPanel.add(canvas, BorderLayout.CENTER);
        return chartPanel;
    }

    private JPanel createRealtimeAveragePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E)),
                "MQTT batazbestekoa"));

        JLabel valueLabel = new JLabel("—", SwingConstants.CENTER);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 22));
        valueLabel.setForeground(Color.BLACK);
        panel.add(valueLabel, BorderLayout.CENTER);

        if (mqtt != null) {
            DecimalFormat df = new DecimalFormat("0.00");
            mqtt.addPropertyChangeListener(evt -> {
                if ("DATO_GAS_ACTUALIZADO".equals(evt.getPropertyName())) {
                    double valor = 0.0;
                    Object nv = evt.getNewValue();
                    if (nv instanceof Number) {
                        valor = ((Number) nv).doubleValue();
                    } else {
                        try {
                            valor = Double.parseDouble(String.valueOf(nv));
                        } catch (Exception ignored) {
                            return;
                        }
                    }
                    final double v = valor;
                    SwingUtilities.invokeLater(() -> valueLabel.setText(df.format(v)));
                }
            });
        } else {
            valueLabel.setText("MQTT ez dago eskuragarri");
        }

        return panel;
    }

    private JPanel wrapTable(String title, JTable table) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E)), title));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }
}
