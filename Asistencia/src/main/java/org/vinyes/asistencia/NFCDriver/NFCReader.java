package org.vinyes.asistencia.NFCDriver;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.vinyes.asistencia.Database.RegistroDAO;
import org.vinyes.asistencia.Entities.Usuario;

import javax.smartcardio.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NFCReader {
    private static final String EXPECTED_MODEL = "ACS ACR122 0";
    private final StringProperty cardInfo = new SimpleStringProperty("Esperando llavero/tarjeta...");
    private volatile boolean keepRunning = true;  // flag para controlar el hilo

    public StringProperty cardInfoProperty() {
        return cardInfo;
    }

    DatabaseWriter dw = new DatabaseWriter();

    private final byte[] ISOCMD = {
            (byte) 0xFF,
            (byte) 0xCA,
            0x00,
            0x00,
            0x00
    };

    public void iniciarLectura(boolean isRegistering) {
        new Thread(() -> {
            while (keepRunning) {
                Card tarjeta = null;
                try {
                    TerminalFactory factory = TerminalFactory.getDefault();
                    CardTerminals lectores = factory.terminals();
                    List<CardTerminal> terminales = lectores.list();

                    if (terminales.isEmpty()) {
                        Platform.runLater(() -> cardInfo.set("No hay lectores disponibles."));
                        Thread.sleep(1000);
                        continue;
                    }

                    CardTerminal lector = terminales.get(0);
                    Platform.runLater(() -> cardInfo.set("Esperando llavero/tarjeta..."));

                    if (!lector.waitForCardPresent(1000)) {
                        continue;  // si no hay tarjeta, vuelve a empezar el loop
                    }

                    tarjeta = lector.connect("*");
                    CardChannel canal = tarjeta.getBasicChannel();
                    CommandAPDU apdu = new CommandAPDU(ISOCMD);
                    ResponseAPDU response = canal.transmit(apdu);

                    byte[] uid = response.getData();
                    String SUID = bytesToHex(uid);

                    Platform.runLater(() -> {
                        if (SUID.isEmpty()) {
                            cardInfo.set("Error al leer el llavero/tarjeta.");
                        } else {
                            Usuario user = RegistroDAO.obtenerUsuarioPorSUID(SUID);

                            Date date = new Date();
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                            if (isRegistering) {
                                // no escribas el csv y mata el thread
                                keepRunning = false;
                                cardInfo.set(SUID);
                            } else {
                                System.out.println("Esta el usuario fichado? - " + user.isFichado());
                                user.setFichado(!user.isFichado());
                                cardInfo.set("Fichaje de " + (user.isFichado() ? "entrada" : "salida") + ": " + user.getNombreCompleto() + " del departament: " + user.getDepartamento() +
                                        "\nA las: " + sdf.format(date));
                                String csv = SUID + "," + user.getNombreCompleto() + "," + sdf.format(date) + ","+ user.isFichado();
                                dw.escribirNuevaLinea(csv); // se escribe a la bbdd
                            }
                        }
                    });


                    tarjeta.disconnect(false);
                    lector.waitForCardAbsent(0);
                } catch (Exception e) {
                    Platform.runLater(() -> cardInfo.set("Lector no encontrado, conecte uno."));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                } finally {
                    if (tarjeta != null) {
                        try {
                            tarjeta.disconnect(false);
                        } catch (CardException ex) {
                            Platform.runLater(() -> cardInfo.set("Error during disconnect: " + ex.getMessage()));
                        }
                    }
                }
            }
        }).start();
    }

    public void detenerLectura() {
        keepRunning = false;
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
