package controller;

import entities.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.Node;
import javafx.stage.Window;
import services.Serviceanimal;

import java.time.LocalDateTime;

public class AdminUpdateServiceController {

    @FXML
    private TextField idField;

    @FXML
    private TextField titleField;

    @FXML
    private TextField typeField;

    @FXML
    private TextArea descriptionField;

    @FXML
    private TextField tarifField;

    @FXML
    private TextField localisationField;

    private final Serviceanimal serviceanimal = new Serviceanimal();

    @FXML
    private void openMapPicker(ActionEvent event) {
        Window owner = ((Node) event.getSource()).getScene().getWindow();
        MapLocationPicker.showModal(owner,
                localisationField::setText,
                msg -> showAlert(Alert.AlertType.ERROR, msg));
    }

    @FXML
    public void initialize() {
        if (AdminNavigation.selectedServiceId != 0) {
            idField.setText(String.valueOf(AdminNavigation.selectedServiceId));
            searchService();
        }
    }

    @FXML
    private void searchService() {
        try {
            int id = Integer.parseInt(idField.getText());

            Service s = serviceanimal.getEntityById(id);

            if (s != null) {
                titleField.setText(s.getTitle());
                typeField.setText(s.getType());
                descriptionField.setText(s.getDescription());
                tarifField.setText(String.valueOf(s.getTarif()));
                localisationField.setText(s.getLocalisation());
                // 🔥 Le status n'est pas affiché dans l'interface, mais on le garde en mémoire
            } else {
                showAlert(Alert.AlertType.WARNING, "Service introuvable !");
            }

        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur : " + e.getMessage());
        }
    }

    @FXML
    private void updateService() {
        try {
            int id = Integer.parseInt(idField.getText());

            // 🔥 Récupérer l'ancien service pour conserver le statut et le propriétaire (user_id)
            Service oldService = serviceanimal.getEntityById(id);
            if (oldService == null) {
                showAlert(Alert.AlertType.WARNING, "Service introuvable !");
                return;
            }

            Service s = new Service();

            s.setTitle(titleField.getText());
            s.setType(typeField.getText());
            s.setDescription(descriptionField.getText());
            s.setTarif(Float.parseFloat(tarifField.getText()));
            s.setLocalisation(localisationField.getText());
            s.setCreatedAt(LocalDateTime.now());

            s.setUser_id(oldService.getUser_id());
            s.setStatus(oldService.getStatus() != null ? oldService.getStatus() : "en_attente");

            serviceanimal.updateEntity(id, s);

            showAlert(Alert.AlertType.INFORMATION, "Service modifié avec succès !");

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
        AdminNavigation.changeScene(event, "admindashboard.fxml");
    }

    @FXML
    private void goServices(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminservices.fxml");
    }
}
