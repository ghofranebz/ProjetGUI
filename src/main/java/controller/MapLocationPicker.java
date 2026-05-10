package controller;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import netscape.javascript.JSObject;
import services.GeocodeResult;
import services.GeocodingService;

import java.net.URL;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Fenêtre carte (Leaflet + tuiles OSM) : clic pour choisir un point,
 * puis géocodage inverse Nominatim pour remplir {@code localisation}.
 */
public final class MapLocationPicker {

    private MapLocationPicker() {}

    public static void showModal(Window owner,
                                 Consumer<String> onChosen,
                                 Consumer<String> onError) {
        URL resource = MapLocationPicker.class.getResource("/map-picker.html");
        if (resource == null) {
            onError.accept("Fichier carte introuvable (map-picker.html).");
            return;
        }

        Stage stage = new Stage();
        stage.initOwner(owner);
        stage.initModality(Modality.WINDOW_MODAL);
        stage.setTitle("Choisir la position sur la carte");

        WebView webView = new WebView();
        webView.setPrefSize(920, 540);
        WebEngine engine = webView.getEngine();

        GeocodingService geo = new GeocodingService();
        AtomicBoolean busy = new AtomicBoolean(false);

        BiConsumer<Double, Double> onPick = (lat, lng) -> {
            if (!busy.compareAndSet(false, true)) {
                return;
            }
            Task<String> task = new Task<>() {
                @Override
                protected String call() {
                    try {
                        return geo.reverseGeocode(lat, lng)
                                .map(GeocodeResult::toStoredLocalisation)
                                .orElseGet(() -> fallbackLabel(lat, lng));
                    } catch (Exception ex) {
                        return fallbackLabel(lat, lng);
                    }
                }
            };
            task.setOnSucceeded(ev -> {
                busy.set(false);
                onChosen.accept(task.getValue());
                stage.close();
            });
            task.setOnFailed(ev -> {
                busy.set(false);
                onChosen.accept(fallbackLabel(lat, lng));
                stage.close();
            });
            new Thread(task, "nominatim-reverse").start();
        };

        JsBridge bridge = new JsBridge(onPick);

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {
                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("javaBridge", bridge);
                engine.executeScript("(function(){ if (typeof initMapBridge === 'function') initMapBridge(); })();");
            }
        });

        engine.load(resource.toExternalForm());

        Label hint = new Label("Zoomez avec la molette ou les boutons +/−, puis cliquez sur le lieu exact du service.");
        hint.setWrapText(true);
        hint.getStyleClass().add("subtitle");

        Button closeBtn = new Button("Fermer sans choisir");
        closeBtn.getStyleClass().add("dark-button");
        closeBtn.setOnAction(e -> stage.close());

        VBox root = new VBox(12, hint, webView, closeBtn);
        root.setPadding(new Insets(14));
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root);
        URL css = MapLocationPicker.class.getResource("/style.css");
        if (css != null) {
            scene.getStylesheets().add(css.toExternalForm());
        }
        stage.setScene(scene);
        stage.show();
    }

    private static String fallbackLabel(double lat, double lng) {
        String label = String.format(Locale.US, "Point %.6f, %.6f", lat, lng);
        return new GeocodeResult(label, lat, lng).toStoredLocalisation();
    }

    /** Pont JS ↔ Java (WebEngine). */
    public static final class JsBridge {
        private final BiConsumer<Double, Double> onPick;

        public JsBridge(BiConsumer<Double, Double> onPick) {
            this.onPick = onPick;
        }

        public void reportClick(double lat, double lng) {
            Platform.runLater(() -> onPick.accept(lat, lng));
        }
    }
}
