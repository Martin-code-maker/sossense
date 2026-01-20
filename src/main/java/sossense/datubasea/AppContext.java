package sossense.datubasea;

public class AppContext {
    private PanelPlano panelPlanoActivo;
    private String instalacionActiva = "";
    private String planoActivo = "";

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
}
