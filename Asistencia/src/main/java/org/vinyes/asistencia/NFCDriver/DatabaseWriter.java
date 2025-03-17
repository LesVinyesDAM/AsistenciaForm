package org.vinyes.asistencia.NFCDriver;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

// Simulando la carga de fichajes anteriores en la base de datos
public class DatabaseWriter {

    public DatabaseWriter() {
    }

    public void escribirNuevaLinea(String csv) {
        // procesa los datos hacia la csv;
        // uid, name, time, status
        String[] csvList = csv.split(",");
        String info = "Profesor: " + csvList[1] + " ha " + (csvList[3].equals("true") ? "entrado" : "salido")  + " a las " + csvList[2] + ", con identificador: " + csvList[0];
        File file = new File("fichaje.txt");
        try (FileWriter fw = new FileWriter(file, true)) {  // Modo append
            fw.write(info + "\n");  // Escribe la línea en el archivo
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir en el archivo", e);
        }

        // porque arrays to string es ASFDSADFDSAFASDF
        String formatoArray = csvList[0] + "," + csvList[1] + "," + csvList[2] + "," + csvList[3];

        File csvFile = new File("database.csv");
        try (FileWriter csvfw = new FileWriter(csvFile, true)) {  // Modo append
            csvfw.write( formatoArray + "\n");  // Escribe la línea en el archivo
        } catch (IOException e) {
            throw new RuntimeException("Error al escribir en el archivo", e);
        }
    }
}