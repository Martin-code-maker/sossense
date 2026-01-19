package sossense.bista;

import java.awt.*;
import javax.swing.*;

public class ContactoPanelBuilder {
    public JPanel build() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(new Color(250, 250, 250));

        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(70, 130, 70, 130)
        ));

        JLabel titulo = new JLabel("KONTAKTUA");
        titulo.setFont(new Font("Arial", Font.BOLD, 32));
        titulo.setForeground(new Color(50, 50, 50));
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JSeparator separator = new JSeparator();
        separator.setMaximumSize(new Dimension(60, 5));
        separator.setForeground(new Color(0xE1, 0x9D, 0x8E));
        separator.setBackground(new Color(0xE1, 0x9D, 0x8E));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT);

        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 50, 50));
        infoPanel.setBackground(Color.WHITE);
        infoPanel.setBorder(BorderFactory.createEmptyBorder(50, 0, 50, 0));

        infoPanel.add(crearFilaContacto("📍", "HELBIDEA", "Nafarroa Hiribidea 16, Arrasate"));
        infoPanel.add(crearFilaContacto("📧", "EMAILA", "info@sossense.eus"));
        infoPanel.add(crearFilaContacto("📞", "TELEFONOA", "+34 943 123 456"));

        JLabel webLabel = new JLabel("www.sossense.eus");
        webLabel.setFont(new Font("Arial", Font.BOLD, 16));
        webLabel.setForeground(new Color(0xE1, 0x9D, 0x8E));
        webLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        card.add(titulo);
        card.add(Box.createVerticalStrut(25));
        card.add(separator);
        card.add(infoPanel);
        card.add(Box.createVerticalGlue());
        card.add(webLabel);

        mainPanel.add(card);
        return mainPanel;
    }

    private JPanel crearFilaContacto(String icono, String titulo, String texto) {
        JPanel panel = new JPanel(new BorderLayout(25, 0));
        panel.setBackground(Color.WHITE);
        JLabel lblIcono = new JLabel(icono);
        lblIcono.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 42));
        JPanel txtPanel = new JPanel(new GridLayout(2, 1, 0, 5));
        txtPanel.setBackground(Color.WHITE);
        JLabel lblTitulo = new JLabel(titulo);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 12));
        lblTitulo.setForeground(Color.GRAY);
        JLabel lblTexto = new JLabel(texto);
        lblTexto.setFont(new Font("Arial", Font.PLAIN, 22));
        lblTexto.setForeground(new Color(40, 40, 40));
        txtPanel.add(lblTitulo);
        txtPanel.add(lblTexto);
        panel.add(lblIcono, BorderLayout.WEST);
        panel.add(txtPanel, BorderLayout.CENTER);
        return panel;
    }
}
