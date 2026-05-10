package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.VPos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;

import java.io.File;

/**
 * Choix du type de service avant le formulaire : titre (et description suggérée) reprennent la carte sélectionnée.
 */
public class ServiceCategoryChoiceController {

    @FXML
    private ImageView imgGardes;
    @FXML
    private ImageView imgToilettage;
    @FXML
    private ImageView imgDressage;
    @FXML
    private ImageView imgPremium;

    @FXML
    private void initialize() {
        // Utilise les images fournies si disponibles ; fallback sur des visuels générés.
        setImageOrFallback(
                imgToilettage,
                "C:/Users/MSI/.cursor/projects/c-Users-MSI-IdeaProjects-ProjetGUI/assets/c__Users_MSI_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_Guide_complet_pour_le_toilettage_de_votre_chien___la_maison-f72ab901-3dbb-438c-b708-47fd8a53f38e.png",
                "🛁", "Toilettage", Color.web("#0F766E"), Color.web("#E5E4DE")
        );
        setImageOrFallback(
                imgGardes,
                "C:/Users/MSI/.cursor/projects/c-Users-MSI-IdeaProjects-ProjetGUI/assets/c__Users_MSI_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_How_to_Turn_Your_Love_for_Pets_into_a_Profitable_Craft_Business-697db364-10b1-4edd-9da0-a959c04e8060.png",
                "🏠", "Gardes", Color.web("#243757"), Color.web("#E5E4DE")
        );
        setImageOrFallback(
                imgDressage,
                "C:/Users/MSI/.cursor/projects/c-Users-MSI-IdeaProjects-ProjetGUI/assets/c__Users_MSI_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_090d7865-c66c-4c66-824c-5cd25e297203-5fa513b8-e9d1-45b8-9478-af35d6a91d42.png",
                "🎓", "Dressage", Color.web("#7C3AED"), Color.web("#E5E4DE")
        );
        setImageOrFallback(
                imgPremium,
                "C:/Users/MSI/.cursor/projects/c-Users-MSI-IdeaProjects-ProjetGUI/assets/c__Users_MSI_AppData_Roaming_Cursor_User_workspaceStorage_empty-window_images_9b8cd385-f9c4-4c89-8370-32b371acba36-fa5f4ffe-d752-489d-84ed-a8d190851f40.png",
                "✨", "Premium", Color.web("#C4A574"), Color.web("#E5E4DE")
        );
    }

    @FXML
    private void pickToilettage(ActionEvent event) {
        openForm(event, "Toilettage", "Toilettage complet : bain, brushing, coupe et soins adaptés.");
    }

    @FXML
    private void pickGardes(ActionEvent event) {
        openForm(event, "Gardes", "Garde et présence : à domicile ou en pension selon votre offre.");
    }

    @FXML
    private void pickPremium(ActionEvent event) {
        openForm(event, "Premium",
                "Formule premium : promenade, séance photo et accès spa / détente pour le compagnon.");
    }

    @FXML
    private void pickDressage(ActionEvent event) {
        openForm(event, "Dressage",
                "Dressage & éducation : obéissance, socialisation, apprentissage des ordres et correction des comportements.");
    }

    private void openForm(ActionEvent event, String title, String suggestedDescription) {
        AdminNavigation.presetServiceCategoryTitle = title;
        AdminNavigation.presetServiceCategoryDescription = suggestedDescription;
        AdminNavigation.changeScene(event, "adminaddservice.fxml");
    }

    private void setImageOrFallback(
            ImageView view,
            String absolutePath,
            String emoji,
            String title,
            Color accent,
            Color base
    ) {
        if (view == null) {
            return;
        }

        try {
            File f = new File(absolutePath);
            if (f.exists() && f.isFile()) {
                Image img = new Image(f.toURI().toString(), false);
                view.setImage(img);
                applyRoundedClip(view);
                return;
            }
        } catch (Exception ignored) {
            // fallback dessous
        }

        applyCardImage(view, emoji, title, accent, base);
    }

    private void applyCardImage(ImageView view, String emoji, String title, Color accent, Color base) {
        if (view == null) {
            return;
        }

        double w = Math.max(1, view.getFitWidth());
        double h = Math.max(1, view.getFitHeight());

        Canvas canvas = new Canvas(w, h);
        GraphicsContext g = canvas.getGraphicsContext2D();

        // fond doux + bande accent
        g.setFill(Color.web("#ffffff", 0.92));
        g.fillRoundRect(0, 0, w, h, 18, 18);
        g.setFill(accent.deriveColor(0, 1, 1, 0.16));
        g.fillRoundRect(0, 0, w, h, 18, 18);

        g.setFill(base.deriveColor(0, 1, 1, 0.35));
        g.fillOval(-h * 0.25, h * 0.15, h * 0.9, h * 0.9);
        g.fillOval(w * 0.55, -h * 0.35, h * 0.9, h * 0.9);

        // emoji
        g.setTextAlign(TextAlignment.LEFT);
        g.setTextBaseline(VPos.CENTER);
        g.setFill(Color.web("#111827"));
        g.setFont(Font.font("System", FontWeight.BOLD, h * 0.55));
        g.fillText(emoji, 18, h * 0.52);

        // titre
        g.setFill(Color.web("#243757"));
        g.setFont(Font.font("System", FontWeight.BOLD, h * 0.18));
        g.fillText(title, 18 + h * 0.55, h * 0.54);

        WritableImage img = new WritableImage((int) Math.ceil(w), (int) Math.ceil(h));
        canvas.snapshot(null, img);
        view.setImage(img);
        applyRoundedClip(view);
    }

    private void applyRoundedClip(ImageView view) {
        double w = Math.max(1, view.getFitWidth());
        double h = Math.max(1, view.getFitHeight());
        Rectangle clip = new Rectangle(w, h);
        clip.setArcWidth(18);
        clip.setArcHeight(18);
        view.setClip(clip);
    }

    @FXML
    private void goBack(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminservices.fxml");
    }

    @FXML
    private void goDashboard(ActionEvent event) {
        AdminNavigation.changeScene(event, "admindashboard.fxml");
    }

    @FXML
    private void goServices(ActionEvent event) {
        AdminNavigation.changeScene(event, "adminservices.fxml");
    }

    @FXML
    private void goAdmin(ActionEvent event) {
        AdminNavigation.changeScene(event, "admin_verifier_services.fxml");
    }
}
