package entities;

import java.sql.Timestamp;

/**
 * Avis client lié à une réservation terminée : une seule ligne par réservation ({@code id_booking} unique).
 */
public class ServiceReview {

    private int idReview;
    private int idBooking;
    private int clientId;
    private int idService;
    /** Note de 1 à 5 inclus. */
    private int rating;
    private String comment;
    private Timestamp createdAt;

    public ServiceReview() {}

    public ServiceReview(int idBooking, int clientId, int idService, int rating, String comment) {
        this.idBooking = idBooking;
        this.clientId = clientId;
        this.idService = idService;
        this.rating = rating;
        this.comment = comment;
    }

    public int getIdReview() {
        return idReview;
    }

    public void setIdReview(int idReview) {
        this.idReview = idReview;
    }

    public int getIdBooking() {
        return idBooking;
    }

    public void setIdBooking(int idBooking) {
        this.idBooking = idBooking;
    }

    public int getClientId() {
        return clientId;
    }

    public void setClientId(int clientId) {
        this.clientId = clientId;
    }

    public int getIdService() {
        return idService;
    }

    public void setIdService(int idService) {
        this.idService = idService;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Timestamp getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }
}
