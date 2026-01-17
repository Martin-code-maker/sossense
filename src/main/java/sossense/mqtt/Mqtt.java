package sossense.mqtt;

import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter; // Necesario para borrar el fichero
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.beans.PropertyChangeSupport;
import java.beans.PropertyChangeListener;

public class Mqtt implements MqttCallback {

    public static final String BROKER = "tcp://172.16.0.10:1883";
    public static final String CLENT_ID = "SOSsenseClient";
    public static final int QoS = 2;
    public static final String TOPIC_GAS = "SOSsense"; // Ajusta tu topic

    private MqttClient client;
    
    // --- NUEVAS VARIABLES ---
    private List<Double> bufferLecturas = new ArrayList<>();
    private int contadorTotal = 0;
    private final int LIMITE_MEDIA = 10;   // Hacer media cada 10 valores
    private final int LIMITE_BORRADO = 100; // Borrar fichero cada 100 valores
    
    // Para avisar a la interfaz gráfica
    private final PropertyChangeSupport support = new PropertyChangeSupport(this);

    public Mqtt() throws MqttException {
        // --- CONEXIÓN A MQTT COMENTADA (SIN RED) ---
        /*
        MemoryPersistence persistence = new MemoryPersistence();
        client = new MqttClient(BROKER, CLENT_ID, persistence);
        MqttConnectOptions connOpts = new MqttConnectOptions();
        connOpts.setCleanSession(true);
        
        client.setCallback(this);
        System.out.println("[MQTT] Conectando a: " + BROKER);
        client.connect(connOpts);
        System.out.println("[MQTT] Conectado");
        
        client.subscribe(TOPIC_GAS, QoS);
        System.out.println("[MQTT] Suscrito a " + TOPIC_GAS);
        */
        System.out.println("[MQTT] Modo offline - conexión desactivada");
    }
    
    // Método para que la App se pueda suscribir a las alertas de gas
    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    @Override
    public void connectionLost(Throwable cause) {
        System.err.println("[MQTT] Conexión perdida!");
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {}

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        String contenido = new String(message.getPayload());
        
        try {
            double valorActual = Double.parseDouble(contenido);
            
            // 1. Guardar dato en el fichero
            guardarEnLog(valorActual);
            
            // 2. Añadir al buffer para la media
            bufferLecturas.add(valorActual);
            contadorTotal++;
            
            // 3. ¿Tenemos ya 10 valores para hacer la media?
            if (bufferLecturas.size() >= LIMITE_MEDIA) {
                double media = calcularMedia();
                System.out.println("Media (10 valores): " + media);
                
                // AVISAR A LA APP (UI) PARA QUE ACTUALICE EL SENSOR
                support.firePropertyChange("DATO_GAS_ACTUALIZADO", null, media);
                
                // Limpiar el buffer para los siguientes 10
                bufferLecturas.clear();
            }
            
            // 4. ¿Hemos llegado a 100 valores totales? -> Limpiar fichero
            if (contadorTotal >= LIMITE_BORRADO) {
                limpiarFicheroLog();
                contadorTotal = 0;
                System.out.println("[LOG] Fichero reseteado por límite de capacidad.");
            }
            
        } catch (NumberFormatException e) {
            System.err.println("Error al leer valor numérico: " + contenido);
        }
    }
    
    private double calcularMedia() {
        double suma = 0;
        for (Double val : bufferLecturas) {
            suma += val;
        }
        return suma / bufferLecturas.size();
    }

    private void guardarEnLog(double valor) {
        try {
            File carpeta = new File("logs");
            if (!carpeta.exists()) carpeta.mkdir();
            
            FileWriter writer = new FileWriter("logs/datuak.txt", true);
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
            writer.write(sdf.format(new Date()) + " | Gas: " + valor + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private void limpiarFicheroLog() {
        try {
            // Sobrescribir el fichero (false en el constructor de FileWriter) lo deja vacío
            new PrintWriter("logs/datuak.txt").close();
        } catch (IOException e) {
            System.err.println("No se pudo limpiar el log.");
        }
    }
}