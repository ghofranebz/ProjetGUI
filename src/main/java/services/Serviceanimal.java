package services;

import entities.Service;
import tools.Mydb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Serviceanimal implements ICrud<Service> {

    Connection connection;

    public Serviceanimal() {
        connection = Mydb.getInstance().getConnection();
    }

    // ================= ADD =================

    @Override
    public void add(Service entity) throws SQLException {

    }

    @Override
    public List<Service> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Service getById(int id) throws SQLException {
        return null;
    }

    @Override
    public void update(Service entity) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public void addEntity(Service service) {
        addEntity2(service);
    }

    @Override
    public void addEntity2(Service service) {

        String req = "INSERT INTO services(title, type, description, tarif, localisation, user_id, created_at) VALUES (?, ?, ?, ?, ?, ?, ?)";

        try {
            PreparedStatement pst = connection.prepareStatement(req);

            pst.setString(1, service.getTitle());
            pst.setString(2, service.getType());
            pst.setString(3, service.getDescription());
            pst.setFloat(4, service.getTarif());
            pst.setString(5, service.getLocalisation());
            pst.setInt(6, service.getUser_id());
            pst.setTimestamp(7, Timestamp.valueOf(service.getCreatedAt()));

            pst.executeUpdate();

            System.out.println("Service ajouté !"); // ✅ seulement si succès

        } catch (SQLException e) {
            System.out.println("Erreur ajout service: " + e.getMessage());
        }
    }

    // ================= DELETE =================

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

    // ================= UPDATE =================

    @Override
    public void updateEntity(int id, Service service) {

        String req = "UPDATE services SET title=?, type=?, description=?, tarif=?, localisation=?, user_id=?, created_at=? WHERE id_service=?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);

            pst.setString(1, service.getTitle());
            pst.setString(2, service.getType());
            pst.setString(3, service.getDescription());
            pst.setFloat(4, service.getTarif());
            pst.setString(5, service.getLocalisation());
            pst.setInt(6, service.getUser_id());
            pst.setTimestamp(7, Timestamp.valueOf(service.getCreatedAt()));
            pst.setInt(8, id);

            pst.executeUpdate();
            System.out.println("Service mis à jour !");

        } catch (SQLException e) {
            System.out.println("Erreur update: " + e.getMessage());
        }
    }

    // ================= GET ALL =================

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

                list.add(s);
            }

        } catch (SQLException e) {
            System.out.println("Erreur getAll: " + e.getMessage());
        }

        return list;
    }

    // ================= GET BY ID =================

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
            }

        } catch (SQLException e) {
            System.out.println("Erreur getById: " + e.getMessage());
        }

        return s;
    }

    // ================= NOT USED =================

    @Override public void addReservation(Service service) {}
    @Override public void annulerReservation(int id, String raison) {}
    @Override public List<Service> getAllReservations() { return List.of(); }
    @Override public Service getReservationById(int id) { return null; }
    @Override public List<Service> getReservationsByUser(int userId) { return List.of(); }
}
