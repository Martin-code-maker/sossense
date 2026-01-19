package sossense.bista;

import java.awt.*;
import javax.swing.*;
import sossense.kontrolatzailea.SOSsenseKontrolatzailea;

public class EstadisticasPanelBuilder {
    private final SOSsenseKontrolatzailea controller;
    public EstadisticasPanelBuilder(SOSsenseKontrolatzailea controller) {
        this.controller = controller;
    }
    public JPanel build() {
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
}
