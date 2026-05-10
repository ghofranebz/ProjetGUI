package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;

/**
 * Hub client « Nos services » : cartes par catégorie ; la liste filtrée est dans {@link ClientServicesCategoryController}.
 */
public class ClientServiceController {

    @FXML
    public void initialize() {
        AdminNavigation.clientServiceCategoryKey = null;
    }

    private void openCategory(ActionEvent event, String key) {
        AdminNavigation.clientServiceCategoryKey = key;
        AdminNavigation.changeScene(event, "clientservices_category.fxml");
    }

    @FXML
    private void exploreGarde(ActionEvent event) {
        openCategory(event, "garde");
    }

    @FXML
    private void exploreToilettage(ActionEvent event) {
        openCategory(event, "toilettage");
    }

    @FXML
    private void exploreDressage(ActionEvent event) {
        openCategory(event, "dressage");
    }

    @FXML
    private void explorePremium(ActionEvent event) {
        openCategory(event, "premium");
    }

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
