package org.vinyes.asistencia.NFCDriver;
import org.vinyes.asistencia.Entities.Usuario;

import javax.smartcardio.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.HashMap;

public class NFCReader {
    private static final String EXPECTED_MODEL = "ACS ACR122 0";

    private byte[] ISOCMD = {
            (byte) 0xFF,
            (byte) 0xCA,
            0x00,
            0x00,
            0x00
    };

    private Map<String, Boolean> estadoFichaje; // para almacenar el estado de fichaje de cada usuario

    public NFCReader() {
        estadoFichaje = new HashMap<>(); // inicializamos el mapa
    }

    public void iniciarTerminal(Map<String, String> bbdd) {
        System.out.println("[DEBUG] Instanciado la clase terminal");
    }

    // hell nah
    public static String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }
}