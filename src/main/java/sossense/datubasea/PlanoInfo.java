package sossense.datubasea;

public class PlanoInfo {
    private String nombreInstalacion;
    private String nombrePlano;
    private String imagenFondo;
    private int ancho;
    private int alto;
    private int sensoresMin;
    private int sensoresMax;
    private java.util.List<SensorLayout> sensoresDefinidos;

    public PlanoInfo(String nombreInstalacion, String nombrePlano, String imagenFondo,
            int ancho, int alto, int sensoresMin, int sensoresMax) {
        this(nombreInstalacion, nombrePlano, imagenFondo, ancho, alto, sensoresMin, sensoresMax,
                new java.util.ArrayList<>());
    }

    public PlanoInfo(String nombreInstalacion, String nombrePlano, String imagenFondo,
            int ancho, int alto, int sensoresMin, int sensoresMax,
            java.util.List<SensorLayout> sensoresDefinidos) {
        this.nombreInstalacion = nombreInstalacion;
        this.nombrePlano = nombrePlano;
        this.imagenFondo = imagenFondo;
        this.ancho = ancho;
        this.alto = alto;
        this.sensoresMin = sensoresMin;
        this.sensoresMax = sensoresMax;
        this.sensoresDefinidos = sensoresDefinidos != null ? new java.util.ArrayList<>(sensoresDefinidos)
                : new java.util.ArrayList<>();
    }

    public String getNombreInstalacion() {
        return nombreInstalacion;
    }

    public String getNombrePlano() {
        return nombrePlano;
    }

    public String getImagenFondo() {
        return imagenFondo;
    }

    public int getAncho() {
        return ancho;
    }

    public int getAlto() {
        return alto;
    }

    public int getSensoresMin() {
        return sensoresMin;
    }

    public int getSensoresMax() {
        return sensoresMax;
    }

    public java.util.List<SensorLayout> getSensoresDefinidos() {
        return new java.util.ArrayList<>(sensoresDefinidos);
    }

    public boolean tieneSensoresDefinidos() {
        return sensoresDefinidos != null && !sensoresDefinidos.isEmpty();
    }

    public void setNombreInstalacion(String nombreInstalacion) {
        this.nombreInstalacion = nombreInstalacion;
    }
}
