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
    private String imagenFondo;

    private boolean simulacionActiva = true;

    public PlanoInstalacion(String nombreInstalacion) {
        this(new PlanoInfo(nombreInstalacion, "Plano General", "", 800, 600, 0, 0));
    }

    public PlanoInstalacion(PlanoInfo planoInfo) {
        this.nombreInstalacion = planoInfo.getNombreInstalacion();
        this.nombrePlano = planoInfo.getNombrePlano();
        this.ancho = planoInfo.getAncho();
        this.alto = planoInfo.getAlto();

        this.sentsoreak = new ArrayList<>();
        this.random = new Random();
        this.imagenFondo = planoInfo.getImagenFondo();

        cargarSensoresDesdeSensoresTxt();
        simularNivelesHumoIniciales();
    }

    private void cargarSensoresDesdeSensoresTxt() {
        String archivo = "datos/sensores.txt";
        boolean encontrado = false;

        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.trim().startsWith("#")) {
                    continue;
                }
                String[] partes = linea.split("\\|");
                if (partes.length >= 6) {
                    String inst = partes[0].trim();
                    String plan = partes[1].trim();
                    if (inst.equalsIgnoreCase(this.nombreInstalacion) &&
                            plan.equalsIgnoreCase(this.nombrePlano)) {
                        String id = partes[2].trim();
                        int x = Integer.parseInt(partes[3].trim());
                        int y = Integer.parseInt(partes[4].trim());
                        String zona = partes[5].trim();
                        sentsoreak.add(new SensorPlano(id, x, y, zona));
                        encontrado = true;
                    }
                }
            }
        } catch (IOException | NumberFormatException e) {
            System.err.println("Error leyendo sensores.txt: " + e.getMessage());
        }

        if (!encontrado) {
            System.out.println("⚠ No hay sensores definidos para: " + nombreInstalacion + " - " + nombrePlano);
        }
    }

    private void simularNivelesHumoIniciales() {
        for (SensorPlano sensor : sentsoreak) {
            sensor.setNivelHumo(random.nextInt(30));
        }
    }

    public void setSimulacionActiva(boolean activa) {
        this.simulacionActiva = activa;
    }

    public void simularNivelesHumo() {
        if (!simulacionActiva)
            return;

        for (SensorPlano sensor : sentsoreak) {
            int cambio = random.nextInt(11) - 5;
            int nuevoNivel = Math.max(0, Math.min(100, sensor.getNivelHumo() + cambio));
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

    public String getImagenFondo() {
        return imagenFondo;
    }

    public int getTotalSentsoreak() {
        return sentsoreak.size();
    }

    public int getSentsoreakAlerta() {
        return (int) sentsoreak.stream().filter(s -> s.getNivelHumo() >= 30).count();
    }

    public int getSentsoreakCriticos() {
        return (int) sentsoreak.stream().filter(s -> s.getNivelHumo() >= 70).count();
    }
}