package tests;

import controller.AdminNavigation;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainFx extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader =
                new FXMLLoader(getClass().getResource("/rolechoice.fxml"));

        Parent root = loader.load();
        AdminNavigation.applyPawsBackground(root);
        Scene scene = new Scene(root, 1200, 750);

        primaryStage.setTitle("Purrly");

        primaryStage.setScene(scene);

        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}