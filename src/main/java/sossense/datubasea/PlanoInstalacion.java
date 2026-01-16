package sossense.datubasea;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class PlanoInstalacion {

    private String nombreInstalacion;
    private String nombrePlano;
    private int ancho;
    private int alto;
    private List<SensorPlano> sentsoreak;
    private Random random;
    private List<String> plantas;
    private List<String> areas;
    private int sensoresMin;
    private int sensoresMax;
    
    public PlanoInstalacion(String nombreInstalacion) {
        this.nombreInstalacion = nombreInstalacion;
        this.nombrePlano = "Plano General";
        this.ancho = 800;
        this.alto = 600;
        this.sensoresMin = 15;
        this.sensoresMax = 34;
        this.sentsoreak = new ArrayList<>();
        this.random = new Random();
        this.plantas = new ArrayList<>();
        this.areas = new ArrayList<>();
        cargarUbicaciones();
        generarSentsoreak();
        simularNivelesHumo();
    }
    
    // Nuevo constructor con PlanoInfo
    public PlanoInstalacion(PlanoInfo planoInfo) {
        this.nombreInstalacion = planoInfo.getNombreInstalacion();
        this.nombrePlano = planoInfo.getNombrePlano();
        this.ancho = planoInfo.getAncho();
        this.alto = planoInfo.getAlto();
        this.sensoresMin = planoInfo.getSensoresMin();
        this.sensoresMax = planoInfo.getSensoresMax();
        this.sentsoreak = new ArrayList<>();
        this.random = new Random();
        this.plantas = new ArrayList<>();
        this.areas = new ArrayList<>();
        cargarUbicaciones();
        generarSentsoreak();
        simularNivelesHumo();
    }
    
    private void generarSentsoreak() {
        int rango = sensoresMax - sensoresMin;
        int numSentsoreak = sensoresMin + random.nextInt(rango + 1);
        
        for (int i = 1; i <= numSentsoreak; i++) {
            int x = 50 + random.nextInt(ancho - 100);
            int y = 50 + random.nextInt(alto - 100);
            String ubicacion = generarUbicacionAleatoria();
            sentsoreak.add(new SensorPlano("S" + i, x, y, ubicacion));
        }
    }
    
    private void cargarUbicaciones() {
        String archivo = "datos/ubicaciones.txt";
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Ignorar líneas vacías y comentarios
                if (linea.trim().isEmpty() || linea.trim().startsWith("#")) {
                    continue;
                }
                
                if (linea.startsWith("PLANTAS=")) {
                    String[] plantasArray = linea.substring(8).split(",");
                    for (String planta : plantasArray) {
                        plantas.add(planta.trim());
                    }
                } else if (linea.startsWith("AREAS=")) {
                    String[] areasArray = linea.substring(6).split(",");
                    for (String area : areasArray) {
                        areas.add(area.trim());
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de ubicaciones: " + e.getMessage());
            // Cargar valores por defecto si falla la lectura
            cargarUbicacionesPorDefecto();
        }
        
        // Si no se cargaron datos, usar valores por defecto
        if (plantas.isEmpty() || areas.isEmpty()) {
            cargarUbicacionesPorDefecto();
        }
    }
    
    private void cargarUbicacionesPorDefecto() {
        plantas.clear();
        areas.clear();
        plantas.add("Planta Baja");
        plantas.add("Planta 1");
        plantas.add("Planta 2");
        plantas.add("Planta 3");
        plantas.add("Sótano");
        
        areas.add("Pasillo Principal");
        areas.add("Oficina");
        areas.add("Sala de Máquinas");
        areas.add("Cocina");
        areas.add("Baños");
        areas.add("Almacén");
        areas.add("Laboratorio");
        areas.add("Aula");
        areas.add("Habitación");
        areas.add("Vestíbulo");
    }
    
    private String generarUbicacionAleatoria() {
        if (plantas.isEmpty() || areas.isEmpty()) {
            return "Ubicación desconocida";
        }
        
        return plantas.get(random.nextInt(plantas.size())) + " - " + 
               areas.get(random.nextInt(areas.size()));
    }
    
    public void simularNivelesHumo() {
        for (SensorPlano sensor : sentsoreak) {
            // Simular un nivel de humo aleatorio
            int nivelBase = random.nextInt(30); // Base 0-29%
            
            // 10% de probabilidad de tener humo alto
            if (random.nextInt(100) < 10) {
                nivelBase += 50 + random.nextInt(50);
            }
            
            sensor.setNivelHumo(nivelBase);
        }
    }

    private boolean simulacionActiva = true; // Por defecto true

    public void setSimulacionActiva(boolean activa) {
        this.simulacionActiva = activa;
    }
    
    public void actualizarNivelesHumo() {
        if (!simulacionActiva) return; // Si es false, no hace nada (respeta MQTT)
        for (SensorPlano sensor : sentsoreak) {
            int cambio = random.nextInt(11) - 5; // Cambio entre -5 y +5
            int nuevoNivel = sensor.getNivelHumo() + cambio;
            sensor.setNivelHumo(nuevoNivel);
        }
    }
    
    public List<SensorPlano> getSentsoreak() {
        return sentsoreak;
    }
    
    public String getNombreInstalacion() {
        return nombreInstalacion;
    }
    
    public String getNombrePlano() {
        return nombrePlano;
    }
    
    public int getAncho() {
        return ancho;
    }
    
    public int getAlto() {
        return alto;
    }
    
    public int getTotalSentsoreak() {
        return sentsoreak.size();
    }
    
    public int getSentsoreakAlerta() {
        int count = 0;
        for (SensorPlano sensor : sentsoreak) {
            if (sensor.getNivelHumo() >= 30) {
                count++;
            }
        }
        return count;
    }
    
    public int getSentsoreakCriticos() {
        int count = 0;
        for (SensorPlano sensor : sentsoreak) {
            if (sensor.getNivelHumo() >= 70) {
                count++;
            }
        }
        return count;
    }

}
