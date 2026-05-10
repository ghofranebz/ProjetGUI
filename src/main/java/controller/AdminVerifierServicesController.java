package controller;

import entities.Service;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import services.serviceanimal;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class AdminVerifierServicesController {

    @FXML
    private VBox servicesContainer;

    @FXML
    private ComboBox<String> filtreStatut;

    @FXML
    private Label statEnAttente;
    @FXML
    private Label statApprouves;
    @FXML
    private Label statRejetes;
    @FXML
    private Label statPrestataires;

    private final serviceanimal serviceAnimal = new serviceanimal();
    private List<Service> tousLesServiceIslems;

    @FXML
    public void initialize() {
        initialiserFiltres();
        chargerDonnees();
    }

    private void initialiserFiltres() {
        filtreStatut.getItems().addAll("Tous", "En attente", "Approuvés", "Rejetés");
        filtreStatut.setValue("En attente");

        // 🔥 Le filtre s'applique AUTOMATIQUEMENT quand on change la valeur
        filtreStatut.valueProperty().addListener((obs, oldVal, newVal) -> {
            filtrerServices(null);
        });
    }

    private void chargerDonnees() {
        // Récupérer tous les services
        tousLesServiceIslems = serviceAnimal.getAllEntities();

        mettreAJourStatistiques();

        // Par défaut, afficher les services en attente
        filtrerServices(null);
    }

    private void mettreAJourStatistiques() {
        long enAttente = tousLesServiceIslems.stream()
                .filter(s -> s.getStatus() == null || "en_attente".equals(s.getStatus()))
                .count();

        long approuves = tousLesServiceIslems.stream()
                .filter(s -> "approuve".equals(s.getStatus()))
                .count();

        long rejetes = tousLesServiceIslems.stream()
                .filter(s -> "rejete".equals(s.getStatus()))
                .count();

        long prestataires = tousLesServiceIslems.stream()
                .map(Service::getUser_id)
                .distinct()
                .count();

        statEnAttente.setText(String.valueOf(enAttente));
        statApprouves.setText(String.valueOf(approuves));
        statRejetes.setText(String.valueOf(rejetes));
        statPrestataires.setText(String.valueOf(prestataires));
    }

    private void filtrerServices(ActionEvent event) {
        String statutSelectionne = filtreStatut.getValue();

        if (statutSelectionne == null || statutSelectionne.equals("Tous")) {
            afficherServices(tousLesServiceIslems);
            return;
        }

        List<Service> filtrees;
        switch (statutSelectionne) {
            case "En attente":
                filtrees = tousLesServiceIslems.stream()
                        .filter(s -> s.getStatus() == null || "en_attente".equals(s.getStatus()))
                        .collect(Collectors.toList());
                break;
            case "Approuvés":
                filtrees = tousLesServiceIslems.stream()
                        .filter(s -> "approuve".equals(s.getStatus()))
                        .collect(Collectors.toList());
                break;
            case "Rejetés":
                filtrees = tousLesServiceIslems.stream()
                        .filter(s -> "rejete".equals(s.getStatus()))
                        .collect(Collectors.toList());
                break;
            default:
                filtrees = tousLesServiceIslems;
                break;
        }

        afficherServices(filtrees);

        if (filtrees.isEmpty()) {
            afficherMessageAucunResultat(statutSelectionne);
        }
    }

    @FXML
    private void actualiser(ActionEvent event) {
        chargerDonnees();
        showAlert("Actualisation", "Les données ont été actualisées", Alert.AlertType.INFORMATION);
    }

    private void afficherServices(List<Service> serviceIslems) {
        servicesContainer.getChildren().clear();

        if (serviceIslems == null || serviceIslems.isEmpty()) {
            Label emptyLabel = new Label("📭 Aucun service trouvé");
            emptyLabel.getStyleClass().add("empty-label");
            servicesContainer.getChildren().add(emptyLabel);
            return;
        }

        for (Service s : serviceIslems) {
            VBox card = new VBox();
            card.getStyleClass().add("service-card");
            card.setSpacing(15);

            // En-tête avec statut
            HBox headerBox = new HBox();
            headerBox.setSpacing(15);
            headerBox.setAlignment(Pos.CENTER_LEFT);

            Label title = new Label(s.getTitle() != null ? s.getTitle() : s.getType());
            title.getStyleClass().add("card-main-title");

            Label statusLabel = new Label(getStatutTexte(s.getStatus()));
            statusLabel.getStyleClass().add(getStatusBadgeClass(s.getStatus()));

            headerBox.getChildren().addAll(title, statusLabel);

            // Détails du service
            GridPane detailsGrid = new GridPane();
            detailsGrid.setHgap(20);
            detailsGrid.setVgap(10);
            detailsGrid.getStyleClass().add("details-grid");

            int row = 0;

            detailsGrid.add(createLabelBold("🐾 Type :"), 0, row);
            detailsGrid.add(new Label(s.getType()), 1, row);

            detailsGrid.add(createLabelBold("👤 Prestataire ID :"), 2, row);
            detailsGrid.add(new Label(String.valueOf(s.getUser_id())), 3, row);

            row++;
            detailsGrid.add(createLabelBold("📝 Description :"), 0, row);
            detailsGrid.add(new Label(s.getDescription()), 1, row, 3, 1);

            row++;
            detailsGrid.add(createLabelBold("💰 Tarif :"), 0, row);
            Label priceLabel = new Label(s.getTarif() + " DT / jour");
            priceLabel.getStyleClass().add("price-label");
            detailsGrid.add(priceLabel, 1, row);

            detailsGrid.add(createLabelBold("📍 Localisation :"), 2, row);
            detailsGrid.add(new Label(s.getLocalisation()), 3, row);

            // Boutons d'action (seulement si le service est en attente)
            HBox actionBox = new HBox();
            actionBox.setSpacing(15);
            actionBox.setAlignment(Pos.CENTER_RIGHT);

            String status = s.getStatus();
            if (status == null || "en_attente".equals(status)) {
                Button approuverBtn = new Button("✅ Approuver");
                approuverBtn.getStyleClass().add("success-button");
                approuverBtn.setOnAction(event -> {
                    if (confirmerAction("Approuver ce service ?")) {
                        serviceAnimal.approuverService(s.getId_services());
                        chargerDonnees();
                        showAlert("Succès", "Service #" + s.getId_services() + " approuvé", Alert.AlertType.INFORMATION);
                    }
                });

                Button rejeterBtn = new Button("❌ Rejeter");
                rejeterBtn.getStyleClass().add("danger-button");
                rejeterBtn.setOnAction(event -> {
                    if (confirmerAction("Rejeter ce service ?")) {
                        serviceAnimal.rejeterService(s.getId_services());
                        chargerDonnees();
                        showAlert("Succès", "Service #" + s.getId_services() + " rejeté", Alert.AlertType.INFORMATION);
                    }
                });

                actionBox.getChildren().addAll(approuverBtn, rejeterBtn);
            } else if ("approuve".equals(status)) {
                Label infoLabel = new Label("✓ Service approuvé");
                infoLabel.getStyleClass().add("info-label-success");
                actionBox.getChildren().add(infoLabel);
            } else if ("rejete".equals(status)) {
                Label infoLabel = new Label("✗ Service rejeté");
                infoLabel.getStyleClass().add("info-label-danger");
                actionBox.getChildren().add(infoLabel);
            }

            card.getChildren().addAll(headerBox, detailsGrid, new Separator(), actionBox);
            servicesContainer.getChildren().add(card);
        }
    }

    private Label createLabelBold(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("bold-label");
        return label;
    }

    private String getStatutTexte(String status) {
        if (status == null || "en_attente".equals(status)) return "⏳ En attente";
        if ("approuve".equals(status)) return "✅ Approuvé";
        if ("rejete".equals(status)) return "❌ Rejeté";
        return status;
    }

    private String getStatusBadgeClass(String status) {
        if (status == null || "en_attente".equals(status)) return "status-badge-en_attente";
        if ("approuve".equals(status)) return "status-badge-confirmee";
        if ("rejete".equals(status)) return "status-badge-refusee";
        return "status-badge-en_attente";
    }

    private void afficherMessageAucunResultat(String statut) {
        Label msg = new Label("❌ Aucun service avec le statut : " + statut);
        msg.getStyleClass().add("error-label");
        servicesContainer.getChildren().clear();
        servicesContainer.getChildren().add(msg);
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
        AdminNavigation.changeScene(event, "admindashboard.fxml");
    }

    @FXML
    private void goServices(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminservices.fxml");
    }

    @FXML
    private void goVerifierServices(ActionEvent event) {
        AdminNavigation.changeScene(event, "admin_verifier_services.fxml");
    }

    @FXML
    private void goBack(ActionEvent event) {
        AdminNavigation.changeScene(event, "admindashboard.fxml");
    }
}