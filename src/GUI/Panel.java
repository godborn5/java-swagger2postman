package GUI;
import Logica.ejecutar_bat;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DnDConstants;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;




public class Panel extends JPanel {

    private JButton seleccionarButton;
    private JLabel estadoLabel;

    public Panel() {
        setLayout(new BorderLayout());
    
        seleccionarButton = new JButton("Seleccionar archivo YAML");
        estadoLabel = new JLabel("Selecciona o arrastra un archivo .yaml o .yml", SwingConstants.CENTER);
    
        // NUEVO botón para abrir panel extra
        JButton abrirUUIDPanelButton = new JButton("Crear colecciones por URL/UUID");
    
        // Área de drop central con texto + imagen
        JLabel dropArea = new JLabel("⬇️ Arrastra aquí tu archivo YAML", SwingConstants.CENTER);
        dropArea.setFont(new Font("Arial", Font.BOLD, 16));
        dropArea.setBorder(BorderFactory.createDashedBorder(Color.GRAY));
        dropArea.setPreferredSize(new Dimension(400, 200));
        dropArea.setHorizontalAlignment(SwingConstants.CENTER);
        dropArea.setVerticalAlignment(SwingConstants.CENTER);
    
        try {
            ImageIcon icono = new ImageIcon("icono_guardar.png");
            dropArea.setIcon(icono);
            dropArea.setHorizontalTextPosition(SwingConstants.CENTER);
            dropArea.setVerticalTextPosition(SwingConstants.BOTTOM);
        } catch (Exception e) {
            System.out.println("No se pudo cargar el icono.");
        }
    
        // Panel superior con los dos botones
        JPanel botonesPanel = new JPanel(new FlowLayout());
        botonesPanel.add(seleccionarButton);
        botonesPanel.add(abrirUUIDPanelButton); // Añadimos el nuevo botón aquí
    
        add(botonesPanel, BorderLayout.NORTH);
        add(dropArea, BorderLayout.CENTER);
        add(estadoLabel, BorderLayout.SOUTH);
    
        seleccionarButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int resultado = fileChooser.showOpenDialog(this);
    
            if (resultado == JFileChooser.APPROVE_OPTION) {
                File archivoSeleccionado = fileChooser.getSelectedFile();
                if (archivoSeleccionado.getName().endsWith(".yaml") || archivoSeleccionado.getName().endsWith(".yml")) {
                    copiarArchivo(archivoSeleccionado);
                } else {
                    estadoLabel.setText("Por favor, selecciona un archivo .yaml o .yml válido.");
                }
            }
        });
    
        // Acción para abrir nuevo marco con el panel api_modulo_tecnico
        abrirUUIDPanelButton.addActionListener(e -> {
            JFrame frame = new JFrame("Generar Colecciones desde API Manager");
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.getContentPane().add(new api_modulo_tecnico());
            frame.pack();
            frame.setLocationRelativeTo(null); // Centrado
            frame.setVisible(true);
        });
    
        // Soporte para arrastrar y soltar archivos .yaml o .yml
        new DropTarget(dropArea, new DropTargetAdapter() {
            @Override
            public void drop(DropTargetDropEvent dtde) {
                try {
                    dtde.acceptDrop(DnDConstants.ACTION_COPY);
                    Transferable t = dtde.getTransferable();
                    Object data = t.getTransferData(DataFlavor.javaFileListFlavor);
    
                    if (data instanceof java.util.List) {
                        @SuppressWarnings("unchecked")
                        java.util.List<File> archivos = (java.util.List<File>) data;
    
                        for (File archivo : archivos) {
                            if (archivo.getName().endsWith(".yaml") || archivo.getName().endsWith(".yml")) {
                                copiarArchivo(archivo);
                            } else {
                                estadoLabel.setText("Solo se permiten archivos .yaml o .yml");
                            }
                        }
                    }
    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    estadoLabel.setText("Error al procesar el archivo arrastrado.");
                }
            }
        });
    }
    
    

    private void copiarArchivo(File archivoOriginal) {
        try {
            // Ruta destino dentro del proyecto (asegurándonos que exista)
            Path carpetaDestino = Paths.get("swagger2postman_v3.0.210", "swagger2postman-md", "Swagger_files");
            if (!Files.exists(carpetaDestino)) {
                Files.createDirectories(carpetaDestino);
            }
    
            Path destino = carpetaDestino.resolve(archivoOriginal.getName());
    
            // Copiar archivo
            Files.copy(archivoOriginal.toPath(), destino, StandardCopyOption.REPLACE_EXISTING);
            estadoLabel.setText("Archivo copiado: " + destino.toString());
    
            // Mostrar confirmación para ejecutar la conversión
            int opcion = JOptionPane.showConfirmDialog(
                    this,
                    "¿Desea crear las colecciones a través de este archivo?",
                    "Crear colecciones",
                    JOptionPane.YES_NO_OPTION
            );
    
            if (opcion == JOptionPane.YES_OPTION) {
                ejecutar_bat.ejecutarBat(destino,"ejemplo");
                
            } else {
                // Reiniciar la aplicación (cerrar y volver a abrir)
                reiniciarAplicacion();
            }
    
        } catch (IOException ex) {
            ex.printStackTrace();
            estadoLabel.setText("Error al copiar el archivo.");
        }
    }
    

    private void reiniciarAplicacion() {
        System.out.println("reiniciar aplicacion");
    }
    

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
    }
}

