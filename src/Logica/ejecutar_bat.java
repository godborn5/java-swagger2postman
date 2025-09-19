package Logica;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.io.InputStream;


public class ejecutar_bat {
 
    



public static void ejecutarBat(Path archivoYaml, String nombreApi) {
    try {
        // Carpeta donde está el .bat
        Path carpetaBat = Paths.get("swagger2postman_v3.0.210", "swagger2postman-md");

        // Ruta absoluta para node local
        String rutaNodeLocal = Paths.get("node-v22.15.0-win-x64").toAbsolutePath().toString();

        // Construimos el comando para ejecutar el .bat con argumentos
        String comando = "swagger2postman.bat " + archivoYaml.getFileName() + " " + nombreApi;

        ProcessBuilder pb = new ProcessBuilder("cmd.exe", "/c", comando);

        // Establecer carpeta de trabajo (donde está el .bat)
        pb.directory(carpetaBat.toFile());

        // Copiamos las variables de entorno y añadimos node local al PATH
        Map<String, String> env = pb.environment();

        String pathActual = env.get("PATH");
        if (pathActual == null) pathActual = "";

        // Añadimos la ruta de node local al principio del PATH
        env.put("PATH", rutaNodeLocal + ";" + pathActual);

        // Iniciar proceso
        Process proceso = pb.start();

        // Opcional: leer salida estándar y error para debug
        new Thread(() -> {
            try (InputStream in = proceso.getInputStream()) {
                int b;
                while ((b = in.read()) != -1) {
                    System.out.print((char) b);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        new Thread(() -> {
            try (InputStream err = proceso.getErrorStream()) {
                int b;
                while ((b = err.read()) != -1) {
                    System.err.print((char) b);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();

        // Esperar a que termine (opcional)
        int exitCode = proceso.waitFor();
        System.out.println("Proceso finalizado con código: " + exitCode);

    } catch (Exception e) {
        e.printStackTrace();
        //estadoLabel.setText("Error al ejecutar el .bat");
    }
}


}
