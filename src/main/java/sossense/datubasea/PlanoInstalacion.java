package sossense.datubasea;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import sossense.datubasea.SensorLayout;

public class PlanoInstalacion {

    private String nombreInstalacion;
    private String nombrePlano;
    private int ancho;
    private int alto;
    private List<SensorPlano> sentsoreak;
    private Random random;
    
    // Variables de control
    private boolean simulacionActiva = true; 

    // Constructor básico (Por si se usa en algún test antiguo)
    public PlanoInstalacion(String nombreInstalacion) {
        this(new PlanoInfo(nombreInstalacion, "Plano General", "", 800, 600, 0, 0));
    }
    
    // Constructor Principal
    public PlanoInstalacion(PlanoInfo planoInfo) {
        this.nombreInstalacion = planoInfo.getNombreInstalacion();
        this.nombrePlano = planoInfo.getNombrePlano();
        this.ancho = planoInfo.getAncho();
        this.alto = planoInfo.getAlto();
        
        this.sentsoreak = new ArrayList<>();
        this.random = new Random();
        
        // Cargar sensores REALES desde el fichero
        cargarSensoresDesdeFichero();
        
        // Inicializar con valores simulados (para que no salgan en blanco al inicio)
        simularNivelesHumoIniciales();
    }
    
    private void cargarSensoresDesdeFichero() {
        String archivo = "datos/sensores.txt";
        boolean encontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Ignorar comentarios y líneas vacías
                if (linea.trim().isEmpty() || linea.trim().startsWith("#")) {
                    continue;
                }
                
                String[] partes = linea.split("\\|");
                // Formato esperado: Instalacion|Plano|ID|X|Y|Zona
                if (partes.length >= 6) {
                    String inst = partes[0].trim();
                    String plan = partes[1].trim();
                    
                    // Solo cargamos los sensores que coincidan con ESTE edificio y ESTA planta
                    if (inst.equalsIgnoreCase(this.nombreInstalacion) && 
                        plan.equalsIgnoreCase(this.nombrePlano)) {
                        
                        String id = partes[2].trim();
                        int x = Integer.parseInt(partes[3].trim());
                        int y = Integer.parseInt(partes[4].trim());
                        String zona = partes[5].trim();
                        
                        // Crear sensor y añadirlo
                        sentsoreak.add(new SensorPlano(id, x, y, zona));
                        encontrado = true;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error leyendo sensores.txt: " + e.getMessage());
        }
        
        // Si no encontramos sensores en el fichero para este plano, 
        // avisamos (o podríamos generar randoms como fallback si quisieras)
        if (!encontrado) {
            System.out.println("⚠ No hay sensores definidos en sensores.txt para: " + 
                             nombreInstalacion + " - " + nombrePlano);
        }
    }
    
    // Método para dar un valor inicial (0-30%) para que no aparezcan todos apagados
    private void simularNivelesHumoIniciales() {
        for (SensorPlano sensor : sentsoreak) {
            sensor.setNivelHumo(random.nextInt(30)); 
        }
    }

    public void setSimulacionActiva(boolean activa) {
        this.simulacionActiva = activa;
    }
    
    // Este método lo llama el Timer de la UI si la simulación está activa
    public void simularNivelesHumo() {
        if (!simulacionActiva) return; // Si estamos con MQTT, no tocamos nada
        
        for (SensorPlano sensor : sentsoreak) {
            // Pequeña variación aleatoria para dar efecto de "vida"
            int cambio = random.nextInt(11) - 5; // -5 a +5
            int nuevoNivel = Math.max(0, Math.min(100, sensor.getNivelHumo() + cambio));
            sensor.setNivelHumo(nuevoNivel);
        }
    }
    
    // Getters
    public List<SensorPlano> getSentsoreak() { return sentsoreak; }
    public String getNombreInstalacion() { return nombreInstalacion; }
    public String getNombrePlano() { return nombrePlano; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    
    public int getTotalSentsoreak() { return sentsoreak.size(); }
    
    public int getSentsoreakAlerta() {
        return (int) sentsoreak.stream().filter(s -> s.getNivelHumo() >= 30).count();
    }
    
    public int getSentsoreakCriticos() {
        return (int) sentsoreak.stream().filter(s -> s.getNivelHumo() >= 70).count();
    }
}