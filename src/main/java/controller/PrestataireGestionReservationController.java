package controller;

import entities.Reservation;
import entities.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.serviceReservation;
import services.serviceanimal;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PrestataireGestionReservationController {

    @FXML
    private VBox reservationsContainer;

    @FXML
    private ComboBox<String> filtreStatut;

    @FXML
    private Label statEnAttente;
    @FXML
    private Label statConfirmees;
    @FXML
    private Label statRefusees;
    @FXML
    private Label statAnnulees;
    @FXML
    private Label statRevenus;

    private final serviceReservation serviceReservationIslem = new serviceReservation();
    private final serviceanimal serviceAnimal = new serviceanimal();

    private List<Reservation> toutesLesReservationIslems;
    private List<Service> mesServiceIslems;

    // 🔥 ID du prestataire connecté (à remplacer par l'ID de session)
    private int currentPrestataireId = 1;

    @FXML
    public void initialize() {
        initialiserFiltres();
        chargerDonnees();
    }

    private void initialiserFiltres() {
        filtreStatut.getItems().addAll("Toutes", "En attente", "Confirmées", "Refusées", "Annulées");
        filtreStatut.setValue("Toutes");

        // Ajouter un listener pour filtrage automatique (optionnel)
        filtreStatut.valueProperty().addListener((obs, oldVal, newVal) -> {
            filtrerReservations(null);
        });
    }

    private void chargerDonnees() {
        // Charger les services du prestataire
        mesServiceIslems = serviceAnimal.getReservationsByUser(currentPrestataireId);

        // Charger les réservations reçues
        toutesLesReservationIslems = serviceReservationIslem.getReservationsByPrestataire(currentPrestataireId);

        // Mettre à jour les statistiques
        mettreAJourStatistiques();

        // Afficher toutes les réservations
        afficherReservations(toutesLesReservationIslems);
    }

    private void mettreAJourStatistiques() {
        long enAttente = toutesLesReservationIslems.stream()
                .filter(r -> "en_attente".equals(r.getStatus()))
                .count();

        long confirmees = toutesLesReservationIslems.stream()
                .filter(r -> "confirmee".equals(r.getStatus()))
                .count();

        long refusees = toutesLesReservationIslems.stream()
                .filter(r -> "refusee".equals(r.getStatus()))
                .count();

        long annulees = toutesLesReservationIslems.stream()
                .filter(r -> "annulee".equals(r.getStatus()))
                .count();

        double revenus = toutesLesReservationIslems.stream()
                .filter(r -> "confirmee".equals(r.getStatus()))
                .mapToDouble(Reservation::getTotal_price)
                .sum();

        statEnAttente.setText(String.valueOf(enAttente));
        statConfirmees.setText(String.valueOf(confirmees));
        statRefusees.setText(String.valueOf(refusees));
        statAnnulees.setText(String.valueOf(annulees));
        statRevenus.setText(String.format("%.2f DT", revenus));
    }

    @FXML
    private void filtrerReservations(ActionEvent event) {
        String statutSelectionne = filtreStatut.getValue();

        if (statutSelectionne == null || statutSelectionne.equals("Toutes")) {
            afficherReservations(toutesLesReservationIslems);
            return;
        }

        List<Reservation> filtrees;
        switch (statutSelectionne) {
            case "En attente":
                filtrees = toutesLesReservationIslems.stream()
                        .filter(r -> "en_attente".equals(r.getStatus()))
                        .collect(Collectors.toList());
                break;
            case "Confirmées":
                filtrees = toutesLesReservationIslems.stream()
                        .filter(r -> "confirmee".equals(r.getStatus()))
                        .collect(Collectors.toList());
                break;
            case "Refusées":
                filtrees = toutesLesReservationIslems.stream()
                        .filter(r -> "refusee".equals(r.getStatus()))
                        .collect(Collectors.toList());
                break;
            case "Annulées":
                filtrees = toutesLesReservationIslems.stream()
                        .filter(r -> "annulee".equals(r.getStatus()))
                        .collect(Collectors.toList());
                break;
            default:
                filtrees = toutesLesReservationIslems;
                break;
        }

        afficherReservations(filtrees);

        if (filtrees.isEmpty()) {
            afficherMessageAucunResultat(statutSelectionne);
        }
    }

    @FXML
    private void actualiser(ActionEvent event) {
        chargerDonnees();
        showAlert("Actualisation", "Les données ont été actualisées", Alert.AlertType.INFORMATION);
    }

    private void afficherReservations(List<Reservation> reservationIslems) {
        reservationsContainer.getChildren().clear();

        if (reservationIslems == null || reservationIslems.isEmpty()) {
            Label emptyLabel = new Label("📭 Aucune réservation trouvée");
            emptyLabel.getStyleClass().add("empty-label");
            reservationsContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Reservation r : reservationIslems) {
            VBox card = new VBox();
            card.getStyleClass().add("reservation-card");
            card.setSpacing(15);

            // En-tête avec statut
            HBox headerBox = new HBox();
            headerBox.setSpacing(15);
            headerBox.setAlignment(Pos.CENTER_LEFT);

            Label title = new Label("Réservation #" + r.getId_booking());
            title.getStyleClass().add("card-main-title");

            Label statusLabel = new Label(getStatutTexte(r.getStatus()));
            statusLabel.getStyleClass().add("status-badge-" + r.getStatus());

            headerBox.getChildren().addAll(title, statusLabel);

            // Détails de la réservation en grille
            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(20);
            detailsGrid.setVgap(10);
            detailsGrid.getStyleClass().add("details-grid");

            int row = 0;

            // Service
            detailsGrid.add(createLabelBold("📦 Service :"), 0, row);
            detailsGrid.add(new Label(getServiceName(r.getId_service())), 1, row);

            // Client
            row++;
            detailsGrid.add(createLabelBold("👤 Client ID :"), 0, row);
            detailsGrid.add(new Label(String.valueOf(r.getClient_id())), 1, row);

            // Animal
            detailsGrid.add(createLabelBold("🐾 Animal ID :"), 2, row);
            detailsGrid.add(new Label(String.valueOf(r.getAnimal_id())), 3, row);

            // Dates
            row++;
            detailsGrid.add(createLabelBold("📅 Date début :"), 0, row);
            detailsGrid.add(new Label(r.getStart_date().toString()), 1, row);

            detailsGrid.add(createLabelBold("Date fin :"), 2, row);
            detailsGrid.add(new Label(r.getEnd_date().toString()), 3, row);

            // Prix
            row++;
            detailsGrid.add(createLabelBold("💰 Prix total :"), 0, row);
            Label priceLabel = new Label(r.getTotal_price() + " DT");
            priceLabel.getStyleClass().add("price-label");
            detailsGrid.add(priceLabel, 1, row);

            // Raison d'annulation (si présente)
            if (r.getCancelled_reason() != null && !r.getCancelled_reason().isEmpty()) {
                row++;
                detailsGrid.add(createLabelBold("📝 Raison :"), 0, row);
                detailsGrid.add(new Label(r.getCancelled_reason()), 1, row, 3, 1);
            }

            // Boutons d'action
            HBox actionBox = new HBox();
            actionBox.setSpacing(15);
            actionBox.setAlignment(Pos.CENTER_RIGHT);

            if ("en_attente".equals(r.getStatus())) {
                Button accepterBtn = new Button("✅ Accepter");
                accepterBtn.getStyleClass().add("success-button");
                accepterBtn.setOnAction(event -> {
                    if (confirmerAction("Accepter cette réservation ?")) {
                        serviceReservationIslem.confirmerReservation(r.getId_booking());
                        chargerDonnees();
                        showAlert("Succès", "Réservation #" + r.getId_booking() + " acceptée", Alert.AlertType.INFORMATION);
                    }
                });

                Button refuserBtn = new Button("❌ Refuser");
                refuserBtn.getStyleClass().add("danger-button");
                refuserBtn.setOnAction(event -> {
                    if (confirmerAction("Refuser cette réservation ?")) {
                        serviceReservationIslem.refuserReservation(r.getId_booking());
                        chargerDonnees();
                        showAlert("Succès", "Réservation #" + r.getId_booking() + " refusée", Alert.AlertType.INFORMATION);
                    }
                });

                actionBox.getChildren().addAll(accepterBtn, refuserBtn);
            } else if ("confirmee".equals(r.getStatus())) {
                Label infoLabel = new Label("✓ Réservation confirmée");
                infoLabel.getStyleClass().add("info-label-success");
                actionBox.getChildren().add(infoLabel);
            } else if ("refusee".equals(r.getStatus())) {
                Label infoLabel = new Label("✗ Réservation refusée");
                infoLabel.getStyleClass().add("info-label-danger");
                actionBox.getChildren().add(infoLabel);
            } else if ("annulee".equals(r.getStatus())) {
                Label infoLabel = new Label("⚠️ Réservation annulée par le client");
                infoLabel.getStyleClass().add("info-label-warning");
                actionBox.getChildren().add(infoLabel);
            }

            card.getChildren().addAll(headerBox, detailsGrid, new Separator(), actionBox);
            reservationsContainer.getChildren().add(card);
        }
    }

    private Label createLabelBold(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("bold-label");
        return label;
    }

    private String getServiceName(int serviceId) {
        for (Service s : mesServiceIslems) {
            if (s.getId_services() == serviceId) {
                return (s.getTitle() != null && !s.getTitle().isEmpty()) ? s.getTitle() : s.getType();
            }
        }

        // Si pas trouvé dans mes services, chercher dans tous
        Service s = serviceAnimal.getEntityById(serviceId);
        if (s != null) {
            return (s.getTitle() != null && !s.getTitle().isEmpty()) ? s.getTitle() : s.getType();
        }

        return "Service #" + serviceId;
    }

    private String getStatutTexte(String status) {
        switch (status) {
            case "en_attente": return "⏳ En attente";
            case "confirmee": return "✅ Confirmée";
            case "refusee": return "❌ Refusée";
            case "annulee": return "🚫 Annulée";
            default: return status;
        }
    }

    private void afficherMessageAucunResultat(String statut) {
        Label msg = new Label("❌ Aucune réservation avec le statut : " + statut);
        msg.getStyleClass().add("error-label");
        reservationsContainer.getChildren().clear();
        reservationsContainer.getChildren().add(msg);
    }

    private boolean confirmerAction(String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText(null);
        alert.setContentText(message);
        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    // ================= NAVIGATION =================

    @FXML
    private void goDashboard(ActionEvent event) {
        AdminNavigation.changeScene(event, "prestatairedashboard.fxml");
    }

    @FXML
    private void goMesServices(ActionEvent event) {
        AdminNavigation.changeScene(event, "prestataire_mes_services.fxml");

    }
    @FXML
    private void goAddService(ActionEvent event) {
        AdminNavigation.changeScene(event, "prestataire_add_service.fxml");
    }
    @FXML
    private void goReservations(ActionEvent event) {
        AdminNavigation.changeScene(event, "prestataire_gesreservation.fxml");
    }
    @FXML
    private void goBack(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminservices.fxml");
    }
}