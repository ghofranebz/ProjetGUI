package controller;

import entities.Reservation;
import entities.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.ServiceReservation;
import services.Serviceanimal;

import java.util.List;
import java.util.ArrayList;
import java.util.Optional;

public class AdminReservationController {

    @FXML
    private VBox reservationsContainer;

    @FXML
    private TextField searchField;

    private final ServiceReservation serviceReservation = new ServiceReservation();
    private final Serviceanimal serviceAnimal = new Serviceanimal();
    private List<Reservation> toutesLesReservations;

    @FXML
    public void initialize() {
        afficherReservations();
    }

    private void afficherReservations() {
        toutesLesReservations = serviceReservation.getAllReservations();
        afficherCartesReservations(toutesLesReservations);
    }

    @FXML
    private void rechercherService(ActionEvent event) {
        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            afficherCartesReservations(toutesLesReservations);
            return;
        }

        // Récupérer les services qui correspondent au mot-clé
        List<Service> servicesTrouves = serviceAnimal.rechercherServices(keyword);

        // Extraire les IDs des services trouvés
        List<Integer> serviceIds = new ArrayList<>();
        for (Service s : servicesTrouves) {
            serviceIds.add(s.getId_services());
        }

        // Filtrer les réservations
        List<Reservation> resultats = new ArrayList<>();
        for (Reservation r : toutesLesReservations) {
            if (serviceIds.contains(r.getId_service())) {
                resultats.add(r);
            }
        }

        afficherCartesReservations(resultats);

        if (resultats.isEmpty()) {
            afficherMessageAucunResultat();
        }
    }

    @FXML
    private void resetRecherche(ActionEvent event) {
        searchField.clear();
        afficherCartesReservations(toutesLesReservations);
    }

    private void afficherCartesReservations(List<Reservation> reservations) {
        reservationsContainer.getChildren().clear();

        if (reservations == null || reservations.isEmpty()) {
            Label emptyLabel = new Label("Aucune réservation trouvée");
            emptyLabel.getStyleClass().add("empty-label");
            reservationsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Reservation r : reservations) {
            HBox card = new HBox();
            card.getStyleClass().add("data-card");
            card.setSpacing(25);
            card.setAlignment(Pos.CENTER_LEFT);

            VBox infoBox = new VBox();
            infoBox.setSpacing(8);
            infoBox.setPrefWidth(600);

            Label title = new Label("Réservation #" + r.getId_booking());
            title.getStyleClass().add("card-main-title");

            Label client = new Label("Client ID : " + r.getClient_id());
            client.getStyleClass().add("card-info");

            Label service = new Label("Service ID : " + r.getId_service());
            service.getStyleClass().add("card-info");

            Label animal = new Label("Animal ID : " + r.getAnimal_id());
            animal.getStyleClass().add("card-info");

            Label dates = new Label("Du " + r.getStart_date() + " au " + r.getEnd_date());
            dates.getStyleClass().add("card-info");

            Label price = new Label("Prix total : " + r.getTotal_price() + " DT");
            price.getStyleClass().add("card-info");

            Label reason = new Label("Raison annulation : " + r.getCancelled_reason());
            reason.getStyleClass().add("card-info");

            infoBox.getChildren().addAll(title, client, service, animal, dates, price, reason);

            VBox actionBox = new VBox();
            actionBox.setSpacing(12);
            actionBox.setAlignment(Pos.CENTER);

            Label idLabel = new Label("ID #" + r.getId_booking());
            idLabel.getStyleClass().add("id-label");

            Button updateBtn = new Button("Modifier");
            updateBtn.getStyleClass().add("dark-button");
            updateBtn.setOnAction(event -> {
                AdminNavigation.selectedReservationId = r.getId_booking();
                AdminNavigation.changeScene(event, "adminupdatereservation.fxml");
            });

            Button deleteBtn = new Button("Supprimer");
            deleteBtn.getStyleClass().add("danger-button");
            deleteBtn.setOnAction(event -> {
                if (!confirmerSuppression(
                        "Supprimer la réservation n°" + r.getId_booking() + " ? Cette action est définitive.")) {
                    return;
                }
                serviceReservation.deleteEntity(r.getId_booking());
                afficherReservations();
            });

            actionBox.getChildren().addAll(idLabel, updateBtn, deleteBtn);
            card.getChildren().addAll(infoBox, actionBox);
            reservationsContainer.getChildren().add(card);
        }
    }

    private boolean confirmerSuppression(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void afficherMessageAucunResultat() {
        Label msg = new Label("❌ Aucune réservation trouvée pour ce service");
        msg.getStyleClass().add("error-label");
        reservationsContainer.getChildren().clear();
        reservationsContainer.getChildren().add(msg);
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
    private void goReservations(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminreservations.fxml");
    }

    @FXML
    private void goAddReservation(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminaddreservation.fxml");
    }
}