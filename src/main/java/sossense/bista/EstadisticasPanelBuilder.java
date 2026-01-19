package sossense.bista;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.util.LinkedHashMap;
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
import sossense.modelo.SOSsenseModeloa;

public class EstadisticasPanelBuilder {
    private final SOSsenseKontrolatzailea controller;
    private JLabel totalInstLabel;
    private JLabel totalSensLabel;
    private DefaultTableModel tipoModel;
    private DefaultTableModel estadoModel;
    private Map<String, Long> porTipoData = new LinkedHashMap<>();
    private JPanel barCanvas;

    public EstadisticasPanelBuilder(SOSsenseKontrolatzailea controller) {
        this.controller = controller;
    }

    public JPanel build() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("ESTATISTIKAK", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 20, 10));
        mainPanel.add(titulo, BorderLayout.NORTH);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setMaximumSize(new Dimension(Integer.MAX_VALUE, Math.max(400, Toolkit.getDefaultToolkit().getScreenSize().height - 240)));

        JPanel resumenPanel = new JPanel(new GridLayout(1, 2, 12, 0));
        resumenPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 120));
        totalInstLabel = new JLabel();
        totalSensLabel = new JLabel();
        resumenPanel.add(createStatCard("Instalazio kopurua", totalInstLabel, new Color(0xD3, 0x85, 0x7E)));
        resumenPanel.add(createStatCard("Sentsoreak guztira", totalSensLabel, new Color(0xF6, 0xB2, 0xB2)));
        content.add(resumenPanel);

        content.add(Box.createVerticalStrut(8));

        JPanel tablasPanel = new JPanel(new GridLayout(1, 2, 12, 0));

        JPanel ezkerPanel = new JPanel();
        ezkerPanel.setLayout(new BoxLayout(ezkerPanel, BoxLayout.Y_AXIS));
        tipoModel = createTableModel();
        ezkerPanel.add(wrapTable("Mota bakoitzeko", tipoModel));
        ezkerPanel.add(Box.createVerticalStrut(8));
        estadoModel = createTableModel();
        ezkerPanel.add(wrapTable("Egoeraren arabera", estadoModel));
        tablasPanel.add(ezkerPanel);

        JPanel eskuinPanel = new JPanel();
        eskuinPanel.setLayout(new BoxLayout(eskuinPanel, BoxLayout.Y_AXIS));
        eskuinPanel.add(createBarChart("Mota grafikoa"));
        tablasPanel.add(eskuinPanel);

        content.add(tablasPanel);

        mainPanel.add(content, BorderLayout.CENTER);

        // Datos iniciales
        refreshData();

        // Escucha cambios del modelo para refrescar en vivo
        controller.addPropertyChangeListener(evt -> {
            if (SOSsenseModeloa.INSTALAZIOAK_ALDAKETA.equals(evt.getPropertyName())) {
                SwingUtilities.invokeLater(this::refreshData);
            }
        });
        return mainPanel;
    }

    private JPanel createStatCard(String titulo, JLabel valueLabel, Color baseColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(baseColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(baseColor.darker(), 1),
                BorderFactory.createEmptyBorder(14, 14, 14, 14)));

        JLabel titleLabel = new JLabel(titulo);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        titleLabel.setForeground(Color.BLACK);

        valueLabel.setFont(new Font("Arial", Font.BOLD, 26));
        valueLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        valueLabel.setForeground(Color.BLACK);

        card.add(titleLabel, BorderLayout.WEST);
        card.add(valueLabel, BorderLayout.EAST);
        return card;
    }

    private JPanel createBarChart(String title) {
        JPanel chartPanel = new JPanel(new BorderLayout());
        chartPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E)), title));

        barCanvas = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (porTipoData.isEmpty()) {
                    g.drawString("Daturik ez", 10, 20);
                    return;
                }

                int width = getWidth();
                int height = getHeight();
                int padding = 24;
                int barWidth = Math.max(20, (width - padding * 2) / Math.max(porTipoData.size(), 1) - 8);
                long maxValue = porTipoData.values().stream().mapToLong(Long::longValue).max().orElse(1);

                int index = 0;
                for (Map.Entry<String, Long> entry : porTipoData.entrySet()) {
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
        barCanvas.setPreferredSize(new Dimension(0, 180));
        chartPanel.add(barCanvas, BorderLayout.CENTER);
        return chartPanel;
    }

    private JPanel wrapTable(String title, DefaultTableModel model) {
        JTable table = new JTable(model);
        table.setRowHeight(26);
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 15));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(new Color(0xD3, 0x85, 0x7E)), title));
        panel.add(new JScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private DefaultTableModel createTableModel() {
        return new DefaultTableModel(new Object[] { "Elementua", "Kopurua" }, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    private void setTableData(DefaultTableModel model, Map<String, Long> data) {
        model.setRowCount(0);
        data.forEach((k, v) -> model.addRow(new Object[] { k, v }));
    }

    private void refreshData() {
        List<Instalazioa> instalazioak = controller.lortuInstalazioak();
        int totalInstalaciones = instalazioak.size();
        int totalSensores = instalazioak.stream().mapToInt(Instalazioa::getSentsoreak).sum();
        totalInstLabel.setText(String.valueOf(totalInstalaciones));
        totalSensLabel.setText(String.valueOf(totalSensores));

        Map<String, Long> porTipo = instalazioak.stream()
                .collect(Collectors.groupingBy(inst -> normalize(inst.getMota(), "Ezezaguna"), Collectors.counting()));

        Map<String, Long> porEstado = instalazioak.stream()
            .collect(Collectors.groupingBy(inst -> normalize(inst.getEgoera(), "Egoera ezezaguna"), Collectors.counting()));

        Map<String, Long> porTipoOrdenado = porTipo.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        Map<String, Long> porEstadoOrdenado = porEstado.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> a, LinkedHashMap::new));

        setTableData(tipoModel, porTipoOrdenado);
        setTableData(estadoModel, porEstadoOrdenado);

        porTipoData = porTipoOrdenado;
        if (barCanvas != null) {
            barCanvas.repaint();
        }
    }

    private String normalize(String value, String fallback) {
        if (value == null || value.trim().isEmpty()) {
            return fallback;
        }
        return value.trim();
    }
}
