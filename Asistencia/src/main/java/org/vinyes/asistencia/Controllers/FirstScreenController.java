package org.vinyes.asistencia.Controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.vinyes.asistencia.NFCDriver.NFCReader;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class FirstScreenController {

    // que feo pero bueno
    static public Map<String, String> usuarios = new HashMap<>();

    // manual map
    static {
        usuarios.put("438B5A40", "JT");
        usuarios.put("D3F5AC40", "Alex");
        usuarios.put("0E22011E", "JT");
        usuarios.put("4B59001E", "Thomas");
        usuarios.put("5A1C011E", "Alex");
        usuarios.put("F043001E", "Sancho");
        usuarios.put("CA58001E", "S4QU3NM3D34QU1");
        usuarios.put("C5FF9DBB", "ÎLE-DE-FRANCE"); // tarjeta de prueba
        usuarios.put("04328042C96880", "CRTM MADRID"); // tarjeta de prueba
    }

    @FXML
    private Label onUpdateText;

    private final Random random = new Random();

    @FXML
    public void initialize() {
        // mira si se puede instanciar el NFCReader primero!
        NFCReader nfcReader = new NFCReader();
        nfcReader.iniciarTerminal(usuarios);
        readCards();
    }

    protected int v = 0;
    // por cada segundo...
    private void readCards() {
        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), event -> {
            // logica para ir leyendo cada 1 segundos un llavero, tarjeta...
            onUpdateText.setText("Esperando tarjeta...\nSegundos pasados desde la ejecucion: " + ++v);
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    @FXML
    protected void onManageUsers(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/vinyes/asistencia/login-screen.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Login");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void onExportCSV(ActionEvent actionEvent) {
    }
}
