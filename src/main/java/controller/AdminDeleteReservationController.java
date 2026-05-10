package controller;

import entities.Reservation;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.collections.FXCollections;
import services.ServiceReservation;
import services.Serviceanimal;

import java.util.Optional;

public class AdminDeleteReservationController {

    @FXML
    private ComboBox<Reservation> reservationCombo;

    private final ServiceReservation serviceReservation = new ServiceReservation();
    private final Serviceanimal serviceAnimal = new Serviceanimal();

    @FXML
    public void initialize() {
        reservationCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Reservation r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null : formatReservationChoice(r));
            }
        });
        reservationCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Reservation r, boolean empty) {
                super.updateItem(r, empty);
                setText(empty || r == null ? null : formatReservationChoice(r));
            }
        });

        refreshReservationChoices();
    }

    private String formatReservationChoice(Reservation r) {
        var s = serviceAnimal.getEntityById(r.getId_service());
        String title = s != null && s.getTitle() != null && !s.getTitle().isBlank()
                ? s.getTitle()
                : (s != null ? s.getType() : "Service");
        return "Du " + r.getStart_date() + " au " + r.getEnd_date() + " · " + title;
    }

    private void refreshReservationChoices() {
        reservationCombo.setItems(FXCollections.observableArrayList(serviceReservation.getAllReservations()));
        reservationCombo.getSelectionModel().clearSelection();
    }

    @FXML
    private void deleteReservation() {
        Reservation selected = reservationCombo.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.INFORMATION, "Information", "Veuillez choisir une réservation dans la liste.");
            return;
        }

        ButtonType okButton = new ButtonType("Supprimer");
        ButtonType cancelButton = new ButtonType("Annuler");
        String summary = formatReservationChoice(selected);
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                "Supprimer définitivement cette réservation ?\n" + summary,
                okButton, cancelButton);
        confirm.setTitle("Confirmer la suppression");
        confirm.setHeaderText(null);

        Optional<ButtonType> result = confirm.showAndWait();

        if (result.isPresent() && result.get() == okButton) {
            serviceReservation.deleteEntity(selected.getId_booking());
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation supprimée.");
            refreshReservationChoices();
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void goDashboard(ActionEvent event) {
        AdminNavigation.changeScene(event, AdminNavigation.homeDashboardFxml);
    }

    @FXML
    private void goServices(ActionEvent event) {
        AdminNavigation.changeScene(event, AdminNavigation.servicesCatalogFxml);
    }

    @FXML
    private void goReservations(ActionEvent event) {
        AdminNavigation.changeScene(event, AdminNavigation.reservationsListFxml);
    }
}
