package sossense.utils;

import java.io.*;

public class UsuarioUtils {

    public static boolean loginValido(String usuario, String contraseña) throws IOException {
        if (usuario == null || usuario.trim().isEmpty() || contraseña == null) {
            return false;
        }
        
        String hash = HashUtils.sha256(contraseña);
        System.out.println("DEBUG - Erabiltzailea: " + usuario + ", Hash kalkulatua: " + hash);

        // Buscar usuarios.txt en la carpeta datos
        String projectRoot = System.getProperty("user.dir");
        File file = new File(projectRoot, "datos/usuarios.txt");
        
        // Si no existe en datos/, intentar en sossense/datos/
        if (!file.exists()) {
            file = new File(projectRoot, "sossense/datos/usuarios.txt");
        }
        
        System.out.println("DEBUG - Fitxategia bilatzen: " + file.getAbsolutePath());
        
        if (!file.exists()) {
            System.out.println("ERROR - Fitxategia ez da aurkitu: " + file.getAbsolutePath());
            return false;
        }

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String linea;
            while ((linea = br.readLine()) != null) {
                linea = linea.trim();
                if (linea.isEmpty() || linea.startsWith("//")) continue;
                
                String[] partes = linea.split(":");
                if (partes.length != 2) continue;
                
                String usuarioArchivo = partes[0].trim();
                String hashArchivo = partes[1].trim();
                
                System.out.println("DEBUG - Konparatzen: " + usuarioArchivo + ":" + hashArchivo);
                
                if (usuarioArchivo.equals(usuario) && hashArchivo.equals(hash)) {
                    System.out.println("DEBUG - Login baliozkoa!");
                    return true;
                }
            }
        }
        System.out.println("DEBUG - Login huts egin du");
        return false;
    }
}
