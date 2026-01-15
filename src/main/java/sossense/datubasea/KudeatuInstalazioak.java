package sossense.datubasea;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.util.Map;
import java.util.HashMap;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class KudeatuInstalazioak {

    private List<Instalazioa> instalazioZerrenda;
    
    public KudeatuInstalazioak() {
        instalazioZerrenda = new ArrayList<>();
        hasieratuInstalazioak();
    }
    
    // Cargar instalaciones desde archivo txt
    private void hasieratuInstalazioak() {
        String archivo = "datos/instalaciones.txt";
        
        try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
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
                    String estado = partes[2].trim();
                    String direccion = partes[3].trim();
                    String tipo = partes[4].trim();
                    
                    // Si hay color personalizado
                    if (partes.length >= 6) {
                        String[] rgb = partes[5].trim().split(",");
                        if (rgb.length == 3) {
                            int r = Integer.parseInt(rgb[0].trim());
                            int g = Integer.parseInt(rgb[1].trim());
                            int b = Integer.parseInt(rgb[2].trim());
                            agregarInstalacion(new Instalazioa(nombre, sensores, estado, direccion, tipo, new Color(r, g, b)));
                        } else {
                            agregarInstalacion(new Instalazioa(nombre, sensores, estado, direccion, tipo));
                        }
                    } else {
                        agregarInstalacion(new Instalazioa(nombre, sensores, estado, direccion, tipo));
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
        agregarInstalacion(new Instalazioa(
            "MU-ko OSPITALA",
            60,
            "LARRIA",
            "Nafarros Himbidea 16.20500 Arrasate, Gipuzkoa, Spain",
            "OSPITALEA"
        ));
        
        agregarInstalacion(new Instalazioa(
            "MU-ko UNIBERTSITATEA",
            20,
            "NORMALA",
            "Elorrieta Kalea 6, 48008 Bilbo, Bizkaia, Spain",
            "UNIBERTSITATEA"
        ));
    }
    
    // Métodos CRUD (Create, Read, Update, Delete)
    
    public void agregarInstalacion(Instalazioa instalacion) {
        instalazioZerrenda.add(instalacion);
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
        return new ArrayList<>(instalazioZerrenda); // Devolver copia
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
        
        // Contar por tipo
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
        
        // Contar por estado
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


}
