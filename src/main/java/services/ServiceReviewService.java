package services;

import entities.ServiceReview;
import tools.Mydb;

import java.sql.*;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Avis utilisateur sur une réservation / service après la prestation.
 */
public class ServiceReviewService {

    private final Connection connection;
    private static volatile boolean tableEnsured;

    public ServiceReviewService() {
        this.connection = Mydb.getInstance().getConnection();
        ensureTable();
    }

    private synchronized void ensureTable() {
        if (tableEnsured) {
            return;
        }
        String ddl = "CREATE TABLE IF NOT EXISTS service_reviews ("
                + "id_review INT AUTO_INCREMENT PRIMARY KEY,"
                + "id_booking INT NOT NULL UNIQUE,"
                + "client_id INT NOT NULL,"
                + "id_service INT NOT NULL,"
                + "rating TINYINT NOT NULL,"
                + "comment VARCHAR(1024),"
                + "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,"
                + "INDEX idx_service (id_service),"
                + "INDEX idx_client (client_id)"
                + ")";
        try (Statement st = connection.createStatement()) {
            st.executeUpdate(ddl);
            tableEnsured = true;
            System.out.println("Table service_reviews prête.");
        } catch (SQLException e) {
            tableEnsured = false;
            System.err.println("ServiceReview — création table: " + e.getMessage());
        }
    }

    public ServiceReview findByBookingId(int idBooking) {
        String sql = "SELECT id_review, id_booking, client_id, id_service, rating, comment, created_at "
                + "FROM service_reviews WHERE id_booking = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, idBooking);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        } catch (SQLException e) {
            System.err.println("findByBookingId: " + e.getMessage());
        }
        return null;
    }

    public Map<Integer, ServiceReview> findByBookingIds(Collection<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<Integer, ServiceReview> out = new HashMap<>();
        StringBuilder inClause = new StringBuilder();
        int n = 0;
        for (Integer ignored : ids) {
            if (n++ > 0) {
                inClause.append(',');
            }
            inClause.append('?');
        }
        String sql = "SELECT id_review, id_booking, client_id, id_service, rating, comment, created_at "
                + "FROM service_reviews WHERE id_booking IN (" + inClause + ")";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            int i = 1;
            for (Integer bid : ids) {
                pst.setInt(i++, bid);
            }
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    ServiceReview r = mapRow(rs);
                    out.put(r.getIdBooking(), r);
                }
            }
        } catch (SQLException e) {
            System.err.println("findByBookingIds: " + e.getMessage());
        }
        return out;
    }

    private static ServiceReview mapRow(ResultSet rs) throws SQLException {
        ServiceReview r = new ServiceReview();
        r.setIdReview(rs.getInt("id_review"));
        r.setIdBooking(rs.getInt("id_booking"));
        r.setClientId(rs.getInt("client_id"));
        r.setIdService(rs.getInt("id_service"));
        r.setRating(rs.getInt("rating"));
        r.setComment(rs.getString("comment"));
        r.setCreatedAt(rs.getTimestamp("created_at"));
        return r;
    }

    /** @return false si doublon, erreur ou note hors 1–5 */
    public boolean insertReview(ServiceReview rev) {
        if (rev == null || rev.getRating() < 1 || rev.getRating() > 5) {
            return false;
        }
        ensureTableIfReset();
        String sql = "INSERT INTO service_reviews (id_booking, client_id, id_service, rating, comment) "
                + "VALUES (?,?,?,?,?)";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, rev.getIdBooking());
            pst.setInt(2, rev.getClientId());
            pst.setInt(3, rev.getIdService());
            pst.setInt(4, rev.getRating());
            String c = rev.getComment();
            pst.setString(5, c != null ? c.trim() : "");
            return pst.executeUpdate() == 1;
        } catch (SQLException e) {
            System.err.println("insertReview: " + e.getMessage());
            return false;
        }
    }

    private void ensureTableIfReset() {
        if (!tableEnsured) {
            ensureTable();
        }
    }

    /** Moyenne pour un service (0 si aucun avis). */
    public double averageRatingForService(int idService) {
        String sql = "SELECT AVG(rating) FROM service_reviews WHERE id_service = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, idService);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("averageRatingForService: " + e.getMessage());
        }
        return 0;
    }

    public int countReviewsForService(int idService) {
        String sql = "SELECT COUNT(*) FROM service_reviews WHERE id_service = ?";
        try (PreparedStatement pst = connection.prepareStatement(sql)) {
            pst.setInt(1, idService);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            System.err.println("countReviewsForService: " + e.getMessage());
        }
        return 0;
    }
}
