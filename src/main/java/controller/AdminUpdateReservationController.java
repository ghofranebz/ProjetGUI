package controller;

import entities.Reservation;
import entities.Service;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import services.ServiceReservation;
import services.Serviceanimal;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

public class AdminUpdateReservationController {

    @FXML
    private Label contextHintLabel;

    @FXML
    private ComboBox<Service> serviceCombo;

    @FXML
    private ComboBox<ServiceReservation.AnimalPick> animalCombo;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField reasonField;

    @FXML
    private TextField statusField;

    private final ServiceReservation serviceReservation = new ServiceReservation();
    private final Serviceanimal serviceAnimal = new Serviceanimal();

    private int editingBookingId;

    @FXML
    public void initialize() {
        serviceCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(Service s) {
                return s == null ? "" : formatService(s);
            }

            @Override
            public Service fromString(String string) {
                return null;
            }
        });

        animalCombo.setConverter(new StringConverter<>() {
            @Override
            public String toString(ServiceReservation.AnimalPick a) {
                return a == null ? "" : a.label();
            }

            @Override
            public ServiceReservation.AnimalPick fromString(String string) {
                return null;
            }
        });

        editingBookingId = AdminNavigation.selectedReservationId;
        if (editingBookingId <= 0) {
            if (contextHintLabel != null) {
                contextHintLabel.setText("Ouvrez une réservation depuis la liste (« Modifier ») pour la mettre à jour.");
            }
            serviceCombo.setDisable(true);
            animalCombo.setDisable(true);
            startDatePicker.setDisable(true);
            endDatePicker.setDisable(true);
            reasonField.setDisable(true);
            statusField.setDisable(true);
            return;
        }

        if (contextHintLabel != null) {
            contextHintLabel.setText("Les dates et le service peuvent être ajustés ; le client reste celui de la réservation.");
        }

        loadReservationIntoForm();
    }

    private static String formatService(Service s) {
        String title = s.getTitle() != null && !s.getTitle().isBlank() ? s.getTitle() : s.getType();
        String loc = s.getLocalisation() != null ? s.getLocalisation() : "";
        return title + " · " + s.getTarif() + " DT · " + loc;
    }

    private void loadReservationIntoForm() {
        Reservation r = serviceReservation.getReservationById(editingBookingId);
        if (r == null) {
            showAlert(Alert.AlertType.WARNING, "Réservation introuvable.");
            return;
        }

        List<Service> items = new ArrayList<>(serviceAnimal.getServicesApprouves());
        Service currentSvc = serviceAnimal.getEntityById(r.getId_service());
        if (currentSvc != null && items.stream().noneMatch(s -> s.getId_services() == currentSvc.getId_services())) {
            items.add(0, currentSvc);
        }
        serviceCombo.setItems(FXCollections.observableArrayList(items));
        if (currentSvc != null) {
            serviceCombo.getSelectionModel().select(currentSvc);
        }

        List<ServiceReservation.AnimalPick> animals =
                new ArrayList<>(serviceReservation.listAnimalsForClient(r.getClient_id()));
        boolean hasAnimal = animals.stream().anyMatch(a -> a.id() == r.getAnimal_id());
        if (!hasAnimal) {
            animals.add(0, new ServiceReservation.AnimalPick(r.getAnimal_id(), "Animal associé à cette réservation"));
        }
        animalCombo.setItems(FXCollections.observableArrayList(animals));
        animals.stream().filter(a -> a.id() == r.getAnimal_id()).findFirst()
                .ifPresent(a -> animalCombo.getSelectionModel().select(a));

        if (r.getStart_date() != null) {
            startDatePicker.setValue(r.getStart_date().toLocalDate());
        }
        if (r.getEnd_date() != null) {
            endDatePicker.setValue(r.getEnd_date().toLocalDate());
        }

        reasonField.setText(r.getCancelled_reason());

        String status = r.getStatus();
        if (status == null || "en_attente".equals(status)) {
            statusField.setText("⏳ En attente");
        } else if ("confirmee".equals(status)) {
            statusField.setText("✅ Confirmée");
        } else if ("refusee".equals(status)) {
            statusField.setText("❌ Refusée");
        } else if ("annulee".equals(status)) {
            statusField.setText("🚫 Annulée");
        } else {
            statusField.setText(status);
        }
    }

    @FXML
    private void updateReservation() {
        if (editingBookingId <= 0) {
            showAlert(Alert.AlertType.WARNING, "Aucune réservation chargée.");
            return;
        }
        try {
            Reservation oldReservation = serviceReservation.getReservationById(editingBookingId);
            if (oldReservation == null) {
                showAlert(Alert.AlertType.ERROR, "Réservation introuvable.");
                return;
            }

            Service selService = serviceCombo.getSelectionModel().getSelectedItem();
            ServiceReservation.AnimalPick selAnimal = animalCombo.getSelectionModel().getSelectedItem();

            if (selService == null || selAnimal == null) {
                showAlert(Alert.AlertType.WARNING, "Veuillez choisir un service et un animal.");
                return;
            }
            if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
                showAlert(Alert.AlertType.WARNING, "Veuillez renseigner les deux dates.");
                return;
            }

            Reservation r = new Reservation();
            r.setClient_id(oldReservation.getClient_id());
            r.setId_service(selService.getId_services());
            r.setAnimal_id(selAnimal.id());
            r.setStart_date(Date.valueOf(startDatePicker.getValue()));
            r.setEnd_date(Date.valueOf(endDatePicker.getValue()));
            r.setCancelled_reason(reasonField.getText());
            r.setStatus(oldReservation.getStatus() != null ? oldReservation.getStatus() : "en_attente");

            serviceReservation.updateEntity(editingBookingId, r);

            showAlert(Alert.AlertType.INFORMATION, "Réservation modifiée avec succès !");

            loadReservationIntoForm();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle("Message");
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
