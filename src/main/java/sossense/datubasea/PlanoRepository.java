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

                        planos.add(new PlanoInfo(instalacion, nombrePlano, imagenFondo,
                                ancho, alto, sensoresMin, sensoresMax, new ArrayList<>()));
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
        return sb.toString();
    }
}
