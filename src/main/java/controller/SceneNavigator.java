package controller;

import javafx.animation.PauseTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.util.Duration;

public class SceneNavigator {

    // Call this instead of loader.load() every time you change page
    public static void navigateTo(Stage stage, String fxmlPath) throws Exception {

        // 1. Load the cat GIF
        Image catGif = new Image(
                SceneNavigator.class.getResourceAsStream("/images/Cat_Movement.gif")
        );
        ImageView catView = new ImageView(catGif);
        catView.setPreserveRatio(true);
        catView.setFitWidth(300);

        // 2. Show a splash scene with the cat
        StackPane splash = new StackPane(catView);
        splash.setStyle("-fx-background-color: white;");
        Scene splashScene = new Scene(splash, stage.getWidth(), stage.getHeight());
        stage.setScene(splashScene);

        // 3. After 1.5 seconds, load the real FXML scene
        PauseTransition pause = new PauseTransition(Duration.millis(1500));
        pause.setOnFinished(event -> {
            try {
                Parent root = FXMLLoader.load(
                        SceneNavigator.class.getResource(fxmlPath)
                );
                stage.setScene(new Scene(root));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        pause.play();
    }
}
