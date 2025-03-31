package org.vinyes.asistencia.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.collections.ObservableList;
import org.vinyes.asistencia.Database.RegistroDAO;

public class MRegistrosController {
    @FXML private TextArea textAreaUsuario;

    public void cargarRegistrosUsuario(String uuid) {
        ObservableList<String> registros = RegistroDAO.obtenerRegistrosPorUsuario(uuid);

        if (registros.isEmpty()) {
            textAreaUsuario.setText("No hay registros disponibles para este usuario.");
        } else {
            StringBuilder texto = new StringBuilder();
            registros.forEach(registro -> texto.append(registro).append("\n"));
            textAreaUsuario.setText(texto.toString());
        }
    }
}
