package sossense.modelo;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.IOException;

import sossense.bista.LoginPanela;
import sossense.utils.UsuarioUtils;

public class LoginModeloa {

    PropertyChangeSupport konektorea;
    private LoginPanela bista;
    public static final String PROPIETATEA = "SarreraKontrola";

    public LoginModeloa() {
        konektorea = new PropertyChangeSupport(this);
    }

    public void datuakKonprobatu() {
        String sartutakoP = bista.getPassword();
        String sartutakoU = bista.getErabiltzailea();
        
        try {
            if (UsuarioUtils.loginValido(sartutakoU, sartutakoP)) {
                konektorea.firePropertyChange(PROPIETATEA, null, "ok");
            } else {
                bista.setPassword("");
                konektorea.firePropertyChange(PROPIETATEA, null, "errorea");
            }
        } catch (IOException e) {
            e.printStackTrace();
            bista.setPassword("");
            konektorea.firePropertyChange(PROPIETATEA, null, "errorea");
        }
    }

    public void itxiLehioa() {
        konektorea.firePropertyChange(PROPIETATEA, null, "itxi");
    }

    public void setBista(LoginPanela bista) {
        this.bista = bista;
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        konektorea.addPropertyChangeListener(listener);
    }
    public void removePropertyChangeListener(PropertyChangeListener listener) {
        konektorea.removePropertyChangeListener(listener);
    }


}
