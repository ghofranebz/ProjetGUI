package services;

import tools.Mydb;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;


public class ContactPrestataireService {

    private final Connection connection;

    public ContactPrestataireService() {
        connection = Mydb.getInstance().getConnection();
        ensureTable();
    }

    private void ensureTable() {
        if (connection == null) {
            return;
        }
        String sql = """
                CREATE TABLE IF NOT EXISTS contact_messages (
                    id_message INT AUTO_INCREMENT PRIMARY KEY,
                    client_id INT NOT NULL,
                    prestataire_user_id INT NOT NULL,
                    id_service INT NOT NULL,
                    message_body TEXT NOT NULL,
                    sent_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
                """;
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("contact_messages ensureTable: " + e.getMessage());
        }
    }

    public boolean sendMessage(int clientId, int prestataireUserId, int serviceId, String body) throws SQLException {
        if (connection == null) {
            throw new SQLException("Pas de connexion à la base.");
        }
        String trimmed = body != null ? body.trim() : "";
        if (trimmed.isEmpty()) {
            throw new IllegalArgumentException("Le message est vide.");
        }
        String sql = """
                INSERT INTO contact_messages (client_id, prestataire_user_id, id_service, message_body)
                VALUES (?, ?, ?, ?)
                """;
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, clientId);
            pst.setInt(2, prestataireUserId);
            pst.setInt(3, serviceId);
            pst.setString(4, trimmed);
            pst.executeUpdate();
            return true;
        }
    }
}
