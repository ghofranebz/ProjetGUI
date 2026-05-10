package controller;

import entities.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.Serviceanimal;

import java.util.List;
import java.util.Optional;

public class AdminServiceController {

    @FXML
    private VBox servicesContainer;

    private final Serviceanimal serviceAnimal = new Serviceanimal();

    @FXML
    public void initialize() {
        afficherServices();
    }

    private void afficherServices() {
        servicesContainer.getChildren().clear();

        List<Service> services = serviceAnimal.getAllEntities();

        if (services == null || services.isEmpty()) {
            Label emptyLabel = new Label("Aucun service disponible");
            emptyLabel.getStyleClass().add("empty-label");
            servicesContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Service s : services) {
            HBox card = new HBox();
            card.getStyleClass().add("data-card");
            card.setSpacing(25);
            card.setAlignment(Pos.CENTER_LEFT);

            VBox infoBox = new VBox();
            infoBox.setSpacing(8);
            infoBox.setPrefWidth(600);

            Label title = new Label(s.getTitle() != null ? s.getTitle() : s.getType());
            title.getStyleClass().add("card-main-title");

            Label type = new Label("Type : " + s.getType());
            type.getStyleClass().add("card-info");

            Label description = new Label("Description : " + s.getDescription());
            description.getStyleClass().add("card-info");

            Label tarif = new Label("Tarif : " + s.getTarif() + " DT");
            tarif.getStyleClass().add("card-info");

            Label localisation = new Label("Localisation : " + s.getLocalisation());
            localisation.getStyleClass().add("card-info");

            // 🔥 AJOUT DU STATUS
            Label statusLabel = new Label("Statut : " + getStatusTexte(s.getStatus()));
            statusLabel.getStyleClass().add("card-info");
            statusLabel.getStyleClass().add(getStatusStyleClass(s.getStatus()));

            infoBox.getChildren().addAll(title, type, description, tarif, localisation, statusLabel);

            VBox actionBox = new VBox();
            actionBox.setSpacing(12);
            actionBox.setAlignment(Pos.CENTER);

            Label idLabel = new Label("ID #" + s.getId_services());
            idLabel.getStyleClass().add("id-label");

            Button updateBtn = new Button("Modifier");
            updateBtn.getStyleClass().add("dark-button");
            updateBtn.setOnAction(event -> {
                AdminNavigation.selectedServiceId = s.getId_services();
                AdminNavigation.changeScene(event, "adminupdateservice.fxml");
            });

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.getStyleClass().add("danger-button");
            deleteBtn.setOnAction(event -> {
                if (!confirmerSuppression(
                        "Supprimer le service n°" + s.getId_services() + " ? Cette action est définitive.")) {
                    return;
                }
                serviceAnimal.deleteEntity(s.getId_services());
                afficherServices();
            });

            actionBox.getChildren().addAll(idLabel, updateBtn, deleteBtn);
            card.getChildren().addAll(infoBox, actionBox);
            servicesContainer.getChildren().add(card);
        }
    }

    private String getStatusTexte(String status) {
        if (status == null || "en_attente".equals(status)) return "⏳ En attente";
        if ("approuve".equals(status)) return "✅ Approuvé";
        if ("rejete".equals(status)) return "❌ Rejeté";
        return status;
    }

    private String getStatusStyleClass(String status) {
        if (status == null || "en_attente".equals(status)) return "status-en-attente";
        if ("approuve".equals(status)) return "status-approuve";
        if ("rejete".equals(status)) return "status-rejete";
        return "";
    }

    private boolean confirmerSuppression(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    // ================= MÉTHODES DE NAVIGATION =================

    @FXML
    private void goDashboard(ActionEvent event) {
        AdminNavigation.changeScene(event, "admindashboard.fxml");
    }

    @FXML
    private void goServices(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminservices.fxml");
    }

    @FXML
    private void goAddService(ActionEvent event) {
        AdminNavigation.changeScene(event, "service_category_choice.fxml");
    }

    @FXML
    private void goGestionReservations(ActionEvent event) {
        AdminNavigation.changeScene(event, "prestataire_gesreservation.fxml");
    }

    @FXML
    private void goAdmin(ActionEvent event) {
        AdminNavigation.changeScene(event, "admin_verifier_services.fxml");
    }
}