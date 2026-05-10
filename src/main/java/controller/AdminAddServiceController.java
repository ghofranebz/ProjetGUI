package controller;

import entities.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.Node;
import javafx.stage.Window;
import services.Serviceanimal;

import java.time.LocalDateTime;

public class AdminAddServiceController {

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

    private final Serviceanimal serviceAnimal = new Serviceanimal();

    @FXML
    public void initialize() {
        if (AdminNavigation.presetServiceCategoryTitle != null) {
            titleField.setText(AdminNavigation.presetServiceCategoryTitle);
            typeField.setText(AdminNavigation.presetServiceCategoryTitle);
            if (AdminNavigation.presetServiceCategoryDescription != null
                    && !AdminNavigation.presetServiceCategoryDescription.isBlank()) {
                descriptionField.setText(AdminNavigation.presetServiceCategoryDescription);
            }
            AdminNavigation.presetServiceCategoryTitle = null;
            AdminNavigation.presetServiceCategoryDescription = null;
        }
    }

    @FXML
    private void openMapPicker(ActionEvent event) {
        Window owner = ((Node) event.getSource()).getScene().getWindow();
        MapLocationPicker.showModal(owner,
                localisationField::setText,
                msg -> showAlert("Carte", msg, Alert.AlertType.ERROR));
    }

    @FXML
    private void ajouterService(ActionEvent event) {
        if (titleField.getText().isEmpty() || typeField.getText().isEmpty() ||
                tarifField.getText().isEmpty() || localisationField.getText().isEmpty()) {

            showAlert("Erreur", "Tous les champs obligatoires doivent être remplis", Alert.AlertType.ERROR);
            return;
        }

        if (AdminNavigation.currentPrestataireId == null) {
            showAlert("Erreur",
                    "Session prestataire inconnue. Déconnectez-vous puis reconnectez-vous avec votre compte.",
                    Alert.AlertType.ERROR);
            return;
        }

        try {
            Service service = new Service();
            service.setTitle(titleField.getText());
            service.setType(typeField.getText());
            service.setDescription(descriptionField.getText());
            service.setTarif(Float.parseFloat(tarifField.getText()));
            service.setLocalisation(localisationField.getText());
            service.setUser_id(AdminNavigation.currentPrestataireId);
            service.setCreatedAt(LocalDateTime.now());
            service.setStatus("en_attente"); // 🔥 STATUS PAR DÉFAUT

            serviceAnimal.addEntity(service);

            showAlert("Succès", "Service ajouté avec succès !", Alert.AlertType.INFORMATION);
            viderChamps();

        } catch (NumberFormatException e) {
            showAlert("Erreur", "Le tarif doit être un nombre valide", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void goBack(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminservices.fxml");
    }

    @FXML
    private void goDashboard(ActionEvent event) {
        AdminNavigation.changeScene(event, "admindashboard.fxml");
    }

    @FXML
    private void goServices(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminservices.fxml");
    }

    private void viderChamps() {
        titleField.clear();
        typeField.clear();
        descriptionField.clear();
        tarifField.clear();
        localisationField.clear();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
