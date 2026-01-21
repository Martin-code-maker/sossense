package sossense.datubasea;

public class AppContext {
    private PanelPlano panelPlanoActivo;
    private String instalacionActiva = "";
    private String planoActivo = "";
    // Sensor values in % keyed as instalacion|plano|sensor
    private final java.util.Map<String, Integer> sensorValores = new java.util.concurrent.ConcurrentHashMap<>();

    public PanelPlano getPanelPlanoActivo() {
        return panelPlanoActivo;
    }

    public void setPanelPlanoActivo(PanelPlano panelPlanoActivo) {
        this.panelPlanoActivo = panelPlanoActivo;
    }

    public String getInstalacionActiva() {
        return instalacionActiva;
    }

    public void setInstalacionActiva(String instalacionActiva) {
        this.instalacionActiva = instalacionActiva;
    }

    public String getPlanoActivo() {
        return planoActivo;
    }

    public void setPlanoActivo(String planoActivo) {
        this.planoActivo = planoActivo;
    }

    public void setSensorValue(String instalacion, String plano, String sensorId, int valorPorcentaje) {
        sensorValores.put(clave(instalacion, plano, sensorId), valorPorcentaje);
    }

    public java.util.Map<String, Integer> getSensorValoresSnapshot() {
        return new java.util.HashMap<>(sensorValores);
    }

    public int contarCriticos(String instalacion, String plano) {
        int count = 0;
        String prefix = instalacion.toLowerCase() + "|" + plano.toLowerCase() + "|";
        for (java.util.Map.Entry<String, Integer> e : sensorValores.entrySet()) {
            if (e.getKey().toLowerCase().startsWith(prefix) && e.getValue() >= 70) count++;
        }
        return count;
    }

    public int contarAlertas(String instalacion, String plano) {
        int count = 0;
        String prefix = instalacion.toLowerCase() + "|" + plano.toLowerCase() + "|";
        for (java.util.Map.Entry<String, Integer> e : sensorValores.entrySet()) {
            int v = e.getValue();
            if (e.getKey().toLowerCase().startsWith(prefix) && v >= 30 && v < 70) count++;
        }
        return count;
    }

    private String clave(String instalacion, String plano, String sensor) {
        return instalacion + "|" + plano + "|" + sensor;
    }
}
