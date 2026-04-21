package services;

import entities.Reservation;
import tools.Mydb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceReservation implements ICrud<Reservation> {

    Connection connection;

    public ServiceReservation() {
        connection = Mydb.getInstance().getConnection();
    }

    // ================= CRUD =================

    @Override
    public void add(Reservation entity) throws SQLException {

    }

    @Override
    public List<Reservation> getAll() throws SQLException {
        return List.of();
    }

    @Override
    public Reservation getById(int id) throws SQLException {
        return null;
    }

    @Override
    public void update(Reservation entity) throws SQLException {

    }

    @Override
    public void delete(int id) throws SQLException {

    }

    @Override
    public void addEntity(Reservation r) {
        addReservation(r);
    }

    @Override
    public void addEntity2(Reservation r) {
        addReservation(r);
    }

    @Override
    public void deleteEntity(int id) {
        String req = "DELETE FROM bookings WHERE id_booking = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);

            pst.executeUpdate();
            System.out.println("Réservation supprimée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEntity(int id, Reservation r) {
        String req = "UPDATE bookings SET client_id=?, id_service=?, animal_id=?, start_date=?, end_date=?, total_price=?, cancelled_reason=? WHERE id_booking=?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);

            pst.setInt(1, r.getClient_id());
            pst.setInt(2, r.getId_service());
            pst.setInt(3, r.getAnimal_id());
            pst.setDate(4, r.getStart_date());
            pst.setDate(5, r.getEnd_date());
            pst.setFloat(6, r.getTotal_price());
            pst.setString(7, r.getCancelled_reason());
            pst.setInt(8, id);

            pst.executeUpdate();
            System.out.println("Réservation mise à jour !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public List<Reservation> getAllEntities() {
        return getAllReservations();
    }

    @Override
    public Reservation getEntityById(int id) {
        return getReservationById(id);
    }



    public void addReservation(Reservation r) {

        String req = "INSERT INTO bookings (client_id, id_service, animal_id, start_date, end_date, total_price, cancelled_reason) VALUES (?,?,?,?,?,?,?)";

        try {
            PreparedStatement pst = connection.prepareStatement(req);

            pst.setInt(1, r.getClient_id());
            pst.setInt(2, r.getId_service());
            pst.setInt(3, r.getAnimal_id());
            pst.setDate(4, r.getStart_date());
            pst.setDate(5, r.getEnd_date());
            pst.setFloat(6, r.getTotal_price());
            pst.setString(7, r.getCancelled_reason());

            pst.executeUpdate();

            System.out.println("Réservation ajoutée !");
        } catch (SQLException e) {
            System.out.println("Erreur reservation: " + e.getMessage());
        }
    }

    public void annulerReservation(int id, String raison) {

        String req = "UPDATE bookings SET cancelled_reason = ? WHERE id_booking = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);

            pst.setString(1, raison);
            pst.setInt(2, id);

            pst.executeUpdate();

            System.out.println("Réservation annulée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public List<Reservation> getAllReservations() {
        List<Reservation> reservations = new ArrayList<>();

        String sql = "SELECT * FROM bookings";

        try {
            PreparedStatement pst = connection.prepareStatement(sql);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                Reservation r = new Reservation();

                r.setId_booking(rs.getInt("id_booking"));
                r.setClient_id(rs.getInt("client_id"));
                r.setId_service(rs.getInt("id_service"));
                r.setAnimal_id(rs.getInt("animal_id"));
                r.setStart_date(rs.getDate("start_date"));
                r.setEnd_date(rs.getDate("end_date"));
                r.setTotal_price(rs.getFloat("total_price"));
                r.setCancelled_reason(rs.getString("cancelled_reason"));

                reservations.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }

    @Override
    public Reservation getReservationById(int id) {
        Reservation r = null;

        String req = "SELECT * FROM bookings WHERE id_booking = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, id);

            ResultSet rs = pst.executeQuery();

            if (rs.next()) {
                r = new Reservation();

                r.setId_booking(rs.getInt("id_booking"));
                r.setClient_id(rs.getInt("client_id"));
                r.setId_service(rs.getInt("id_service"));
                r.setAnimal_id(rs.getInt("animal_id"));
                r.setStart_date(rs.getDate("start_date"));
                r.setEnd_date(rs.getDate("end_date"));
                r.setTotal_price(rs.getFloat("total_price"));
                r.setCancelled_reason(rs.getString("cancelled_reason"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return r;
    }

    @Override
    public List<Reservation> getReservationsByUser(int clientId) {
        List<Reservation> reservations = new ArrayList<>();

        String req = "SELECT * FROM bookings WHERE client_id = ?";

        try {
            PreparedStatement pst = connection.prepareStatement(req);
            pst.setInt(1, clientId);

            ResultSet rs = pst.executeQuery();

            while (rs.next()) {

                Reservation r = new Reservation();

                r.setId_booking(rs.getInt("id_booking"));
                r.setClient_id(rs.getInt("client_id"));
                r.setId_service(rs.getInt("id_service"));
                r.setAnimal_id(rs.getInt("animal_id"));
                r.setStart_date(rs.getDate("start_date"));
                r.setEnd_date(rs.getDate("end_date"));
                r.setTotal_price(rs.getFloat("total_price"));
                r.setCancelled_reason(rs.getString("cancelled_reason"));

                reservations.add(r);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return reservations;
    }
}

