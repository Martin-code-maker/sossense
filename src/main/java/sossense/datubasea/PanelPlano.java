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
    
    // Nueva variable para la imagen
    private Image imagenPlano; 
    
    public PanelPlano(PlanoInstalacion plano) {
        this(plano, null);
    }
    
    public PanelPlano(PlanoInstalacion plano, String nombreImagenFondo) {
        this.plano = plano;
        this.sensorSeleccionado = null;
        setPreferredSize(new Dimension(plano.getAncho(), plano.getAlto()));
        setBackground(Color.WHITE);
        
        // --- LOGICA PARA ELEGIR LA IMAGEN SEGUN EL NOMBRE ---
        String nombreInstalacion = plano.getNombreInstalacion();

        java.util.List<String> rutas = new java.util.ArrayList<>();
        if (nombreImagenFondo != null && !nombreImagenFondo.isEmpty()) {
            // Añadimos la ruta tal cual del fichero de datos
            rutas.add("/sossense/img/" + nombreImagenFondo);
            // Fallback automático si en datos figura .jpg pero el recurso es .png
            if (nombreImagenFondo.toLowerCase().endsWith(".jpg")) {
                rutas.add("/sossense/img/" + nombreImagenFondo.substring(0, nombreImagenFondo.length() - 4) + ".png");
            }
            if (nombreImagenFondo.toLowerCase().endsWith(".png")) {
                rutas.add("/sossense/img/" + nombreImagenFondo.substring(0, nombreImagenFondo.length() - 4) + ".jpg");
            }
        } else {
            // Lógica por nombre de instalación
            if (nombreInstalacion.equalsIgnoreCase("MU-ko OSPITALA")) {
                rutas.add("/sossense/img/plano_hospital.png");
            }
            else if (nombreInstalacion.equalsIgnoreCase("MU-ko UNIBERTSITATEA")) {
                rutas.add("/sossense/img/plano_universidad.png");
            }
            else if (nombreInstalacion.equalsIgnoreCase("Mondragon Fabrika")) {
                rutas.add("/sossense/img/plano_fabrica.png");
            }
            else if (nombreInstalacion.equalsIgnoreCase("Eskola Nagusia")) {
                rutas.add("/sossense/img/plano_escuela.png");
            }
            else if (nombreInstalacion.equalsIgnoreCase("Ikerketa Laborategia")) {
                rutas.add("/sossense/img/plano_laboratorio.png");
            }
            else {
                rutas.add("/sossense/img/plano_default.png");
            }
        }

        // Aseguramos un fallback genérico al final
        rutas.add("/sossense/img/plano_default.png");

        // Cargar la primera imagen disponible
        for (String ruta : rutas) {
            try {
                java.net.URL imgUrl = getClass().getResource(ruta);
                if (imgUrl != null) {
                    imagenPlano = new ImageIcon(imgUrl).getImage();
                    break;
                }
            } catch (Exception e) {
                // Continuar probando siguientes rutas
            }
        }

        if (imagenPlano == null) {
            System.err.println("No se encontró ninguna imagen de plano para " + nombreInstalacion + " (intentado: " + rutas + ")");
        }
        // ------------------------------------------
        
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
                //plano.actualizarNivelesHumo();
                repaint();
            }
        });
        timerActualizacion.start();
    }
    
    // ... (El método buscarSensorClickeado se mantiene igual) ...
    private void buscarSensorClickeado(int x, int y) {
        for (SensorPlano sensor : plano.getSentsoreak()) {
            int distancia = (int) Math.sqrt(Math.pow(x - sensor.getX(), 2) + 
                                          Math.pow(y - sensor.getY(), 2));
            if (distancia <= 15) { 
                sensorSeleccionado = sensor;
                repaint();
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
        
        // --- MODIFICADO: Dibujar imagen de fondo o color gris si falla ---
        int margen = 10;
        int anchoDibujo = getWidth() - (margen * 2);
        int altoDibujo = getHeight() - (margen * 2);

        if (imagenPlano != null) {
            // Dibujamos la imagen dentro del margen (10, 10) estirándola para que encaje
            g2d.drawImage(imagenPlano, margen, margen, anchoDibujo, altoDibujo, this);
        } else {
            // Fallback: Si no hay imagen, usamos el gris de antes
            g2d.setColor(new Color(240, 240, 240));
            g2d.fillRect(margen, margen, anchoDibujo, altoDibujo);
        }
        // ----------------------------------------------------------------
        
        // Dibujar contorno del edificio (borde negro encima de la imagen)
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(3));
        g2d.drawRect(margen, margen, anchoDibujo, altoDibujo);
        
        // ... (El resto del código: dibujar sensores, leyenda, título, etc. se mantiene igual) ...
        
        // Dibujar sensores
        for (SensorPlano sensor : plano.getSentsoreak()) {
            g2d.setColor(sensor.getColor());
            g2d.fillOval(sensor.getX() - 10, sensor.getY() - 10, 20, 20);
            
            if (sensor == sensorSeleccionado) {
                g2d.setColor(Color.BLUE);
                g2d.setStroke(new BasicStroke(3));
            } else {
                g2d.setColor(Color.BLACK);
                g2d.setStroke(new BasicStroke(1));
            }
            g2d.drawOval(sensor.getX() - 10, sensor.getY() - 10, 20, 20);
            
            g2d.setColor(Color.BLACK);
            g2d.setFont(new Font("Arial", Font.BOLD, 9));
            String id = sensor.getId();
            FontMetrics fm = g2d.getFontMetrics();
            int idWidth = fm.stringWidth(id);
            g2d.drawString(id, sensor.getX() - idWidth/2, sensor.getY() + 4);
        }
        
        dibujarLeyenda(g2d);
        
        // Título y estadísticas...
        g2d.setColor(Color.BLACK);
        g2d.setFont(new Font("Arial", Font.BOLD, 20));
        String titulo = "Plano: " + plano.getNombreInstalacion();
        FontMetrics fm = g2d.getFontMetrics();
        int tituloWidth = fm.stringWidth(titulo);
        g2d.drawString(titulo, (getWidth() - tituloWidth) / 2, 30);
        
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        String stats = "Sentsoreak: " + plano.getTotalSentsoreak() + 
                      " | Alerta: " + plano.getSentsoreakAlerta() + 
                      " | Críticos: " + plano.getSentsoreakCriticos();
        int statsWidth = fm.stringWidth(stats);
        g2d.drawString(stats, (getWidth() - statsWidth) / 2, getHeight() - 20);
    }

    // ... (Resto de métodos: dibujarLeyenda y detenerActualizacion se mantienen igual) ...
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

    // Método nuevo para actualizar un sensor concreto desde fuera (MQTT)
    public void actualizarSensorEspecifico(String idSensor, int valorHumo) {
        if (plano != null) {
            for (SensorPlano sensor : plano.getSentsoreak()) {
                if (sensor.getId().equalsIgnoreCase(idSensor)) {
                    sensor.setNivelHumo(valorHumo);
                    repaint(); // Redibujar el mapa para ver el cambio de color
                    return;
                }
            }
        }
    }
}