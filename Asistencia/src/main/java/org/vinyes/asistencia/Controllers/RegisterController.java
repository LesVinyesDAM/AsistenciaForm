package org.vinyes.asistencia.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.util.Duration;
import org.vinyes.asistencia.NFCDriver.NFCReader;

public class RegisterController {
    public TextField onPassCardNfc;
    public TextField nameField;
    public TextField surnameField;
    public TextField departmentField;
    public Button cancelButton;
    public Button registerButton;

    @FXML
    public void initialize() {
        NFCReader nfcReader = new NFCReader(); // este se ejectua 1 sola vez
        onPassCardNfc.textProperty().bind(nfcReader.cardInfoProperty());
        nfcReader.iniciarLectura(true);
    }

    public void handleCancel(ActionEvent actionEvent) {

    }

    public void handleRegister(ActionEvent actionEvent) {

    }
}
