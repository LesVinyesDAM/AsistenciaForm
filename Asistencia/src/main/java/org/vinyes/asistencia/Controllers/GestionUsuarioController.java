package org.vinyes.asistencia.Controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.vinyes.asistencia.Database.RegistroDAO;
import org.vinyes.asistencia.Entities.Usuario;

public class GestionUsuarioController {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, String> colUUID;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colDepartamento;
    @FXML private TextField txtNombre;
    @FXML private TextField txtDepartamento;
    @FXML private Button btnEliminar;
    @FXML private Button btnModificar;

    private ObservableList<Usuario> listaUsuarios = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colUUID.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getUuid()));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombreCompleto()));
        colDepartamento.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getDepartamento()));

        cargarUsuarios();

        // tenemos que ir escuchando que usuario se ha seleccionado para modificar
        tablaUsuarios.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                txtNombre.setText(newSelection.getNombreCompleto());
                txtDepartamento.setText(newSelection.getDepartamento());
            }
        });
    }

    private void cargarUsuarios() {
        listaUsuarios.setAll(RegistroDAO.obtenerTodosLosUsuarios());
        tablaUsuarios.setItems(listaUsuarios);
    }

    @FXML
    public void handleEliminarUsuario() {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION, "¿Seguro que deseas eliminar a " + seleccionado.getNombreCompleto() + "?", ButtonType.YES, ButtonType.NO);
            confirmacion.showAndWait().ifPresent(response -> {
                if (response == ButtonType.YES) {
                    RegistroDAO.eliminarUsuario(seleccionado.getUuid());
                    listaUsuarios.remove(seleccionado);
                    txtNombre.clear();
                    txtDepartamento.clear();
                    System.out.println("Usuario eliminado: " + seleccionado.getNombreCompleto());
                }
            });
        } else {
            Alert alerta = new Alert(Alert.AlertType.WARNING, "Selecciona un usuario para eliminar.");
            alerta.show();
        }
    }

    @FXML
    public void handleModificarUsuario(ActionEvent actionEvent) {
        Usuario seleccionado = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (seleccionado != null) {
            String nuevoNombre = txtNombre.getText().trim();
            String nuevoDepartamento = txtDepartamento.getText().trim();

            if (!nuevoNombre.isEmpty() && !nuevoDepartamento.isEmpty()) {
                RegistroDAO.actualizarUsuario(seleccionado.getUuid(), nuevoNombre, nuevoDepartamento);
                seleccionado.setNombreCompleto(nuevoNombre);
                seleccionado.setDepartamento(nuevoDepartamento);
                tablaUsuarios.refresh();
                System.out.println("Usuario actualizado: " + nuevoNombre);
            } else {
                Alert alerta = new Alert(Alert.AlertType.WARNING, "Por favor, ingresa un nombre y un departamento.");
                alerta.show();
            }
        } else {
            Alert alerta = new Alert(Alert.AlertType.WARNING, "Selecciona un usuario para modificar.");
            alerta.show();
        }
    }
}
