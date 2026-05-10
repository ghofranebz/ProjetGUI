package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class RoleChoiceController {

    @FXML
    private void choosePrestataire(ActionEvent event) {
        AdminNavigation.homeDashboardFxml = "admindashboard.fxml";
        AdminNavigation.servicesCatalogFxml = "adminservices.fxml";
        // À remplacer par l’ID réel retourné après connexion / inscription.
        if (AdminNavigation.currentPrestataireId == null) {
            AdminNavigation.currentPrestataireId = 1;
        }
        AdminNavigation.changeScene(event, "admindashboard.fxml");
    }

    @FXML
    private void chooseClient(ActionEvent event) {
        AdminNavigation.homeDashboardFxml = "clientdashboard.fxml";
        AdminNavigation.servicesCatalogFxml = "clientservices.fxml";
        AdminNavigation.reservationsListFxml = "clientreservations.fxml";

        // ✅ Mettre l'ID d'un vrai client qui existe dans ta base de données
        if (AdminNavigation.currentClientId == null) {
            AdminNavigation.currentClientId = 1; // ← change ce chiffre
        }

        AdminNavigation.changeScene(event, "clientdashboard.fxml");
    }
}
