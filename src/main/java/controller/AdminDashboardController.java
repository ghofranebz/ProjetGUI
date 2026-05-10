package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class AdminDashboardController {

    @FXML
    public void initialize() {
    }

    @FXML
    private void goDashboard(ActionEvent event) {
        AdminNavigation.changeScene(event, "admindashboard.fxml");
    }

    @FXML
    private void goServices(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminservices.fxml");
    }

    // 🔥 NOUVEAU : Aller à la page de vérification des services
    @FXML
    private void goAdmin(ActionEvent event) {
        AdminNavigation.changeScene(event, "admin_verifier_services.fxml");
    }
}