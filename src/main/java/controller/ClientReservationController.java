package controller;

import javafx.fxml.FXML;

import entities.Reservation;
import entities.Service;
import entities.ServiceReview;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import services.ServiceReservation;
import services.ServiceReviewService;
import services.Serviceanimal;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ClientReservationController {

    @FXML
    private VBox reservationsContainer;

    @FXML
    private TextField searchField;

    private final ServiceReservation serviceReservation = new ServiceReservation();
    private final Serviceanimal serviceAnimal = new Serviceanimal();
    private final ServiceReviewService serviceReviewService = new ServiceReviewService();
    private List<Reservation> toutesLesReservations;

    @FXML
    public void initialize() {
        afficherReservations();
    }

    private void afficherReservations() {
        reservationsContainer.getChildren().clear();

        if (AdminNavigation.currentClientId == null) {
            Label msg = new Label("Connectez-vous pour afficher vos réservations.");
            msg.getStyleClass().add("empty-label");
            reservationsContainer.getChildren().add(msg);
            return;
        }

        toutesLesReservations = serviceReservation.getReservationsByUser(AdminNavigation.currentClientId);
        afficherCartesReservations(toutesLesReservations);
    }

    @FXML
    private void rechercherService(ActionEvent event) {
        if (AdminNavigation.currentClientId == null) {
            return;
        }

        String keyword = searchField.getText().trim();

        if (keyword.isEmpty()) {
            afficherCartesReservations(toutesLesReservations);
            return;
        }

        List<Service> servicesTrouves = serviceAnimal.rechercherServices(keyword);

        List<Integer> serviceIds = new ArrayList<>();
        for (Service s : servicesTrouves) {
            serviceIds.add(s.getId_services());
        }

        List<Reservation> resultats = new ArrayList<>();
        for (Reservation r : toutesLesReservations) {
            if (serviceIds.contains(r.getId_service())) {
                resultats.add(r);
            }
        }

        afficherCartesReservations(resultats);

        if (resultats.isEmpty()) {
            afficherMessageAucunResultat();
        }
    }

    private void afficherCartesReservations(List<Reservation> reservations) {
        reservationsContainer.getChildren().clear();

        if (reservations == null || reservations.isEmpty()) {
            Label emptyLabel = new Label("Aucune réservation");
            emptyLabel.getStyleClass().add("empty-label");
            reservationsContainer.getChildren().add(emptyLabel);
            return;
        }

        List<Integer> bookingIds = new ArrayList<>();
        for (Reservation r : reservations) {
            bookingIds.add(r.getId_booking());
        }
        Map<Integer, ServiceReview> reviewsByBooking = serviceReviewService.findByBookingIds(bookingIds);

        for (Reservation r : reservations) {
            HBox card = new HBox();
            card.getStyleClass().add("data-card");
            card.setSpacing(25);
            card.setAlignment(Pos.CENTER_LEFT);

            VBox infoBox = new VBox();
            infoBox.setSpacing(8);
            infoBox.setPrefWidth(600);

            Service svc = serviceAnimal.getEntityById(r.getId_service());
            String serviceTitle = svc != null && svc.getTitle() != null && !svc.getTitle().isBlank()
                    ? svc.getTitle()
                    : "Service #" + r.getId_service();

            Label title = new Label("Réservation #" + r.getId_booking());
            title.getStyleClass().add("card-main-title");

            Label serviceLbl = new Label("Service : " + serviceTitle);
            serviceLbl.getStyleClass().add("card-info");

            Label animal = new Label("Animal ID : " + r.getAnimal_id());
            animal.getStyleClass().add("card-info");

            Label dates = new Label("Du " + r.getStart_date() + " au " + r.getEnd_date());
            dates.getStyleClass().add("card-info");

            Label price = new Label("Prix total : " + r.getTotal_price() + " DT");
            price.getStyleClass().add("card-info");

            Label status = new Label("Statut : " + libelleStatut(r.getStatus()));
            status.getStyleClass().add("card-info");

            infoBox.getChildren().addAll(title, serviceLbl, animal, dates, price, status);

            String reason = r.getCancelled_reason();
            if (reason != null && !reason.isBlank()) {
                Label lr = new Label("Raison annulation : " + reason);
                lr.getStyleClass().add("card-info");
                infoBox.getChildren().add(lr);
            }

            VBox actionBox = new VBox();
            actionBox.setSpacing(12);
            actionBox.setAlignment(Pos.TOP_CENTER);

            Label idLabel = new Label("ID #" + r.getId_booking());
            idLabel.getStyleClass().add("id-label");
            actionBox.getChildren().add(idLabel);

            ServiceReview existing = reviewsByBooking.get(r.getId_booking());
            if (existing != null) {
                Label avisLbl = new Label(formatStars(existing.getRating()) + " · Votre avis");
                avisLbl.getStyleClass().add("review-inline");
                avisLbl.setWrapText(true);
                String comment = existing.getComment();
                if (comment != null && !comment.isBlank()) {
                    Label c = new Label("\"" + trunc(comment, 140) + "\"");
                    c.getStyleClass().add("review-comment-preview");
                    c.setWrapText(true);
                    actionBox.getChildren().addAll(avisLbl, c);
                } else {
                    actionBox.getChildren().add(avisLbl);
                }
            } else if (peutEvaluerReservation(r)) {
                Button rateBtn = new Button("★ Donner un avis");
                rateBtn.getStyleClass().add("primary-button");
                rateBtn.setOnAction(ev ->
                        ouvrirDialogueAvis(getWindow(ev), r, svc != null ? svc : minimalServicePlaceholder(r)));
                actionBox.getChildren().add(rateBtn);
            } else {
                Label hint = new Label(ratingHint(r));
                hint.getStyleClass().add("card-info");
                hint.setWrapText(true);
                hint.setPrefWidth(200);
                actionBox.getChildren().add(hint);
            }

            card.getChildren().addAll(infoBox, actionBox);
            reservationsContainer.getChildren().add(card);
        }
    }

    private static Service minimalServicePlaceholder(Reservation r) {
        Service s = new Service();
        s.setId_services(r.getId_service());
        s.setTitle("Service #" + r.getId_service());
        return s;
    }

    private static String trunc(String s, int max) {
        if (s.length() <= max) {
            return s;
        }
        return s.substring(0, max - 1) + "…";
    }

    private static String formatStars(int n) {
        if (n < 1) {
            n = 1;
        }
        if (n > 5) {
            n = 5;
        }
        return "★".repeat(n) + "☆".repeat(5 - n);
    }

    /** Une fois la date de fin atteinte (jour inclus ou passé), pour une réservation confirmée. */
    private boolean peutEvaluerReservation(Reservation r) {
        if (r == null || r.getEnd_date() == null) {
            return false;
        }
        if (!"confirmee".equalsIgnoreCase(trimOrEmpty(r.getStatus()))) {
            return false;
        }
        LocalDate end = r.getEnd_date().toLocalDate();
        LocalDate today = LocalDate.now();
        return !today.isBefore(end);
    }

    private String ratingHint(Reservation r) {
        if (!"confirmee".equalsIgnoreCase(trimOrEmpty(r.getStatus()))) {
            return "Avis possible après une réservation confirmée et terminée.";
        }
        if (r.getEnd_date() != null && LocalDate.now().isBefore(r.getEnd_date().toLocalDate())) {
            return "Vous pourrez noter ce service après le " + r.getEnd_date() + ".";
        }
        return "";
    }

    private static String trimOrEmpty(String s) {
        return s == null ? "" : s.trim();
    }

    private void ouvrirDialogueAvis(Window owner, Reservation r, Service service) {
        if (AdminNavigation.currentClientId == null
                || AdminNavigation.currentClientId != r.getClient_id()) {
            alert(Alert.AlertType.ERROR, "Session invalide pour cette réservation.");
            return;
        }

        Stage dialog = new Stage();
        dialog.initOwner(owner);
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.setTitle("Votre avis — " + service.getTitle());
        dialog.setResizable(false);

        Label intro = new Label("Comment s’est passée la prestation « " + service.getTitle() + " » ?");

        ComboBox<Integer> noteBox = new ComboBox<>(FXCollections.observableArrayList(5, 4, 3, 2, 1));
        noteBox.setValue(5);
        noteBox.setEditable(false);

        Label noteLbl = new Label("Note");

        TextArea comment = new TextArea();
        comment.setPromptText("Commentaire (facultatif, quelques lignes)");
        comment.setWrapText(true);
        comment.setPrefRowCount(4);
        comment.setPrefWidth(380);

        Button publier = new Button("Publier l’avis");
        publier.getStyleClass().add("primary-button");
        Button annuler = new Button("Annuler");
        annuler.getStyleClass().add("ghost-outline-button");
        annuler.setOnAction(e -> dialog.close());

        HBox boutons = new HBox(10, annuler, publier);
        boutons.setAlignment(Pos.CENTER_RIGHT);

        GridPane gp = new GridPane();
        gp.setHgap(10);
        gp.setVgap(10);
        gp.add(intro, 0, 0, 2, 1);
        gp.add(noteLbl, 0, 1);
        gp.add(noteBox, 1, 1);
        Label cpt = new Label("Commentaire");
        gp.add(cpt, 0, 2);
        gp.add(comment, 0, 3, 2, 1);
        gp.add(boutons, 0, 4, 2, 1);

        VBox root = new VBox(14, gp);
        root.setPadding(new Insets(20));
        Scene sc = new Scene(root);
        if (owner.getScene() != null && !owner.getScene().getStylesheets().isEmpty()) {
            sc.getStylesheets().addAll(owner.getScene().getStylesheets());
        }
        dialog.setScene(sc);

        publier.setOnAction(e -> {
            Integer v = noteBox.getValue();
            if (v == null) {
                alert(Alert.AlertType.WARNING, "Choisissez une note.");
                return;
            }
            ServiceReview rev = new ServiceReview(
                    r.getId_booking(),
                    r.getClient_id(),
                    r.getId_service(),
                    v,
                    comment.getText()
            );
            if (serviceReviewService.insertReview(rev)) {
                dialog.close();
                afficherReservations();
                alert(Alert.AlertType.INFORMATION, "Merci — votre avis a été enregistré.");
            } else {
                alert(Alert.AlertType.ERROR,
                        "Impossible d’enregistrer (avis déjà existant ou problème base de données).");
            }
        });

        dialog.showAndWait();
    }

    private void alert(Alert.AlertType type, String msg) {
        Alert a = new Alert(type);
        a.setHeaderText(null);
        a.setContentText(msg);
        a.showAndWait();
    }

    private Window getWindow(ActionEvent ev) {
        if (ev != null && ev.getSource() instanceof Node n && n.getScene() != null) {
            return n.getScene().getWindow();
        }
        return reservationsContainer.getScene().getWindow();
    }

    private String libelleStatut(String status) {
        if (status == null || "en_attente".equals(status)) {
            return "En attente";
        }
        if ("confirmee".equals(status)) {
            return "Confirmée";
        }
        if ("refusee".equals(status)) {
            return "Refusée";
        }
        if ("annulee".equals(status)) {
            return "Annulée";
        }
        return status;
    }

    private void afficherMessageAucunResultat() {
        Label msg = new Label("Aucune réservation trouvée pour ce service");
        msg.getStyleClass().add("error-label");
        reservationsContainer.getChildren().clear();
        reservationsContainer.getChildren().add(msg);
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
