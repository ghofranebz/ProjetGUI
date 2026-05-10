package controller;

import entities.Reservation;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import services.KonnectPaymentService;

import java.awt.Desktop;
import java.net.URI;

public class Payment{

    @FXML private Label reservationIdLabel;
    @FXML private Label serviceLabel;
    @FXML private Label datesLabel;
    @FXML private Label montantLabel;
    @FXML private Button payerButton;
    @FXML private Label statusLabel;
    @FXML private VBox loadingBox;

    private Reservation reservationIslem;

    public void setReservation(Reservation r) {
        this.reservationIslem = r;
        reservationIdLabel.setText("Réservation #" + r.getId_booking());
        datesLabel.setText("Du " + r.getStart_date() + " au " + r.getEnd_date());
        montantLabel.setText(String.format("%.2f DT", r.getTotal_price()));
        serviceLabel.setText("Service #" + r.getId_service());
    }

    @FXML
    public void initialize() {
        loadingBox.setVisible(false);
        statusLabel.setText("");
    }

    @FXML
    private void handlePayer() {
        if (reservationIslem == null) return;

        payerButton.setDisable(true);
        loadingBox.setVisible(true);
        statusLabel.setText("");

        Thread thread = new Thread(() -> {
            try {
                KonnectPaymentService konnect = new KonnectPaymentService();

                long montantMillimes = (long) (reservationIslem.getTotal_price() * 1000);
                String orderId = "booking-" + reservationIslem.getId_booking();
                String description = "Réservation #" + reservationIslem.getId_booking();
                String sellerWallet = konnect.getDefaultSellerWalletId();

                String payUrl = konnect.initSplitPayment(
                        sellerWallet,
                        montantMillimes,
                        description,
                        orderId
                );

                Platform.runLater(() -> {
                    loadingBox.setVisible(false);
                    try {
                        Desktop.getDesktop().browse(new URI(payUrl));
                        statusLabel.setText("✅ Redirection vers Konnect...");
                        statusLabel.setStyle("-fx-text-fill: green;");
                    } catch (Exception e) {
                        statusLabel.setText("Lien : " + payUrl);
                        statusLabel.setStyle("-fx-text-fill: #243757;");
                    }
                    payerButton.setDisable(false);
                });

            } catch (Exception e) {
                Platform.runLater(() -> {
                    loadingBox.setVisible(false);
                    statusLabel.setText("❌ Erreur : " + e.getMessage());
                    statusLabel.setStyle("-fx-text-fill: red;");
                    payerButton.setDisable(false);
                });
            }
        });
        thread.setDaemon(true);
        thread.start();
    }

    @FXML
    private void handleRetour() {
        // retour vers mes reservations via le stage
        javafx.stage.Stage stage = (javafx.stage.Stage) payerButton.getScene().getWindow();
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    getClass().getResource("/clientreservations.fxml")
            );
            javafx.scene.Parent root = loader.load();
            AdminNavigation.applyWhiteBackground(root);
            stage.setScene(new javafx.scene.Scene(root, 1200, 750));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

