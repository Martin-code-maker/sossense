package sossense.modelo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.List;
import java.util.stream.Collectors;

import sossense.datubasea.Instalazioa;
import sossense.datubasea.KudeatuInstalazioak;

public class SOSsenseModeloa {

    public static final String INSTALAZIOAK_ALDAKETA = "instalazioakAldatuta";

    private final KudeatuInstalazioak gestion;
    private final PropertyChangeSupport support;

    public SOSsenseModeloa() {
        this.gestion = new KudeatuInstalazioak();
        this.support = new PropertyChangeSupport(this);
    }

    public List<Instalazioa> lortuInstalazioak() {
        return gestion.getInstalaciones();
    }

    public List<Instalazioa> bilatuInstalazioak(String filtro) {
        if (filtro == null || filtro.trim().isEmpty()) {
            return lortuInstalazioak();
        }
        final String normalized = filtro.toLowerCase().trim();
        return gestion.getInstalaciones().stream()
                .filter(inst -> inst.getIzena().toLowerCase().contains(normalized)
                        || inst.getMota().toLowerCase().contains(normalized)
                        || inst.getHelbidea().toLowerCase().contains(normalized))
                .collect(Collectors.toList());
    }

    public Instalazioa bilatuInstalazioa(String izena) {
        return gestion.buscarInstalacion(izena);
    }

    public void gehituInstalazioa(Instalazioa instalazioa) {
        List<Instalazioa> before = gestion.getInstalaciones();
        gestion.agregarInstalacion(instalazioa);
        support.firePropertyChange(INSTALAZIOAK_ALDAKETA, before, gestion.getInstalaciones());
    }

    public String lortuEstadistikak() {
        return gestion.getEstadisticas();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        support.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        support.removePropertyChangeListener(listener);
    }
}
