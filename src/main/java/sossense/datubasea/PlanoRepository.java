package sossense.datubasea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import sossense.datubasea.SensorLayout;

public class PlanoRepository {

    private final String archivo;

    public PlanoRepository(String archivo) {
        this.archivo = archivo;
    }

    public List<PlanoInfo> cargarPlanosDeInstalacion(String nombreInstalacion) {
        List<PlanoInfo> planos = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.trim().startsWith("#")) {
                    continue;
                }
                String[] partes = linea.split("\\|");
                if (partes.length >= 7) {
                    String instalacion = partes[0].trim();
                    if (instalacion.equalsIgnoreCase(nombreInstalacion)) {
                        String nombrePlano = partes[1].trim();
                        String imagenFondo = partes[2].trim();
                        int ancho = Integer.parseInt(partes[3].trim());
                        int alto = Integer.parseInt(partes[4].trim());
                        int sensoresMin = Integer.parseInt(partes[5].trim());
                        int sensoresMax = Integer.parseInt(partes[6].trim());

                        List<SensorLayout> sensoresDefinidos = new ArrayList<>();
                        if (partes.length >= 8) {
                            sensoresDefinidos.addAll(parseSensores(partes[7].trim(), nombrePlano));
                        }

                        planos.add(new PlanoInfo(instalacion, nombrePlano, imagenFondo,
                                ancho, alto, sensoresMin, sensoresMax, sensoresDefinidos));
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de planos: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear números del archivo de planos: " + e.getMessage());
        }
        return planos;
    }

    public void guardarPlanos(String nombreInstalacion, List<PlanoInfo> planos) {
        if (planos == null || planos.isEmpty()) {
            return;
        }
        try (FileWriter fw = new FileWriter(archivo, true)) {
            for (PlanoInfo plano : planos) {
                plano.setNombreInstalacion(nombreInstalacion);
                String linea = formatearPlano(plano);
                fw.write(linea + System.lineSeparator());
            }
        } catch (IOException e) {
            System.err.println("Ezin izan da planoa gorde fitxategian: " + e.getMessage());
        }
    }

    private String formatearPlano(PlanoInfo plano) {
        StringBuilder sb = new StringBuilder();
        sb.append(plano.getNombreInstalacion()).append('|')
          .append(plano.getNombrePlano()).append('|')
          .append(plano.getImagenFondo() == null ? "" : plano.getImagenFondo()).append('|')
          .append(plano.getAncho()).append('|')
          .append(plano.getAlto()).append('|')
          .append(plano.getSensoresMin()).append('|')
          .append(plano.getSensoresMax());

        if (plano.tieneSensoresDefinidos()) {
            sb.append('|').append(serializarSensores(plano.getSensoresDefinidos(), plano.getNombrePlano()));
        }
        return sb.toString();
    }

    private String serializarSensores(List<SensorLayout> sensores, String nombrePlano) {
        List<String> serializados = new ArrayList<>();
        for (SensorLayout sensor : sensores) {
            String ubicacionLimpia = sensor.getUbicacion().replace('|', ' ').replace(';', ',');
            serializados.add(sensor.getId() + "," + sensor.getX() + "," + sensor.getY() + "," + ubicacionLimpia);
        }
        return String.join(";", serializados);
    }

    private List<SensorLayout> parseSensores(String raw, String nombrePlano) {
        List<SensorLayout> sensores = new ArrayList<>();
        if (raw == null || raw.isEmpty()) {
            return sensores;
        }
        String[] trozos = raw.split(";");
        for (String trozo : trozos) {
            String limpio = trozo.trim();
            if (limpio.isEmpty()) {
                continue;
            }
            String[] partesSensor = limpio.split(",", 4);
            if (partesSensor.length < 4) {
                continue;
            }
            try {
                String id = partesSensor[0].trim();
                int x = Integer.parseInt(partesSensor[1].trim());
                int y = Integer.parseInt(partesSensor[2].trim());
                String ubicacion = partesSensor[3].trim();
                sensores.add(new SensorLayout(id, x, y, ubicacion));
            } catch (NumberFormatException ex) {
                System.err.println("Ezin izan da sentsorea parseatu planoan " + nombrePlano + ": " + trozo);
            }
        }
        return sensores;
    }
}
