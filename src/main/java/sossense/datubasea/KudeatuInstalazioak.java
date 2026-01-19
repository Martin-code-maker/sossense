package sossense.datubasea;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;


public class KudeatuInstalazioak {

    private static final String ARCHIVO_INSTALACIONES = "datos/instalaciones.txt";
    private static final Set<String> MOTA_BAIMENDUAK = new HashSet<>(Arrays.asList(
            "FABRIKA",
            "OSPITALEA",
            "LABORATORIO",
            "IKASTOLA",
            "UNIBERTSITATEA"
    ));
    private List<Instalazioa> instalazioZerrenda;
    
    public KudeatuInstalazioak() {
        instalazioZerrenda = new ArrayList<>();
        hasieratuInstalazioak();
    }
    
    // Cargar instalaciones desde archivo txt
    private void hasieratuInstalazioak() {
        try (BufferedReader br = new BufferedReader(new FileReader(ARCHIVO_INSTALACIONES))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                // Ignorar líneas vacías y comentarios
                if (linea.trim().isEmpty() || linea.trim().startsWith("#")) {
                    continue;
                }
                
                String[] partes = linea.split("\\|");
                if (partes.length >= 5) {
                    String nombre = partes[0].trim();
                    int sensores = Integer.parseInt(partes[1].trim());
                    String direccion = partes[3].trim();
                    String tipo = partes[4].trim();

                    if (!motaBaimendua(tipo)) {
                        System.err.println("Mota ezezaguna instalazioan: " + nombre + " -> " + tipo + ". Saltatzen...");
                        continue; 
                    }
                    
                    // Si hay color personalizado
                    if (partes.length >= 6) {
                        String[] rgb = partes[5].trim().split(",");
                        if (rgb.length == 3) {
                            int r = Integer.parseInt(rgb[0].trim());
                            int g = Integer.parseInt(rgb[1].trim());
                            int b = Integer.parseInt(rgb[2].trim());
                            agregarInstalacionInternal(new Instalazioa(nombre, sensores, "", direccion, tipo, new Color(r, g, b)), false);
                        } else {
                            agregarInstalacionInternal(new Instalazioa(nombre, sensores, "", direccion, tipo), false);
                        }
                    } else {
                        agregarInstalacionInternal(new Instalazioa(nombre, sensores, "", direccion, tipo), false);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo de instalaciones: " + e.getMessage());
            // Cargar instalaciones por defecto si falla la lectura
            cargarInstalacionesPorDefecto();
        } catch (NumberFormatException e) {
            System.err.println("Error al parsear números del archivo: " + e.getMessage());
            cargarInstalacionesPorDefecto();
        }
    }
    
    // Método de respaldo con instalaciones por defecto
    private void cargarInstalacionesPorDefecto() {
        agregarInstalacionInternal(new Instalazioa(
            "MU-ko OSPITALA",
            60,
            "",
            "Nafarros Himbidea 16.20500 Arrasate, Gipuzkoa, Spain",
            "OSPITALEA"
        ), false);
        
        agregarInstalacionInternal(new Instalazioa(
            "MU-ko UNIBERTSITATEA",
            20,
            "",
            "Elorrieta Kalea 6, 48008 Bilbo, Bizkaia, Spain",
            "UNIBERTSITATEA"
        ), false);
    }
    
    // Métodos CRUD (Create, Read, Update, Delete)
    
    public void agregarInstalacion(Instalazioa instalacion) {
        if (!motaBaimendua(instalacion.getMota())) {
            throw new IllegalArgumentException("Mota baliogabea: " + instalacion.getMota());
        }
        agregarInstalacionInternal(instalacion, true);
    }

    private void agregarInstalacionInternal(Instalazioa instalacion, boolean persistir) {
        instalazioZerrenda.add(instalacion);
        if (persistir) {
            guardarInstalacionEnArchivo(instalacion);
        }
    }
    
    public Instalazioa buscarInstalacion(String nombre) {
        for (Instalazioa inst : instalazioZerrenda) {
            if (inst.getIzena().equalsIgnoreCase(nombre)) {
                return inst;
            }
        }
        return null;
    }
    
    public List<Instalazioa> getInstalaciones() {
        return new ArrayList<>(instalazioZerrenda); 
    }

    public int getTotalSensores() {
        int total = 0;
        for (Instalazioa inst : instalazioZerrenda) {
            total += inst.getSentsoreak();
        }
        return total;
    }
    
    public int getTotalInstalaciones() {
        return instalazioZerrenda.size();
    }

    // Estadistika botoia pultsatzerakoan agertzen den informazio
    public String getEstadisticas() {
        StringBuilder stats = new StringBuilder();
        stats.append("=== ESTADÍSTICAS ===\n");
        stats.append("Total instalazioZerrenda: ").append(getTotalInstalaciones()).append("\n");
        stats.append("Total sensores: ").append(getTotalSensores()).append("\n");
        
        stats.append("\nPor tipo:\n");
        Map<String, Integer> porTipo = new HashMap<>();
        for (Instalazioa inst : instalazioZerrenda) {
            String tipo = inst.getMota();

            if (porTipo.containsKey(tipo)) {
                int valorActual = porTipo.get(tipo);
                porTipo.put(tipo, valorActual + 1);
            } else {
                porTipo.put(tipo, 1);
            }
        }
        for (Map.Entry<String, Integer> entry : porTipo.entrySet()) {
            stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        stats.append("\nPor estado:\n");
        Map<String, Integer> porEstado = new HashMap<>();
        for (Instalazioa inst : instalazioZerrenda) {
            String estado = inst.getEgoera();
            porEstado.put(estado, porEstado.getOrDefault(estado, 0) + 1);
        }
        for (Map.Entry<String, Integer> entry : porEstado.entrySet()) {
            stats.append("  ").append(entry.getKey()).append(": ").append(entry.getValue()).append("\n");
        }
        
        return stats.toString();
    }
    

    //METODO HAU EZ NAGO ERABILTZEN MOMENTUZ! 
    public boolean eliminarInstalacion(String nombre) {
       // Buscamos la instalación a eliminar
       for (int i = 0; i < instalazioZerrenda.size(); i++) {
        Instalazioa inst = instalazioZerrenda.get(i);
        
        // Si encontramos una instalación con el nombre buscado (ignorando mayúsculas)
        if (inst.getIzena().equalsIgnoreCase(nombre)) {
            // La eliminamos de la lista
            instalazioZerrenda.remove(i);
            return true; // Devolvemos true porque encontramos y eliminamos
        }
    }
    return false; // No encontramos ninguna instalación con ese nombre
    }
        
    //METODO HAU EZ NAGO ERABILTZEN MOMENTUZ!
    public List<Instalazioa> getInstalacionesPorTipo(String tipo) {
        List<Instalazioa> resultado = new ArrayList<>();
        for (Instalazioa inst : instalazioZerrenda) {
            if (inst.getMota().equalsIgnoreCase(tipo)) {
                resultado.add(inst);
            }
        }
        return resultado;
    }
    
    //METODO HAU EZ NAGO ERABILTZEN MOMENTUZ!
    public List<Instalazioa> getInstalacionesPorEstado(String estado) {
        List<Instalazioa> resultado = new ArrayList<>();
        for (Instalazioa inst : instalazioZerrenda) {
            if (inst.getEgoera().equalsIgnoreCase(estado)) {
                resultado.add(inst);
            }
        }
        return resultado;
    }
    

    //METODO HAU EZ NAGO ERABILTZEN MOMENTUZ!
    public void actualizarSensores(String nombre, int nuevosSensores) {
        Instalazioa inst = buscarInstalacion(nombre);
        if (inst != null) {
            inst.setSentsoreak(nuevosSensores);
        }
    }
    
    //METODO HAU EZ NAGO ERABILTZEN MOMENTUZ!
    public void actualizarEstado(String nombre, String nuevoEstado) {
        Instalazioa inst = buscarInstalacion(nombre);
        if (inst != null) {
            inst.setEgoera(nuevoEstado);
        }
    }
    
    // Método para mostrar todas las instalazioZerrenda, //METODO HAU EZ NAGO ERABILTZEN MOMENTUZ!
    public void mostrarInstalaciones() {
        System.out.println("=== LISTA DE INSTALACIONES ===");
        for (int i = 0; i < instalazioZerrenda.size(); i++) {
            Instalazioa inst = instalazioZerrenda.get(i);
            System.out.println((i + 1) + ". " + /*inst.getIcono() +*/ " " + inst);
        }
        System.out.println("==============================");
    }

    private void guardarInstalacionEnArchivo(Instalazioa instalacion) {
        if (!motaBaimendua(instalacion.getMota())) {
            System.err.println("Ez da gorde: mota baliogabea " + instalacion.getMota());
            return;
        }
        try (java.io.FileWriter fw = new java.io.FileWriter(ARCHIVO_INSTALACIONES, true)) {
            StringBuilder sb = new StringBuilder();
            sb.append(instalacion.getIzena()).append('|')
              .append(instalacion.getSentsoreak()).append('|')
              .append("").append('|')
              .append(instalacion.getHelbidea()).append('|')
              .append(instalacion.getMota());
            fw.write(sb.toString());
            fw.write(System.lineSeparator());
        } catch (IOException e) {
            System.err.println("Ezin izan da instalazioa gorde fitxategian: " + e.getMessage());
        }
    }

    private boolean motaBaimendua(String mota) {
        if (mota == null) return false;
        return MOTA_BAIMENDUAK.contains(mota.trim().toUpperCase());
    }

    // Eguneratu egoerak sensores.txt fitxategiko balioen arabera.
    // Logika: max balioa >=70 -> LARRIA; >=30 -> ARINGARRI; bestela NORMALA.
    public boolean actualizarEgoerakDesdeSensores(String rutaSensores) {
        Map<String, Integer> maximoPorInstalacion = new HashMap<>();

        try (BufferedReader br = new BufferedReader(new FileReader(rutaSensores))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                if (linea.trim().isEmpty() || linea.trim().startsWith("#")) {
                    continue;
                }
                String[] partes = linea.split("\\|");
                if (partes.length >= 6) {
                    String inst = partes[0].trim();
                    try {
                        int nivel = 0;
                        // Si hay valor numérico de nivel en la 7ª columna, úsalo. Si no, intenta con la 6ª.
                        if (partes.length >= 7) {
                            nivel = Integer.parseInt(partes[6].trim());
                        } else {
                            nivel = Integer.parseInt(partes[5].trim());
                        }
                        int max = maximoPorInstalacion.getOrDefault(inst, 0);
                        if (nivel > max) {
                            maximoPorInstalacion.put(inst, nivel);
                        }
                    } catch (NumberFormatException ex) {
                        // Saltar sensor mal formateado
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Ezin da irakurri sensores.txt: " + e.getMessage());
        }

        boolean changed = false;
        for (Instalazioa inst : instalazioZerrenda) {
            int maxNivel = maximoPorInstalacion.getOrDefault(inst.getIzena(), 0);
            String berria;
            if (maxNivel >= 70) {
                berria = "LARRIA";
            } else if (maxNivel >= 30) {
                berria = "ARINGARRI";
            } else {
                berria = "NORMALA";
            }
            if (!berria.equalsIgnoreCase(inst.getEgoera())) {
                inst.setEgoera(berria);
                changed = true;
            }
        }
        return changed;
    }

}
