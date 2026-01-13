package sossense.datubasea;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PlanoInstalacion {

    private String nombreInstalacion;
    private int ancho;
    private int alto;
    private List<SensorPlano> sentsoreak;
    private Random random;
    
    public PlanoInstalacion(String nombreInstalacion) {
        this.nombreInstalacion = nombreInstalacion;
        this.ancho = 800;
        this.alto = 600;
        this.sentsoreak = new ArrayList<>();
        this.random = new Random();
        generarSentsoreak();
        simularNivelesHumo();
    }
    
    private void generarSentsoreak() {
        int numSentsoreak = 15 + random.nextInt(20); // Entre 15 y 34 sentsoreak
        
        for (int i = 1; i <= numSentsoreak; i++) {
            int x = 50 + random.nextInt(ancho - 100);
            int y = 50 + random.nextInt(alto - 100);
            String ubicacion = generarUbicacionAleatoria();
            sentsoreak.add(new SensorPlano("S" + i, x, y, ubicacion));
        }
    }
    
    private String generarUbicacionAleatoria() {
        String[] plantas = {"Planta Baja", "Planta 1", "Planta 2", "Planta 3", "Sótano"};
        String[] areas = {"Pasillo Principal", "Oficina", "Sala de Máquinas", "Cocina", 
                         "Baños", "Almacén", "Laboratorio", "Aula", "Habitación", "Vestíbulo"};
        
        return plantas[random.nextInt(plantas.length)] + " - " + 
               areas[random.nextInt(areas.length)];
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
    
    public void actualizarNivelesHumo() {
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
