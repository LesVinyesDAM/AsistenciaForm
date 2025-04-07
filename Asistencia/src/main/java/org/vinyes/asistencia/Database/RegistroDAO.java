package org.vinyes.asistencia.Database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.vinyes.asistencia.Entities.Usuario;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

    public static void registrarUsuarioEnBD(Usuario usuario) {
        String sql = "INSERT INTO usuario (uid, nombre, departamento) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, usuario.getUuid());
            pstmt.setString(2, usuario.getNombreCompleto());
            pstmt.setString(3, usuario.getDepartamento());

            pstmt.executeUpdate();
            System.out.println("Usuario registrado exitosamente: " + usuario.getNombreCompleto());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static List<Usuario> obtenerTodosLosUsuarios() {
        List<Usuario> usuarios = new ArrayList<>();
        String sql = "SELECT uid, nombre, departamento FROM usuario";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            while (rs.next()) {
                usuarios.add(new Usuario(rs.getString("uid"), rs.getString("nombre"), rs.getString("departamento")));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return usuarios;
    }

    public static boolean existeUsuario(String uuid) {
        String sql = "SELECT COUNT(*) FROM usuario WHERE uid = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) {
                return rs.getInt(1) > 0; // si existe en la bd
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public static void eliminarUsuario(String uuid) {
        String sql = "DELETE FROM usuario WHERE uid = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, uuid);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void actualizarUsuario(String uuid, String nuevoNombre, String nuevoDepartamento) {
        String sql = "UPDATE usuario SET nombre = ?, departamento = ? WHERE uid = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, nuevoNombre);
            pstmt.setString(2, nuevoDepartamento);
            pstmt.setString(3, uuid);
            pstmt.executeUpdate();
            System.out.println("Usuario actualizado en la base de datos.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // observa
    public static ObservableList<String> obtenerRegistrosPorUsuario(String uuid) {
        ObservableList<String> registros = FXCollections.observableArrayList();
        String sql = "SELECT fecha, tipo FROM registro WHERE uid = ? ORDER BY fecha ASC";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, uuid);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                String fecha = rs.getString("fecha");
                String tipo = rs.getString("tipo");
                registros.add(fecha + " - " + tipo);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return registros;
    }

    public static void exportarDatosA_CSV(File archivo) {
        String sql = """
                    SELECT u.id AS id_usuario, u.uid, u.nombre, u.departamento,
                           r.id AS id_registro, r.fecha, r.tipo
                    FROM usuario u
                    LEFT JOIN registro r ON u.uid = r.uid
                    ORDER BY u.id, r.fecha;
                """;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery();
             FileWriter writer = new FileWriter(archivo)) {

            writer.append("id_usuario,uid,nombre,departamento,id_registro,fecha,tipo\n");

            // Escribir filas de datos
            while (rs.next()) {
                writer.append(rs.getInt("id_usuario") + ",")
                        .append(rs.getString("uid") + ",")
                        .append(rs.getString("nombre") + ",")
                        .append(rs.getString("departamento") + ",")
                        .append(rs.getString("id_registro") + ",")
                        .append(rs.getString("fecha") + ",")
                        .append(rs.getString("tipo") + "\n");
            }

            System.out.println("Exportació completada: " + archivo.getAbsolutePath());
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
    }
}