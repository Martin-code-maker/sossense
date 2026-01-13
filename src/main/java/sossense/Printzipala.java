package sossense;


import sossense.bista.LoginPanela;
import sossense.kontrolatzailea.LoginKontrolatzailea;
import sossense.modelo.LoginModeloa;

public class Printzipala {

    LoginPanela loginPanela;

    public void hasieratu() {
        LoginModeloa m = new LoginModeloa();
        LoginKontrolatzailea k = new LoginKontrolatzailea(m);
        loginPanela = new LoginPanela(m,k);
    }

    public static void main(String[] args) {
        Printzipala printzipala = new Printzipala();
        printzipala.hasieratu();
    }
}
