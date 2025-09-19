package GUI;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ConfigEditJson extends JPanel {

    private JTextArea jsonTextArea;
    private final Path configPath = Paths.get("config.json");

    public ConfigEditJson() {
        setLayout(new BorderLayout(10, 10));

        // ===== Área de texto para el JSON =====
        jsonTextArea = new JTextArea();
        jsonTextArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        jsonTextArea.setBackground(Color.BLACK);
        jsonTextArea.setForeground(Color.RED);
        jsonTextArea.setCaretColor(Color.WHITE);

        JScrollPane scrollPane = new JScrollPane(jsonTextArea);
        scrollPane.setBorder(BorderFactory.createTitledBorder("Editar config.json"));
        add(scrollPane, BorderLayout.CENTER);

        // ===== Botón guardar =====
        JButton saveButton = new JButton("Guardar JSON");
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(saveButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // ===== Acciones =====
        saveButton.addActionListener((ActionEvent e) -> saveConfig());

        // Cargar JSON existente al iniciar
        loadConfig();
    }

    private void loadConfig() {
        try {
            if (Files.exists(configPath)) {
                String content = new String(Files.readAllBytes(configPath));
                jsonTextArea.setText(formatJson(content));
            } else {
                // JSON de ejemplo inicial
                String defaultJson = """
                        {
                          "baseUrl": "https://apiscmintra.val.comunidad.madrid/t/dacm.comunidad.madrid/tec/gob-api/catalogo-apis/v1",
                          "endpoints": {
                            "moduloTecnico": "/modulos-tecnicos/{modulo}/apis?%24init=0&%24limit=25&%24total=true",
                            "apiInfo": "/apis/{uuid}?%24select=id&%24select=nombre&%24expand=subcategoria"
                          },
                          "fields": {
                            "uuidArrayPath": "data.apis",
                            "uuidField": "uuid",
                            "swaggerField": "definicion"
                          },
                          "headers": {
                            "accept": "application/json",
                            "x-trace-id": "12345abcde"
                          }
                        }
                        """;
                jsonTextArea.setText(defaultJson);
            }
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error cargando config.json: " + ex.getMessage());
        }
    }

    private void saveConfig() {
        try {
            String text = jsonTextArea.getText().trim();
            Files.write(configPath, text.getBytes());
            JOptionPane.showMessageDialog(this, "JSON guardado correctamente");
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(this, "Error guardando JSON: " + ex.getMessage());
        }
    }

    private String formatJson(String json) {
        try {
            org.json.JSONObject jsonObject = new org.json.JSONObject(json);
            return jsonObject.toString(4); // Pretty print con tabulación
        } catch (Exception e) {
            return json; // Si falla, devuelve tal cual
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Editor de config.json (Raw)");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);
        frame.setContentPane(new ConfigEditJson());
        frame.setVisible(true);
    }
}
