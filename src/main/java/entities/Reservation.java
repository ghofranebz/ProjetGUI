package entities;

import java.sql.Date;

public class Reservation {
    private int id_booking;
    private int client_id;
    private int id_service;
    private int animal_id;
    private Date start_date;
    private Date end_date;
    private float total_price;
    private String cancelled_reason;
    private String status;  // 🔥 NOUVEAU : en_attente, confirmee, refusee, annulee

    public Reservation() {}

    public Reservation(int id_booking, int client_id, int id_service, int animal_id,
                       Date start_date, Date end_date, float total_price, String cancelled_reason) {
        this.id_booking = id_booking;
        this.client_id = client_id;
        this.id_service = id_service;
        this.animal_id = animal_id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.total_price = total_price;
        this.cancelled_reason = cancelled_reason;
        this.status = "en_attente"; // 🔥 STATUT PAR DÉFAUT
    }

    // 🔥 NOUVEAU CONSTRUCTEUR AVEC STATUS
    public Reservation(int id_booking, int client_id, int id_service, int animal_id,
                       Date start_date, Date end_date, float total_price,
                       String cancelled_reason, String status) {
        this.id_booking = id_booking;
        this.client_id = client_id;
        this.id_service = id_service;
        this.animal_id = animal_id;
        this.start_date = start_date;
        this.end_date = end_date;
        this.total_price = total_price;
        this.cancelled_reason = cancelled_reason;
        this.status = status;
    }

    // GETTERS ET SETTERS
    public int getId_booking() { return id_booking; }
    public void setId_booking(int id_booking) { this.id_booking = id_booking; }

    public int getClient_id() { return client_id; }
    public void setClient_id(int client_id) { this.client_id = client_id; }

    public int getId_service() { return id_service; }
    public void setId_service(int id_service) { this.id_service = id_service; }

    public int getAnimal_id() { return animal_id; }
    public void setAnimal_id(int animal_id) { this.animal_id = animal_id; }

    public Date getStart_date() { return start_date; }
    public void setStart_date(Date start_date) { this.start_date = start_date; }

    public Date getEnd_date() { return end_date; }
    public void setEnd_date(Date end_date) { this.end_date = end_date; }

    public float getTotal_price() { return total_price; }
    public void setTotal_price(float total_price) { this.total_price = total_price; }

    public String getCancelled_reason() { return cancelled_reason; }
    public void setCancelled_reason(String cancelled_reason) { this.cancelled_reason = cancelled_reason; }

    // 🔥 GETTER ET SETTER POUR STATUS
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    @Override
    public String toString() {
        return "Reservation{" +
                "id_booking=" + id_booking +
                ", client_id=" + client_id +
                ", id_service=" + id_service +
                ", animal_id=" + animal_id +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                ", total_price=" + total_price +
                ", cancelled_reason='" + cancelled_reason + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}
