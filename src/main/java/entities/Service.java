package entities;


import java.time.LocalDateTime;

public class Service {
    private int id_services;
    private String title;
    private String type;
    private String description;
    private float tarif;
    private String localisation;
    private int user_id;
    private LocalDateTime createdAt;

    public Service() {
    }

    public Service(int id_services, String title, String type, String description,
                   float tarif, String localisation, int user_id,
                   LocalDateTime createdAt) {
        this.id_services = id_services;
        this.title = title;
        this.type = type;
        this.description = description;
        this.tarif = tarif;
        this.localisation = localisation;
        this.user_id = user_id;
        this.createdAt = createdAt;
    }

    public int getId_services() {
        return id_services;
    }

    public void setId_services(int id_services) {
        this.id_services = id_services;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public float getTarif() {
        return tarif;
    }

    public void setTarif(float tarif) {
        this.tarif = tarif;
    }

    public String getLocalisation() {
        return localisation;
    }

    public void setLocalisation(String localisation) {
        this.localisation = localisation;
    }

    public int getUser_id() {
        return user_id;
    }

    public void setUser_id(int user_id) {
        this.user_id = user_id;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Service{" +
                "id=" + id_services +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", description='" + description + '\'' +
                ", tarif=" + tarif +
                ", localisation='" + localisation + '\'' +
                ", user_id=" + user_id +
                ", createdAt=" + createdAt +
                '}';
    }
}
