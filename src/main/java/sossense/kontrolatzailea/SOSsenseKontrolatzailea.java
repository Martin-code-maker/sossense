package sossense.kontrolatzailea;

import java.util.List;
import java.beans.PropertyChangeListener;

import sossense.datubasea.Instalazioa;
import sossense.modelo.SOSsenseModeloa;

public class SOSsenseKontrolatzailea {

    private final SOSsenseModeloa model;

    public SOSsenseKontrolatzailea(SOSsenseModeloa model) {
        this.model = model;
    }

    public List<Instalazioa> lortuInstalazioak() {
        return model.lortuInstalazioak();
    }

    public List<Instalazioa> bilatuInstalazioak(String filtro) {
        return model.bilatuInstalazioak(filtro);
    }

    public Instalazioa bilatuInstalazioa(String izena) {
        return model.bilatuInstalazioa(izena);
    }

    public void gehituInstalazioa(String izena, int sentsoreak, String egoera, String helbidea, String mota) {
        Instalazioa instalazioa = new Instalazioa(izena, sentsoreak, "", helbidea, mota);
        model.gehituInstalazioa(instalazioa);
    }

    public String lortuEstadistikak() {
        return model.lortuEstadistikak();
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        model.addPropertyChangeListener(listener);
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        model.removePropertyChangeListener(listener);
    }
}
