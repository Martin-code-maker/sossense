package sossense.datubasea;

import java.util.ArrayList;
import java.util.List;
import java.awt.Color;
import java.util.Map;        // ← AÑADIR
import java.util.HashMap;    // ← AÑADIR

public class KudeatuInstalazioak {

    private List<Instalazioa> instalazioZerrenda;
    
    public KudeatuInstalazioak() {
        instalazioZerrenda = new ArrayList<>();
        hasieratuInstalazioak();
    }
    
    // Cargar algunas instalazioZerrenda de ejemplo
    private void hasieratuInstalazioak() {
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
        
        agregarInstalacion(new Instalazioa(
            "Mondragon Fabrika",
            35,
            "ARINGARRI",
            "Goiru Kalea 1, 20500 Arrasate, Gipuzkoa",
            "FABRIKA"
        ));
        
        agregarInstalacion(new Instalazioa(
            "Eskola Nagusia",
            15,
            "NORMALA",
            "San Andres Kalea 12, 20500 Arrasate",
            "IKASTOLA"
        ));
        
        agregarInstalacion(new Instalazioa(
            "Ikerketa Laborategia",
            25,
            "MANTENIMIENTO",
            "Teknologia Parkea, 20100 Errenteria",
            "LABORATORIO",
            new Color(255, 230, 230) // Rosa muy claro personalizado
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
