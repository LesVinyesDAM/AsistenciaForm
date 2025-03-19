package org.vinyes.asistencia.NFCDriver;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import javax.smartcardio.*;
import java.util.List;

public class NFCReader {
    private static final String EXPECTED_MODEL = "ACS ACR122 0";
    private final StringProperty cardInfo = new SimpleStringProperty("Esperando tarjeta...");

    public StringProperty cardInfoProperty() {
        return cardInfo;
    }

    private final byte[] ISOCMD = {
            (byte) 0xFF,
            (byte) 0xCA,
            0x00,
            0x00,
            0x00
    };

    public void iniciarLectura() {
        new Thread(() -> {
            while (true) {  // Keep running indefinitely
                Card tarjeta = null;
                try {
                    TerminalFactory factory = TerminalFactory.getDefault();
                    CardTerminals lectores = factory.terminals();
                    List<CardTerminal> terminales = lectores.list();

                    // esto casi que es boilerplate...
                    if (terminales.isEmpty()) {
                        Platform.runLater(() -> cardInfo.set("No hay lectores disponibles."));
                        Thread.sleep(1000);
                        continue;
                    }

                    CardTerminal lector = terminales.get(0);  // Obtener el primer lector disponible
                    Platform.runLater(() -> cardInfo.set("Esperando tarjeta..."));

                    lector.waitForCardPresent(0);
                    tarjeta = lector.connect("*");
                    CardChannel canal = tarjeta.getBasicChannel();
                    CommandAPDU apdu = new CommandAPDU(ISOCMD);
                    ResponseAPDU response = canal.transmit(apdu);

                    byte[] uid = response.getData();
                    String SUID = bytesToHex(uid);

                    Platform.runLater(() -> {
                        if (SUID.isEmpty()) {
                            cardInfo.set("Error al leer la tarjeta.");
                        } else {
                            cardInfo.set("Tarjeta detectada: " + SUID);
                        }
                    });

                    tarjeta.disconnect(false);
                    lector.waitForCardAbsent(0);
                } catch (Exception e) {
                    Platform.runLater(() -> cardInfo.set("Error: " + e.getMessage()));
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ignored) {}
                } finally {
                    if (tarjeta != null) {
                        try {
                            tarjeta.disconnect(false);
                        } catch (CardException ignored) {
                        }
                    }
                }
            }
        }).start();
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}
