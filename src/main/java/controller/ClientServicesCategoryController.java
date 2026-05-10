package controller;

import entities.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import services.ContactPrestataireService;
import services.ServiceReviewService;
import services.Serviceanimal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Liste les services approuvés filtrés par catégorie (après le hub « Nos services »).
 */
public class ClientServicesCategoryController {

    @FXML
    private Label categoryTitleLabel;

    @FXML
    private VBox servicesContainer;

    private final Serviceanimal serviceAnimal = new Serviceanimal();
    private final ContactPrestataireService contactPrestataireService = new ContactPrestataireService();
    private final ServiceReviewService serviceReviewService = new ServiceReviewService();

    @FXML
    public void initialize() {
        String key = AdminNavigation.clientServiceCategoryKey != null
                ? AdminNavigation.clientServiceCategoryKey.trim().toLowerCase()
                : "";
        categoryTitleLabel.setText(titleForKey(key));
        afficherServicesFiltres(key);
    }

    private static String titleForKey(String key) {
        return switch (key) {
            case "garde" -> "Services de garde";
            case "toilettage" -> "Services de toilettage";
            case "dressage" -> "Services de dressage";
            case "premium" -> "Services premium";
            default -> "Services";
        };
    }

    private void afficherServicesFiltres(String key) {
        servicesContainer.getChildren().clear();

        List<Service> approved = serviceAnimal.getServicesApprouves();
        if (approved == null) {
            approved = List.of();
        }

        List<Service> filtered = new ArrayList<>();
        for (Service s : approved) {
            if (matchesCategory(s, key)) {
                filtered.add(s);
            }
        }

        if (filtered.isEmpty()) {
            Label empty = new Label("Aucun service dans cette catégorie pour le moment.");
            empty.getStyleClass().add("empty-label");
            servicesContainer.getChildren().add(empty);
            return;
        }

        for (Service s : filtered) {
            HBox card = new HBox();
            card.getStyleClass().add("data-card");
            card.setSpacing(25);
            card.setAlignment(Pos.CENTER_LEFT);

            VBox infoBox = new VBox();
            infoBox.setSpacing(8);
            infoBox.setPrefWidth(600);

            Label title = new Label(s.getTitle() != null ? s.getTitle() : s.getType());
            title.getStyleClass().add("card-main-title");

            int nbAvis = serviceReviewService.countReviewsForService(s.getId_services());
            if (nbAvis > 0) {
                double moy = serviceReviewService.averageRatingForService(s.getId_services());
                Label avisMoyen = new Label(String.format(Locale.FRANCE,
                        "★ Note moyenne : %.1f/5 · %d avis", moy, nbAvis));
                avisMoyen.getStyleClass().add("service-rating-summary");
                infoBox.getChildren().addAll(title, avisMoyen);
            } else {
                infoBox.getChildren().add(title);
            }

            Label type = new Label("Type : " + s.getType());
            type.getStyleClass().add("card-info");

            Label description = new Label("Description : " + nullSafe(s.getDescription()));
            description.getStyleClass().add("card-info");
            description.setWrapText(true);

            Label tarif = new Label("Tarif : " + s.getTarif() + " DT");
            tarif.getStyleClass().add("card-info");

            Label localisation = new Label("Localisation : " + nullSafe(s.getLocalisation()));
            localisation.getStyleClass().add("card-info");

            infoBox.getChildren().addAll(type, description, tarif, localisation);

            VBox actionBox = new VBox();
            actionBox.setSpacing(12);
            actionBox.setAlignment(Pos.CENTER);

            Button contactBtn = new Button("Contacter prestataire");
            contactBtn.getStyleClass().add("dark-button");
            contactBtn.setOnAction(event -> showContactPrestataireDialog(event, s));

            Button reserverBtn = new Button("Réserver");
            reserverBtn.getStyleClass().add("primary-button");
            reserverBtn.setOnAction(event -> {
                AdminNavigation.selectedServiceId = s.getId_services();
                AdminNavigation.homeDashboardFxml = "clientdashboard.fxml";
                AdminNavigation.servicesCatalogFxml = "clientservices_category.fxml";
                AdminNavigation.reservationsListFxml = "clientreservations.fxml";
                AdminNavigation.changeScene(event, "adminaddreservation.fxml");
            });

            HBox actionsRow = new HBox(12);
            actionsRow.setAlignment(Pos.CENTER);
            actionsRow.getChildren().addAll(contactBtn, reserverBtn);

            actionBox.getChildren().add(actionsRow);
            card.getChildren().addAll(infoBox, actionBox);
            servicesContainer.getChildren().add(card);
        }
    }

    private static boolean matchesCategory(Service s, String key) {
        if (key == null || key.isBlank()) {
            return true;
        }
        String hay = "";
        if (s.getType() != null) hay += " " + s.getType();
        if (s.getTitle() != null) hay += " " + s.getTitle();
        hay = hay.toLowerCase();

        return switch (key) {
            case "garde" -> hay.contains("garde");
            case "toilettage" -> hay.contains("toilett");
            case "dressage" -> hay.contains("dressage") || hay.contains("dresse");
            case "premium" -> hay.contains("premium");
            default -> true;
        };
    }

    private static String nullSafe(String v) {
        return v != null ? v : "";
    }

    private void showContactPrestataireDialog(ActionEvent event, Service service) {
        Window owner = ((Node) event.getSource()).getScene().getWindow();

        if (AdminNavigation.currentClientId == null) {
            showAlert(Alert.AlertType.WARNING, "Connexion requise",
                    "Connectez-vous en tant que client pour envoyer un message.", owner);
            return;
        }

        if (service.getUser_id() <= 0) {
            showAlert(Alert.AlertType.WARNING, "Prestataire introuvable",
                    "Ce service n'a pas de prestataire associé.", owner);
            return;
        }

        Stage popup = new Stage();
        popup.initOwner(owner);
        popup.initModality(Modality.WINDOW_MODAL);
        popup.setTitle("Contacter le prestataire");

        String serviceLabel = service.getTitle() != null && !service.getTitle().isBlank()
                ? service.getTitle()
                : nullSafe(service.getType());

        Label head = new Label("Message au prestataire");
        head.getStyleClass().add("contact-popup-title");

        Label sub = new Label("Service : " + serviceLabel);
        sub.getStyleClass().add("subtitle");
        sub.setWrapText(true);

        TextArea bodyField = new TextArea();
        bodyField.setPromptText("Écrivez votre message (questions, disponibilités, besoins de votre animal…)");
        bodyField.setWrapText(true);
        bodyField.setPrefRowCount(8);
        bodyField.setMaxWidth(Double.MAX_VALUE);

        Button sendBtn = new Button("Envoyer");
        sendBtn.getStyleClass().add("primary-button");
        Button cancelBtn = new Button("Annuler");
        cancelBtn.getStyleClass().add("dark-button");

        HBox btnRow = new HBox(12);
        btnRow.setAlignment(Pos.CENTER_RIGHT);
        btnRow.getChildren().addAll(cancelBtn, sendBtn);

        VBox root = new VBox(14);
        root.setPadding(new Insets(22));
        root.setPrefWidth(460);
        root.getStyleClass().add("contact-popup-root");
        root.getChildren().addAll(head, sub, bodyField, btnRow);

        cancelBtn.setOnAction(e -> popup.close());
        sendBtn.setOnAction(e -> {
            try {
                contactPrestataireService.sendMessage(
                        AdminNavigation.currentClientId,
                        service.getUser_id(),
                        service.getId_services(),
                        bodyField.getText()
                );
                showAlert(Alert.AlertType.INFORMATION, "Message envoyé",
                        "Votre message a été transmis au prestataire.", popup);
                popup.close();
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.WARNING, "Message invalide", ex.getMessage(), popup);
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Envoi impossible",
                        "Impossible d'enregistrer le message : " + ex.getMessage(), popup);
            }
        });

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/style.css").toExternalForm());
        popup.setScene(scene);
        popup.showAndWait();
    }

    private static void showAlert(Alert.AlertType type, String title, String message, Window owner) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        if (owner != null) {
            alert.initOwner(owner);
        }
        alert.showAndWait();
    }

    private static void showAlert(Alert.AlertType type, String title, String message) {
        showAlert(type, title, message, null);
    }

    @FXML
    private void goHub(ActionEvent event) {
        AdminNavigation.changeScene(event, "clientservices.fxml");
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
