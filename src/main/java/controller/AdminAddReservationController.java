package controller;

import entities.Reservation;
import entities.Service;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import services.serviceReservation;
import services.serviceanimal;

import java.sql.Date;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class AdminAddReservationController {

    private static final DateTimeFormatter DATE_FR = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    private ComboBox<Service> serviceCombo;

    @FXML
    private ComboBox<serviceReservation.AnimalPick> animalCombo;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField reasonField;

    @FXML
    private TextField statusField;

    private final serviceReservation serviceReservationIslem = new serviceReservation();
    private final serviceanimal serviceAnimal = new serviceanimal();

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
            public String toString(serviceReservation.AnimalPick a) {
                return a == null ? "" : a.label();
            }

            @Override
            public serviceReservation.AnimalPick fromString(String string) {
                return null;
            }
        });

        java.util.List<Service> approved = serviceAnimal.getServicesApprouves();
        serviceCombo.setItems(FXCollections.observableArrayList(approved));

        Integer cid = AdminNavigation.currentClientId;
        if (cid != null) {
            animalCombo.setItems(FXCollections.observableArrayList(serviceReservationIslem.listAnimalsForClient(cid)));
        } else {
            animalCombo.setItems(FXCollections.observableArrayList());
        }

        selectServiceFromNavigation(approved);
    }

    private static String formatService(Service s) {
        String title = s.getTitle() != null && !s.getTitle().isBlank() ? s.getTitle() : s.getType();
        String loc = s.getLocalisation() != null ? s.getLocalisation() : "";
        return title + " · " + s.getTarif() + " DT · " + loc;
    }

    private void selectServiceFromNavigation(java.util.List<Service> approved) {
        int pref = AdminNavigation.selectedServiceId;
        if (pref <= 0) {
            return;
        }
        for (Service s : approved) {
            if (s.getId_services() == pref) {
                serviceCombo.getSelectionModel().select(s);
                return;
            }
        }
        Service extra = serviceAnimal.getEntityById(pref);
        if (extra != null) {
            serviceCombo.getItems().add(0, extra);
            serviceCombo.getSelectionModel().select(extra);
        }
    }

    @FXML
    private void addReservation() {
        Service selServiceIslem = serviceCombo.getSelectionModel().getSelectedItem();
        serviceReservation.AnimalPick selAnimal = animalCombo.getSelectionModel().getSelectedItem();

        if (selServiceIslem == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez choisir un serviceIslem.");
            return;
        }
        if (selAnimal == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez choisir un animal.");
            return;
        }
        if (AdminNavigation.currentClientId == null) {
            showAlert(Alert.AlertType.ERROR, "Erreur : utilisateur non connecté (client_id manquant).");
            return;
        }
        if (startDatePicker.getValue() == null || endDatePicker.getValue() == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez renseigner les deux dates.");
            return;
        }
        if (endDatePicker.getValue().isBefore(startDatePicker.getValue())) {
            showAlert(Alert.AlertType.WARNING, "La date de fin doit être après la date de début.");
            return;
        }

        Date startSql = Date.valueOf(startDatePicker.getValue());
        Date endSql = Date.valueOf(endDatePicker.getValue());
        float estimatedTotal = serviceReservationIslem.calculateTotalPrice(startSql, endSql, selServiceIslem.getTarif());

        String serviceTitle = selServiceIslem.getTitle() != null && !selServiceIslem.getTitle().isBlank()
                ? selServiceIslem.getTitle()
                : selServiceIslem.getType();
        String reason = reasonField.getText();
        String reasonBlock = (reason != null && !reason.isBlank())
                ? "Commentaire / raison (si besoin) : " + reason.trim() + "\n\n"
                : "";

        String summary = """
                Service : %s
                Type : %s
                Tarif : %s DT / jour
                Localisation : %s
                
                Animal : %s
                
                %sDu %s au %s
                
                Prix total estimé : %s DT
                
                Statut après envoi : en attente de confirmation du prestataire.
                
                Souhaitez-vous confirmer cette réservation ?
                """.formatted(
                serviceTitle,
                nullSafe(selServiceIslem.getType()),
                selServiceIslem.getTarif(),
                nullSafe(selServiceIslem.getLocalisation()),
                selAnimal.label(),
                reasonBlock,
                startDatePicker.getValue().format(DATE_FR),
                endDatePicker.getValue().format(DATE_FR),
                estimatedTotal
        );

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Récapitulatif");
        confirm.setHeaderText("Vérifiez les informations avant de valider");
        confirm.setContentText(summary);

        Optional<ButtonType> choice = confirm.showAndWait();
        if (choice.isEmpty() || choice.get() != ButtonType.OK) {
            return;
        }

        try {
            Reservation r = new Reservation();
            r.setClient_id(AdminNavigation.currentClientId);
            r.setId_service(selServiceIslem.getId_services());
            r.setAnimal_id(selAnimal.id());
            r.setStart_date(startSql);
            r.setEnd_date(endSql);
            r.setCancelled_reason(reasonField.getText());
            r.setStatus("en_attente");

            serviceReservationIslem.addReservation(r);

            Alert ok = new Alert(Alert.AlertType.INFORMATION);
            ok.setTitle("Terminé");
            ok.setHeaderText(null);
            ok.setContentText("Votre réservation a bien été enregistrée.\nLe prestataire pourra la traiter sous peu.");
            ok.showAndWait();
            clearFields();

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
        }
    }

    private static String nullSafe(String s) {
        return s != null ? s : "—";
    }

    private void clearFields() {
        serviceCombo.getSelectionModel().clearSelection();
        animalCombo.getSelectionModel().clearSelection();
        startDatePicker.setValue(null);
        endDatePicker.setValue(null);
        reasonField.clear();

        java.util.List<Service> approved = serviceAnimal.getServicesApprouves();
        serviceCombo.setItems(FXCollections.observableArrayList(approved));
        Integer cid = AdminNavigation.currentClientId;
        if (cid != null) {
            animalCombo.setItems(FXCollections.observableArrayList(serviceReservationIslem.listAnimalsForClient(cid)));
        }
        selectServiceFromNavigation(approved);
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
