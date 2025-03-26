package org.vinyes.asistencia.Database;

import org.vinyes.asistencia.Entities.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RegistroDAO {
    private static Usuario noUser = new Usuario(null, "Usuario sin identificar", null);
    public static void insertarRegistro(String uid, String fecha, String tipo) {
        System.out.println("Registro esto: " + uid + ","+fecha+","+tipo);
        String sql = "INSERT INTO registro (uid, fecha, tipo) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uid);
            pstmt.setString(2, fecha);
            pstmt.setString(3, tipo);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Obtener un usuario por su SUID (ahora también con departamento)
    public static Usuario obtenerUsuarioPorSUID(String suid) {
        String sql = "SELECT nombre, departamento FROM usuario WHERE uid = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, suid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                String nombre = rs.getString("nombre");
                String departamento = rs.getString("departamento");
                return new Usuario(suid, nombre, departamento);
            } else return noUser;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Si no se encuentra el usuario
    }

    // Método para obtener el último fichaje del usuario
    public static boolean obtenerEstadoFichaje(String suid) {
        String sql = "SELECT tipo FROM registro WHERE uid = ? ORDER BY fecha DESC LIMIT 1";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, suid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getString("tipo").equals("entrada"); // Devuelve true si el último fichaje fue entrada
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false; // Si no hay registros, se asume que no está fichado
    }
}