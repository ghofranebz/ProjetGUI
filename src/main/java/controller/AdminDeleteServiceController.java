package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import services.Serviceanimal;

import java.util.Optional;

public class AdminDeleteServiceController {

    @FXML
    private TextField idField;

    private final Serviceanimal serviceanimal = new Serviceanimal();

    @FXML
    public void initialize() {
    }

    @FXML
    private void deleteService() {
        try {
            int id = Integer.parseInt(idField.getText());

            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirmation");
            confirm.setHeaderText(null);
            confirm.setContentText("Supprimer le service n°" + id + " ? Cette action est définitive.");
            Optional<ButtonType> choix = confirm.showAndWait();
            if (choix.isEmpty() || choix.get() != ButtonType.OK) {
                return;
            }

            serviceanimal.deleteEntity(id);

            showAlert(Alert.AlertType.INFORMATION, "Service supprimé avec succès !");
            idField.clear();

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
