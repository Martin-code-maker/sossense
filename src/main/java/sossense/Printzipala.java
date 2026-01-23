package sossense;


import sossense.bista.LoginPanela;
import sossense.kontrolatzailea.LoginKontrolatzailea;
import sossense.modelo.LoginModeloa;
import java.io.FileWriter;
import java.io.IOException;

public class Printzipala {

    LoginPanela loginPanela;

    public void hasieratu() {
        // Vaciar el archivo datuak.txt al iniciar
        vaciarArchivoLogs();
        
        LoginModeloa m = new LoginModeloa();
        LoginKontrolatzailea k = new LoginKontrolatzailea(m);
        loginPanela = new LoginPanela(m,k);
    }
    
    private void vaciarArchivoLogs() {
        try (FileWriter fw = new FileWriter("logs/datuak.txt")) {
            // Escribir nada (el archivo se vacía)
            System.out.println("[LOG] datuak.txt hutsik hasieratuta");
        } catch (IOException e) {
            System.err.println("[ERROR] Ezin izan da fitxategia hutsik jarri: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        Printzipala printzipala = new Printzipala();
        printzipala.hasieratu();
    }
}