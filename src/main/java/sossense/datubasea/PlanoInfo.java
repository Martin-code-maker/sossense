package sossense.datubasea;

public class PlanoInfo {
    private String nombreInstalacion;
    private String nombrePlano;
    private String imagenFondo;
    private int ancho;
    private int alto;
    private int sensoresMin;
    private int sensoresMax;
    
    public PlanoInfo(String nombreInstalacion, String nombrePlano, String imagenFondo, 
                     int ancho, int alto, int sensoresMin, int sensoresMax) {
        this.nombreInstalacion = nombreInstalacion;
        this.nombrePlano = nombrePlano;
        this.imagenFondo = imagenFondo;
        this.ancho = ancho;
        this.alto = alto;
        this.sensoresMin = sensoresMin;
        this.sensoresMax = sensoresMax;
    }
    
    // Getters
    public String getNombreInstalacion() { return nombreInstalacion; }
    public String getNombrePlano() { return nombrePlano; }
    public String getImagenFondo() { return imagenFondo; }
    public int getAncho() { return ancho; }
    public int getAlto() { return alto; }
    public int getSensoresMin() { return sensoresMin; }
    public int getSensoresMax() { return sensoresMax; }
}
