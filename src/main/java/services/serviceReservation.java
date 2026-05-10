package services;

import entities.Reservation;
import tools.Mydb;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class serviceReservation implements ICrud<Reservation> {

    /** Sélection d'animal en liste sans exposer l'identifiant dans l'UI. */
    public record AnimalPick(int id, String label) {}

    Connection connection;

    public serviceReservation() {
        connection = Mydb.getInstance().getConnection();
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
    public void addReservation(Reservation r) {
        String req = "INSERT INTO bookings (client_id, id_service, animal_id, start_date, end_date, total_price, cancelled_reason, status) VALUES (?,?,?,?,?,?,?,?)";

        try (PreparedStatement pst = connection.prepareStatement(req)) {

            float tarif = getServiceTarif(r.getId_service());
            float totalPrice = calculateTotalPrice(r.getStart_date(), r.getEnd_date(), tarif);

            pst.setInt(1, r.getClient_id());
            pst.setInt(2, r.getId_service());
            pst.setInt(3, r.getAnimal_id());
            pst.setDate(4, r.getStart_date());
            pst.setDate(5, r.getEnd_date());
            pst.setFloat(6, totalPrice);
            pst.setString(7, r.getCancelled_reason());

            String status = r.getStatus();
            pst.setString(8, (status != null && !status.isEmpty()) ? status : "en_attente");

            pst.executeUpdate();
            System.out.println("Réservation ajoutée. Prix total = " + totalPrice);

        } catch (SQLException e) {
            System.out.println("Erreur reservation: " + e.getMessage());
        }
    }

    @Override
    public void deleteEntity(int id) {
        String req = "DELETE FROM bookings WHERE id_booking = ?";
        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setInt(1, id);
            pst.executeUpdate();
            System.out.println("Réservation supprimée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Reservation> getReservationsByPrestataire(int prestataireId) {
        List<Reservation> reservationIslems = new ArrayList<>();

        String sql = "SELECT b.* FROM bookings b " +
                "INNER JOIN services s ON b.id_service = s.id_service " +
                "WHERE s.user_id = ? " +
                "ORDER BY b.start_date DESC";

        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, prestataireId);
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

                String status = rs.getString("status");
                r.setStatus(status != null ? status : "en_attente");

                reservationIslems.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationIslems;
    }

    public void confirmerReservation(int idReservation) {
        String sql = "UPDATE bookings SET status = 'confirmee' WHERE id_booking = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, idReservation);
            pst.executeUpdate();
            System.out.println("Réservation confirmée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void refuserReservation(int idReservation) {
        String sql = "UPDATE bookings SET status = 'refusee' WHERE id_booking = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, idReservation);
            pst.executeUpdate();
            System.out.println("Réservation refusée !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateEntity(int id, Reservation r) {
        String req = "UPDATE bookings SET client_id=?, id_service=?, animal_id=?, start_date=?, end_date=?, total_price=?, cancelled_reason=?, status=? WHERE id_booking=?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {

            float tarif = getServiceTarif(r.getId_service());
            float totalPrice = calculateTotalPrice(r.getStart_date(), r.getEnd_date(), tarif);

            pst.setInt(1, r.getClient_id());
            pst.setInt(2, r.getId_service());
            pst.setInt(3, r.getAnimal_id());
            pst.setDate(4, r.getStart_date());
            pst.setDate(5, r.getEnd_date());
            pst.setFloat(6, totalPrice);
            pst.setString(7, r.getCancelled_reason());
            pst.setString(8, r.getStatus() != null ? r.getStatus() : "en_attente");
            pst.setInt(9, id);

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
    public List<Reservation> getAllReservations() {
        List<Reservation> reservationIslems = new ArrayList<>();
        String sql = "SELECT * FROM bookings";

        try (PreparedStatement pst = connection.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

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

                String status = rs.getString("status");
                r.setStatus(status != null ? status : "en_attente");

                reservationIslems.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationIslems;
    }

    @Override
    public Reservation getEntityById(int id) {
        return getReservationById(id);
    }

    @Override
    public Reservation getReservationById(int id) {
        Reservation r = null;
        String req = "SELECT * FROM bookings WHERE id_booking = ?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
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

                String status = rs.getString("status");
                r.setStatus(status != null ? status : "en_attente");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return r;
    }

    /**
     * Animaux du client pour ComboBox.
     * Utilise owner_id et name (noms réels des colonnes dans la table animals).
     */
    public List<AnimalPick> listAnimalsForClient(int clientId) {
        return loadAnimals(clientId,
                "SELECT id, name FROM animals WHERE owner_id = ? ORDER BY name");
    }

    private List<AnimalPick> loadAnimals(int clientId, String sql) {
        List<AnimalPick> list = new ArrayList<>();
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, clientId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    int id = rs.getInt("id");
                    String name = rs.getString("name");
                    String label = (name != null && !name.isBlank()) ? name.trim() : "Animal";
                    list.add(new AnimalPick(id, label));
                }
            }
        } catch (SQLException e) {
            System.out.println("Erreur loadAnimals: " + e.getMessage());
        }
        return list;
    }

    private float getServiceTarif(int serviceId) {
        String sql = "SELECT tarif FROM services WHERE id_service = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, serviceId);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getFloat("tarif");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public float calculateTotalPrice(Date startDate, Date endDate, float tarif) {
        long diff = endDate.getTime() - startDate.getTime();
        long days = (diff / (1000 * 60 * 60 * 24)) + 1;
        return days * tarif;
    }

    @Override
    public List<Reservation> getReservationsByUser(int clientId) {
        List<Reservation> reservationIslems = new ArrayList<>();
        String req = "SELECT * FROM bookings WHERE client_id = ?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
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

                String status = rs.getString("status");
                r.setStatus(status != null ? status : "en_attente");

                reservationIslems.add(r);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return reservationIslems;
    }

    @Override
    public void annulerReservation(int id, String raison) {
        String req = "UPDATE bookings SET cancelled_reason = ?, status = 'annulee' WHERE id_booking = ?";

        try (PreparedStatement pst = connection.prepareStatement(req)) {
            pst.setString(1, raison);
            pst.setInt(2, id);
            pst.executeUpdate();
            System.out.println("Réservation annulée !");
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}
