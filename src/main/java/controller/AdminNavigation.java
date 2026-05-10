package controller;

import javafx.animation.PauseTransition;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Duration;

public class AdminNavigation {

    public static int selectedServiceId = 0;
    public static int selectedReservationId = 0;
    public static Integer currentClientId = null;
    public static Integer currentPrestataireId = null;
    public static String presetServiceCategoryTitle = null;
    public static String presetServiceCategoryDescription = null;
    public static String servicesCatalogFxml = "clientservices.fxml";
    public static String clientServiceCategoryKey = null;
    public static String homeDashboardFxml = "admindashboard.fxml";
    public static String reservationsListFxml = "clientreservations.fxml";

    public static void changeScene(ActionEvent event, String fxml) {
        try {
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            FXMLLoader loader = new FXMLLoader(
                    AdminNavigation.class.getResource("/" + fxml)
            );
            Parent root = loader.load();
            applyWhiteBackground(root);
            Scene scene = new Scene(root, 1200, 750);
            stage.setScene(scene);
            stage.show();

            // Cat GIF overlay bottom-right
            Image catGif = new Image(
                    AdminNavigation.class.getResourceAsStream("/images/Cat_Movement.gif")
            );
            ImageView catView = new ImageView(catGif);
            catView.setPreserveRatio(true);
            catView.setFitWidth(120);

            StackPane overlay = new StackPane(catView);
            StackPane.setAlignment(catView, javafx.geometry.Pos.BOTTOM_RIGHT);
            overlay.setPickOnBounds(false);
            overlay.setPrefSize(1200, 750);
            overlay.setStyle("-fx-background-color: transparent;");

            StackPane wrapper = new StackPane(root, overlay);
            scene.setRoot(wrapper);

            PauseTransition pause = new PauseTransition(Duration.millis(2000));
            pause.setOnFinished(e -> wrapper.getChildren().remove(overlay));
            pause.play();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void applyPawsBackground(Parent root) {
        applyWhiteBackground(root);
    }

    public static void applyWhiteBackground(Parent root) {
        if (!(root instanceof Region region)) return;
        region.setBackground(new Background(
                new BackgroundFill(Color.WHITE, CornerRadii.EMPTY, null)
        ));
    }
}
