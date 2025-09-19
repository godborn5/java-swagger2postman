package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.net.http.*;
import java.net.URI;
import java.nio.file.*;
import java.time.Duration;

import org.json.JSONArray;
import org.json.JSONObject;

import Logica.ejecutar_bat;

public class api_modulo_tecnico extends JPanel {

    private JTextField modulo_tecnicoField;
    private JTextField authHeaderField;
    private JTextArea uuidTextArea;
    private JTextArea logArea;
    private JSONObject config;

    public api_modulo_tecnico() {
        setLayout(new BorderLayout());

        // ===== Cargar config.json =====
        try {
            String configContent = Files.readString(Paths.get("config.json"));
            config = new JSONObject(configContent);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "No se pudo cargar config.json: " + e.getMessage());
            config = new JSONObject(); // JSON vacío para evitar null pointer
        }

        // ===== Sección 1: Desde Endpoint =====
        JPanel endpointPanel = new JPanel(new GridLayout(0, 1, 5, 5));
        endpointPanel.setBorder(BorderFactory.createTitledBorder("📡 Obtener UUIDs desde módulo técnico"));

        modulo_tecnicoField = new JTextField();
        authHeaderField = new JTextField();

        endpointPanel.add(new JLabel("Nombre del módulo técnico:"));
        endpointPanel.add(modulo_tecnicoField);
        endpointPanel.add(new JLabel("Cabecera de autorización (Bearer token):"));
        endpointPanel.add(authHeaderField);

        JButton obtenerDesdeEndpointButton = new JButton("Obtener UUIDs y generar colecciones");
        endpointPanel.add(obtenerDesdeEndpointButton);

        // ===== Sección 2: Desde UUID manual =====
        JPanel uuidPanel = new JPanel(new BorderLayout(5, 5));
        uuidPanel.setBorder(BorderFactory.createTitledBorder("✍️ Introducir UUIDs manualmente (uno por línea)"));

        uuidTextArea = new JTextArea(5, 40);
        JScrollPane uuidScrollPane = new JScrollPane(uuidTextArea);

        JButton generarDesdeUUIDButton = new JButton("Generar colecciones desde UUIDs");

        uuidPanel.add(uuidScrollPane, BorderLayout.CENTER);
        uuidPanel.add(generarDesdeUUIDButton, BorderLayout.SOUTH);

        // ===== Área de log =====
        logArea = new JTextArea(10, 50);
        logArea.setEditable(false);
        JScrollPane logScrollPane = new JScrollPane(logArea);
        logScrollPane.setBorder(BorderFactory.createTitledBorder("📝 Log del proceso"));

        // Añadir todo al panel principal
        JPanel topPanel = new JPanel(new GridLayout(0, 1));
        topPanel.add(endpointPanel);
        topPanel.add(uuidPanel);

        add(topPanel, BorderLayout.NORTH);
        add(logScrollPane, BorderLayout.CENTER);

        // ===== Acciones de los botones =====
        obtenerDesdeEndpointButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String modulo = modulo_tecnicoField.getText().trim();
                String auth = authHeaderField.getText().trim();
                log("➡️ Consultando APIs para módulo técnico: " + modulo);
                generarColeccionesDesdeModuloTecnico(modulo, auth);
            }
        });

        generarDesdeUUIDButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String[] uuids = uuidTextArea.getText().split("\\s+");
                log("➡️ Generando colecciones para UUIDs: " + uuids.length);
                generarColeccionesDesdeUUIDs(uuids);
            }
        });
    }

    private void log(String mensaje) {
        logArea.append(mensaje + "\n");
    }

    // ====== Usando config.json para endpoints ======

    private void generarColeccionesDesdeModuloTecnico(String moduloTecnico, String authHeader) {
    try {
        // Leer el config.json
        String configContent = Files.readString(Paths.get("config.json"));
        JSONObject config = new JSONObject(configContent);

        String baseUrl = config.getString("baseUrl");
        JSONObject endpoints = config.getJSONObject("endpoints");
        JSONObject fields = config.getJSONObject("fields");
        JSONObject headersConfig = config.getJSONObject("headers");

        String moduloEndpoint = endpoints.getString("moduloTecnico").replace("{modulo_tecnico}", moduloTecnico);
        String uuidArrayPath = fields.getString("uuidArrayPath");
        String uuidField = fields.getString("uuidField");

        HttpClient client = HttpClient.newHttpClient();

        String url = baseUrl + moduloEndpoint;

        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .GET();

        // Cabeceras del config.json
        for (String key : headersConfig.keySet()) {
            requestBuilder.header(key, headersConfig.getString(key));
        }

        // Cabecera de Authorization desde GUI
        requestBuilder.header("Authorization", authHeader);

        HttpRequest request = requestBuilder.build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            log("❌ Error al consultar módulo técnico " + moduloTecnico + " (HTTP " + response.statusCode() + ")");
            return;
        }

        JSONObject json = new JSONObject(response.body());

        // Recorrer path dinámico para llegar al JSONArray de UUIDs
        String[] pathParts = uuidArrayPath.split("\\.");
        Object current = json;
        for (String part : pathParts) {
            if (current instanceof JSONObject) {
                current = ((JSONObject) current).get(part);
            } else {
                throw new RuntimeException("Ruta inválida en config.json: " + uuidArrayPath);
            }
        }

        if (!(current instanceof JSONArray)) {
            throw new RuntimeException("El path " + uuidArrayPath + " no apunta a un JSONArray");
        }

        JSONArray apisArray = (JSONArray) current;

        String[] uuids = new String[apisArray.length()];
        for (int i = 0; i < apisArray.length(); i++) {
            uuids[i] = apisArray.getJSONObject(i).getString(uuidField);
        }

        log("✅ UUIDs obtenidos: " + apisArray.length());
        generarColeccionesDesdeUUIDs(uuids);

    } catch (Exception e) {
        log("❌ Error al obtener UUIDs del módulo técnico: " + e.getMessage());
        e.printStackTrace();
    }
}


    private void generarColeccionesDesdeUUIDs(String[] uuids) {
    try {
        // Leer el config.json
        String configContent = Files.readString(Paths.get("config.json"));
        JSONObject config = new JSONObject(configContent);

        String baseUrl = config.getString("baseUrl");
        JSONObject endpoints = config.getJSONObject("endpoints");
        JSONObject fields = config.getJSONObject("fields");
        JSONObject headersConfig = config.getJSONObject("headers");

        String apiInfoEndpointTemplate = endpoints.getString("apiInfo");
        String swaggerField = fields.getString("swaggerField");

        HttpClient client = HttpClient.newHttpClient();

        // Token dinámico desde la GUI
        String authHeader = authHeaderField.getText().trim();

        for (String uuid : uuids) {
            if (!uuid.trim().isEmpty()) {
                log("🔧 Consultando API con UUID: " + uuid);
                try {
                    String apiInfoEndpoint = apiInfoEndpointTemplate.replace("{uuid}", uuid);
                    String apiUrl = baseUrl + apiInfoEndpoint;

                    HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                            .uri(URI.create(apiUrl))
                            .timeout(Duration.ofSeconds(20))
                            .GET();

                    // Cabeceras del config.json
                    for (String key : headersConfig.keySet()) {
                        requestBuilder.header(key, headersConfig.getString(key));
                    }

                    // Añadir token dinámico desde la GUI
                    if (!authHeader.isEmpty()) {
                        requestBuilder.header("Authorization",   authHeader);
                    }

                    HttpRequest request = requestBuilder.build();
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

                    if (response.statusCode() != 200) {
                        log("❌ Error al consultar API " + uuid + " (HTTP " + response.statusCode() + ")");
                        continue;
                    }

                    JSONObject jsonObj = new JSONObject(response.body());
                    String swaggerUrl = jsonObj.getJSONObject("data").getString(swaggerField);

                    String apiName = jsonObj.getJSONObject("data").getString("nombre");


                    log("📄 Swagger URL: " + swaggerUrl);

                    // Descargar Swagger
                    HttpRequest swaggerRequest = HttpRequest.newBuilder()
                            .uri(URI.create(swaggerUrl))
                            .timeout(Duration.ofSeconds(20))
                            .GET()
                            .build();

                    HttpResponse<String> swaggerResponse = client.send(swaggerRequest, HttpResponse.BodyHandlers.ofString());

                    if (swaggerResponse.statusCode() != 200) {
                        log("❌ Error al obtener Swagger del UUID " + uuid + " (HTTP " + swaggerResponse.statusCode() + ")");
                        continue;
                    }

                    // Guardar a archivo
                    Path carpetaDestino = Paths.get("swagger2postman_v3.0.210", "swagger2postman-md", "Swagger_files");
                    Files.createDirectories(carpetaDestino);

                    Path archivoDestino = carpetaDestino.resolve(uuid + ".yaml");
                    Files.write(archivoDestino, swaggerResponse.body().getBytes());

                    log("✅ Swagger guardado como: " + archivoDestino.toAbsolutePath());

                    ejecutar_bat.ejecutarBat(archivoDestino, apiName);

                } catch (Exception ex) {
                    log("❌ Error procesando UUID " + uuid + ": " + ex.getMessage());
                    ex.printStackTrace();
                }
            }
        }

    } catch (Exception e) {
        log("❌ Error al leer config.json o inicializar HTTP client: " + e.getMessage());
        e.printStackTrace();
    }
}


}
