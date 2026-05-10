package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

public class ClientDashboardController {

    @FXML
    private void goDashboard(ActionEvent event) {
        AdminNavigation.changeScene(event, "clientdashboard.fxml");
    }

    @FXML
    private void goServices(ActionEvent event) {
        AdminNavigation.changeScene(event, "clientservices.fxml");
    }

    @FXML
    private void goReservations(ActionEvent event) {
        AdminNavigation.changeScene(event, "clientreservations.fxml");
    }
}
