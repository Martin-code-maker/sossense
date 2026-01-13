package sossense.datubasea;

import java.awt.Color;

public class Instalazioa {

   private String izena;
    private int sentsoreak;
    private String egoera;
    private String helbidea;
    private String mota; // mota: OSPITALEA, UNIBERTSITATEA, IKASTOLA, FABRIKA, etc.
    private Color kolorea;
    
    // Konstruktoeaa kolore gabe
    public Instalazioa(String izena, int sentsoreak, String egoera, String helbidea, String mota) {
        this.izena = izena;
        this.sentsoreak = sentsoreak;
        this.egoera = egoera;
        this.helbidea = helbidea;
        this.mota = mota;
        this.kolorea = null;
    }
    
    // Konstruktoa kolorearekin
    public Instalazioa(String izena, int sentsoreak, String egoera, String helbidea, String mota, Color kolorea) {
        this.izena = izena;
        this.sentsoreak = sentsoreak;
        this.egoera = egoera;
        this.helbidea = helbidea;
        this.mota = mota;
        this.kolorea = kolorea;
    }
        
    // Egoeraren arabera kolorea ezarri
    public Color getKolorEgoera() {
        if (egoera == null) return Color.BLACK;
        
        switch (egoera.toUpperCase()) {
            case "LARRIA":
            case "GRAVE":
            case "CRITICO":
                return Color.RED;
            case "NORMALA":
            case "NORMAL":
            case "OK":
                return Color.GREEN;
            case "KALTEA":
            case "DEFECTUOSO":
            case "INACTIVO":
                return Color.MAGENTA;
            case "MANTENIMIENTO":
                return Color.YELLOW;
            default:
                return Color.BLACK;
        }
    }
    
    // Background kolorea
    public Color getKolorFondo() {
        if (kolorea != null) {
            return kolorea;
        }
        
        // Colores por defecto según mota
        if (mota != null) {
            switch (mota.toUpperCase()) {
                case "OSPITALEA":
                    return new Color(220, 240, 255); // Azul claro
                case "UNIBERTSITATEA":
                case "IKASTOLA":
                    return new Color(240, 255, 220); // Verde claro
                case "FABRIKA":
                case "FACTORY":
                    return new Color(255, 240, 220); // Naranja claro
                case "OFICINA":
                case "OFFICE":
                    return new Color(240, 220, 255); // Lila claro
                case "ALMACEN":
                case "WAREHOUSE":
                    return new Color(255, 255, 220); // Amarillo claro
                case "LABORATORIO":
                    return new Color(255, 220, 220); // Rosa claro
                default:
                    return Color.WHITE;
            }
        }
        return Color.WHITE;
    }
    
    // Método para obtener color de borde, METODO HAU EZ DA ERABILTZEN
    public Color getKolorErtza() {
        if (mota != null) {
            switch (mota.toUpperCase()) {
                case "OSPITALEA":
                    return Color.BLUE;
                case "UNIBERTSITATEA":
                case "IKASTOLA":
                    return Color.GREEN.darker();
                case "FABRIKA":
                case "FACTORY":
                    return Color.ORANGE.darker();
                case "OFICINA":
                case "OFFICE":
                    return Color.MAGENTA.darker();
                case "ALMACEN":
                case "WAREHOUSE":
                    return Color.YELLOW.darker();
                case "LABORATORIO":
                    return Color.RED.darker();
                default:
                    return Color.GRAY;
            }
        }
        return Color.GRAY;
    }

    // Getters
    public String getIzena() {
        return izena;
    }
    
    public int getSentsoreak() {
        return sentsoreak;
    }
    
    public String getEgoera() {
        return egoera;
    }
    
    public String getHelbidea() {
        return helbidea;
    }
    
    public String getMota() {
        return mota;
    }
    
    public Color getKolorea() {
        return kolorea;
    }
    
    // Setters
    public void setSentsoreak(int sentsoreak) {
        this.sentsoreak = sentsoreak;
    }
    
    public void setEgoera(String egoera) {
        this.egoera = egoera;
    }
    
    public void setHelbidea(String helbidea) {
        this.helbidea = helbidea;
    }
    
    public void setMota(String mota) {
        this.mota = mota;
    }
    
    public void setKolorea(Color kolorea) {
        this.kolorea = kolorea;
    }
    
    // Método toString para debugging
    @Override
    public String toString() {
        return izena + " [" + mota + "] - " + sentsoreak + " sentsoreak - Egoera: " + egoera;
    }
    

}
