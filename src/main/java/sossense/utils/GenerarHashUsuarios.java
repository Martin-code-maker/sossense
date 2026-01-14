package sossense.utils;

import java.io.Console;
import java.util.Arrays;
import java.util.Scanner;

public final class GenerarHashUsuarios {

    private GenerarHashUsuarios() {
    }

    public static void main(String[] args) {
        Console console = System.console();

        String usuario;
        char[] passwordChars;

        if (console != null) {
            usuario = console.readLine("Usuario (opcional, ENTER para omitir): ");
            passwordChars = console.readPassword("Contraseña: ");
        } else {
            Scanner sc = new Scanner(System.in);
            System.out.print("Usuario (opcional, ENTER para omitir): ");
            usuario = sc.nextLine();
            System.out.print("Contraseña (se verá al escribirla): ");
            passwordChars = sc.nextLine().toCharArray();
        }

        String password = new String(passwordChars);
        Arrays.fill(passwordChars, '\0');

        String hash = HashUtils.sha256(password);

        System.out.println("\nHash (SHA-256):");
        System.out.println(hash);

        if (usuario != null && !usuario.trim().isEmpty()) {
            System.out.println("\nLínea para usuarios.txt:");
            System.out.println(usuario.trim() + ":" + hash);
        }
    }
}
