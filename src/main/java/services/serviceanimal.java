package services;

import entities.Service;
import tools.Mydb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceanimal implements ICrud<Service> {

    Connection connection;

    public serviceanimal() {
        connection = Mydb.getInstance().getConnection();
    }

    // ================= ADD =================

    @Override
    public void addEntity(Service serviceIslem) {
        addEntity2(serviceIslem);
    }

    @Override
    public void addEntity2(Service serviceIslem) {

        String req = "INSERT INTO services(title, type, description, tarif, localisation, user_id, created_at, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = connection.prepareStatement(req);

            pst.setString(1, serviceIslem.getTitle());
            pst.setString(2, serviceIslem.getType());
            pst.setString(3, serviceIslem.getDescription());
            pst.setFloat(4, serviceIslem.getTarif());
            pst.setString(5, serviceIslem.getLocalisation());
            pst.setInt(6, serviceIslem.getUser_id());
            pst.setTimestamp(7, Timestamp.valueOf(serviceIslem.getCreatedAt()));

            // 🔥 Sécurité : si status est null, mettre "en_attente"
            String status = serviceIslem.getStatus();
            pst.setString(8, (status != null && !status.isEmpty()) ? status : "en_attente");

            pst.executeUpdate();
            System.out.println("Service ajouté !");

        } catch (SQLException e) {
            System.out.println("Erreur ajout service: " + e.getMessage());
        }
    }

    public List<Service> rechercherServices(String keyword) {
        List<Service> resultats = new ArrayList<>();

        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllEntities();
        }

        String sql = "SELECT * FROM services WHERE LOWER(title) LIKE LOWER(?)";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            String searchPattern = "%" + keyword.trim() + "%";
            pst.setString(1, searchPattern);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Service s = new Service();
                s.setId_services(rs.getInt("id_service"));
                s.setTitle(rs.getString("title"));
                s.setType(rs.getString("type"));
                s.setDescription(rs.getString("description"));
                s.setTarif(rs.getFloat("tarif"));
                s.setLocalisation(rs.getString("localisation"));
                s.setUser_id(rs.getInt("user_id"));
                s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                String status = rs.getString("status");
                s.setStatus(status != null ? status : "en_attente");
                resultats.add(s);
            }

        } catch (SQLException e) {
            System.out.println("Erreur lors de la recherche: " + e.getMessage());
        }

        return resultats;
    }

    @Override
    public void deleteEntity(int id) {
        String req = "DELETE FROM services WHERE id_service = ?";
        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Service supprimé !");
        } catch (SQLException e) {
            System.out.println("Erreur delete: " + e.getMessage());
        }
    }

    // 🔥 CORRECTION : updateEntity avec status
    @Override
    public void updateEntity(int id, Service serviceIslem) {
        String req = "UPDATE services SET title=?, type=?, description=?, tarif=?, localisation=?, user_id=?, created_at=?, status=? WHERE id_service=?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);

            pst.setString(1, serviceIslem.getTitle());
            pst.setString(2, serviceIslem.getType());
            pst.setString(3, serviceIslem.getDescription());
            pst.setFloat(4, serviceIslem.getTarif());
            pst.setString(5, serviceIslem.getLocalisation());
            pst.setInt(6, serviceIslem.getUser_id());
            pst.setTimestamp(7, Timestamp.valueOf(serviceIslem.getCreatedAt()));
            pst.setString(8, serviceIslem.getStatus() != null ? serviceIslem.getStatus() : "en_attente");
            pst.setInt(9, id);

            pst.executeUpdate();
            System.out.println("Service mis à jour !");

        } catch (SQLException e) {
            System.out.println("Erreur update: " + e.getMessage());
        }
    }

    @Override
    public List<Service> getAllEntities() {
        List<Service> list = new ArrayList<>();
        String req = "SELECT * FROM services";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Service s = new Service();
                s.setId_services(rs.getInt("id_service"));
                s.setTitle(rs.getString("title"));
                s.setType(rs.getString("type"));
                s.setDescription(rs.getString("description"));
                s.setTarif(rs.getFloat("tarif"));
                s.setLocalisation(rs.getString("localisation"));
                s.setUser_id(rs.getInt("user_id"));
                s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                String status = rs.getString("status");
                s.setStatus(status != null ? status : "en_attente");
                list.add(s);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getAll: " + e.getMessage());
        }
        return list;
    }

    /** Services visibles côté client (catalogue). */
    public List<Service> getServicesApprouves() {
        List<Service> list = new ArrayList<>();
        String req = "SELECT * FROM services WHERE LOWER(COALESCE(status, '')) = 'approuve'";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Service s = new Service();
                s.setId_services(rs.getInt("id_service"));
                s.setTitle(rs.getString("title"));
                s.setType(rs.getString("type"));
                s.setDescription(rs.getString("description"));
                s.setTarif(rs.getFloat("tarif"));
                s.setLocalisation(rs.getString("localisation"));
                s.setUser_id(rs.getInt("user_id"));
                s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                String status = rs.getString("status");
                s.setStatus(status != null ? status : "en_attente");
                list.add(s);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getServicesApprouves: " + e.getMessage());
        }
        return list;
    }

    @Override
    public Service getEntityById(int id) {
        Service s = null;
        String req = "SELECT * FROM services WHERE id_service = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                s = new Service();
                s.setId_services(rs.getInt("id_service"));
                s.setTitle(rs.getString("title"));
                s.setType(rs.getString("type"));
                s.setDescription(rs.getString("description"));
                s.setTarif(rs.getFloat("tarif"));
                s.setLocalisation(rs.getString("localisation"));
                s.setUser_id(rs.getInt("user_id"));
                s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                String status = rs.getString("status");
                s.setStatus(status != null ? status : "en_attente");
            }
        } catch (SQLException e) {
            System.out.println("Erreur getById: " + e.getMessage());
        }
        return s;
    }

    public void approuverService(int serviceId) {
        String sql = "UPDATE services SET status = 'approuve' WHERE id_service = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, serviceId);
            pst.executeUpdate();
            System.out.println("Service approuvé !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void rejeterService(int serviceId) {
        String sql = "UPDATE services SET status = 'rejete' WHERE id_service = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, serviceId);
            pst.executeUpdate();
            System.out.println("Service rejeté !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // 🔥 CORRECTION : getReservationsByUser - retourne les services du prestataire
    @Override
    public List<Service> getReservationsByUser(int userId) {
        List<Service> list = new ArrayList<>();
        String req = "SELECT * FROM services WHERE user_id = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, userId);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Service s = new Service();
                s.setId_services(rs.getInt("id_service"));
                s.setTitle(rs.getString("title"));
                s.setType(rs.getString("type"));
                s.setDescription(rs.getString("description"));
                s.setTarif(rs.getFloat("tarif"));
                s.setLocalisation(rs.getString("localisation"));
                s.setUser_id(rs.getInt("user_id"));
                s.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
                String status = rs.getString("status");
                s.setStatus(status != null ? status : "en_attente");
                list.add(s);
            }
        } catch (SQLException e) {
            System.out.println("Erreur getReservationsByUser: " + e.getMessage());
        }
        return list;
    }

    @Override
    public void addReservation(Service serviceIslem) {}

    @Override
    public void annulerReservation(int id, String raison) {}

    @Override
    public List<Service> getAllReservations() {
        return List.of();
    }

    @Override
    public Service getReservationById(int id) {
        return null;
    }

    @Override
    public float calculateTotalPrice(Date startDate, Date endDate, float pricePerDay) {
        return 0;
    }
}