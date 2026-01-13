package sossense.datubasea;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class PanelPlano extends JPanel {
    private PlanoInstalacion plano;
    private Timer timerActualizacion;
    private SensorPlano sensorSeleccionado;
    
    public PanelPlano(PlanoInstalacion plano) {
        this.plano = plano;
        this.sensorSeleccionado = null;
        setPreferredSize(new Dimension(plano.getAncho(), plano.getAlto()));
        setBackground(Color.WHITE);
        
        // Configurar eventos del ratón
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buscarSensorClickeado(e.getX(), e.getY());
            }
        });
        
        // Timer para actualizar los niveles de humo cada 3 segundos
        timerActualizacion = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                plano.actualizarNivelesHumo();
                repaint();
            }
        });
        timerActualizacion.start();
    }
    
    private void buscarSensorClickeado(int x, int y) {
        for (SensorPlano sensor : plano.getSentsoreak()) {
            int distancia = (int) Math.sqrt(Math.pow(x - sensor.getX(), 2) + 
                                          Math.pow(y - sensor.getY(), 2));
            if (distancia <= 15) { // Radio del sensor
                sensorSeleccionado = sensor;
                repaint();
                
                // Mostrar información del sensor
                JOptionPane.showMessageDialog(this,
                    sensor.getInfo(),
                    "Información del Sensor",
                    JOptionPane.INFORMATION_MESSAGE);
                return;
            }
        }
        sensorSeleccionado = null;
        repaint();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, 
                            RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Dibujar fondo del edificio
        g2d.setColor(new Color(240, 240, 240));
        g2d.fillRect(10, 10, getWidth() - 20, getHeight() - 20);
        
        // Dibujar contorno del edificio
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(10, 10, getWidth() - 20, getHeight() - 20);
        
        // Dibujar sensores
        for (SensorPlano sensor : plano.getSentsoreak()) {
            // Dibujar círculo del sensor
            g2d.setColor(sensor.getColor());
            g2d.fillOval(sensor.getX() - 10, sensor.getY() - 10, 20, 20);
            
            // Dibujar borde
            if (sensor == sensorSeleccionado) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(3));
            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
            }
            g2d.drawOval(sensor.getX() - 10, sensor.getY() - 10, 20, 20);
            
            // Dibujar ID del sensor (pequeño)
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 9));
            String id = sensor.getId();
            FontMetrics fm = g2d.getFontMetrics();
            int idWidth = fm.stringWidth(id);
            g2d.drawString(id, sensor.getX() - idWidth/2, sensor.getY() + 4);
        }
        
        // Dibujar leyenda
        dibujarLeyenda(g2d);
        
        // Dibujar título
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String titulo = "Plano: " + plano.getNombreInstalacion();
        FontMetrics fm = g2d.getFontMetrics();
        int tituloWidth = fm.stringWidth(titulo);
        g2d.drawString(titulo, (getWidth() - tituloWidth) / 2, 30);
        
        // Dibujar estadísticas
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String stats = "Sentsoreak: " + plano.getTotalSentsoreak() + 
                      " | Alerta: " + plano.getSentsoreakAlerta() + 
                      " | Críticos: " + plano.getSentsoreakCriticos();
        int statsWidth = fm.stringWidth(stats);
        g2d.drawString(stats, (getWidth() - statsWidth) / 2, getHeight() - 20);
    }
    

    private void dibujarLeyenda(Graphics2D g2d) {
        int x = getWidth() - 150;
        int y = 50;
        
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 12));
        g2d.drawString("LEYENDA", x, y);
        
        y += 20;
        g2d.setColor(Color.GREEN);
        g2d.fillRect(x, y, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, 15, 15);
        g2d.drawString("Normal (0-29%)", x + 25, y + 12);
        
        y += 25;
        g2d.setColor(Color.ORANGE);
        g2d.fillRect(x, y, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, 15, 15);
        g2d.drawString("Alerta (30-69%)", x + 25, y + 12);
        
        y += 25;
        g2d.setColor(Color.RED);
        g2d.fillRect(x, y, 15, 15);
        g2d.setColor(Color.BLACK);
        g2d.drawRect(x, y, 15, 15);
        g2d.drawString("Crítico (70-100%)", x + 25, y + 12);
        
        y += 30;
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.PLAIN, 10));
        g2d.drawString("Haz clic en un", x, y);
        g2d.drawString("sensor para más info", x, y + 15);
    }
    
    public void detenerActualizacion() {
        if (timerActualizacion != null) {
            timerActualizacion.stop();
        }
    }

}
