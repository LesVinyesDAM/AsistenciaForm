package org.vinyes.asistencia.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class UsuariosScreenController {

    @FXML
    public TextArea textAreaRegistros;

    @FXML
    public void setupTextbox(String texto) {
        leerArchivo("C:\\Users\\JT\\Documents\\GitHub\\Asistencia\\AsistenciaForm\\Asistencia\\fichaje.txt");
        textAreaRegistros.appendText(texto + "\n");
    }

    public void leerArchivo(String rutaArchivo) {
        try (BufferedReader br = new BufferedReader(new FileReader(rutaArchivo))) {
            String linea;
            textAreaRegistros.clear();
            while ((linea = br.readLine()) != null) {
                setupTextbox(linea);
            }
        } catch (IOException e) {
            setupTextbox("No se pudo leer el archivo. Error: " + e.getMessage());
        }
    }
}